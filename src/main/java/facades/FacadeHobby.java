package facades;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Hobby;
import entities.Person;
import errorhandling.EntityNotFoundException;

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

    public Hobby getHobbyByID(long id) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        Hobby hobby = em.find(Hobby.class, id);
        if (hobby == null)
            throw new EntityNotFoundException("The Hobby entity with ID: '"+id+"' was not found");
        return hobby;
    }

    public void checkValidHobbyIds(PersonDTO personDTO) throws EntityNotFoundException {
        for (HobbyDTO hobbyDTO : personDTO.getHobbyDTOList()) {
            getHobbyByID(hobbyDTO.getId());
        }
    }


    // Not sure if working..
//    public HobbyDTO getHobbyByName(String hobbyName) {
//        EntityManager em = emf.createEntityManager();
//        Hobby hobby = em.find(Hobby.class, hobbyName);
//        return new HobbyDTO(hobby);
//    }
}
