package rest;

import com.google.gson.*;
import dtos.*;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;
import facades.*;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("person")
public class PersonResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final FacadePerson FACADE = FacadePerson.getFacadePerson(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // TODO: Handle errors when adding hobbies with invalid ID's

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        return "{\"count\":" + FACADE.getPersonCount() + "}";
    }

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonByID(@PathParam("id") long id) throws EntityNotFoundException {
        PersonDTO personDTO = FACADE.getById(id);
        return Response
                .ok()
                .entity(GSON.toJson(personDTO))
                .build();
    }

    // Get all persons with a given hobby ID
    @Path("hobby/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonsWithHobby(@PathParam("id") long id) {
        List<PersonDTO> personsWithHobby = FACADE.getPersonsWithHobby(id);

        return Response
                .ok()
                .entity(GSON.toJson(personsWithHobby))
                .build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(String jsonContext) throws EntityAlreadyExistsException {
        PersonDTO personDTO = GSON.fromJson(jsonContext, PersonDTO.class);
        PersonDTO newPersonDTO = FACADE.create(personDTO);

        return Response.ok().entity(GSON.toJson(newPersonDTO)).build();
    }

    // Update by id
    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") long id, String jsonContext) throws EntityNotFoundException, EntityAlreadyExistsException {
        PersonDTO personDTO = GSON.fromJson(jsonContext, PersonDTO.class);
        personDTO.setId(id);

        FACADE.removeAllPhones(personDTO);
        FACADE.removeAllHobbies(personDTO);
        PersonDTO updatedPersonDTO = FACADE.update(personDTO);

        System.out.println(GSON.toJson(updatedPersonDTO));

        return Response
                .ok("SUCCESS")
                .entity(GSON.toJson(updatedPersonDTO))
                .build();
    }

    // Get all info from a person by phone number
    @Path("phone/{number}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getPersonByPhone(@PathParam("number") String number) throws EntityNotFoundException {
        PersonDTO personDTO = FACADE.getByPhoneNumber(number);
        return Response.ok().entity(GSON.toJson(personDTO)).build();
    }
}


