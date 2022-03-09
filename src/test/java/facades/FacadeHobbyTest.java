package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Hobby;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FacadeHobbyTest {

    private static EntityManagerFactory emf;
    private static FacadeHobby facade;

    public FacadeHobbyTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = FacadeHobby.getFacadeHobby(emf);
    }

    @BeforeEach
    public void setup() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.persist(new Hobby("Cykling", "Man cykler rundt"));
            em.persist(new Hobby("Klatring", "Det er sjovt"));
            em.persist(new Hobby("RingPolo", "for rige mennesker"));
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @Test
    public void tesCreateMethod() {
        CityInfoDTO ciDTO = new CityInfoDTO("2400", "KBHNV");
        AddressDTO aDTO = new AddressDTO("Ravnevej", "Her bor Preben", ciDTO);
        PersonDTO pDTO = new PersonDTO("preben@123.dk", "Preben", "Stenstrøm", aDTO);
        FacadePerson facadePerson = FacadePerson.getFacadePerson(emf);
        PersonDTO newDTO =  facadePerson.create(pDTO);
        facadePerson.addHobby(newDTO.getId(), 1);
    }

    @Test
    public void testAddAllHobbies() {
        CityInfoDTO ciDTO = new CityInfoDTO("2450", "Vejle");
        AddressDTO aDTO = new AddressDTO("vejlevej", "Her bor Jan", ciDTO);
        PersonDTO pDTO = new PersonDTO("jan@123.dk", "jan", "Langballe", aDTO);
        FacadePerson facadePerson = FacadePerson.getFacadePerson(emf);
        PersonDTO newDTO =  facadePerson.create(pDTO);
        newDTO.addHobbyDTO(facade.getHobbyByID(1));
        newDTO.addHobbyDTO(facade.getHobbyByID(2));
        newDTO.addHobbyDTO(facade.getHobbyByID(3));
        facadePerson.addAllHobbies(newDTO);
    }

}