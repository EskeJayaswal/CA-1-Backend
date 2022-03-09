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

}
