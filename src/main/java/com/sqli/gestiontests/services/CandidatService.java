package com.sqli.gestiontests.services;

import com.sqli.gestiontests.dao.CandidatDAO;
import com.sqli.gestiontests.dao.CreneauDAO;
import com.sqli.gestiontests.entities.Candidat;
import com.sqli.gestiontests.entities.Creneau;
import com.sqli.gestiontests.utils.CodeGenerator;
import com.sqli.gestiontests.utils.EmailSender;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CandidatService {

    private final CandidatDAO candidatDAO;
    private final CreneauDAO creneauDAO;
    private final ConfigurationService configService;

    public CandidatService() {
        this.candidatDAO = new CandidatDAO();
        this.creneauDAO = new CreneauDAO();
        this.configService = new ConfigurationService();
    }

    /**
     * Inscrit un nouveau candidat
     */
    public Candidat inscrireCandidat(Candidat candidat, Long creneauId) throws Exception {
        // Vérifier si l'email existe déjà
        if (candidatDAO.findByEmail(candidat.getEmail()) != null) {
            throw new Exception("Un candidat avec cet email existe déjà");
        }

        // Récupérer le créneau
        Creneau creneau = creneauDAO.findById(creneauId);
        if (creneau == null) {
            throw new Exception("Créneau invalide");
        }

        if (!creneau.isDisponible()) {
            throw new Exception("Ce créneau n'est plus disponible");
        }

        // Générer un code unique
        Set<String> existingCodes = new HashSet<>();
        candidatDAO.findAll().forEach(c -> existingCodes.add(c.getCode()));
        String code = CodeGenerator.generateUniqueCode(existingCodes);

        candidat.setCode(code);
        candidat.setCreneau(creneau);

        // Vérifier si validation admin requise
        boolean validationRequise = configService.isValidationAdminRequise();
        if (validationRequise) {
            candidat.setStatut(Candidat.StatutInscription.EN_ATTENTE);
        } else {
            candidat.setStatut(Candidat.StatutInscription.VALIDEE);
        }

        // Sauvegarder le candidat
        candidatDAO.create(candidat);

        // Envoyer l'email de confirmation
        try {
            envoyerEmailInscription(candidat, validationRequise);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }

        return candidat;
    }

    /**
     * Valide l'inscription d'un candidat
     */
    public void validerInscription(Long candidatId) throws Exception {
        Candidat candidat = candidatDAO.findById(candidatId);
        if (candidat == null) {
            throw new Exception("Candidat introuvable");
        }

        candidat.setStatut(Candidat.StatutInscription.VALIDEE);
        candidatDAO.update(candidat);

        // Envoyer email de confirmation avec le code
        try {
            envoyerEmailInscription(candidat, false);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    /**
     * Rejette l'inscription d'un candidat
     */
    public void rejeterInscription(Long candidatId) throws Exception {
        Candidat candidat = candidatDAO.findById(candidatId);
        if (candidat == null) {
            throw new Exception("Candidat introuvable");
        }

        candidat.setStatut(Candidat.StatutInscription.REJETEE);
        candidatDAO.update(candidat);
    }

    /**
     * Trouve un candidat par son code
     */
    public Candidat trouverParCode(String code) {
        return candidatDAO.findByCode(code);
    }

    /**
     * Recherche des candidats
     */
    public List<Candidat> rechercherCandidats(String terme) {
        return candidatDAO.searchCandidats(terme);
    }

    /**
     * Récupère les candidats en attente de validation
     */
    public List<Candidat> getCandidatsEnAttente() {
        return candidatDAO.findByStatut(Candidat.StatutInscription.EN_ATTENTE);
    }

    /**
     * Envoie l'email d'inscription
     */
    private void envoyerEmailInscription(Candidat candidat, boolean validationRequise)
            throws MessagingException {
        String creneauStr = formatCreneau(candidat.getCreneau());
        String htmlContent = EmailSender.generateInscriptionEmail(
                candidat.getNom(),
                candidat.getPrenom(),
                candidat.getCode(),
                creneauStr,
                validationRequise);

        String subject = validationRequise ? "Inscription en cours de validation"
                : "Confirmation d'inscription - Code de session";

        EmailSender.sendEmail(candidat.getEmail(), subject, htmlContent);
    }

    /**
     * Formate un créneau pour affichage
     */
    private String formatCreneau(Creneau creneau) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return creneau.getDate().format(dateFormatter) +
                " de " + creneau.getHeureDebut() +
                " à " + creneau.getHeureFin();
    }
}
