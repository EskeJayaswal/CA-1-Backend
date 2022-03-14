package facades;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dtos.*;
import entities.*;
import errorhandling.EntityAlreadyExistsException;
import errorhandling.EntityNotFoundException;

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

    public PersonDTO create(PersonDTO personDTO) throws EntityAlreadyExistsException{
        EntityManager em = emf.createEntityManager();

        CityInfo cityInfo = new CityInfo(personDTO.getAddressDTO().getCityInfoDTO());
        Address address = new Address(personDTO.getAddressDTO());
        Person person = new Person(personDTO);

        // Loop through all phoneDTOs and add them to the new person entity.
        for (PhoneDTO phoneDTO : personDTO.getPhoneList()) {
            if (FacadePhone.getFacadePhone(emf).alreadyExists(phoneDTO.getNumber()))
                throw new EntityAlreadyExistsException("Phone number: "+ phoneDTO.getNumber() +" already exists in the database");
            person.addPhone(new Phone(phoneDTO));
        }

        // Loop through hobbyDTOs and add to new person entity. Find the existing hobbies from database and pair them with person.
        personDTO.getHobbyDTOList().forEach(hobbyDTO -> {
            person.addHobby(em.find(Hobby.class, hobbyDTO.getId()));
        });

        // Add references for bi-directional relationships.
        cityInfo.addAddress(address);
        address.addPerson(person);

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


    public PersonDTO getById(long id) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null)
            throw new EntityNotFoundException("The Person entity with ID: '"+id+"' was not found");
        return new PersonDTO(person);
    }


    public PersonDTO getByPhoneNumber(String phoneNumber) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Person> typedQuery = em.createQuery("SELECT p FROM Phone ph JOIN ph.person p WHERE ph.number =" + phoneNumber, Person.class);
        if (typedQuery.getResultList().size() == 0)
            throw new EntityNotFoundException("The Person entity with phone number: '"+phoneNumber+"' was not found");
        Person person = typedQuery.getSingleResult();

        return new PersonDTO(person);
    }

    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getPersonsWithHobby(long hobbyId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> typedQueryPerson
                = em.createQuery("SELECT p FROM Person p LEFT JOIN p.hobbyList h WHERE h.id=" + hobbyId, Person.class);
        List<Person> personList = typedQueryPerson.getResultList();
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person p : personList) {
            personDTOList.add(new PersonDTO(p));
        }
        return personDTOList;
    }

    // Updates everything
    public PersonDTO update(PersonDTO personDTO) throws EntityAlreadyExistsException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, personDTO.getId());                       // Read Person entity from DB
        Address address = em.find(Address.class, person.getAddress().getId());          // Read Address entity from DB
        CityInfo cityInfo = em.find(CityInfo.class, address.getCityInfo().getId());     // Read cityInfo entity from DB

        TypedQuery<Phone> tq = em.createQuery("SELECT p FROM Phone p  WHERE p.person.id = " + person.getId(), Phone.class);
        List<Phone> phoneList = tq.getResultList();                 // returns list of person numbers already in database

        person.setEmail(personDTO.getEmail());                                          // Update Person entity
        person.setFirstName(personDTO.getFirstName());
        person.setFirstName(personDTO.getLastName());

        address.setStreet(personDTO.getAddressDTO().getStreet());                       // Update Address entity
        address.setAdditionalInfo(personDTO.getAddressDTO().getAdditionalInfo());

        cityInfo.setZipCode(personDTO.getAddressDTO().getCityInfoDTO().getZipCode());   // Update CityInfo entity
        cityInfo.setCity(personDTO.getAddressDTO().getCityInfoDTO().getCity());

        for (PhoneDTO phoneDTO : personDTO.getPhoneList()) {                            // Check for updates on every phone number
            if (FacadePhone.getFacadePhone(emf).alreadyExists(phoneDTO.getNumber()))
                throw new EntityAlreadyExistsException("Phone number: "+ phoneDTO.getNumber() +" already exists in the database");

            person.addPhone(new Phone(phoneDTO));
        }

        // Loop through hobbyDTOs and add to new person entity. Find the existing hobbies from database and pair them with person.
        personDTO.getHobbyDTOList().forEach(hobbyDTO -> {
            person.addHobby(em.find(Hobby.class, hobbyDTO.getId()));
        });

        try {
            em.getTransaction().begin();
            em.merge(cityInfo);
            em.merge(address);
            person.getHobbyList().forEach(em::merge);
            em.merge(person);
            person.getPhoneList().forEach(em::merge);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    // Being used before updating a persons infos
    public void removeAllPhones(PersonDTO personDTO) throws EntityNotFoundException, EntityAlreadyExistsException {
        for (PhoneDTO phoneDTO : personDTO.getPhoneList()) {        // Check if the new number already exists in database under a different person
            if (FacadePhone.getFacadePhone(emf).alreadyExists(phoneDTO.getNumber(), personDTO))
                throw new EntityAlreadyExistsException("Phone number: "+ phoneDTO.getNumber() +" already exists in the database");
        }

        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, personDTO.getId());
            if (person == null)
                throw new EntityNotFoundException("The Person entity with ID: '"+ personDTO.getId() +"' was not found");

            em.getTransaction().begin();

            List<Phone> originalPhoneList = new ArrayList<>(person.getPhoneList());
            originalPhoneList.forEach(person::removePhone);
            person.getPhoneList().forEach(em::remove);

            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Being used before updating a persons infos
    public void removeAllHobbies(PersonDTO personDTO) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, personDTO.getId());
            if (person == null)
                throw new EntityNotFoundException("The Person entity with ID: '"+ personDTO.getId() +"' was not found");

            em.getTransaction().begin();

            List<Hobby> originalHobbyList = new ArrayList<>(person.getHobbyList());
            originalHobbyList.forEach(hobby -> {
                person.removeHobby(em.find(Hobby.class, hobby.getId()));
            });
            person.getHobbyList().forEach(em::merge);
            em.merge(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }
}



// Overflow methods

//    public List<PhoneDTO> getPhoneDTOs(long personId) {
//        EntityManager em = emf.createEntityManager();
//        TypedQuery<Phone> typedQueryPhone
//                = em.createQuery("SELECT p FROM Phone p WHERE p.person.id= " + personId, Phone.class);
//
//        List<Phone> phoneList = typedQueryPhone.getResultList();
//        List<PhoneDTO> phoneDTOList = new ArrayList<>();
//        for (Phone p : phoneList) {
//            phoneDTOList.add(new PhoneDTO(p));
//        }
//        return phoneDTOList;
//    }