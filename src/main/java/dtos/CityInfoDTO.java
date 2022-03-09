package dtos;

import entities.CityInfo;

import java.util.ArrayList;
import java.util.List;

public class CityInfoDTO {
    private long id;
    private String zipCode;
    private String city;

    public CityInfoDTO(String zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;
    }

    public CityInfoDTO(CityInfo ci) {
        if (ci.getId() != null) {
            this.id = ci.getId();
        }
        this.zipCode = ci.getZipCode();
        this.city = ci.getCity();
    }

    public static List<CityInfoDTO> getDtos(List<CityInfo> cis) {
        List<CityInfoDTO> ciDtos = new ArrayList<>();
        cis.forEach(ci -> ciDtos.add(new CityInfoDTO(ci)));
        return ciDtos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "CityInfoDTO{" +
                "id=" + id +
                ", zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
