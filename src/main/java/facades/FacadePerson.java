package facades;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dtos.*;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class FacadePerson {
    private static FacadePerson instance;
    private static EntityManagerFactory emf;

    public FacadePerson() {}

    public static FacadePerson getFacadePerson(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadePerson();
        }
        return instance;
    }

    public PersonDTO create(PersonDTO pDTO) {
        CityInfo cityInfo = new CityInfo(pDTO.getAddressDTO().getCityInfoDTO());
        Address address = new Address(pDTO.getAddressDTO());
        Person person = new Person(pDTO);

        pDTO.getPhoneList().forEach(phoneDTO -> {
            Phone phone = new Phone(phoneDTO);
            person.addPhone(phone);
        });


        cityInfo.addAddress(address);
        address.addPerson(person);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.persist(cityInfo);
            em.persist(address);
            em.persist(person);
            person.getPhoneList().forEach(em::persist);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public void addHobby(long personID, long hobbyID) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, personID);
        Hobby hobby = em.find(Hobby.class, hobbyID);
        person.addHobby(hobby);

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void addAllHobbies(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, personDTO.getId());
            em.getTransaction().begin();
            personDTO.getHobbyDTOList().forEach(hobbyDTO -> {
                person.addHobby(em.find(Hobby.class, hobbyDTO.getId()));
            });
            em.merge(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

    }

    public PersonDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        // TODO: if null -> Throw custom exception
        return new PersonDTO(person);
    }

    public PersonDTO getByPhoneNumber(String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Phone> typedQueryPhone
                = em.createQuery("SELECT p FROM Phone p WHERE p.number=:number", Phone.class);
        typedQueryPhone.setParameter("number", phoneNumber);

        List<Phone> phoneList = typedQueryPhone.getResultList();
        long personId = phoneList.get(0).getPerson().getId();

        return getById(personId);
    }

    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
        } finally {
            em.close();
        }
    }

    public JsonObject getPersonInfo(PersonDTO personDTO) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("personId", personDTO.getId());
        jsonObject.addProperty("email", personDTO.getEmail());
        jsonObject.addProperty("firstName", personDTO.getFirstName());
        jsonObject.addProperty("lastName", personDTO.getLastName());

        jsonObject.addProperty("addressId", personDTO.getAddressDTO().getId());
        jsonObject.addProperty("street", personDTO.getAddressDTO().getStreet());
        jsonObject.addProperty("additionalInfo", personDTO.getAddressDTO().getAdditionalInfo());

        jsonObject.addProperty("cityInfoId", personDTO.getAddressDTO().getCityInfoDTO().getId());
        jsonObject.addProperty("zipCode", personDTO.getAddressDTO().getCityInfoDTO().getZipCode());
        jsonObject.addProperty("city", personDTO.getAddressDTO().getCityInfoDTO().getCity());

        JsonArray phoneArray = new JsonArray();
        for (PhoneDTO p : getPhoneDTOs(personDTO)) {
            JsonObject pObject = new JsonObject();
            pObject.addProperty("phoneId", p.getId());
            pObject.addProperty("number", p.getNumber());
            pObject.addProperty("description", p.getDescription());
            phoneArray.add(pObject);
        }
        jsonObject.add("phone", phoneArray);

        JsonArray hobbyArray = new JsonArray();
        for (HobbyDTO h : personDTO.getHobbyDTOList()) {
            JsonObject hObject = new JsonObject();
            hObject.addProperty("hobbyId", h.getId());
            hObject.addProperty("name", h.getName());
            hObject.addProperty("description", h.getDescription());
            hobbyArray.add(hObject);
        }
        jsonObject.add("hobby", hobbyArray);

        return jsonObject;
    }

    public List<PhoneDTO> getPhoneDTOs(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Phone> typedQueryPhone
                = em.createQuery("SELECT p FROM Phone p WHERE p.person.id=:personId", Phone.class);
        typedQueryPhone.setParameter("personId", personDTO.getId());

        List<Phone> phoneList = typedQueryPhone.getResultList();
        List<PhoneDTO> phoneDTOList = new ArrayList<>();
        for (Phone p : phoneList) {
            phoneDTOList.add(new PhoneDTO(p));
        }
        return phoneDTOList;
    }

    public List<PersonDTO> getPersonsWithHobby(HobbyDTO hobbyDTO) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> typedQueryPerson
                = em.createQuery("SELECT p FROM Person p LEFT JOIN p.hobbyList h WHERE h.id=:hobbyId", Person.class);
        typedQueryPerson.setParameter("hobbyId", hobbyDTO.getId());

        List<Person> personList = typedQueryPerson.getResultList();
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person p : personList) {
            personDTOList.add(new PersonDTO(p));
        }
        return personDTOList;
    }

    public PersonDTO updatePerson(long id, String email, String firstName, String lastName) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);

        person.setEmail(email);
        person.setFirstName(firstName);
        person.setLastName(lastName);

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return new PersonDTO(person);
    }


    public PersonDTO update(PersonDTO pDTO) {
        EntityManager em = emf.createEntityManager();

        // Read entities from DB
        Person person = em.find(Person.class, pDTO.getId());
        Address address = em.find(Address.class, person.getAddress().getId());
        CityInfo cityInfo = em.find(CityInfo.class, address.getCityInfo().getId());

        TypedQuery<Phone> tq = em.createQuery("SELECT p FROM Phone p  WHERE p.person.id = " + person.getId(), Phone.class);
        List<Phone> phoneList = tq.getResultList();


        // Check for updates on Person
        person.setEmail(pDTO.getEmail());
        person.setFirstName(pDTO.getFirstName());
        person.setFirstName(pDTO.getLastName());

        // Check for updates on Address
        address.setStreet(pDTO.getAddressDTO().getStreet());
        address.setAdditionalInfo(pDTO.getAddressDTO().getAdditionalInfo());

        // Check for updates on CityInfo
        cityInfo.setZipCode(pDTO.getAddressDTO().getCityInfoDTO().getZipCode());
        cityInfo.setCity(pDTO.getAddressDTO().getCityInfoDTO().getCity());

        // Check for updates on every phone number.
        for (int i = 0; i < pDTO.getPhoneList().size(); i++) {
            phoneList.get(i).setNumber(pDTO.getPhoneList().get(i).getNumber());
            phoneList.get(i).setDescription(pDTO.getPhoneList().get(i).getDescription());
        }




        em.getTransaction().begin();
        em.merge(cityInfo);
        em.merge(address);
        em.merge(person);
        person.getPhoneList().forEach(em::merge);
        em.getTransaction().commit();



        return new PersonDTO(person);
    }


}
