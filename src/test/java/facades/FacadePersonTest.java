package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
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
        PhoneDTO phoneDTO = new PhoneDTO("32344343", "facadePerson");
        PhoneDTO phoneDTO2 = new PhoneDTO("12211122", "facadePerson2");
        CityInfoDTO ciDTO = new CityInfoDTO("2510", "SimCity");
        AddressDTO aDTO = new AddressDTO("Bobyvej", "Her bor Bob", ciDTO);
        PersonDTO pDTO = new PersonDTO("bob@123.dk", "Bobby", "Longjon", aDTO);
        PersonDTO newDto = facade.create(pDTO);
        newDto.addPhoneDTO(phoneDTO);
        newDto.addPhoneDTO(phoneDTO2);
        FacadePhone facadePhone = FacadePhone.getFacadePhone(emf);
        facadePhone.create(phoneDTO);
        facadePhone.create(phoneDTO2);
        assertEquals(3, facade.getPersonCount(), "Tests expects 3 persons in database");
//        System.out.println("newDTO ID = "+ newDto.getId());
    }
}