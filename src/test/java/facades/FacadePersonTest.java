package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import entities.Person;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FacadePersonTest {
    private static EntityManagerFactory emf;
    private static FacadePerson facade;

    public FacadePersonTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = FacadePerson.getFacadePerson(emf);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(new Person("email1", "first1", "last1"));
            em.persist(new Person("email2", "first2", "last2"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testPersonAmount() {
        assertEquals(2, facade.getPersonCount(), "Tests expects 2 persons in database");
    }

    @Test
    public void testCreateMethod() {
        CityInfoDTO ciDTO = new CityInfoDTO("2510", "SimCity");
        AddressDTO aDTO = new AddressDTO("Bobyvej", "Her bor Bob", ciDTO);
        PersonDTO pDTO = new PersonDTO("bob@123.dk", "Bobby", "Longjon", aDTO);
        facade.create(pDTO);
        assertEquals(3, facade.getPersonCount(), "Tests expects 3 persons in database");
    }
}