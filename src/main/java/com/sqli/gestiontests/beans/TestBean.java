package com.sqli.gestiontests.beans;

import com.sqli.gestiontests.entities.*;
import com.sqli.gestiontests.services.TestService;
import com.sqli.gestiontests.services.ResultatService;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "testBean")
@SessionScoped
public class TestBean implements Serializable {

    private String codeSession;
    private Test test;
    private List<Question> questions;
    private int indexQuestionCourante = 0;
    private Map<Long, List<Long>> reponsesSelectionnees = new HashMap<>();
    private boolean testDemarre = false;
    private boolean testTermine = false;
    private String messageAccess;
    private Resultat resultat;

    private final TestService testService;
    private final ResultatService resultatService;
    private final com.sqli.gestiontests.services.CertificateService certificateService;
    private final com.sqli.gestiontests.services.EmailService emailService;

    public TestBean() {
        this.testService = new TestService();
        this.resultatService = new ResultatService();
        this.certificateService = new com.sqli.gestiontests.services.CertificateService();
        this.emailService = new com.sqli.gestiontests.services.EmailService();
    }

    /**
     * Vérifier le code de session
     */
    public void verifierCode() {
        String verif = testService.verifierAccesTest(codeSession);

        // MODE DÉMO: Autoriser l'accès même avant l'heure du créneau
        // Pour désactiver le mode démo, décommenter la ligne suivante:
        // messageAccess = verif;

        // Mode démo activé - toujours autoriser
        messageAccess = "OK";

        if ("OK".equals(verif) || true) { // Mode démo: toujours OK
            addMessage(FacesMessage.SEVERITY_INFO, "C'est bon !",
                    "Code valide. Cliquez sur 'Commencer' pour lancer l'examen.");
        } else if ("LE_CRENEAU_NA_PAS_COMMENCE".equals(verif)) {
            addMessage(FacesMessage.SEVERITY_WARN, "Attention",
                    "Le créneau n'a pas encore commencé. Veuillez attendre.");
        } else if ("LE_CRENEAU_EST_PASSE".equals(verif)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                    "Le créneau est passé. Veuillez choisir un autre créneau.");
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", verif);
        }
    }

    /**
     * Démarre le test - MODE NORMAL
     */
    public String demarrerTest() {
        try {
            if (!"OK".equals(messageAccess)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Vous ne pouvez pas démarrer le test maintenant. Veuillez d'abord vérifier votre code.");
                return null;
            }

            test = testService.demarrerTest(codeSession);
            questions = testService.genererQuestions();
            testDemarre = true;
            indexQuestionCourante = 0;

            // Redirection explicite vers la page de test
            return "test?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
            return null;
        }
    }

    /**
     * Démarre le test - MODE DÉMO
     * Ignore la vérification du créneau
     */
    public String demarrerTestDemo() {
        try {
            // Force l'accès en mode démo
            messageAccess = "OK";

            test = testService.demarrerTest(codeSession);
            questions = testService.genererQuestions();
            testDemarre = true;
            indexQuestionCourante = 0;

            addMessage(FacesMessage.SEVERITY_INFO, "Mode Démo",
                    "Test démarré en mode démonstration");
            return null;

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
            return null;
        }
    }

    /**
     * Passe à la question suivante
     */
    public String questionSuivante() {
        enregistrerReponseActuelle();

        if (indexQuestionCourante < questions.size() - 1) {
            indexQuestionCourante++;
            return null;
        } else {
            return soumettreTest();
        }
    }

    /**
     * Retourne à la question précédente
     */
    public void questionPrecedente() {
        enregistrerReponseActuelle();

        if (indexQuestionCourante > 0) {
            indexQuestionCourante--;
        }
    }

    /**
     * Enregistre la réponse de la question courante
     */
    private void enregistrerReponseActuelle() {
        if (questions == null || indexQuestionCourante >= questions.size())
            return;

        Question questionCourante = questions.get(indexQuestionCourante);
        List<Long> reponses = reponsesSelectionnees.get(questionCourante.getId());

        if (reponses != null && !reponses.isEmpty()) {
            testService.enregistrerReponse(test, questionCourante, reponses);
        }
    }

    /**
     * Soumet le test
     */
    public String soumettreTest() {
        try {
            enregistrerReponseActuelle();

            testService.terminerTest(test);

            resultat = resultatService.calculerResultat(test);

            // CORRECTION SCORE
            if (questions != null && !questions.isEmpty()) {
                resultat.setTotalQuestions(questions.size());
                double pourcentage = (double) resultat.getScore() / questions.size() * 100;
                resultat.setPourcentage(pourcentage);
                resultat.setNote(pourcentage * 20 / 100);
            }

            // GENERATION CERTIFICAT & EMAIL
            if (resultat.getNote() >= 10) {
                try {
                    byte[] pdfContent = certificateService.genererCertificat(resultat);
                    String pdfName = "Certificat_" + test.getCandidat().getNom() + "_ISGA.pdf";

                    // Envoi Email
                    String subject = "Votre Certificat de Réussite - ISGA Management";
                    String body = "Bonjour " + test.getCandidat().getPrenom() + ",\n\n" +
                            "Félicitations ! Vous avez réussi votre test avec une note de " +
                            String.format("%.1f", resultat.getNote()) + "/20.\n" +
                            "Veuillez trouver votre certificat en pièce jointe.\n\n" +
                            "Cordialement,\nL'équipe ISGA Management";

                    emailService.envoyerEmailAvecCertificat(test.getCandidat().getEmail(), subject, body, pdfContent,
                            pdfName);

                } catch (Exception e) {
                    System.err.println("Erreur génération certificat/email: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            testTermine = true;
            testDemarre = false;

            return "resultat?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void telechargerCertificat() {
        try {
            if (resultat == null || resultat.getNote() < 10)
                return;

            byte[] pdfContent = certificateService.genererCertificat(resultat);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse) facesContext
                    .getExternalContext().getResponse();

            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Certificat_ISGA.pdf\"");
            response.setContentLength(pdfContent.length);

            response.getOutputStream().write(pdfContent);
            response.getOutputStream().flush();

            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Nouveau test
     */
    public String nouveauTest() {
        codeSession = null;
        test = null;
        questions = null;
        indexQuestionCourante = 0;
        reponsesSelectionnees.clear();
        testDemarre = false;
        testTermine = false;
        messageAccess = null;
        resultat = null;

        return "test?faces-redirect=true";
    }

    /**
     * Récupère la question courante
     */
    public Question getQuestionCourante() {
        if (questions != null && !questions.isEmpty() &&
                indexQuestionCourante < questions.size()) {
            return questions.get(indexQuestionCourante);
        }
        return null;
    }

    /**
     * Récupère les réponses sélectionnées pour la question courante
     */
    public List<Long> getReponsesCourantes() {
        Question questionCourante = getQuestionCourante();
        if (questionCourante != null) {
            return reponsesSelectionnees.computeIfAbsent(
                    questionCourante.getId(), k -> new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public void setReponsesCourantes(List<Long> reponses) {
        Question questionCourante = getQuestionCourante();
        if (questionCourante != null) {
            reponsesSelectionnees.put(questionCourante.getId(), reponses);
        }
    }

    public Long getReponseUnique() {
        List<Long> reponses = getReponsesCourantes();
        return (reponses != null && !reponses.isEmpty()) ? reponses.get(0) : null;
    }

    public void setReponseUnique(Long idReponse) {
        if (idReponse != null) {
            List<Long> reponses = new ArrayList<>();
            reponses.add(idReponse);
            setReponsesCourantes(reponses);
        }
    }

    public void setReponsesSelectionneesPourQuestion(Long questionId, List<Long> reponses) {
        reponsesSelectionnees.put(questionId, reponses);
    }

    /**
     * Vérifie si c'est la dernière question
     */
    public boolean isDerniereQuestion() {
        return questions != null && indexQuestionCourante == questions.size() - 1;
    }

    /**
     * Vérifie si c'est la première question
     */
    public boolean isPremiereQuestion() {
        return indexQuestionCourante == 0;
    }

    /**
     * Récupère le temps restant en secondes
     */
    public long getTempsRestant() {
        if (test != null) {
            return testService.getTempsRestant(test);
        }
        return 0;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Protection contre double soumission
    private transient volatile boolean submittingInProgress = false;

    /**
     * Méthode appelée périodiquement (p:poll) pour vérifier côté serveur si le
     * temps est écoulé
     * et déclencher la soumission du test une seule fois.
     */
    public void checkTempsAndSubmit() {
        try {
            if (submittingInProgress) {
                return;
            }
            if (Boolean.TRUE.equals(this.testDemarre) && !Boolean.TRUE.equals(this.testTermine)) {
                long tempsRestant = 0L;
                try {
                    // adapter selon la signature réelle de testService.getTempsRestant(...)
                    tempsRestant = testService.getTempsRestant(this.test);
                } catch (Exception e) {
                    // ignore si la méthode n'existe pas ou lance une exception
                }
                if (tempsRestant <= 0L) {
                    synchronized (this) {
                        if (!submittingInProgress && !Boolean.TRUE.equals(this.testTermine)) {
                            submittingInProgress = true;
                            try {
                                soumettreTest();
                            } catch (Exception ex) {
                                // log si nécessaire
                            } finally {
                                submittingInProgress = false;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // silencieux pour l'UI; log si disponible
        }
    }

    // Getters et Setters
    public Map<Long, List<Long>> getReponsesSelectionnees() {
        return reponsesSelectionnees;
    }

    public String getCodeSession() {
        return codeSession;
    }

    public void setCodeSession(String codeSession) {
        this.codeSession = codeSession;
    }

    public Test getTest() {
        return test;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getIndexQuestionCourante() {
        return indexQuestionCourante;
    }

    public boolean isTestDemarre() {
        return testDemarre;
    }

    public boolean isTestTermine() {
        return testTermine;
    }

    public String getMessageAccess() {
        return messageAccess;
    }

    public Resultat getResultat() {
        return resultat;
    }

    public int getNombreQuestions() {
        return questions != null ? questions.size() : 0;
    }

    public int getNumeroQuestion() {
        return indexQuestionCourante + 1;
    }
}
