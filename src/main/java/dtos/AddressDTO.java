package dtos;

import entities.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressDTO {
    private long id;
    private String street;
    private String additionalInfo;

    public AddressDTO(String street, String additionalInfo) {
        this.street = street;
        this.additionalInfo = additionalInfo;
    }

    public AddressDTO(Address address) {
        if (address.getId() != null) {
            this.id  = address.getId();
        }
        this.street = address.getStreet();
        this.additionalInfo = address.getAdditionalInfo();
    }

    public static List<AddressDTO> getDtos(List<Address> list) {
        List<AddressDTO> aDtos = new ArrayList<>();
        list.forEach(address -> aDtos.add(new AddressDTO(address)));
        return aDtos;
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

    @Override
    public String toString() {
        return "AddressDTO{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
