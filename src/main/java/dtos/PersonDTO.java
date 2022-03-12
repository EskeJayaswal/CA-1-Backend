package dtos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.Hobby;
import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.List;

public class PersonDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private AddressDTO addressDTO;
    private List<PhoneDTO> phoneList = new ArrayList<>();
    private List<HobbyDTO> hobbyDTOList = new ArrayList<>();

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
        for (Phone phone : person.getPhoneList()) {
            this.phoneList.add(new PhoneDTO(phone));
        }
        for (Hobby h : person.getHobbyList()) {
            this.hobbyDTOList.add(new HobbyDTO(h));
        }
    }

    public static List<PersonDTO> getDtos(List<Person> persons) {
        List<PersonDTO> pDtos = new ArrayList<>();
        persons.forEach(p -> pDtos.add(new PersonDTO(p)));
        return pDtos;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<PhoneDTO> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<PhoneDTO> phoneList) {
        this.phoneList = phoneList;
    }

    public List<HobbyDTO> getHobbyDTOList() {
        return hobbyDTOList;
    }

    public void setHobbyDTOList(List<HobbyDTO> hobbyDTOList) {
        this.hobbyDTOList = hobbyDTOList;
    }

    public void addHobbyDTO(HobbyDTO hobbyDTO) {
        this.hobbyDTOList.add(hobbyDTO);
    }

    public void addPhoneDTO(PhoneDTO phoneDTO) {
        this.phoneList.add(phoneDTO);
        phoneDTO.setPersonDTO(this);
    }

    public void updatePhoneDTOId(PhoneDTO phoneDTO) {
        for (PhoneDTO p : phoneList) {
            if (p.getNumber().equals(phoneDTO.getNumber())) {
                p.setId(phoneDTO.getId());
            }
        }
    }

//    @Override
//    public String toString() {
//        return "PersonDTO{" +
//                "id=" + id +
//                ", email='" + email + '\'' +
//                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                '}';
//    }



//    public JsonObject toJson() {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("personID", getId());
//        jsonObject.addProperty("email", getEmail());
//        jsonObject.addProperty("firstName", getFirstName());
//        jsonObject.addProperty("lastName", getLastName());
//        jsonObject.addProperty("addressId", getAddressDTO().getId());
//        jsonObject.addProperty("street", getAddressDTO().getStreet());
//        jsonObject.addProperty("additionalInfo", getAddressDTO().getAdditionalInfo());
//        jsonObject.addProperty("cityInfoId", getAddressDTO().getCityInfoDTO().getId());
//        jsonObject.addProperty("zipCode", getAddressDTO().getCityInfoDTO().getZipCode());
//        jsonObject.addProperty("city", getAddressDTO().getCityInfoDTO().getCity());
//        JsonArray jsonArray = new JsonArray();
//        for (PhoneDTO p : getPhoneList()) {
//            JsonObject pObject = new JsonObject();
//            pObject.addProperty("phoneId", p.getId());
//            pObject.addProperty("number", p.getNumber());
//            pObject.addProperty("description", p.getDescription());
//            jsonArray.add(pObject);
//        }
//        jsonObject.add("phone", jsonArray);
//        return jsonObject;
//    }
}
