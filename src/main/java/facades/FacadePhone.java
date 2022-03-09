package facades;

import dtos.PhoneDTO;
import entities.Person;
import entities.Phone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class FacadePhone {
    private static FacadePhone instance;
    private static EntityManagerFactory emf;

    public FacadePhone() {}

    public static FacadePhone getFacadePhone(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadePhone();
        }
        return instance;
    }

    public PhoneDTO create(PhoneDTO phoneDTO) {
        Phone phone = new Phone(phoneDTO);
        long id = phoneDTO.getPersonDTO().getId();

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            phone.setPerson(em.find(Person.class, id));
            em.persist(phone);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PhoneDTO(phone);
    }

    public long getPhoneCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(p) FROM Phone p").getSingleResult();
        } finally {
            em.close();
        }
    }
}
