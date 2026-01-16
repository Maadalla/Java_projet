package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Resultat;
import com.sqli.gestiontests.entities.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ResultatDAO extends GenericDAO<Resultat> {

    public ResultatDAO() {
        super(Resultat.class);
    }

    public Resultat findByTest(Test test) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Resultat> query = em.createQuery(
                    "SELECT r FROM Resultat r WHERE r.test = :test", Resultat.class);
            query.setParameter("test", test);
            List<Resultat> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Resultat> searchResultats(String searchTerm) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Resultat> query = em.createQuery(
                    "SELECT r FROM Resultat r " +
                            "WHERE LOWER(r.test.candidat.nom) LIKE :term " +
                            "OR LOWER(r.test.candidat.prenom) LIKE :term " +
                            "OR LOWER(r.test.candidat.ecole) LIKE :term " +
                            "OR r.test.candidat.code LIKE :term " +
                            "ORDER BY r.dateCalcul DESC",
                    Resultat.class);
            query.setParameter("term", "%" + searchTerm.toLowerCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
