package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.*;
import entities.Person;
import entities.RenameMe;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import facades.FacadeCityInfo;
import facades.FacadeHobby;
import facades.FacadePerson;
import io.restassured.http.ContentType;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static EntityManagerFactory emf;
    private static FacadeHobby facadeHobby;
    private static FacadePerson facadePerson;
    private static FacadeCityInfo facadeCityInfo;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        System.out.println("--- RESOURCE PERSON ASSURED TESTS STARTING ---");
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        truncateData();

        httpServer = startServer();
        // setup assured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

        facadeHobby = FacadeHobby.getFacadeHobby(emf);
        facadePerson = FacadePerson.getFacadePerson(emf);
        facadeCityInfo = FacadeCityInfo.getFacadeCityInfo(emf);
    }

    @AfterAll
    public static void closeTestServer() {
        truncateData();
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
        System.out.println("--- RESOURCE PERSON ASSURED TESTS COMPLETE ---");
    }

    @BeforeEach
    public void setUp() throws EntityAlreadyExistsException, EntityNotFoundException {
        truncateData();
        //EntityManager em = emf.createEntityManager();
        p1 = new Person(createPerson());
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
    public void testServerIsUp() {
        System.out.println("Testing server is running");
        given().when().get("/person/count").then().statusCode(200);
    }

    @Test
    public void testCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/count")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(1));
    }

    @Test
    public void testGetPersonById() {
        given()
                .contentType("application/json")
                .get("/person/1")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("allan"))
                .body("lastName", equalTo("boje"))
                .body("phoneList", hasItem(hasEntry("number", "616881")));
    }

    @Test
    public void testGetpersonByPhone() {
        given()
                .contentType("application/json")
                .get("/person/phone/616881")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("allan"))
                .body("hobbyDTOList", hasItem(hasEntry("name", "Sport")));
    }

    @Test
    public void testFailPersonByHobby() {
        given()
                .contentType("application/json")
                .get("/person/hobby/2")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("The Hobby entity with ID: '2' was not found"));
    }

    @Test
    public void testPersonByZipcode() {
        given()
                .contentType("application/json")
                .get("/person/zipcode/3460")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("", hasItem(hasEntry("firstName", "allan")));
    }

   /* @Test
    public void testPostPerson() {
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        //facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Vejnavn", "2 tv", ciDTO);
        PersonDTO pDTO = new PersonDTO("valby@Mail", "Bjek", "Hansen", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("55555", "Home");

        HobbyDTO hDTO = new HobbyDTO("Sport", "Spark til en bold");
//        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

  //      pDTO.addPhoneDTO(phoneDTO);
 //       pDTO.addHobbyDTO(persistedHDTO);

        String requestBody = GSON.toJson(pDTO);
        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }*/

    public PersonDTO createPerson() throws EntityAlreadyExistsException, EntityNotFoundException {
        CityInfoDTO ciDTO = new CityInfoDTO("3460", "Birkerød");
        facadeCityInfo.create(ciDTO);

        AddressDTO aDTO = new AddressDTO("Vejnavn", "2 tv", ciDTO);
        PersonDTO pDTO = new PersonDTO("email", "allan", "boje", aDTO);

        PhoneDTO phoneDTO = new PhoneDTO("616881", "Home");

        HobbyDTO hDTO = new HobbyDTO("Sport", "Spark til en bold");
        HobbyDTO persistedHDTO = facadeHobby.create(hDTO);

        pDTO.addPhoneDTO(phoneDTO);
        pDTO.addHobbyDTO(persistedHDTO);

        return facadePerson.create(pDTO);

    }
}
