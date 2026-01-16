package com.sqli.gestiontests.dao;

import com.sqli.gestiontests.entities.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ConfigurationDAO extends GenericDAO<Configuration> {

    public ConfigurationDAO() {
        super(Configuration.class);
    }

    public Configuration findByCle(String cle) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Configuration> query = em.createQuery(
                    "SELECT c FROM Configuration c WHERE c.cle = :cle", Configuration.class);
            query.setParameter("cle", cle);
            List<Configuration> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public String getValeur(String cle, String defaultValue) {
        Configuration config = findByCle(cle);
        return config != null ? config.getValeur() : defaultValue;
    }

    public void setValeur(String cle, String valeur, String description) {
        Configuration config = findByCle(cle);
        if (config == null) {
            config = new Configuration();
            config.setCle(cle);
            config.setValeur(valeur);
            config.setDescription(description);
            create(config);
        } else {
            config.setValeur(valeur);
            update(config);
        }
    }
}
