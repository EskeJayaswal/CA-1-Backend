package facades;

import dtos.CityInfoDTO;
import entities.CityInfo;
import entities.Person;
import errorhandling.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

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

    public CityInfo getCityInfoById(long id) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        CityInfo cityInfo = em.find(CityInfo.class, id);
        if (cityInfo == null)
            throw new EntityNotFoundException("The CityInfo entity with ID: '"+id+"' was not found");
        return cityInfo;
    }

    public CityInfoDTO getCityInfoByZip(String zipCode) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> tq = em.createQuery("SELECT c FROM CityInfo c  WHERE c.zipCode = " + zipCode, CityInfo.class);
        List<CityInfo> cityInfoList = tq.getResultList();

        if (cityInfoList.size() == 0) {
            throw new EntityNotFoundException("Zipcode: '"+ zipCode +"' was not found");
        }

        return new CityInfoDTO(cityInfoList.get(0));
    }
}
