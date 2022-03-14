/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.RenameMeDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import utils.EMF_Creator;

/**
 *
 * @author tha
 */
public class Populator {
    public static void newPopulator() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Phone phone = new Phone("23984312", "Work");
        Person person = new Person("loadsfsdfl@wow.dk", "Bobby", "Anderson");
        Address address = new Address("Alkovej 1", "Bla bla");
        CityInfo cityInfo = new CityInfo("1234", "Aarhus");
        Hobby hobby = new Hobby("Football", "NFL");

//        cityInfo.addAddress(address);
//        address.addPerson(person);
//        person.addPhone(phone);
//        person.addHobby(hobby);

        try {
            em.getTransaction().begin();
//            em.persist(hobby);
//            em.persist(cityInfo);
//            em.persist(address);
            em.persist(person);
//            em.persist(phone);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public static void main(String[] args) {
//        populate();
        newPopulator();
    }


}
