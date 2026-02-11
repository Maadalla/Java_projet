package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Question;
import com.sqli.gestiontests.entities.Theme;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class QuestionDAO extends GenericDAO<Question> {

    public QuestionDAO() {
        super(Question.class);
    }

    public List<Question> findByTheme(Theme theme) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.theme = :theme AND q.actif = true", Question.class);
            query.setParameter("theme", theme);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Question> findByThemeId(Long themeId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.theme.id = :themeId AND q.actif = true", Question.class);
            query.setParameter("themeId", themeId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Question> findQuestionsActives() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.actif = true", Question.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sélectionne n questions aléatoires pour un thème donné
     */
    public List<Question> findRandomByTheme(Long themeId, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.theme.id = :themeId AND q.actif = true " +
                            "ORDER BY FUNCTION('RAND')",
                    Question.class);
            query.setParameter("themeId", themeId);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
