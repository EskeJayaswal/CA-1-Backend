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
    public static void populate(){
//        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
//        FacadeExample fe = FacadeExample.getFacadeExample(emf);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Person olaf = new Person("p1@wd", "Olaf", "Berken");

        Hobby h1 = new Hobby("Tennis", "Slå til bold");
        olaf.addHobby(h1);

//        Phone p1 = new Phone("56161", "Home Phone");
//        olaf.addPhone(p1);
//
//        Address a1 = new Address("Oladvej", "Olafs vej");
//        a1.addPerson(olaf);
//
//        CityInfo c1 = new CityInfo("1864", "VikingVille");
//        c1.addAddress(a1);

//        try {
//            em.getTransaction().begin();
//            em.persist(olaf);
//            em.persist(a1);
//            em.persist(c1);
//            em.persist(h1);
//            em.persist(p1);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }

//        fe.create(new RenameMeDTO(new RenameMe("First 1", "Last 1")));
//        fe.create(new RenameMeDTO(new RenameMe("First 2", "Last 2")));
//        fe.create(new RenameMeDTO(new RenameMe("First 3", "Last 3")));
    }


    public static void newPopulator() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();


        Phone phone = new Phone("12345678", "Work");

        Person olaf = new Person("p1@wd", "Olaf", "Berken");

        Hobby h1 = new Hobby("Tennis", "Slå til bold");

        olaf.addPhone(phone);

        olaf.addHobby(h1);


        try {
            em.getTransaction().begin();


            em.persist(h1);



            em.persist(olaf);



            em.persist(phone);


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
