package facades;

import dtos.CityInfoDTO;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import errorhandling.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

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

    public List<HobbyDTO> create(List<HobbyDTO> hobbyDTOList) {
        List<HobbyDTO> hobbyList = new ArrayList<>();
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            for (HobbyDTO h : hobbyDTOList) {
                Hobby hobby = new Hobby(h);
                em.persist(hobby);
                hobbyList.add(new HobbyDTO(hobby));
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return hobbyList;
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

    public long getHobbyCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(h) FROM Hobby h").getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<HobbyDTO> getAllHobbies() throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Hobby> typedQueryHobby
                = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
        List<Hobby> hobbyList = typedQueryHobby.getResultList();

        if (hobbyList.size() == 0)
            throw new EntityNotFoundException("No Hobby entities exist. Call '.../api/hobby/populate' to populate the database with valid Hobby data");

        List<HobbyDTO> hobbyDTOList = new ArrayList<>();
        for (Hobby h : hobbyList) {
            hobbyDTOList.add(new HobbyDTO(h));
        }
        return hobbyDTOList;
    }

    public List<HobbyDTO> populateHobby() {
        List<HobbyDTO> hobbyDTOList = new ArrayList<>();
        hobbyDTOList.add(new HobbyDTO("Sport", "Being physically active"));
        hobbyDTOList.add(new HobbyDTO("Painting", "Being creative on a canvas"));
        hobbyDTOList.add(new HobbyDTO("Gaming", "Sitting in a chair punching the keyboard"));
        hobbyDTOList.add(new HobbyDTO("Hiking", "Exploring the wilderness"));
        hobbyDTOList.add(new HobbyDTO("Programming", "We love databases... NOT"));

        return create(hobbyDTOList);
    }


    // Not sure if working..
//    public HobbyDTO getHobbyByName(String hobbyName) {
//        EntityManager em = emf.createEntityManager();
//        Hobby hobby = em.find(Hobby.class, hobbyName);
//        return new HobbyDTO(hobby);
//    }
}
