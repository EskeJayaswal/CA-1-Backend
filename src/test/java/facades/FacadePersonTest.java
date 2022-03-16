package facades;

import dtos.*;
import entities.Person;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacadePersonTest {
    private static EntityManagerFactory emf;
    private static FacadePerson facadePerson;
    private static FacadeHobby facadeHobby;
    private static FacadeCityInfo facadeCityInfo;


    public FacadePersonTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadeHobby = FacadeHobby.getFacadeHobby(emf);
        facadePerson = FacadePerson.getFacadePerson(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);
    }

    @BeforeEach
    public void setUp() {
        truncateData();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new Person("email1", "first1", "last1"));
            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(new Person("email2", "first2", "last2"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void cleanup() {
        truncateData();
    }

    private static void truncateData() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE phone AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE hobby AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE person AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE address AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
        } finally {
            em.close();
        }
    }

    @Test
    public void testPersonCount() {
        assertEquals(2, facadePerson.getPersonCount());
    }

    @Test
    public void testGetByID() throws EntityNotFoundException, EntityAlreadyExistsException {
        createPerson();
        PersonDTO foundPDTO = facadePerson.getById(3);

        assertEquals(3, foundPDTO.getId());
        assertEquals("email", foundPDTO.getEmail());
        assertEquals("fname", foundPDTO.getFirstName());
        assertEquals("lname", foundPDTO.getLastName());
    }

    @Test
    public void testGetByPhoneNumber() throws EntityNotFoundException, EntityAlreadyExistsException {
        createPerson();
        PersonDTO foundPDTO = facadePerson.getByPhoneNumber("616881");

        assertEquals(3, foundPDTO.getId());
        assertEquals("email", foundPDTO.getEmail());
        assertEquals("fname", foundPDTO.getFirstName());
        assertEquals("lname", foundPDTO.getLastName());
    }

    @Test
    public void testGetPersonsWithHobby() throws EntityAlreadyExistsException, EntityNotFoundException {
        createPerson();
        List<PersonDTO> pList = facadePerson.getPersonsWithHobby(1);

        assertEquals(3, pList.get(0).getId());
        assertEquals("email", pList.get(0).getEmail());
        assertEquals("fname", pList.get(0).getFirstName());
        assertEquals("lname", pList.get(0).getLastName());
    }

    @Test
    public void testGetPersonsInCity() throws EntityAlreadyExistsException, EntityNotFoundException {
        createPerson();
        List<PersonDTO> pList = facadePerson.getPersonsInCity("3460");

        assertEquals(3, pList.get(0).getId());
        assertEquals("email", pList.get(0).getEmail());
        assertEquals("fname", pList.get(0).getFirstName());
        assertEquals("lname", pList.get(0).getLastName());
    }

    @Test
    public void testPersonCreate() throws EntityAlreadyExistsException, EntityNotFoundException {
        PersonDTO pDTO = createPerson();

        assertEquals(3, pDTO.getId());
        assertEquals("email", pDTO.getEmail());
        assertEquals("fname", pDTO.getFirstName());
        assertEquals("lname", pDTO.getLastName());
    }

    @Test
    public void testPersonUpdate() throws EntityAlreadyExistsException, EntityNotFoundException {
        createPerson();

        CityInfoDTO ciDTO = new CityInfoDTO("3400", "Hillerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Street", "12", ciDTO);
        PersonDTO pDTO = new PersonDTO("hotmail", "Jimmy", "Jones", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("123", "Work");

        HobbyDTO hDTO = new HobbyDTO("Golf", "Putting");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        pDTO.addPhoneDTO(phoneDTO);
        pDTO.addHobbyDTO(persistedHDTO);
        pDTO.setId(3);

        PersonDTO uPDTO = facadePerson.update(pDTO);

        assertEquals(3 , uPDTO.getId());
        assertEquals("3400", uPDTO.getAddressDTO().getCityInfoDTO().getZipCode());
        assertEquals("Hillerød", uPDTO.getAddressDTO().getCityInfoDTO().getCity());
        assertEquals("Street", uPDTO.getAddressDTO().getStreet());
        assertEquals("12", uPDTO.getAddressDTO().getAdditionalInfo());
        assertEquals("hotmail", uPDTO.getEmail());
        assertEquals("Jimmy", uPDTO.getFirstName());
        assertEquals("Jones", uPDTO.getLastName());
        assertEquals("123", uPDTO.getPhoneList().get(1).getNumber());
        assertEquals("Work", uPDTO.getPhoneList().get(1).getDescription());
        assertEquals("Golf", uPDTO.getHobbyDTOList().get(1).getName());
        assertEquals("Putting", uPDTO.getHobbyDTOList().get(1).getDescription());
    }

    @Test
    public void testRemoveAllPhones() throws EntityAlreadyExistsException, EntityNotFoundException {
        PersonDTO pDTO = createPerson();
        facadePerson.removeAllPhones(pDTO);
        PersonDTO newPDTO = facadePerson.getById(3);

        assertEquals(0, newPDTO.getPhoneList().size());
    }

    @Test
    public void testRemoveAllHobbies() throws EntityAlreadyExistsException, EntityNotFoundException {
        PersonDTO pDTO = createPerson();
        facadePerson.removeAllHobbies(pDTO);
        PersonDTO newPDTO = facadePerson.getById(3);

        assertEquals(0, newPDTO.getHobbyDTOList().size());
    }

    private PersonDTO createPerson() throws EntityAlreadyExistsException, EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Vejnavn", "2 tv", ciDTO);
        PersonDTO pDTO = new PersonDTO("email", "fname", "lname", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("616881", "Home");

        HobbyDTO hDTO = new HobbyDTO("Sport", "Spark til en bold");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        pDTO.addPhoneDTO(phoneDTO);
        pDTO.addHobbyDTO(persistedHDTO);

        PersonDTO persistedPDTO = facadePerson.create(pDTO);
        return persistedPDTO;
    }
}