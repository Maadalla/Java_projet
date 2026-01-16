-- ============================================
-- SCRIPT D'INITIALISATION - SQLI E-CHALLENGE
-- Base de données de test complète
-- ============================================

USE gestion_tests;

-- Nettoyage des données existantes (dans l'ordre des dépendances)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reponses_candidats;
TRUNCATE TABLE resultats;
TRUNCATE TABLE tests;
TRUNCATE TABLE candidats;
TRUNCATE TABLE creneaux;
TRUNCATE TABLE reponses;
TRUNCATE TABLE questions;
TRUNCATE TABLE themes;
TRUNCATE TABLE administrateurs;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. CRÉATION DE L'ADMINISTRATEUR
-- ============================================
INSERT INTO administrateurs (actif, email, login, password)
VALUES (true,'admin@sqli.com', 'admin', 'admin123');

-- ============================================
-- 2. CRÉATION DES THÈMES
-- ============================================
INSERT INTO themes (nom, description, actif) VALUES
('Java', 'Programmation Java et concepts orientés objet', true),
('Base de Données', 'SQL, conception et optimisation de bases de données', true),
('Web', 'HTML, CSS, JavaScript et développement web', true),
('Architecture', 'Architecture logicielle et design patterns', true);

-- ============================================
-- 3. CRÉATION DES QUESTIONS ET RÉPONSES
-- ============================================

-- THÈME: JAVA
-- Question 1
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quel est le principe de base de l''encapsulation en Java ?', 'CHOIX_UNIQUE', 1, true);
SET @q1 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Masquer les détails d''implémentation et exposer uniquement une interface publique', true, @q1),
('Utiliser plusieurs classes dans un même fichier', false, @q1),
('Créer des objets sans constructeur', false, @q1),
('Utiliser uniquement des variables statiques', false, @q1);

-- Question 2
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quelle(s) caractéristique(s) définissent une interface en Java ?', 'CHOIX_MULTIPLE', 1, true);
SET @q2 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Tous les méthodes sont abstraites par défaut', true, @q2),
('Une interface peut contenir des méthodes default', true, @q2),
('Une interface peut avoir des constructeurs', false, @q2),
('Les variables sont final et static par défaut', true, @q2);

-- Question 3
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quelle est la différence entre == et equals() en Java ?', 'CHOIX_UNIQUE', 1, true);
SET @q3 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('== compare les références, equals() compare le contenu', true, @q3),
('== compare le contenu, equals() compare les références', false, @q3),
('Il n''y a aucune différence', false, @q3),
('== est plus rapide que equals()', false, @q3);

-- Question 4
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quels sont les principes SOLID ?', 'CHOIX_MULTIPLE', 1, true);
SET @q4 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Single Responsibility Principle', true, @q4),
('Open/Closed Principle', true, @q4),
('Serialization Principle', false, @q4),
('Dependency Inversion Principle', true, @q4);

-- THÈME: BASE DE DONNÉES
-- Question 5
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce qu''une clé primaire dans une base de données ?', 'CHOIX_UNIQUE', 2, true);
SET @q5 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Un identifiant unique pour chaque enregistrement d''une table', true, @q5),
('Une clé pour crypter les données', false, @q5),
('Un index secondaire', false, @q5),
('Une contrainte de non-nullité', false, @q5);

-- Question 6
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quelles sont les propriétés ACID d''une transaction ?', 'CHOIX_MULTIPLE', 2, true);
SET @q6 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Atomicity (Atomicité)', true, @q6),
('Consistency (Cohérence)', true, @q6),
('Isolation (Isolation)', true, @q6),
('Durability (Durabilité)', true, @q6);

-- Question 7
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quelle commande SQL permet de récupérer des données ?', 'CHOIX_UNIQUE', 2, true);
SET @q7 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('SELECT', true, @q7),
('GET', false, @q7),
('FETCH', false, @q7),
('RETRIEVE', false, @q7);

-- Question 8
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce qu''une jointure INNER JOIN ?', 'CHOIX_UNIQUE', 2, true);
SET @q8 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Retourne uniquement les enregistrements qui ont des correspondances dans les deux tables', true, @q8),
('Retourne tous les enregistrements de la table de gauche', false, @q8),
('Retourne tous les enregistrements des deux tables', false, @q8),
('Retourne uniquement les enregistrements uniques', false, @q8);

-- THÈME: WEB
-- Question 9
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quel langage est utilisé pour structurer le contenu d''une page web ?', 'CHOIX_UNIQUE', 3, true);
SET @q9 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('HTML', true, @q9),
('CSS', false, @q9),
('JavaScript', false, @q9),
('PHP', false, @q9);

-- Question 10
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quelles sont les méthodes HTTP les plus courantes ?', 'CHOIX_MULTIPLE', 3, true);
SET @q10 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('GET', true, @q10),
('POST', true, @q10),
('PUT', true, @q10),
('SEND', false, @q10);

-- Question 11
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('À quoi sert CSS ?', 'CHOIX_UNIQUE', 3, true);
SET @q11 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('À styliser et mettre en forme les pages web', true, @q11),
('À créer des bases de données', false, @q11),
('À programmer la logique métier', false, @q11),
('À gérer les serveurs web', false, @q11);

-- Question 12
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce que le DOM en JavaScript ?', 'CHOIX_UNIQUE', 3, true);
SET @q12 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Document Object Model - représentation de la structure HTML', true, @q12),
('Data Object Management', false, @q12),
('Dynamic Output Method', false, @q12),
('Database Object Mapping', false, @q12);

-- THÈME: ARCHITECTURE
-- Question 13
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce que le pattern MVC ?', 'CHOIX_UNIQUE', 4, true);
SET @q13 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Model-View-Controller : séparation de la logique, présentation et contrôle', true, @q13),
('Multi-Version Control', false, @q13),
('Modular Virtual Component', false, @q13),
('Master-View-Client', false, @q13);

-- Question 14
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Quels sont les avantages des microservices ?', 'CHOIX_MULTIPLE', 4, true);
SET @q14 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Déploiement indépendant des services', true, @q14),
('Scalabilité fine', true, @q14),
('Technologies hétérogènes possibles', true, @q14),
('Réduit la complexité globale', false, @q14);

-- Question 15
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce que le pattern Singleton ?', 'CHOIX_UNIQUE', 4, true);
SET @q15 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Un pattern qui garantit qu''une classe n''a qu''une seule instance', true, @q15),
('Un pattern pour créer plusieurs objets similaires', false, @q15),
('Un pattern pour la communication entre objets', false, @q15),
('Un pattern de base de données', false, @q15);

-- Question 16
INSERT INTO questions (texte, type, theme_id, actif)
VALUES ('Qu''est-ce que l''injection de dépendances ?', 'CHOIX_UNIQUE', 4, true);
SET @q16 = LAST_INSERT_ID();

INSERT INTO reponses (texte, correcte, question_id) VALUES
('Une technique pour fournir les dépendances d''un objet de l''extérieur', true, @q16),
('Une méthode pour créer des objets', false, @q16),
('Un type de base de données', false, @q16),
('Un pattern de sécurité', false, @q16);

-- ============================================
-- 4. CRÉATION DES CRÉNEAUX
-- ============================================
INSERT INTO creneaux (date, heure_debut, heure_fin, capacite_max, duree_examen, actif) VALUES
-- Créneaux passés (pour tester l'historique)
('2025-12-15', '09:00:00', '10:30:00', 30, 90, true),
('2025-12-15', '14:00:00', '15:30:00', 30, 90, true),

-- Créneaux futurs (pour les inscriptions)
('2026-02-15', '09:00:00', '10:30:00', 30, 90, true),
('2026-02-15', '14:00:00', '15:30:00', 30, 90, true),
('2026-02-20', '10:00:00', '11:30:00', 25, 90, true),
('2026-02-20', '15:00:00', '16:30:00', 25, 90, true),

-- Créneau très proche (pour tester mode démo)
(CURDATE(), '08:00:00', '09:30:00', 20, 90, true);

-- ============================================
-- 5. CANDIDAT DE TEST
-- ============================================
INSERT INTO candidats (nom, prenom, email, gsm, ecole, filiere, code, creneau_id, statut)
VALUES ('Dupont', 'Jean', 'jean.dupont@test.com', '0612345678', 'ENSIAS', 'Génie Logiciel', 'TEST-2025', 7, 'VALIDEE');

-- ============================================
-- RÉSUMÉ DES DONNÉES CRÉÉES
-- ============================================
SELECT '=== RÉSUMÉ ===' AS '';
SELECT COUNT(*) AS 'Administrateurs' FROM administrateurs;
SELECT COUNT(*) AS 'Thèmes' FROM themes;
SELECT COUNT(*) AS 'Questions' FROM questions;
SELECT COUNT(*) AS 'Réponses' FROM reponses;
SELECT COUNT(*) AS 'Créneaux' FROM creneaux;
SELECT COUNT(*) AS 'Candidats' FROM candidats;

SELECT '=== CODE DE TEST ===' AS '';
SELECT CONCAT('Code pour tester: ', code) AS 'Info', CONCAT(nom, ' ', prenom) AS 'Candidat' FROM candidats;

SELECT '=== STATISTIQUES PAR THÈME ===' AS '';
SELECT t.nom AS 'Thème', COUNT(q.id) AS 'Nombre de Questions'
FROM themes t
LEFT JOIN questions q ON t.id = q.theme_id
WHERE t.actif = true
GROUP BY t.nom;
