package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Candidat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class CandidatDAO extends GenericDAO<Candidat> {

    public CandidatDAO() {
        super(Candidat.class);
    }

    public Candidat findByCode(String code) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Candidat> query = em.createQuery(
                    "SELECT c FROM Candidat c WHERE c.code = :code", Candidat.class);
            query.setParameter("code", code);
            List<Candidat> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public Candidat findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Candidat> query = em.createQuery(
                    "SELECT c FROM Candidat c WHERE c.email = :email", Candidat.class);
            query.setParameter("email", email);
            List<Candidat> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Candidat> findByStatut(Candidat.StatutInscription statut) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Candidat> query = em.createQuery(
                    "SELECT c FROM Candidat c WHERE c.statut = :statut", Candidat.class);
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Candidat> searchCandidats(String searchTerm) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Candidat> query = em.createQuery(
                    "SELECT c FROM Candidat c WHERE " +
                            "LOWER(c.nom) LIKE :term OR " +
                            "LOWER(c.prenom) LIKE :term OR " +
                            "LOWER(c.ecole) LIKE :term OR " +
                            "c.code LIKE :term",
                    Candidat.class);
            query.setParameter("term", "%" + searchTerm.toLowerCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
