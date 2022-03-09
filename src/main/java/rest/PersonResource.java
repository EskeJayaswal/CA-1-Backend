package rest;

import com.google.gson.*;
import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import facades.FacadeHobby;
import facades.FacadePerson;
import facades.FacadePhone;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("person")
public class PersonResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final FacadePerson FACADE = FacadePerson.getFacadePerson(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        return "{\"count\":" + FACADE.getPersonCount() + "}";
    }

    // TODO: Add checks to ensure correct data is provided
    @Path("addperson")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addPerson(String jsonContext) {
        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);

        // Person
        String email = jsonObject.get("email").getAsString();
        String firstName = jsonObject.get("firstName").getAsString();
        String lastName = jsonObject.get("lastName").getAsString();

        // Address
        String street = jsonObject.get("street").getAsString();
        String additionalInfo = jsonObject.get("additionalInfo").getAsString();

        // CityInfo
        String zipCode = jsonObject.get("zipCode").getAsString();
        String city = jsonObject.get("city").getAsString();

        // Phone
        List<PhoneDTO> phoneDTOList = new ArrayList<>();
        JsonArray phoneArray = jsonObject.getAsJsonArray("phone");
        for (JsonElement phone : phoneArray) {
            JsonObject phoneObject = GSON.fromJson(phone.toString(), JsonObject.class);

            String number = phoneObject.get("number").getAsString();
            String description = phoneObject.get("description").getAsString();
            phoneDTOList.add(new PhoneDTO(number, description));
        }

        CityInfoDTO cityInfoDTO = new CityInfoDTO(zipCode, city);
        AddressDTO addressDTO = new AddressDTO(street, additionalInfo, cityInfoDTO);
        PersonDTO personDTO = new PersonDTO(email, firstName, lastName, addressDTO);
        PersonDTO persistedPerson = FACADE.create(personDTO);

        FacadePhone facadePhone = FacadePhone.getFacadePhone(EMF);
        for (PhoneDTO phoneDTO : phoneDTOList) {
            persistedPerson.addPhoneDTO(phoneDTO);
            facadePhone.create(phoneDTO);
        }

        return Response      // Does not return valid phone ID's
                .ok("SUCCESS")
                .entity(GSON.toJson(persistedPerson.toJson()))
                .build();
    }

    // TODO: Add checks to ensure correct data is provided
    @Path("addhobby")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addHobby(String jsonContext) {
        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
        long personId = jsonObject.get("personId").getAsLong();
        long hobbyId = jsonObject.get("hobbyId").getAsLong();

        FACADE.addHobby(personId, hobbyId);

        return Response
                .ok("SUCCESS")
                .entity("Added hobby id '"+ hobbyId +"' to person id '"+ personId +"'")
                .build();


    }

    // TODO: Add checks to ensure correct data is provided
    @Path("addallhobbies")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addAllHobbies(String jsonContext) {
        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
        long personId = jsonObject.get("personId").getAsLong();
        JsonArray hobbyArray = jsonObject.getAsJsonArray("hobbies");

        PersonDTO personDTO = FACADE.getById(personId);
        FacadeHobby facadeHobby = FacadeHobby.getFacadeHobby(EMF);

        for (JsonElement hobby : hobbyArray) {
            Long hobbyId = hobby.getAsLong();
            personDTO.addHobbyDTO(facadeHobby.getHobbyByID(hobbyId));
        }
        FACADE.addAllHobbies(personDTO);

        return Response
                .ok("SUCCESS")
                .entity("Added '"+ hobbyArray.size() +"' hobbies to person id '"+ personId +"'")
                .build();
    }
}
