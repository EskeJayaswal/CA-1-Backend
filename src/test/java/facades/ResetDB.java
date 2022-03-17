package facades;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ResetDB {

    public static void delete(EntityManager em) {
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE phone AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE hobby AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE person AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE address AUTO_INCREMENT = 1").executeUpdate();

            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER TABLE city_info AUTO_INCREMENT = 1").executeUpdate();
        } finally {
            em.close();
        }
    }

    public static void truncate(EntityManagerFactory emf) {
//        System.out.println("--- TRUNCATING TEST DATABASE ---");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            em.createNativeQuery("truncate table phone").executeUpdate();
            em.createNativeQuery("truncate table hobby").executeUpdate();
            em.createNativeQuery("truncate table person_hobby").executeUpdate();
            em.createNativeQuery("truncate table person").executeUpdate();
            em.createNativeQuery("truncate table address").executeUpdate();
            em.createNativeQuery("truncate table city_info").executeUpdate();
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        } finally {
            em.close();
        }
    }
}
