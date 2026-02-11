package com.sqli.gestiontests.services;

import com.sqli.gestiontests.dao.ConfigurationDAO;
import com.sqli.gestiontests.entities.Configuration;

public class ConfigurationService {

    private final ConfigurationDAO configDAO;

    public ConfigurationService() {
        this.configDAO = new ConfigurationDAO();
    }

    /**
     * Récupère la durée de l'examen en minutes
     */
    public int getDureeExamen() {
        String valeur = configDAO.getValeur(Configuration.DUREE_EXAMEN, "120");
        return Integer.parseInt(valeur);
    }

    /**
     * Définit la durée de l'examen
     */
    public void setDureeExamen(int duree) {
        configDAO.setValeur(Configuration.DUREE_EXAMEN, String.valueOf(duree),
                "Durée de l'examen en minutes");
    }

    /**
     * Récupère le nombre de questions par thème
     */
    public int getNombreQuestionsParTheme() {
        String valeur = configDAO.getValeur(Configuration.NOMBRE_QUESTIONS_PAR_THEME, "5");
        return Integer.parseInt(valeur);
    }

    /**
     * Définit le nombre de questions par thème
     */
    public void setNombreQuestionsParTheme(int nombre) {
        configDAO.setValeur(Configuration.NOMBRE_QUESTIONS_PAR_THEME, String.valueOf(nombre),
                "Nombre de questions par thème");
    }

    /**
     * Récupère le temps par question en secondes
     */
    public int getTempsParQuestion() {
        String valeur = configDAO.getValeur(Configuration.TEMPS_PAR_QUESTION, "120");
        return Integer.parseInt(valeur);
    }

    /**
     * Définit le temps par question
     */
    public void setTempsParQuestion(int temps) {
        configDAO.setValeur(Configuration.TEMPS_PAR_QUESTION, String.valueOf(temps),
                "Temps par question en secondes");
    }

    /**
     * Vérifie si la validation admin est requise
     */
    public boolean isValidationAdminRequise() {
        String valeur = configDAO.getValeur(Configuration.VALIDATION_ADMIN_REQUISE, "false");
        return Boolean.parseBoolean(valeur);
    }

    /**
     * Active/désactive la validation admin
     */
    public void setValidationAdminRequise(boolean requise) {
        configDAO.setValeur(Configuration.VALIDATION_ADMIN_REQUISE, String.valueOf(requise),
                "Validation des inscriptions par l'administrateur");
    }
}
