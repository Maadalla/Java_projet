package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Administrateur;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdministrateurDAO extends GenericDAO<Administrateur> {

    public AdministrateurDAO() {
        super(Administrateur.class);
    }

    public Administrateur findByLogin(String login) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Administrateur> query = em.createQuery(
                    "SELECT a FROM Administrateur a WHERE a.login = :login AND a.actif = true",
                    Administrateur.class);
            query.setParameter("login", login);
            List<Administrateur> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}
