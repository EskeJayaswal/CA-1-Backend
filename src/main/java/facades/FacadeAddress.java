package facades;

import dtos.AddressDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;
import errorhandling.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class FacadeAddress {
    private static FacadeAddress instance;
    private static EntityManagerFactory emf;

    public FacadeAddress() {}

    public static FacadeAddress getFacadeAddress(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeAddress();
        }
        return instance;
    }

    public AddressDTO create(AddressDTO aDto) throws EntityNotFoundException {
        Address address = new Address(aDto.getStreet(), aDto.getAdditionalInfo());
        EntityManager em = emf.createEntityManager();

        CityInfo cityInfo = FacadeCityInfo.getFacadeCityInfo(emf).getCityInfoByZip(aDto.getCityInfoDTO().getZipCode());
        System.out.println("CityInfo ID: "+ cityInfo.getId());
        cityInfo.addAddress(address);             // Add references for bi-directional relationships.

        try {
            em.getTransaction().begin();
            em.merge(address);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        System.out.println("Create Address ID: "+ address.getId());
        return new AddressDTO(address);
    }

    public AddressDTO findOrCreate(AddressDTO aDTO) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Address> typedQueryAddress
                = em.createQuery(
                "SELECT a FROM Address a, CityInfo ci " +
                        "WHERE a.street = '" + aDTO.getStreet() +
                        "' and a.additionalInfo = '" + aDTO.getAdditionalInfo() +
                        "' and ci.zipCode = '" + aDTO.getCityInfoDTO().getZipCode() +"'", Address.class);

        List<Address> addressList = typedQueryAddress.getResultList();
        if (addressList.size() != 0) {
//            System.out.println("Address list is NOT empty");
            return new AddressDTO(addressList.get(0));
        }
        else {
            return aDTO;
        }
    }

    public long getAddressCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(a) FROM Address a").getSingleResult();
        } finally {
            em.close();
        }
    }

//    public AddressDTO updateAddress(long personId, String street, String addInfo) {
//        EntityManager em = emf.createEntityManager();
//        Person person = em.find(Person.class, personId);
//        Address address = person.getAddress();
//        address.setStreet(street);
//        address.setAdditionalInfo(addInfo);
//
//        try {
//            em.getTransaction().begin();
//            em.merge(address);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//
//        return new AddressDTO(address);
//    }
}
