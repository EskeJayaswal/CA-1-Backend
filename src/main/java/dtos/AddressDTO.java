package dtos;

import entities.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressDTO {
    private Long id;
    private String street;
    private String additionalInfo;
//    private List<PersonDTO> personDTOList;
    private CityInfoDTO cityInfoDTO;

    public AddressDTO(String street, String additionalInfo, CityInfoDTO ciDTO) {
        this.street = street;
        this.additionalInfo = additionalInfo;
        this.cityInfoDTO = ciDTO;
    }

    public AddressDTO(Address address) {
        if (address.getId() != null) {
            this.id  = address.getId();
        }
        this.street = address.getStreet();
        this.additionalInfo = address.getAdditionalInfo();
//        address.getPersonList().forEach(p -> personDTOList.add(new PersonDTO(p)));
        this.cityInfoDTO = new CityInfoDTO(address.getCityInfo());
    }

    public static List<AddressDTO> getDtos(List<Address> list) {
        List<AddressDTO> aDtos = new ArrayList<>();
        list.forEach(address -> aDtos.add(new AddressDTO(address)));
        return aDtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public CityInfoDTO getCityInfoDTO() {
        return cityInfoDTO;
    }

    public void setCityInfoDTO(CityInfoDTO cityInfoDTO) {
        this.cityInfoDTO = cityInfoDTO;
    }
}
