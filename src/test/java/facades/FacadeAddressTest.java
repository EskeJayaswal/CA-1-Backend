package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import entities.Address;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FacadeAddressTest {
    private static EntityManagerFactory emf;
    private static FacadeAddress facadeAddress;
    private static FacadeCityInfo facadeCityInfo;

    public FacadeAddressTest() {}

    @BeforeAll
    public static void setUpClass() {
        System.out.println("--- FACADE ADDRESS TESTS STARTING ---");
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadeAddress = FacadeAddress.getFacadeAddress(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);

        System.out.println("FacadeAddressTest - Truncating CityInfo");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE address AUTO_INCREMENT = 1").executeUpdate();
            em.persist(new Address("street1", "addInfo1"));
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.persist(new Address("street2", "addInfo2"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void cleanup() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE address AUTO_INCREMENT = 1").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
        } finally {
            em.close();
        }
        System.out.println("--- FACADE ADDRESS TESTS COMPLETE ---");
    }

    @Test
    public void testAddressAmount() {
        assertEquals(2, facadeAddress.getAddressCount());
    }

    @Test
    public void testCreateMethod() throws EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Roadname", "North of South", ciDTO);
        AddressDTO persistedADTO = facadeAddress.create(aDTO);

        assertEquals(3, facadeAddress.getAddressCount());
        assertEquals(3, persistedADTO.getId());
        assertEquals("Roadname", persistedADTO.getStreet());
        assertEquals("North of South", persistedADTO.getAdditionalInfo());
    }

    @Test
    public void testFindOrCreateExistingAddress() throws EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3400", "Hillerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Testvej", "3 TV", ciDTO);
        facadeAddress.create(aDTO);

        AddressDTO newADTO = new AddressDTO("Testvej", "3 TV", ciDTO);
        AddressDTO foundADTO = facadeAddress.findOrCreate(newADTO);

        assertEquals(3, facadeAddress.getAddressCount());
        assertEquals(3, foundADTO.getId());
        assertEquals("Testvej", foundADTO.getStreet());
        assertEquals("3 TV", foundADTO.getAdditionalInfo());
        assertEquals("3400", foundADTO.getCityInfoDTO().getZipCode());
        assertEquals("Hillerød", foundADTO.getCityInfoDTO().getCity());
    }

    @Test
    public void testFindOrCreateNonExistingAddress() {
        CityInfoDTO ciDTO = new CityInfoDTO("3450", "Allerød");
        AddressDTO aDTO = new AddressDTO("Testvejen", "2 TV", ciDTO);
        AddressDTO foundADTO = facadeAddress.findOrCreate(aDTO);

        assertNull(foundADTO.getId());
        assertEquals("Testvejen", foundADTO.getStreet());
        assertEquals("2 TV", foundADTO.getAdditionalInfo());
        assertEquals("3450", foundADTO.getCityInfoDTO().getZipCode());
        assertEquals("Allerød", foundADTO.getCityInfoDTO().getCity());
    }
}