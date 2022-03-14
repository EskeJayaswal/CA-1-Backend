package facades;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Hobby;
import entities.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class FacadeHobby {
    private static FacadeHobby instance;
    private static EntityManagerFactory emf;

    public FacadeHobby() {}

    public static FacadeHobby getFacadeHobby(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeHobby();
        }
        return instance;
    }

    public HobbyDTO create(HobbyDTO hobbyDTO) {
        Hobby hobby = new Hobby(hobbyDTO);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.getTransaction().commit();
        } finally {
            em.close();

        }
        return new HobbyDTO(hobby);
    }

    public HobbyDTO getHobbyByID(long id) {
        EntityManager em = emf.createEntityManager();
        Hobby hobby = em.find(Hobby.class, id);
        return new HobbyDTO(hobby);
    }

    // Not sure if working..
//    public HobbyDTO getHobbyByName(String hobbyName) {
//        EntityManager em = emf.createEntityManager();
//        Hobby hobby = em.find(Hobby.class, hobbyName);
//        return new HobbyDTO(hobby);
//    }
}
