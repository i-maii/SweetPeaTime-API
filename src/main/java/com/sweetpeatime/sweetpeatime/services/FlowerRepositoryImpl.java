package com.sweetpeatime.sweetpeatime.services;

import com.sweetpeatime.sweetpeatime.entities.Flower;
import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

//@Component
public class FlowerRepositoryImpl {

    @PersistenceUnit
    EntityManagerFactory emf;

//    @Override
//    public List<Flower> getAllFlower() {
//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();
//
//        Query q = em.createNativeQuery("SELECT * FROM Flower");
//
//        List<Flower> flowers = q.getResultList();
//
//        em.getTransaction().commit();
//        em.close();
//
//        return flowers;
//    }
}
