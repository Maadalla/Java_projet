package com.sqli.gestiontests;

import com.sqli.gestiontests.dao.*;
import com.sqli.gestiontests.entities.*;
import com.sqli.gestiontests.utils.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe pour initialiser la base de données avec des données de test
 * Exécuter cette classe pour peupler la base de données
 */
public class DataInitializer {

    public static void main(String[] args) {
        System.out.println("Initialisation de la base de données...");

        try {
            // Créer un administrateur par défaut
            creerAdmin();

            // Créer des thèmes
            creerThemes();

            // Créer des questions
            creerQuestions();

            // Créer des créneaux
            creerCreneaux();

            // Initialiser la configuration
            initialiserConfiguration();

            System.out.println("Base de données initialisée avec succès!");
            System.out.println("\n=== INFORMATIONS DE CONNEXION ADMIN ===");
            System.out.println("Login: admin");
            System.out.println("Password: admin123");
            System.out.println("=======================================\n");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void creerAdmin() {
        AdministrateurDAO adminDAO = new AdministrateurDAO();

        // Vérifier si un admin existe déjà
        if (adminDAO.findByLogin("admin") != null) {
            System.out.println("Admin déjà existant");
            return;
        }

        Administrateur admin = new Administrateur();
        admin.setLogin("admin");
        admin.setPassword(PasswordUtil.hashPassword("admin123"));
        admin.setEmail("admin@gestiontests.com");
        admin.setActif(true);

        adminDAO.create(admin);
        System.out.println("✓ Administrateur créé");
    }

    private static void creerThemes() {
        ThemeDAO themeDAO = new ThemeDAO();

        String[] themes = { "Java", "Programmation", "Base de données", "Web", "Algorithmique" };

        for (String nomTheme : themes) {
            if (themeDAO.findByNom(nomTheme) == null) {
                Theme theme = new Theme();
                theme.setNom(nomTheme);
                theme.setDescription("Questions sur " + nomTheme);
                theme.setActif(true);
                themeDAO.create(theme);
            }
        }

        System.out.println("✓ Thèmes créés");
    }

    private static void creerQuestions() {
        ThemeDAO themeDAO = new ThemeDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        ReponseDAO reponseDAO = new ReponseDAO();

        Theme themeJava = themeDAO.findByNom("Java");
        Theme themeProg = themeDAO.findByNom("Programmation");

        // Question Java 1
        Question q1 = new Question();
        q1.setTexte("Quel est le mot-clé pour définir une classe en Java?");
        q1.setType(Question.TypeQuestion.CHOIX_UNIQUE);
        q1.setTheme(themeJava);
        q1.setActif(true);
        questionDAO.create(q1);

        creerReponse(reponseDAO, q1, "class", true);
        creerReponse(reponseDAO, q1, "Class", false);
        creerReponse(reponseDAO, q1, "define", false);
        creerReponse(reponseDAO, q1, "object", false);

        // Question Java 2
        Question q2 = new Question();
        q2.setTexte("Quels sont les modificateurs d'accès en Java?");
        q2.setType(Question.TypeQuestion.CHOIX_MULTIPLE);
        q2.setTheme(themeJava);
        q2.setActif(true);
        questionDAO.create(q2);

        creerReponse(reponseDAO, q2, "public", true);
        creerReponse(reponseDAO, q2, "private", true);
        creerReponse(reponseDAO, q2, "protected", true);
        creerReponse(reponseDAO, q2, "package", false);

        // Question Programmation
        Question q3 = new Question();
        q3.setTexte("Qu'est-ce que la programmation orientée objet?");
        q3.setType(Question.TypeQuestion.CHOIX_UNIQUE);
        q3.setTheme(themeProg);
        q3.setActif(true);
        questionDAO.create(q3);

        creerReponse(reponseDAO, q3, "Un paradigme de programmation basé sur le concept d'objets", true);
        creerReponse(reponseDAO, q3, "Un langage de programmation", false);
        creerReponse(reponseDAO, q3, "Un compilateur", false);
        creerReponse(reponseDAO, q3, "Un système d'exploitation", false);

        System.out.println("✓ Questions créées");
    }

    private static void creerReponse(ReponseDAO reponseDAO, Question question,
            String texte, boolean correcte) {
        Reponse reponse = new Reponse();
        reponse.setTexte(texte);
        reponse.setCorrecte(correcte);
        reponse.setQuestion(question);
        reponseDAO.create(reponse);
    }

    private static void creerCreneaux() {
        CreneauDAO creneauDAO = new CreneauDAO();

        LocalDate today = LocalDate.now();

        for (int i = 1; i <= 5; i++) {
            Creneau creneau = new Creneau();
            creneau.setDate(today.plusDays(i));
            creneau.setHeureDebut(LocalTime.of(9, 0));
            creneau.setDureeExamen(120);
            creneau.setHeureFin(LocalTime.of(11, 0));
            creneau.setCapaciteMax(30);
            creneau.setActif(true);

            creneauDAO.create(creneau);
        }

        System.out.println("✓ Créneaux créés");
    }

    private static void initialiserConfiguration() {
        ConfigurationDAO configDAO = new ConfigurationDAO();

        configDAO.setValeur(Configuration.DUREE_EXAMEN, "120", "Durée de l'examen en minutes");
        configDAO.setValeur(Configuration.NOMBRE_QUESTIONS_PAR_THEME, "5", "Nombre de questions par thème");
        configDAO.setValeur(Configuration.TEMPS_PAR_QUESTION, "120", "Temps par question en secondes");
        configDAO.setValeur(Configuration.VALIDATION_ADMIN_REQUISE, "false",
                "Validation des inscriptions par l'administrateur");

        System.out.println("✓ Configuration initialisée");
    }
}
