package facades;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Person;
import entities.Phone;
import errorhandling.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

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

    public void updatePhone(long personId, JsonArray phoneArray) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Phone> typedQueryPhone
                = em.createQuery("SELECT p FROM Phone p WHERE p.person.id =:id", Phone.class);
        typedQueryPhone.setParameter("id", personId);
        List<Phone> phoneList = typedQueryPhone.getResultList();

        try {
            em.getTransaction().begin();
            for (int i = 0; i < phoneArray.size(); i++) {
                JsonObject obj = phoneArray.get(i).getAsJsonObject();
                phoneList.get(i).setNumber(obj.get("number").getAsString());
                phoneList.get(i).setDescription(obj.get("description").getAsString());

                em.merge(phoneList.get(i));
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public boolean alreadyExists(String phoneNumber) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Phone> typedQuery = em.createQuery("SELECT p FROM Phone p WHERE p.number =" + phoneNumber, Phone.class);
        if (typedQuery.getResultList().size() != 0)
            return true;
        return false;
    }

    public boolean alreadyExists(String phoneNumber, PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Phone> typedQuery = em.createQuery("SELECT p FROM Phone p WHERE p.number =" + phoneNumber, Phone.class);
        if (typedQuery.getResultList().size() != 0) {

            boolean personsOwnNumber = false;
            for (Phone phone : typedQuery.getResultList()) {
                if (phone.getPerson().getId() == personDTO.getId()) {
                    personsOwnNumber = true;
                }
            }
            if (personsOwnNumber) {
                return false;
            }

            return true;
        }
        return false;
    }
}
