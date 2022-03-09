package dtos;

import entities.Phone;

import java.util.ArrayList;
import java.util.List;

public class PhoneDTO {
    private long id;
    private String number;
    private String description;

    public PhoneDTO(String number, String description) {
        this.number = number;
        this.description = description;
    }

    public PhoneDTO(Phone p) {
        if (p.getId() != null) {
            this.id = p.getId();
        }
        this.number = p.getNumber();
        this.description = p.getDescription();
    }

    public static List<PhoneDTO> getDtos(List<Phone> phones) {
        List<PhoneDTO> pDtos = new ArrayList<>();
        phones.forEach(p -> pDtos.add(new PhoneDTO(p)));
        return pDtos;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PhoneDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
