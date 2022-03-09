package dtos;

import entities.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private AddressDTO addressDTO;

    public PersonDTO(String email, String firstName, String lastName, AddressDTO addressDTO) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressDTO = addressDTO;
    }

    public PersonDTO(Person person) {
        if (person.getId() != null) {
            this.id = person.getId();
        }
        this.email = person.getEmail();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.addressDTO = new AddressDTO(person.getAddress());
    }

    public static List<PersonDTO> getDtos(List<Person> persons) {
        List<PersonDTO> pDtos = new ArrayList<>();
        persons.forEach(p -> pDtos.add(new PersonDTO(p)));
        return pDtos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }
}
