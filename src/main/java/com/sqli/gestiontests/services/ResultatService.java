package com.sqli.gestiontests.services;

import com.sqli.gestiontests.dao.ResultatDAO;
import com.sqli.gestiontests.entities.*;
import com.sqli.gestiontests.utils.EmailSender;

import javax.mail.MessagingException;
import java.util.List;

public class ResultatService {

    private final ResultatDAO resultatDAO;

    public ResultatService() {
        this.resultatDAO = new ResultatDAO();
    }

    /**
     * Calcule et enregistre le résultat d'un test
     */
    public Resultat calculerResultat(Test test) {
        List<ReponseCandidat> reponsesCandidat = test.getReponsesCandidat();

        int score = 0;
        int totalQuestions = reponsesCandidat.size();

        for (ReponseCandidat rc : reponsesCandidat) {
            if (rc.getCorrecte()) {
                score++;
            }
        }

        Resultat resultat = getResultatParTest(test);

        if (resultat == null) {
            resultat = new Resultat();
            resultat.setTest(test);
            resultat.setScore(score);
            resultat.setTotalQuestions(totalQuestions);
            resultat.calculerNote();
            resultatDAO.create(resultat);
        } else {
            resultat.setScore(score);
            resultat.setTotalQuestions(totalQuestions);
            resultat.calculerNote();
            // Reset Date Calcul if needed or keep original? Update implies new calculation.
            // resultat.setDateCalcul(LocalDateTime.now()); // Handled by
            // @PrePersist/Update? No, manually update if needed
            resultatDAO.update(resultat);
        }

        // Envoyer le résultat par email
        try {
            // Only send if not already sent ? Or resend?
            // Let's keep existing logic but just update flag
            envoyerEmailResultat(resultat);
            resultat.setEmailEnvoye(true);
            resultatDAO.update(resultat);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de résultat: " + e.getMessage());
        }

        return resultat;
    }

    /**
     * Recherche des résultats
     */
    public List<Resultat> rechercherResultats(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            return resultatDAO.findAll();
        }
        return resultatDAO.searchResultats(terme);
    }

    /**
     * Récupère le résultat d'un test
     */
    public Resultat getResultatParTest(Test test) {
        return resultatDAO.findByTest(test);
    }

    /**
     * Envoie l'email de résultat
     */
    private void envoyerEmailResultat(Resultat resultat) throws MessagingException {
        Candidat candidat = resultat.getTest().getCandidat();

        String htmlContent = EmailSender.generateResultatEmail(
                candidat.getNom(),
                candidat.getPrenom(),
                resultat.getNote(),
                resultat.getScore(),
                resultat.getTotalQuestions(),
                resultat.getMention());

        EmailSender.sendEmail(candidat.getEmail(), "Résultats de votre test", htmlContent);
    }
}
