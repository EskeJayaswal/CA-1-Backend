package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HobbyDTO;
import errorhandling.EntityNotFoundException;
import facades.FacadeHobby;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("hobby")
public class HobbyResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    FacadeHobby FACADE = FacadeHobby.getFacadeHobby(EMF);
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getHobbyCount() {
        long count = FACADE.getHobbyCount();
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllHobbies() throws EntityNotFoundException {
        List<HobbyDTO> hobbyDTOList = FACADE.getAllHobbies();
        return Response
                .ok("SUCCESS")
                .entity(GSON.toJson(hobbyDTOList))
                .build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(String jsonContext) {
        HobbyDTO hobbyDTO = GSON.fromJson(jsonContext, HobbyDTO.class);
        HobbyDTO newHobbyDTO = FACADE.create(hobbyDTO);

        return Response.ok("SUCCESS").entity(GSON.toJson(newHobbyDTO)).build();
    }

    @Path("populate")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response populateHobby() {
        List<HobbyDTO> hobbyDTOList = FACADE.populateHobby();

        return Response
                .ok("SUCCESS")
                .entity(GSON.toJson(hobbyDTOList))
                .build();
    }
}
