package facades;

import dtos.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Person;
import entities.Phone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class FacadePerson {
    private static FacadePerson instance;
    private static EntityManagerFactory emf;

    public FacadePerson() {}

    public static FacadePerson getFacadePerson(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadePerson();
        }
        return instance;
    }

    public PersonDTO create(PersonDTO pDTO) {
        CityInfo cityInfo = new CityInfo(pDTO.getAddressDTO().getCityInfoDTO());
        Address address = new Address(pDTO.getAddressDTO());
        Person person = new Person(pDTO);
        cityInfo.addAddress(address);
        address.addPerson(person);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            /*
            person.getPhoneList().forEach(el-> {
                if(el.getId() != 0) {
                    el = em.find(Phone.class, el.getId());
                } else {
                    em.persist(el);
                }
            });
            */
            em.persist(cityInfo);
            em.persist(address);
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public PersonDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        // TODO: if null -> Throw custom exception
        return new PersonDTO(person);
    }

    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
        } finally {
            em.close();
        }
    }
}
