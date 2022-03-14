package rest;

import com.google.gson.*;
import dtos.*;
import entities.Phone;
import facades.*;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
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


//    // TODO: Add checks to ensure correct data is provided
//    // Add new persons with all relevant information
//    @Path("addperson")
//    @POST
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response addPerson(String jsonContext) {
//        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
//
//        // Person
//        String email = jsonObject.get("email").getAsString();
//        String firstName = jsonObject.get("firstName").getAsString();
//        String lastName = jsonObject.get("lastName").getAsString();
//
//        // Address
//        String street = jsonObject.get("street").getAsString();
//        String additionalInfo = jsonObject.get("additionalInfo").getAsString();
//
//        // CityInfo
//        String zipCode = jsonObject.get("zipCode").getAsString();
//        String city = jsonObject.get("city").getAsString();
//
//        // Phone
//        List<PhoneDTO> phoneDTOList = new ArrayList<>();
//        JsonArray phoneArray = jsonObject.getAsJsonArray("phone");
//        for (JsonElement phone : phoneArray) {
//            JsonObject phoneObject = GSON.fromJson(phone.toString(), JsonObject.class);
//
//            String number = phoneObject.get("number").getAsString();
//            String description = phoneObject.get("description").getAsString();
//            phoneDTOList.add(new PhoneDTO(number, description));
//        }
//
//        CityInfoDTO cityInfoDTO = new CityInfoDTO(zipCode, city);
//        AddressDTO addressDTO = new AddressDTO(street, additionalInfo, cityInfoDTO);
//        PersonDTO personDTO = new PersonDTO(email, firstName, lastName, addressDTO);
//        PersonDTO persistedPerson = FACADE.create(personDTO);
//
//        FacadePhone facadePhone = FacadePhone.getFacadePhone(EMF);
//        for (PhoneDTO phoneDTO : phoneDTOList) {
//            persistedPerson.addPhoneDTO(phoneDTO);
//            PhoneDTO persistedPhone = facadePhone.create(phoneDTO);     // has a database id
//            persistedPerson.updatePhoneDTOId(persistedPhone);           // update persons phoneDTO with database id
//        }
//
//        return Response
//                .ok("SUCCESS")
//                .entity(GSON.toJson(persistedPerson.toJson()))
//                .build();
//    }

//    // TODO: Add checks to ensure correct data is provided
//    // Add single hobby to person with the persons ID
//    @Path("addhobby")
//    @POST
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response addHobby(String jsonContext) {
//        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
//        long personId = jsonObject.get("personId").getAsLong();
//        long hobbyId = jsonObject.get("hobbyId").getAsLong();
//
//        FACADE.addHobby(personId, hobbyId);
//
//        return Response
//                .ok("SUCCESS")
//                .entity("Added hobby id '"+ hobbyId +"' to person id '"+ personId +"'")
//                .build();
//    }

    // TODO: Add checks to ensure correct data is provided
    // Add multiple hobbies to a person with the persons ID
    @Path("addhobbies")
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

//    // Get all info from a person by phone number
//    @Path("infophone")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response getInfoPhone(String jsonContext) {
//        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
//        String phoneNumber = jsonObject.get("number").getAsString();
//        PersonDTO personDTO = FACADE.getByPhoneNumber(phoneNumber);
//
//        return Response
//                .ok("SUCCESS")
//                .entity(GSON.toJson(FACADE.getPersonInfo(personDTO)))
//                .build();
//    }

//    // Get all info from a person by person ID
//    @Path("info{id}")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
////    @Consumes({MediaType.APPLICATION_JSON})
//    public Response getInfoId(@PathParam("id") long id,String jsonContext) {
//        PersonDTO personDTO = FACADE.getById(id);
//        return Response
//                .ok("SUCCESS")
//                .entity(GSON.toJson(FACADE.getPersonInfo(personDTO)))
//                .build();
//    }

    // Get all persons with a given hobby ID
    @Path("hobby{hobbyId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response getPersonsWithHobby(@PathParam("hobbyId") long hobbyId, String jsonContext) {
        HobbyDTO hobbyDTO = FacadeHobby.getFacadeHobby(EMF).getHobbyByID(hobbyId);
        List<PersonDTO> personsWithHobby = FACADE.getPersonsWithHobby(hobbyDTO);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hobbyId", hobbyDTO.getId());
        jsonObject.addProperty("name", hobbyDTO.getName());
        jsonObject.addProperty("description", hobbyDTO.getDescription());
        JsonArray personArray = new JsonArray();
        for (PersonDTO p : personsWithHobby) {
            JsonObject pObject = new JsonObject();
            pObject.addProperty("personId", p.getId());
            pObject.addProperty("email", p.getEmail());
            pObject.addProperty("firstName", p.getFirstName());
            pObject.addProperty("lastName", p.getLastName());
            personArray.add(pObject);
        }
        jsonObject.add("person", personArray);

        return Response
                .ok("SUCCESS")
                .entity(GSON.toJson(jsonObject))
                .build();
    }

//    // Change a persons name by ID
//    @Path("update{id}")
//    @PUT
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response updatePerson(@PathParam("id") long id, String jsonContext) {
//        JsonObject jsonObject = GSON.fromJson(jsonContext, JsonObject.class);
////        PersonDTO personDTO = FACADE.getById(id);
//
//        // CityInfo
//        FacadeCityInfo.getFacadeCityInfo(EMF).updateCityInfo(id,
//                jsonObject.get("zipCode").getAsString(),
//                jsonObject.get("city").getAsString());
//
//        // Address
//        FacadeAddress.getFacadeAddress(EMF).updateAddress(id,
//                jsonObject.get("street").getAsString(),
//                jsonObject.get("additionalInfo").getAsString());
//
//        // Person
//        PersonDTO personDTO = FACADE.updatePerson(id,
//                jsonObject.get("email").getAsString(),
//                jsonObject.get("firstName").getAsString(),
//                jsonObject.get("lastName").getAsString());
//
//        // Phone
//        FacadePhone.getFacadePhone(EMF).updatePhone(id, jsonObject.get("phone").getAsJsonArray());
//
//        return Response
//                .ok("SUCCESS")
//                .entity(GSON.toJson(FACADE.getPersonInfo(personDTO)))
//                .build();
//    }





    // TESTING:







    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonByID(@PathParam("id") long id) {
        PersonDTO personDTO = FACADE.getById(id);
        return Response
                .ok()
                .entity(GSON.toJson(personDTO))
                .build();
    }




    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(String jsonContext) {
        PersonDTO personDTO = GSON.fromJson(jsonContext, PersonDTO.class);



        PersonDTO newPersonDTO = FACADE.create(personDTO);


        return Response.ok().entity(GSON.toJson(newPersonDTO)).build();

    }


    // Update by id. OBS: Can't add another phone number og remove one with this method.
    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") long id, String jsonContext) {
        PersonDTO personDTO = GSON.fromJson(jsonContext, PersonDTO.class);
        personDTO.setId(id);
        PersonDTO updatedPersonDTO = FACADE.update(personDTO);


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
    public Response getPersonByPhone(@PathParam("number") String number) {
        PersonDTO personDTO = FACADE.newGetByPhoneNumber(number);
        return Response.ok().entity(GSON.toJson(personDTO)).build();
    }



    // Not gonna use this for anything.
    @Path("removehobbies/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response removeAllHobbies(@PathParam("id") long id) {
        PersonDTO personDTO = FACADE.getById(id);
        PersonDTO updatedPersonDTO = FACADE.removeAllHobbies(personDTO);


        return Response
                .ok("SUCCESS")
                .entity(GSON.toJson(updatedPersonDTO))
                .build();
    }














}


