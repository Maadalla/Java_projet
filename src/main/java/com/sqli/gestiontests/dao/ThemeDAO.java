package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Theme;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ThemeDAO extends GenericDAO<Theme> {

    public ThemeDAO() {
        super(Theme.class);
    }

    public List<Theme> findThemesActifs() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Theme> query = em.createQuery(
                    "SELECT t FROM Theme t WHERE t.actif = true ORDER BY t.nom", Theme.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Theme findByNom(String nom) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Theme> query = em.createQuery(
                    "SELECT t FROM Theme t WHERE t.nom = :nom", Theme.class);
            query.setParameter("nom", nom);
            List<Theme> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}
