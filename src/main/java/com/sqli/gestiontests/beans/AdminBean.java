package com.sqli.gestiontests.beans;

import com.sqli.gestiontests.dao.*;
import com.sqli.gestiontests.entities.*;
import com.sqli.gestiontests.services.*;
import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    private String login;
    private String password;
    private Administrateur adminConnecte;

    // Pour la gestion des questions
    private List<Theme> themes;
    private List<Question> questions;
    private Question questionSelectionnee;
    private Question nouvelleQuestion = new Question();
    private Reponse nouvelleReponse = new Reponse();
    private Long themeId; // Pour les dropdowns

    // Pour la gestion des réponses dans le formulaire
    private List<Reponse> reponsesTemp = new ArrayList<>();
    private String texteNouvelleReponse; // Pour ajouter une réponse
    private boolean nouvelleReponseCorrecte = false;

    // Pour la gestion des créneaux
    private List<Creneau> creneaux;
    private Creneau creneauSelectionne;
    private Creneau nouveauCreneau = new Creneau();

    // Pour la visualisation des résultats
    private List<Resultat> resultats;
    private String rechercheTerme;

    // Pour la validation des candidats
    private List<Candidat> candidatsEnAttente;

    // Configuration
    private int dureeExamen;
    private int nombreQuestionsParTheme;
    private int tempsParQuestion;
    private boolean validationAdminRequise;

    // Services
    private final AdminService adminService;
    private final ThemeDAO themeDAO;
    private final QuestionDAO questionDAO;
    private final ReponseDAO reponseDAO;
    private final CreneauDAO creneauDAO;
    private final ResultatService resultatService;
    private final CandidatService candidatService;
    private final ConfigurationService configService;

    public AdminBean() {
        this.adminService = new AdminService();
        this.themeDAO = new ThemeDAO();
        this.questionDAO = new QuestionDAO();
        this.reponseDAO = new ReponseDAO();
        this.creneauDAO = new CreneauDAO();
        this.resultatService = new ResultatService();
        this.candidatService = new CandidatService();
        this.configService = new ConfigurationService();
    }

    @PostConstruct
    public void init() {
        chargerThemes();
        chargerQuestions();
        chargerCreneaux();
        chargerResultats();
    }

    /**
     * Connexion de l'administrateur
     */
    public String seConnecter() {
        adminConnecte = adminService.authentifier(login, password);

        if (adminConnecte != null) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("adminUser", adminConnecte);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Bienvenue " + adminConnecte.getLogin());
            return "dashboard?faces-redirect=true";
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Identifiants incorrects");
            return null;
        }
    }

    /**
     * Déconnexion
     */
    public String seDeconnecter() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        adminConnecte = null;
        return "/admin/login?faces-redirect=true";
    }

    // ========== GESTION DES THEMES ==========

    public void chargerThemes() {
        themes = themeDAO.findAll();
    }

    // ========== GESTION DES QUESTIONS ==========

    public void chargerQuestions() {
        questions = questionDAO.findQuestionsActives();
        if (themes == null || themes.isEmpty()) {
            chargerThemes();
        }
    }

    public void ajouterQuestion() {
        try {
            // Ensure we have a fresh question object
            if (nouvelleQuestion == null) {
                nouvelleQuestion = new Question();
            }

            if (themeId == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez sélectionner un thème");
                return;
            }

            // Validation des réponses
            if (reponsesTemp == null || reponsesTemp.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez ajouter au moins 2 réponses");
                return;
            }

            if (reponsesTemp.size() < 2) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une question doit avoir au moins 2 réponses");
                return;
            }

            // Vérifier qu'au moins une réponse est correcte
            long nbCorrectes = reponsesTemp.stream().filter(Reponse::getCorrecte).count();
            if (nbCorrectes == 0) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Au moins une réponse doit être correcte");
                return;
            }

            // Pour CHOIX_UNIQUE, exactement 1 réponse correcte
            if (nouvelleQuestion.getType() == Question.TypeQuestion.CHOIX_UNIQUE && nbCorrectes > 1) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Une question à choix unique ne peut avoir qu'une seule réponse correcte");
                return;
            }

            // Récupérer le thème complet par ID
            Theme theme = themeDAO.findById(themeId);
            if (theme == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Thème introuvable");
                return;
            }
            nouvelleQuestion.setTheme(theme);

            // Créer la question d'abord
            questionDAO.create(nouvelleQuestion);

            // Ensuite créer les réponses associées
            for (Reponse r : reponsesTemp) {
                r.setQuestion(nouvelleQuestion);
                reponseDAO.create(r);
            }

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Question et réponses ajoutées");
            nouvelleQuestion = new Question(); // Reset for next use
            themeId = null; // Reset dropdown
            reponsesTemp.clear(); // Clear responses
            chargerQuestions();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void supprimerQuestion(Question question) {
        try {
            question.setActif(false);
            questionDAO.update(question);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Question supprimée");
            chargerQuestions();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void selectionnerQuestion(Question question) {
        this.questionSelectionnee = question;
        if (question != null && question.getTheme() != null) {
            this.themeId = question.getTheme().getId();
        }
        // Charger les réponses pour édition
        chargerReponsesTemp(question);
    }

    public void modifierQuestion() {
        try {
            if (questionSelectionnee == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucune question sélectionnée");
                return;
            }

            // Validation des réponses
            if (reponsesTemp == null || reponsesTemp.size() < 2) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une question doit avoir au moins 2 réponses");
                return;
            }

            long nbCorrectes = reponsesTemp.stream().filter(Reponse::getCorrecte).count();
            if (nbCorrectes == 0) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Au moins une réponse doit être correcte");
                return;
            }

            if (questionSelectionnee.getType() == Question.TypeQuestion.CHOIX_UNIQUE && nbCorrectes > 1) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Une question à choix unique ne peut avoir qu'une seule réponse correcte");
                return;
            }

            if (themeId != null) {
                Theme theme = themeDAO.findById(themeId);
                questionSelectionnee.setTheme(theme);
            }

            // Gestion des réponses : on vide la liste existante et on ajoute les nouvelles
            // Hibernate gère la suppression des orphelins et l'insertion grâce à
            // CascadeType.ALL + orphanRemoval
            questionSelectionnee.getReponses().clear();

            for (Reponse temp : reponsesTemp) {
                Reponse nouvelle = new Reponse();
                nouvelle.setTexte(temp.getTexte());
                nouvelle.setCorrecte(temp.getCorrecte());
                nouvelle.setQuestion(questionSelectionnee);
                questionSelectionnee.getReponses().add(nouvelle);
            }

            questionDAO.update(questionSelectionnee);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Question et réponses modifiées");
            questionSelectionnee = null;
            themeId = null;
            reponsesTemp.clear();
            chargerQuestions();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void ajouterReponse() {
        try {
            if (questionSelectionnee == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez sélectionner une question");
                return;
            }

            nouvelleReponse.setQuestion(questionSelectionnee);
            reponseDAO.create(nouvelleReponse);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Réponse ajoutée");
            nouvelleReponse = new Reponse();

            // Recharger la question
            questionSelectionnee = questionDAO.findById(questionSelectionnee.getId());

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void supprimerReponse(Reponse reponse) {
        try {
            reponseDAO.delete(reponse);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Réponse supprimée");

            // Recharger la question
            if (questionSelectionnee != null) {
                questionSelectionnee = questionDAO.findById(questionSelectionnee.getId());
            }

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    // ========== GESTION DES RÉPONSES TEM PORAIRES ==========

    /**
     * Ajoute une réponse à la liste temporaire
     */
    public void ajouterReponseTemp() {
        if (texteNouvelleReponse == null || texteNouvelleReponse.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Attention", "Veuillez saisir le texte de la réponse");
            return;
        }

        Reponse r = new Reponse();
        r.setTexte(texteNouvelleReponse.trim());
        r.setCorrecte(nouvelleReponseCorrecte);
        reponsesTemp.add(r);

        // Reset fields
        texteNouvelleReponse = "";
        nouvelleReponseCorrecte = false;
    }

    /**
     * Supprime une réponse de la liste temporaire
     */
    public void supprimerReponseTemp(Reponse reponse) {
        reponsesTemp.remove(reponse);
    }

    /**
     * Toggle le statut correcte d'une réponse temporaire
     */
    public void toggleCorrect(Reponse reponse) {
        reponse.setCorrecte(!reponse.getCorrecte());
    }

    /**
     * Charge les réponses d'une question dans reponsesTemp pour édition
     */
    public void chargerReponsesTemp(Question question) {
        reponsesTemp.clear();
        if (question != null && question.getReponses() != null) {
            // Clone the responses
            for (Reponse r : question.getReponses()) {
                Reponse clone = new Reponse();
                clone.setId(r.getId());
                clone.setTexte(r.getTexte());
                clone.setCorrecte(r.getCorrecte());
                clone.setQuestion(r.getQuestion());
                reponsesTemp.add(clone);
            }
        }
    }

    // ========== GESTION DES CRENEAUX ==========

    public void chargerCreneaux() {
        creneaux = creneauDAO.findAll();
    }

    public void preparerCreationCreneau() {
        nouveauCreneau = new Creneau();
        nouveauCreneau.setDate(java.time.LocalDate.now().plusDays(1)); // Demain par défaut
        nouveauCreneau.setHeureDebut(java.time.LocalTime.of(10, 0)); // 10:00 par défaut
        nouveauCreneau.setHeureFin(java.time.LocalTime.of(12, 0)); // 12:00 par défaut
        nouveauCreneau.setDureeExamen(120); // 2h par défaut
        nouveauCreneau.setCapaciteMax(20); // 20 places par défaut
    }

    public void ajouterCreneau() {
        try {
            // Calculer l'heure de fin automatiquement si la durée est changée
            if (nouveauCreneau.getHeureDebut() != null && nouveauCreneau.getDureeExamen() != null) {
                // On laisse l'utilisateur choisir, ou on pourrait forcer ici.
                // Pour la validation :
            }

            if (nouveauCreneau.getDate() == null || nouveauCreneau.getHeureDebut() == null
                    || nouveauCreneau.getHeureFin() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Date et horaires obligatoires");
                return;
            }
            if (nouveauCreneau.getHeureFin().isBefore(nouveauCreneau.getHeureDebut())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "L'heure de fin doit être après l'heure de début");
                return;
            }

            creneauDAO.create(nouveauCreneau);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Créneau ajouté");
            nouveauCreneau = new Creneau(); // Reset
            chargerCreneaux();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void selectionnerCreneau(Creneau creneau) {
        this.creneauSelectionne = creneau;
    }

    public void modifierCreneau() {
        try {
            if (creneauSelectionne == null)
                return;

            if (creneauSelectionne.getHeureFin().isBefore(creneauSelectionne.getHeureDebut())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "L'heure de fin doit être après l'heure de début");
                return;
            }

            creneauDAO.update(creneauSelectionne);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Créneau modifié");
            creneauSelectionne = null;
            chargerCreneaux();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void supprimerCreneau(Creneau creneau) {
        try {
            if (creneau.getCandidats() != null && !creneau.getCandidats().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Impossible de supprimer : des candidats sont inscrits");
                return;
            }
            creneauDAO.delete(creneau);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Créneau supprimé");
            chargerCreneaux();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    // ========== GESTION DES RESULTATS ==========

    public void chargerResultats() {
        resultats = resultatService.rechercherResultats(rechercheTerme);
    }

    public void rechercherResultats() {
        chargerResultats();
    }

    // ========== VALIDATION DES CANDIDATS ==========

    public void chargerCandidatsEnAttente() {
        candidatsEnAttente = candidatService.getCandidatsEnAttente();
    }

    public void validerCandidat(Candidat candidat) {
        try {
            candidatService.validerInscription(candidat.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Inscription validée");
            chargerCandidatsEnAttente();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    public void rejeterCandidat(Candidat candidat) {
        try {
            candidatService.rejeterInscription(candidat.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Inscription rejetée");
            chargerCandidatsEnAttente();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    // ========== CONFIGURATION ==========

    public void chargerConfiguration() {
        dureeExamen = configService.getDureeExamen();
        nombreQuestionsParTheme = configService.getNombreQuestionsParTheme();
        tempsParQuestion = configService.getTempsParQuestion();
        validationAdminRequise = configService.isValidationAdminRequise();
    }

    public void sauvegarderConfiguration() {
        try {
            configService.setDureeExamen(dureeExamen);
            configService.setNombreQuestionsParTheme(nombreQuestionsParTheme);
            configService.setTempsParQuestion(tempsParQuestion);
            configService.setValidationAdminRequise(validationAdminRequise);

            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Configuration sauvegardée");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // ========== GETTERS ET SETTERS ==========

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Administrateur getAdminConnecte() {
        return adminConnecte;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Question getQuestionSelectionnee() {
        return questionSelectionnee;
    }

    public void setQuestionSelectionnee(Question questionSelectionnee) {
        this.questionSelectionnee = questionSelectionnee;
    }

    public Question getNouvelleQuestion() {
        return nouvelleQuestion;
    }

    public void setNouvelleQuestion(Question nouvelleQuestion) {
        this.nouvelleQuestion = nouvelleQuestion;
    }

    public Reponse getNouvelleReponse() {
        return nouvelleReponse;
    }

    public void setNouvelleReponse(Reponse nouvelleReponse) {
        this.nouvelleReponse = nouvelleReponse;
    }

    public List<Creneau> getCreneaux() {
        return creneaux;
    }

    public Creneau getCreneauSelectionne() {
        return creneauSelectionne;
    }

    public void setCreneauSelectionne(Creneau creneauSelectionne) {
        this.creneauSelectionne = creneauSelectionne;
    }

    public Creneau getNouveauCreneau() {
        return nouveauCreneau;
    }

    public void setNouveauCreneau(Creneau nouveauCreneau) {
        this.nouveauCreneau = nouveauCreneau;
    }

    public List<Resultat> getResultats() {
        return resultats;
    }

    public String getRechercheTerme() {
        return rechercheTerme;
    }

    public void setRechercheTerme(String rechercheTerme) {
        this.rechercheTerme = rechercheTerme;
    }

    public List<Candidat> getCandidatsEnAttente() {
        return candidatsEnAttente;
    }

    public int getDureeExamen() {
        return dureeExamen;
    }

    public void setDureeExamen(int dureeExamen) {
        this.dureeExamen = dureeExamen;
    }

    public int getNombreQuestionsParTheme() {
        return nombreQuestionsParTheme;
    }

    public void setNombreQuestionsParTheme(int nombreQuestionsParTheme) {
        this.nombreQuestionsParTheme = nombreQuestionsParTheme;
    }

    public int getTempsParQuestion() {
        return tempsParQuestion;
    }

    public void setTempsParQuestion(int tempsParQuestion) {
        this.tempsParQuestion = tempsParQuestion;
    }

    public boolean isValidationAdminRequise() {
        return validationAdminRequise;
    }

    public void setValidationAdminRequise(boolean validationAdminRequise) {
        this.validationAdminRequise = validationAdminRequise;
    }

    // Statistiques
    public int getNombreThemes() {
        if (themes == null) {
            chargerThemes();
        }
        return themes != null ? themes.size() : 0;
    }

    public int getNombreQuestionsChoixMultiple() {
        if (questions == null) {
            chargerQuestions();
        }
        if (questions == null)
            return 0;
        return (int) questions.stream()
                .filter(q -> q.getType() == Question.TypeQuestion.CHOIX_MULTIPLE)
                .count();
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public List<Reponse> getReponsesTemp() {
        return reponsesTemp;
    }

    public void setReponsesTemp(List<Reponse> reponsesTemp) {
        this.reponsesTemp = reponsesTemp;
    }

    public String getTexteNouvelleReponse() {
        return texteNouvelleReponse;
    }

    public void setTexteNouvelleReponse(String texteNouvelleReponse) {
        this.texteNouvelleReponse = texteNouvelleReponse;
    }

    public boolean isNouvelleReponseCorrecte() {
        return nouvelleReponseCorrecte;
    }

    public void setNouvelleReponseCorrecte(boolean nouvelleReponseCorrecte) {
        this.nouvelleReponseCorrecte = nouvelleReponseCorrecte;
    }
}
