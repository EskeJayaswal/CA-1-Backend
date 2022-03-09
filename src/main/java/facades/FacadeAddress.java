package facades;

import dtos.AddressDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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

    public AddressDTO create(AddressDTO aDto) {
        Address address = new Address(aDto.getStreet(), aDto.getAdditionalInfo());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(address);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new AddressDTO(address);
    }

    public long getAddressCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(a) FROM Address a").getSingleResult();
        } finally {
            em.close();
        }
    }
}
