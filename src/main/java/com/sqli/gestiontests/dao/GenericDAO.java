package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public abstract class GenericDAO<T> {

    protected Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    public void create(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating entity", e);
        } finally {
            em.close();
        }
    }

    public T findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(
                    "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating entity", e);
        } finally {
            em.close();
        }
    }

    public void delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting entity", e);
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }
}
