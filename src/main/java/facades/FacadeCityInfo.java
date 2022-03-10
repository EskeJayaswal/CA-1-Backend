package facades;

import dtos.CityInfoDTO;
import entities.CityInfo;
import entities.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class FacadeCityInfo {
    private static FacadeCityInfo instance;
    private static EntityManagerFactory emf;

    public FacadeCityInfo() {}

    public static FacadeCityInfo getFacadeCityInfo(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeCityInfo();
        }
        return instance;
    }

    public CityInfoDTO create(CityInfoDTO ciDTO) {
        CityInfo ci = new CityInfo(ciDTO);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ci);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new CityInfoDTO(ci);
    }

    public CityInfoDTO updateCityInfo(long personId, String zipCode, String city) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, personId);
        CityInfo cityInfo = person.getAddress().getCityInfo();
        cityInfo.setZipCode(zipCode);
        cityInfo.setCity(city);

        try {
            em.getTransaction().begin();
            em.merge(cityInfo);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new CityInfoDTO(cityInfo);
    }
}
