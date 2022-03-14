package facades;

import dtos.AddressDTO;
import dtos.CityInfoDTO;
import entities.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FacadeAddressTest {
    private static EntityManagerFactory emf;
    private static FacadeAddress facade;

    public FacadeAddressTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = FacadeAddress.getFacadeAddress(emf);
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
        assertEquals(2, facade.getAddressCount());
    }

    @Test
    public void testCreateMethod() {
        CityInfoDTO ciDTO = new CityInfoDTO("2510", "SimCity");
        AddressDTO aDTO = new AddressDTO("Roadname", "North of South", ciDTO);
        facade.create(aDTO);
        assertEquals(3, facade.getAddressCount());
    }
}