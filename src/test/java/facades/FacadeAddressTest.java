package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import entities.Address;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FacadeAddressTest {
    private static EntityManagerFactory emf;
    private static FacadeAddress facadeAddress;
    private static FacadeCityInfo facadeCityInfo;

    public FacadeAddressTest() {}

    @BeforeAll
    public static void setUpClass() throws IOException, InterruptedException {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadeAddress = FacadeAddress.getFacadeAddress(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);

        System.out.println("FacadeAddressTest - Truncating CityInfo");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        System.out.println("FacadeAddressTest - Populating CityInfo");
        facadeCityInfo.populateCityInfo();
        System.out.println("FacadeAddressTest - Completed populating CityInfo");
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.persist(new Address("street1", "addInfo1"));
            em.persist(new Address("street2", "addInfo2"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testAddressAmount() {
        assertEquals(2, facadeAddress.getAddressCount());
    }

    @Test
    public void testCreateMethod() throws EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        AddressDTO aDTO = new AddressDTO("Roadname", "North of South", ciDTO);
        facadeAddress.create(aDTO);

        AddressDTO newAddressDTO = facadeAddress.findOrCreate(aDTO);

        assertEquals(3, facadeAddress.getAddressCount());
        assertEquals(3, newAddressDTO.getId());
        assertEquals("Roadname", newAddressDTO.getStreet());
        assertEquals("North of South", newAddressDTO.getAdditionalInfo());
    }
}