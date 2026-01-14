# Gestion de Tests en Ligne - Application JavaFaces

Application web de gestion de tests en ligne développée avec JavaServer Faces (JSF), PrimeFaces et JPA.

## Fonctionnalités

### Module Candidat
- Inscription en ligne avec sélection de créneau
- Génération automatique de code de session
- Email de confirmation
- Passage de test en ligne avec timer
- Questions aléatoires réparties par thème
- Deux types de questions: choix unique et choix multiple
- Affichage et envoi automatique des résultats par email

### Module Test
- Vérification du créneau horaire
- Timer automatique avec soumission auto au temps écoulé
- Navigation entre les questions
- Sauvegarde automatique des réponses

### Module Administration
- Authentification sécurisée (BCrypt)
- Gestion des thèmes et questions
- Gestion des créneaux d'examen
- Visualisation et recherche des résultats
- Configuration de l'application
- Validation des inscriptions (optionnel)

## Prérequis

- JDK 11 ou supérieur
- Maven 3.6+
- (Optionnel) MySQL ou PostgreSQL pour la production

## Installation

### 1. Cloner le projet

```bash
cd Gestion_Candidat_java
```

### 2. Configuration de la base de données

Par défaut, le projet utilise H2 (base de données embarquée). Pour utiliser MySQL:

1. Créer une base de données:
```sql
CREATE DATABASE gestion_tests;
```

2. Modifier `src/main/resources/META-INF/persistence.xml`:
   - Décommenter les lignes MySQL
   -Commenter les lignes H2
   - Mettre à jour les identifiants

### 3. Configuration de l'email

Modifier `src/main/resources/application.properties`:

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.username=votre-email@gmail.com
mail.password=votre-mot-de-passe-application
```

**Note**: Pour Gmail, utilisez un [mot de passe d'application](https://support.google.com/accounts/answer/185833).

### 4. Initialiser la base de données

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.sqli.gestiontests.DataInitializer"
```

Cette commande va créer:
- Un administrateur (login: `admin`, password: `admin123`)
- Des thèmes de questions
- Des questions d'exemple
- Des créneaux d'examen
- La configuration par défaut

### 5. Compiler et déployer

```bash
mvn clean package payara-micro:start
```

L'application sera accessible sur: **http://localhost:8080/gestion-tests/**

## Utilisation

### Candidat

1. **S'inscrire**: Aller sur la page d'accueil et cliquer sur "S'inscrire"
2. **Remplir le formulaire**: Nom, prénom, école, email, GSM, sélectionner un créneau
3. **Noter le code**: Un code unique sera généré (ex: ABCD-1234)
4. **Passer le test**: Utiliser le code pour accéder au test
5. **Voir les résultats**: Les résultats sont affichés et envoyés par email

### Administrateur

1. **Se connecter**: http://localhost:8080/gestion-tests/admin/login.xhtml
   - Login: `admin`
   - Password: `admin123`

2. **Gérer les questions**:
   - Créer des thèmes
   - Ajouter des questions (choix unique ou multiple)
   - Ajouter des réponses et marquer les bonnes réponses

3. **Gérer les créneaux**:
   - Créer des créneaux avec date, heure et capacité

4. **Voir les résultats**:
   - Rechercher par nom, prénom, école ou code
   - Voir les détails de chaque test

5. **Configuration**:
   - Durée de l'examen
   - Nombre de questions par thème
   - Temps par question
   - Validation administrative des inscriptions

## Structure du Projet

```
src/main/java/com/sqli/gestiontests/
├── entities/          # Entités JPA
├── dao/               # Data Access Objects
├── services/          # Services métier
├── beans/             # Managed Beans JSF
├── filters/           # Filtres (auth, encoding)
└── utils/             # Utilitaires (email, password, code)

src/main/webapp/
├── admin/             # Pages d'administration
├── resources/         # CSS, JS, images
├── WEB-INF/           # Configuration
├── index.xhtml        # Page d'accueil
├── inscription.xhtml  # Formulaire d'inscription
├── test.xhtml         # Interface de test
└── resultat.xhtml     # Affichage des résultats
```

## Technologies Utilisées

- **JavaServer Faces (JSF) 2.3** - Framework web
- **PrimeFaces 12** - Composants UI riches
- **JPA / Hibernate** - Persistance des données
- **H2 / MySQL** - Base de données
- **JavaMail API** - Envoi d'emails
- **BCrypt** - Hashage des mots de passe
- **Maven** - Gestion des dépendances
- **Payara Micro** - Serveur d'application

## Personnalisation

### Changer les thèmes de questions

Modifier la classe `DataInitializer.java` ou utiliser l'interface d'administration.

### Modifier la durée des tests

Dans l'administration, aller dans "Configuration".

### Changer le thème PrimeFaces

Dans `web.xml`, modifier:
```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>nova-light</param-value>
</context-param>
```

Thèmes disponibles: saga, vela, nova, bootstrap, etc.

## Dépannage

### Erreur de connexion à la base de données
- Vérifier que la base de données est démarrée
- Vérifier les identifiants dans `persistence.xml`

### Les emails ne sont pas envoyés
- Vérifier la configuration SMTP dans `application.properties`
- Pour Gmail, activer l'accès des applications moins sécurisées ou utiliser un mot de passe d'application

### Port 8080 déjà utilisé
Modifier le port dans la commande de démarrage:
```bash
mvn payara-micro:start -Dpayara.micro.httpPort=9090
```

## Licence

Ce projet est développé dans le cadre d'un concours E-Challenge SQLI.

## Support

Pour toute question ou problème, contactez l'administrateur du système.
