package dtos;

import entities.Phone;

import java.util.ArrayList;
import java.util.List;

public class PhoneDTO {
    private long id;
    private String number;
    private String description;
    private PersonDTO personDTO;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public PersonDTO getPersonDTO() {
        return personDTO;
    }

    public void setPersonDTO(PersonDTO personDTO) {
        this.personDTO = personDTO;
    }
}
