package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Creneau;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CreneauDAO extends GenericDAO<Creneau> {

    public CreneauDAO() {
        super(Creneau.class);
    }

    public List<Creneau> findCreneauxDisponibles() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c WHERE c.actif = true AND c.date >= :today " +
                            "ORDER BY c.date, c.heureDebut",
                    Creneau.class);
            query.setParameter("today", LocalDate.now());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Creneau> findByDate(LocalDate date) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c WHERE c.date = :date ORDER BY c.heureDebut", Creneau.class);
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Creneau> findCreneauxActifs() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c WHERE c.actif = true ORDER BY c.date, c.heureDebut", Creneau.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
