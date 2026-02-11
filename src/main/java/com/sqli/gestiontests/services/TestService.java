package com.sqli.gestiontests.services;

import com.sqli.gestiontests.dao.*;
import com.sqli.gestiontests.entities.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestService {

    private final TestDAO testDAO;
    private final CandidatDAO candidatDAO;
    private final QuestionDAO questionDAO;
    private final ThemeDAO themeDAO;
    private final ConfigurationService configService;

    public TestService() {
        this.testDAO = new TestDAO();
        this.candidatDAO = new CandidatDAO();
        this.questionDAO = new QuestionDAO();
        this.themeDAO = new ThemeDAO();
        this.configService = new ConfigurationService();
    }

    /**
     * Vérifie si un candidat peut démarrer le test
     */
    public String verifierAccesTest(String code) {
        Candidat candidat = candidatDAO.findByCode(code);

        if (candidat == null) {
            return "Code invalide";
        }

        if (candidat.getStatut() != Candidat.StatutInscription.VALIDEE) {
            return "Votre inscription n'a pas encore été validée";
        }

        Creneau creneau = candidat.getCreneau();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creneauDebut = LocalDateTime.of(creneau.getDate(), creneau.getHeureDebut());
        LocalDateTime creneauFin = LocalDateTime.of(creneau.getDate(), creneau.getHeureFin());

        if (now.isBefore(creneauDebut.minusMinutes(1))) {
            return "LE_CRENEAU_NA_PAS_COMMENCE";
        }

        if (now.isAfter(creneauFin)) {
            return "LE_CRENEAU_EST_PASSE";
        }

        return "OK";
    }

    /**
     * Démarre un test pour un candidat
     */
    /**
     * Démarre un test pour un candidat
     */
    /**
     * Démarre un test pour un candidat
     */
    public Test demarrerTest(String code) throws Exception {
        Candidat candidat = candidatDAO.findByCode(code);

        if (candidat == null) {
            throw new Exception("Code invalide");
        }

        // Vérifier si le candidat a déjà un test en cours
        List<Test> tests = testDAO.findByCandidat(candidat);
        for (Test t : tests) {
            if (t.getStatut() == Test.StatutTest.EN_COURS) {
                return t; // Retourner le test en cours
            }
        }

        // Créer un nouveau test
        Test test = new Test();
        test.setCandidat(candidat);
        test.setCreneau(candidat.getCreneau());
        test.setDureeConfiguree(configService.getDureeExamen());
        test.setStatut(Test.StatutTest.EN_COURS);

        testDAO.create(test);

        return test;
    }

    /**
     * Génère les questions pour le test (réparties équitablement par thème)
     */
    public List<Question> genererQuestions() {
        List<Theme> themes = themeDAO.findThemesActifs();
        int questionsParTheme = configService.getNombreQuestionsParTheme();

        List<Question> questionsTest = new ArrayList<>();

        for (Theme theme : themes) {
            List<Question> questionsTheme = questionDAO.findRandomByTheme(
                    theme.getId(), questionsParTheme);
            questionsTest.addAll(questionsTheme);
        }

        // Mélanger les questions pour un ordre aléatoire
        Collections.shuffle(questionsTest);

        return questionsTest;
    }

    /**
     * Enregistre une réponse du candidat
     */
    public void enregistrerReponse(Test test, Question question, List<Long> reponseIds) {
        // Créer une réponse candidat
        ReponseCandidat reponseCandidat = new ReponseCandidat();
        reponseCandidat.setTest(test);
        reponseCandidat.setQuestion(question);

        if (question.getType() == Question.TypeQuestion.CHOIX_UNIQUE) {
            // Pour choix unique, on stocke la réponse sélectionnée
            if (!reponseIds.isEmpty()) {
                ReponseDAO reponseDAO = new ReponseDAO();
                Reponse reponse = reponseDAO.findById(reponseIds.get(0));
                reponseCandidat.setReponse(reponse);
                reponseCandidat.setCorrecte(reponse.getCorrecte());
            }
        } else {
            // Pour choix multiple, on stocke la liste des IDs
            reponseCandidat.setReponsesMultiplesList(reponseIds);

            // Vérifier si toutes les réponses sont correctes
            List<Reponse> reponsesCorrectes = question.getReponsesCorrectes();
            List<Long> idsCorrects = new ArrayList<>();
            for (Reponse r : reponsesCorrectes) {
                idsCorrects.add(r.getId());
            }

            // La réponse est correcte si les deux listes sont égales
            Collections.sort(reponseIds);
            Collections.sort(idsCorrects);
            reponseCandidat.setCorrecte(reponseIds.equals(idsCorrects));
        }

        test.getReponsesCandidat().add(reponseCandidat);
    }

    /**
     * Termine un test
     */
    public void terminerTest(Test test) {
        test.terminer();
        testDAO.update(test);
    }

    /**
     * Récupère un test par son ID
     */
    public Test getTest(Long id) {
        return testDAO.findById(id);
    }

    /**
     * Calcule le temps restant pour un test
     */
    public long getTempsRestant(Test test) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime fin = test.getDateDebut().plusMinutes(test.getDureeConfiguree());

        long secondes = java.time.Duration.between(maintenant, fin).getSeconds();
        return Math.max(0, secondes);
    }
}
