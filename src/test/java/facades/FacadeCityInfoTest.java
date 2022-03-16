package facades;

import dtos.CityInfoDTO;
import entities.CityInfo;
import errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacadeCityInfoTest {
    private static EntityManagerFactory emf;
    private static FacadeCityInfo facadeCityInfo;

    public FacadeCityInfoTest() {}

    @BeforeAll
    public static void setUpClass() {
        System.out.println("--- FACADE CITY INFO TESTS STARTING ---");
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
            em.persist(new CityInfo("3460", "Birkerød"));
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.persist(new CityInfo("2300", "Amager"));
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
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
        } finally {
            em.close();
        }
        System.out.println("--- FACADE CITY INFO TESTS COMPLETE ---");
    }

    @Test
    public void testCityInfoCount() {
        assertEquals(2, facadeCityInfo.getCityInfoCount());
    }

    @Test
    public void testCityInfoCreate() {
        CityInfoDTO ciDTO = new CityInfoDTO("3400", "Hillerød");
        CityInfoDTO persistedCiDTO = facadeCityInfo.create(ciDTO);

        assertEquals(3, facadeCityInfo.getCityInfoCount());
        assertEquals(3, persistedCiDTO.getId());
        assertEquals("3400", persistedCiDTO.getZipCode());
        assertEquals("Hillerød", persistedCiDTO.getCity());
    }

    @Test
    public void testCityInfoCreateList() {
        CityInfoDTO ciDTO1 = new CityInfoDTO("3400", "Hillerød");
        CityInfoDTO ciDTO2 = new CityInfoDTO("3450", "Allerød");

        List<CityInfoDTO> ciDTOList = new ArrayList<>();
        ciDTOList.add(ciDTO1);
        ciDTOList.add(ciDTO2);
        List<CityInfoDTO> persistedCiDTOList = facadeCityInfo.create(ciDTOList);

        assertEquals(3, persistedCiDTOList.get(0).getId());
        assertEquals("3400", persistedCiDTOList.get(0).getZipCode());
        assertEquals("Hillerød", persistedCiDTOList.get(0).getCity());

        assertEquals(4, persistedCiDTOList.get(1).getId());
        assertEquals("3450", persistedCiDTOList.get(1).getZipCode());
        assertEquals("Allerød", persistedCiDTOList.get(1).getCity());
    }

    @Test
    public void testGetCityInfoByZip() throws EntityNotFoundException {
        CityInfo ci = facadeCityInfo.getCityInfoByZip("3460");

        assertEquals(1, ci.getId());
        assertEquals("3460", ci.getZipCode());
        assertEquals("Birkerød", ci.getCity());
    }

    @Test
    public void testGetCityInfoDTOByZip() throws EntityNotFoundException {
        CityInfoDTO ciDTO = facadeCityInfo.getCityInfoDTOByZip("3460");

        assertEquals(1, ciDTO.getId());
        assertEquals("3460", ciDTO.getZipCode());
        assertEquals("Birkerød", ciDTO.getCity());
    }

    @Test
    public void testGetAllCityInfo() throws EntityNotFoundException {
        List<CityInfoDTO> ciList = facadeCityInfo.getAllCityInfo();

        assertEquals(1, ciList.get(0).getId());
        assertEquals("3460", ciList.get(0).getZipCode());
        assertEquals("Birkerød", ciList.get(0).getCity());

        assertEquals(2, ciList.get(1).getId());
        assertEquals("2300", ciList.get(1).getZipCode());
        assertEquals("Amager", ciList.get(1).getCity());
    }

//    @Test
//    void testCityInfoPopulate() throws IOException, InterruptedException {
//        EntityManager em = emf.createEntityManager();
//        try {
//            em.getTransaction().begin();
//            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
//            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
//        } finally {
//            em.close();
//        }
//
//        System.out.println("FacadeCityInfoTest - Populating (This might take some time)");
//        List<CityInfoDTO> ciList = facadeCityInfo.populateCityInfo();
//
//        assertEquals(1089, ciList.size());
//        assertEquals(1, ciList.get(0).getId());
//        assertEquals("1050", ciList.get(0).getZipCode());
//        assertEquals("København K", ciList.get(0).getCity());
//    }
}