package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Test;
import com.sqli.gestiontests.entities.Candidat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class TestDAO extends GenericDAO<Test> {

    public TestDAO() {
        super(Test.class);
    }

    public List<Test> findByCandidat(Candidat candidat) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Test> query = em.createQuery(
                    "SELECT t FROM Test t WHERE t.candidat = :candidat ORDER BY t.dateDebut DESC", Test.class);
            query.setParameter("candidat", candidat);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Test findByCandidatCode(String code) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Test> query = em.createQuery(
                    "SELECT t FROM Test t WHERE t.candidat.code = :code " +
                            "ORDER BY t.dateDebut DESC",
                    Test.class);
            query.setParameter("code", code);
            List<Test> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}
