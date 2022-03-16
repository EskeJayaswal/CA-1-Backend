package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.*;
import entities.*;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
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
import org.junit.jupiter.api.Test;

public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2;
    private static Phone ph1, ph2;
    private static Address a1;
    private static CityInfo c1;
    private static Hobby h1;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static EntityManagerFactory emf;


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

        httpServer = startServer();
        // setup assured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
        System.out.println("--- RESOURCE PERSON ASSURED TESTS COMPLETE ---");       // bøh
    }

    @BeforeEach
    public void setUp() throws EntityAlreadyExistsException, EntityNotFoundException {
        EntityManager em = emf.createEntityManager();

        ph1 = new Phone("12345678", "Work");
        p1 = new Person("AA@mail.dk", "Anders", "And");
        a1 = new Address("Adelsvej", "1");
        c1 = new CityInfo("2000", "Frederiksberg");
        h1 = new Hobby("Football", "NFL");

        c1.addAddress(a1);
        a1.addPerson(p1);
        p1.addPhone(ph1);
        p1.addHobby(h1);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.persist(h1);
            em.persist(c1);
            em.persist(a1);
            em.persist(p1);
            em.persist(ph1);
            em.getTransaction().commit();
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
                .body("firstName", equalTo("Anders"))
                .body("lastName", equalTo("And"))
                .body("phoneList", hasItem(hasEntry("number", "12345678")));
    }

    @Test
    public void testGetpersonByPhone() {
        given()
                .contentType("application/json")
                .get("/person/phone/12345678")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("Anders"))
                .body("hobbyDTOList", hasItem(hasEntry("name", "Football")));
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
                .get("/person/zipcode/2000")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("", hasItem(hasEntry("firstName", "Anders")));
    }

   @Test
    public void testPostPerson() {
       ph2 = new Phone("87654321", "Work");
       p2 = new Person("BB@mail.dk", "Bo", "Baker");


       a1.addPerson(p2);
       p2.addPhone(ph2);
       p2.addHobby(h1);

        String requestBody = GSON.toJson(new PersonDTO(p2));

       System.out.println(requestBody);

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("Bo"));
    }
}
