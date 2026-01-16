package com.sqli.gestiontests.beans;

import com.sqli.gestiontests.dao.CreneauDAO;
import com.sqli.gestiontests.entities.Candidat;
import com.sqli.gestiontests.entities.Creneau;
import com.sqli.gestiontests.services.CandidatService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "inscriptionBean")
@SessionScoped
public class InscriptionBean implements Serializable {

    private Candidat candidat = new Candidat();
    private Long creneauSelectionne;
    private List<Creneau> creneauxDisponibles;
    private boolean inscriptionReussie = false;

    private final CandidatService candidatService;
    private final CreneauDAO creneauDAO;

    public InscriptionBean() {
        this.candidatService = new CandidatService();
        this.creneauDAO = new CreneauDAO();
        chargerCreneauxDisponibles();
    }

    /**
     * Charge les créneaux disponibles
     */
    private void chargerCreneauxDisponibles() {
        creneauxDisponibles = creneauDAO.findCreneauxDisponibles();
    }

    /**
     * Inscription du candidat
     */
    public String inscrire() {
        try {
            if (creneauSelectionne == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Veuillez sélectionner un créneau");
                return null;
            }

            candidat = candidatService.inscrireCandidat(candidat, creneauSelectionne);
            inscriptionReussie = true;

            addMessage(FacesMessage.SEVERITY_INFO, "Succès",
                    "Inscription réussie! Votre code: " + candidat.getCode());

            return "confirmation?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
            return null;
        }
    }

    /**
     * Nouvelle inscription
     */
    public String nouvelleInscription() {
        candidat = new Candidat();
        creneauSelectionne = null;
        inscriptionReussie = false;
        chargerCreneauxDisponibles();
        return "inscription?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters et Setters
    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Long getCreneauSelectionne() {
        return creneauSelectionne;
    }

    public void setCreneauSelectionne(Long creneauSelectionne) {
        this.creneauSelectionne = creneauSelectionne;
    }

    public List<Creneau> getCreneauxDisponibles() {
        return creneauxDisponibles;
    }

    public boolean isInscriptionReussie() {
        return inscriptionReussie;
    }
}
