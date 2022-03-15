package facades;

import dtos.CityInfoDTO;
import entities.CityInfo;
import entities.Person;
import errorhandling.EntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
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

    public void create(List<CityInfoDTO> ciDTOs) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (CityInfoDTO ciDTO : ciDTOs) {
                em.persist(new CityInfo(ciDTO));
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
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

    public void populateCityInfo() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.dataforsyningen.dk/postnumre")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonArray = new JSONArray(response.body());

        List<CityInfoDTO> ciDTOs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = (JSONObject) jsonArray.get(i);
            String zipCode = (String) o.get("nr");
            String city = (String) o.get("navn");

            ciDTOs.add(new CityInfoDTO(zipCode, city));
        }
        create(ciDTOs);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FacadeCityInfo facade = getFacadeCityInfo(emf);
        facade.populateCityInfo();
    }
}
