package facades;

import dtos.*;
import entities.Hobby;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacadeHobbyTest {

    private static EntityManagerFactory emf;
    private static FacadeHobby facadeHobby;
    private static FacadePerson facadePerson;
    private static FacadeCityInfo facadeCityInfo;

    public FacadeHobbyTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadeHobby = FacadeHobby.getFacadeHobby(emf);
        facadePerson = FacadePerson.getFacadePerson(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);
    }

    @BeforeEach
    public void setup() {
        truncateData();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new Hobby("Cykling", "Man cykler rundt"));
            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(new Hobby("Klatring", "Det er sjovt"));
            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(new Hobby("RingPolo", "for rige mennesker"));
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
    public void testGetHobbyCount() {
        Long count = facadeHobby.getHobbyCount();
        assertEquals(3, count);
    }

    @Test
    public void testHobbyCreate() {
        HobbyDTO hDTO = new HobbyDTO("Sport", "Spark til en bold");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        assertEquals(4, persistedHDTO.getId());
        assertEquals("Sport", persistedHDTO.getName());
        assertEquals("Spark til en bold", persistedHDTO.getDescription());
    }

    @Test
    public void testHobbyCreateList() {
        HobbyDTO hDTO1 = new HobbyDTO("Sport", "Spark til en bold");
        HobbyDTO hDTO2 = new HobbyDTO("Hiking", "Ude i skoven");
        List<HobbyDTO> hList = new ArrayList<>();
        hList.add(hDTO1);
        hList.add(hDTO2);
        List<HobbyDTO> persistedList = facadeHobby.create(hList);

        assertEquals(4, persistedList.get(0).getId());
        assertEquals("Sport", persistedList.get(0).getName());
        assertEquals("Spark til en bold", persistedList.get(0).getDescription());

        assertEquals(5, persistedList.get(1).getId());
        assertEquals("Hiking", persistedList.get(1).getName());
        assertEquals("Ude i skoven", persistedList.get(1).getDescription());
    }

    @Test
    public void testGetHobbyByID() throws EntityNotFoundException {
        Hobby hobby = facadeHobby.getHobbyByID(1);

        assertEquals("Cykling", hobby.getName());
        assertEquals("Man cykler rundt", hobby.getDescription());
    }

    @Test
    public void testCheckValidHobbyIds() throws EntityAlreadyExistsException, EntityNotFoundException {
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
        facadeHobby.checkValidHobbyIds(persistedPDTO);

        assertEquals(4, persistedPDTO.getHobbyDTOList().get(0).getId());
        assertEquals("Sport", persistedPDTO.getHobbyDTOList().get(0).getName());
        assertEquals("Spark til en bold", persistedPDTO.getHobbyDTOList().get(0).getDescription());
    }

    @Test
    public void testGetAllHobbies() throws EntityNotFoundException {
        List<HobbyDTO> hList = facadeHobby.getAllHobbies();

        assertEquals(1, hList.get(0).getId());
        assertEquals("Cykling", hList.get(0).getName());
        assertEquals("Man cykler rundt", hList.get(0).getDescription());

        assertEquals(2, hList.get(1).getId());
        assertEquals("Klatring", hList.get(1).getName());
        assertEquals("Det er sjovt", hList.get(1).getDescription());
    }

    @Test
    public void testPopulateHobbies() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE hobby AUTO_INCREMENT = 1").executeUpdate();
        } finally {
            em.close();
        }

        List<HobbyDTO> hList = facadeHobby.populateHobby();

        assertEquals(1, hList.get(0).getId());
        assertEquals("Sport", hList.get(0).getName());
        assertEquals("Being physically active", hList.get(0).getDescription());

        assertEquals(2, hList.get(1).getId());
        assertEquals("Painting", hList.get(1).getName());
        assertEquals("Being creative on a canvas", hList.get(1).getDescription());
    }
}