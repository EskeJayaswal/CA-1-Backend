package facades;

import dtos.*;
import entities.Phone;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FacadePhoneTest {
    private static EntityManagerFactory emf;
    private static FacadePhone facadePhone;
    private static FacadePerson facadePerson;
    private static FacadeHobby facadeHobby;
    private static FacadeCityInfo facadeCityInfo;

    public FacadePhoneTest() {}

    @BeforeAll
    public static void setUpClass() {
        System.out.println("--- FACADE PHONE TESTS STARTING ---");
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadePhone = FacadePhone.getFacadePhone(emf);
        facadeHobby = FacadeHobby.getFacadeHobby(emf);
        facadePerson = FacadePerson.getFacadePerson(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);
        System.out.println("--- FACADE PHONE - Created facades ---");
    }

    @BeforeEach
    public void setUp() {
        ResetDB.truncate(emf);
//        EntityManager em = emf.createEntityManager();
//        System.out.println("--- FACADE PHONE - BeforeEach - Completed truncate ---");

        PersonDTO pDTO = new PersonDTO("email", "testFirstName", "testLastName", null);
        PhoneDTO phoneDTO = new PhoneDTO("34302011", "Cell number");
        phoneDTO.setPersonDTO(pDTO);

        facadePhone.create(phoneDTO);
//        System.out.println("--- FACADE PHONE - BeforeEach - Created phoneDTO ---");
//
//        try {
//            em.getTransaction().begin();
////            em.persist(new Phone("34302011", "Cell number"));
//
//
//
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
    }

    @AfterAll
    public static void cleanup() {
        ResetDB.truncate(emf);
        System.out.println("--- FACADE PHONE TESTS COMPLETE ---");
    }

    @Test
    public void testPhoneCount() {
        System.out.println("--- FACADE PHONE - testPhoneCount() ---");
        assertEquals(1, facadePhone.getPhoneCount());
    }

    @Test
    public void testPhoneCreate() {
        System.out.println("--- FACADE PHONE - testPhoneCreate() ---");
        PersonDTO pDTO = new PersonDTO("hotmail", "Kim", "Dirk", null);
        PhoneDTO phoneDTO = new PhoneDTO("123", "Home");
        phoneDTO.setPersonDTO(pDTO);

        PhoneDTO persistedPhone = facadePhone.create(phoneDTO);

        assertEquals(2, persistedPhone.getId());
        assertEquals("123", persistedPhone.getNumber());
        assertEquals("Home", persistedPhone.getDescription());
    }

    @Test
    public void testAlreadyExists1() {
        System.out.println("--- FACADE PHONE - testAlreadyExists1() ---");
        boolean alreadyExists = facadePhone.alreadyExists("34302011");
        assertTrue(alreadyExists);
    }

    @Test
    public void testAlreadyExists2() {
        System.out.println("--- FACADE PHONE - testAlreadyExists2() ---");
        boolean alreadyExists = facadePhone.alreadyExists("999");
        assertFalse(alreadyExists);
    }

    @Test
    public void testAlreadyExists3() throws EntityAlreadyExistsException, EntityNotFoundException {
        System.out.println("--- FACADE PHONE - testAlreadyExists3() ---");
        createPerson();
        PersonDTO pDTO2 = createPerson2();

        boolean alreadyExists = facadePhone.alreadyExists("123", pDTO2);
        assertFalse(alreadyExists);
    }

    @Test
    public void testAlreadyExists4() throws EntityAlreadyExistsException, EntityNotFoundException {
        System.out.println("--- FACADE PHONE - testAlreadyExists4() ---");
        createPerson();
        PersonDTO pDTO2 = createPerson2();

        boolean alreadyExists = facadePhone.alreadyExists("616881", pDTO2);
        assertTrue(alreadyExists);
    }


    private PersonDTO createPerson() throws EntityAlreadyExistsException, EntityNotFoundException {
//        System.out.println("--- FACADE PHONE - createPerson() ---");
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Vejnavn", "2 tv", ciDTO);
        PersonDTO pDTO = new PersonDTO("email", "fname", "lname", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("616881", "Home");

        HobbyDTO hDTO = new HobbyDTO("Sport", "Spark til en bold");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        pDTO.addPhoneDTO(phoneDTO);
        pDTO.addHobbyDTO(persistedHDTO);

        return facadePerson.create(pDTO);
    }

    private PersonDTO createPerson2() throws EntityAlreadyExistsException, EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3400", "Hillerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Streetname", "5 th", ciDTO);
        PersonDTO pDTO = new PersonDTO("hotmail", "firstname", "lastname", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("1486", "Home");

        HobbyDTO hDTO = new HobbyDTO("Golf", "Go putting");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        pDTO.addPhoneDTO(phoneDTO);
        pDTO.addHobbyDTO(persistedHDTO);

        return facadePerson.create(pDTO);
    }
}