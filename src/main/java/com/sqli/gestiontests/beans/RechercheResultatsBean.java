package com.sqli.gestiontests.beans;

import com.sqli.gestiontests.dao.ResultatDAO;
import com.sqli.gestiontests.entities.Resultat;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "rechercheResultatsBean")
@ViewScoped
public class RechercheResultatsBean implements Serializable {

    private String nomRecherche;
    private String prenomRecherche;
    private String ecoleRecherche;
    private String codeRecherche;
    private Date dateDebut;
    private Date dateFin;

    private List<Resultat> resultatsRecherche = new ArrayList<>();
    private final ResultatDAO resultatDAO;

    public RechercheResultatsBean() {
        this.resultatDAO = new ResultatDAO();
        chargerTousLesResultats();
    }

    private void chargerTousLesResultats() {
        try {
            resultatsRecherche = resultatDAO.findAll();
        } catch (Exception e) {
            resultatsRecherche = new ArrayList<>();
        }
    }

    public void rechercher() {
        try {
            List<Resultat> tous = resultatDAO.findAll();
            resultatsRecherche = tous.stream()
                    .filter(r -> {
                        boolean match = true;

                        if (nomRecherche != null && !nomRecherche.trim().isEmpty()) {
                            match = match && r.getTest().getCandidat().getNom()
                                    .toLowerCase().contains(nomRecherche.toLowerCase());
                        }

                        if (prenomRecherche != null && !prenomRecherche.trim().isEmpty()) {
                            match = match && r.getTest().getCandidat().getPrenom()
                                    .toLowerCase().contains(prenomRecherche.toLowerCase());
                        }

                        if (ecoleRecherche != null && !ecoleRecherche.trim().isEmpty()) {
                            match = match && r.getTest().getCandidat().getEcole()
                                    .toLowerCase().contains(ecoleRecherche.toLowerCase());
                        }

                        if (codeRecherche != null && !codeRecherche.trim().isEmpty()) {
                            match = match && r.getTest().getCandidat().getCode()
                                    .toLowerCase().contains(codeRecherche.toLowerCase());
                        }

                        if (dateDebut != null) {
                            // Convertir Date en LocalDateTime pour comparaison
                            java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();
                            java.time.LocalDateTime debut = dateDebut.toInstant().atZone(zoneId).toLocalDateTime();
                            match = match && !r.getTest().getDateDebut().isBefore(debut);
                        }

                        if (dateFin != null) {
                            java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();
                            java.time.LocalDateTime fin = dateFin.toInstant().atZone(zoneId).toLocalDateTime();
                            match = match && !r.getTest().getDateDebut().isAfter(fin);
                        }

                        return match;
                    })
                    .collect(Collectors.toList());

            addMessage(FacesMessage.SEVERITY_INFO, "Recherche terminée",
                    resultatsRecherche.size() + " résultat(s) trouvé(s)");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la recherche");
        }
    }

    public void reinitialiser() {
        nomRecherche = null;
        prenomRecherche = null;
        ecoleRecherche = null;
        codeRecherche = null;
        dateDebut = null;
        dateFin = null;
        chargerTousLesResultats();
    }

    public void exporterCSV() {
        // TODO: Implémenter l'export CSV
        addMessage(FacesMessage.SEVERITY_INFO, "Export", "Fonctionnalité en cours de développement");
    }

    // Statistiques
    public int getNombreTotal() {
        return resultatsRecherche.size();
    }

    public String getMoyenneGenerale() {
        if (resultatsRecherche.isEmpty())
            return "0.0";

        double moyenne = resultatsRecherche.stream()
                .mapToDouble(Resultat::getNote)
                .average()
                .orElse(0.0);

        return String.format("%.2f", moyenne);
    }

    public int getTauxReussite() {
        if (resultatsRecherche.isEmpty())
            return 0;

        long reussis = resultatsRecherche.stream()
                .filter(r -> r.getNote() >= 10.0)
                .count();

        return (int) ((reussis * 100) / resultatsRecherche.size());
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters et Setters
    public String getNomRecherche() {
        return nomRecherche;
    }

    public void setNomRecherche(String nomRecherche) {
        this.nomRecherche = nomRecherche;
    }

    public String getPrenomRecherche() {
        return prenomRecherche;
    }

    public void setPrenomRecherche(String prenomRecherche) {
        this.prenomRecherche = prenomRecherche;
    }

    public String getEcoleRecherche() {
        return ecoleRecherche;
    }

    public void setEcoleRecherche(String ecoleRecherche) {
        this.ecoleRecherche = ecoleRecherche;
    }

    public String getCodeRecherche() {
        return codeRecherche;
    }

    public void setCodeRecherche(String codeRecherche) {
        this.codeRecherche = codeRecherche;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public List<Resultat> getResultatsRecherche() {
        return resultatsRecherche;
    }

    public void setResultatsRecherche(List<Resultat> resultatsRecherche) {
        this.resultatsRecherche = resultatsRecherche;
    }
}
