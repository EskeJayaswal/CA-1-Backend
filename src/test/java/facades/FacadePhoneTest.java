package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Phone;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FacadePhoneTest {

    private static EntityManagerFactory emf;
    private static FacadePhone facade;

    public FacadePhoneTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = FacadePhone.getFacadePhone(emf);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.persist(new Phone("34302011", "Cell number"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testPhoneAmount() {assertEquals(1, facade.getPhoneCount(), "Test expects 1 Phone number in database");}

    @Test
    public void testCreateMethod() throws EntityAlreadyExistsException, EntityNotFoundException {
        PhoneDTO phoneDTO = new PhoneDTO("32344343", "Cell");
        CityInfoDTO ciDTO = new CityInfoDTO("2510", "SimCity");
        AddressDTO aDTO = new AddressDTO("Bobyvej", "Her bor Bob", ciDTO);
        PersonDTO pDTO = new PersonDTO("bob@123.dk", "Bobby", "Longjon", aDTO);
        FacadePerson facadePerson = FacadePerson.getFacadePerson(emf);
        PersonDTO newDto = facadePerson.create(pDTO);
        facade.create(phoneDTO);
        assertEquals(2, facade.getPhoneCount());

    }

}