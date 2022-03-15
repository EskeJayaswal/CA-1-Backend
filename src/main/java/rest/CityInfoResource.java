package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.CityInfoDTO;
import errorhandling.APIException;
import errorhandling.EntityNotFoundException;
import facades.FacadeCityInfo;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Path("cityinfo")
public class CityInfoResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final FacadeCityInfo FACADE = FacadeCityInfo.getFacadeCityInfo(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllCityInfo() throws EntityNotFoundException {
        List<CityInfoDTO> cityInfoDTOList = FACADE.getAllCityInfo();

        return Response
                .ok()
                .entity(GSON.toJson(cityInfoDTOList))
                .build();
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getCityInfoCount() {
        return "{\"count\":" + FACADE.getCityInfoCount() + "}";
    }

    @Path("populate")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response populateCityInfo() throws APIException {
        // TODO: Check if CityInfo table is empty before populating
        try {
            List<CityInfoDTO> cityInfoDTOList = FACADE.populateCityInfo();
            return Response
                    .ok("SUCCESS")
                    .entity(GSON.toJson(cityInfoDTOList))
                    .build();

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            throw new APIException("The requested API could not complete due to IOException:\n" + sw);
        } catch (InterruptedException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            throw new APIException("The requested API could not complete due to InterruptedException:\n" + sw);
        }
    }
}
