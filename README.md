# 🎓 ISGA Management - Plateforme de Tests en Ligne

**ISGA Management** est une application web Java JEE complète permettant la gestion et le passage de tests en ligne (QCM). Elle offre une interface moderne pour les candidats et un panel d'administration puissant pour les gestionnaires.

![ISGA Management](https://img.shields.io/badge/Status-Production-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Payara](https://img.shields.io/badge/Server-Payara_Micro-blue)

---

## Vidéo du projet


Voici une démonstration de notre projet :



https://github.com/user-attachments/assets/81e27d4c-30da-4d25-906b-7744abf32e97

---

## 🚀 Fonctionnalités Clés

### 👨‍🎓 Espace Candidat
- **Design Moderne** : Interface fluide et responsive (Glassmorphism).
- **Passage d'Examen** :
    - Navigation libre entre les questions (Barre de progression interactive).
    - Timer temps réel avec auto-soumission.
    - Support des questions à choix unique et multiple.
- **Résultats Immédiats** :
    - Score calculé automatiquement.
    - Génération PDF du **Certificat de Réussite**.
    - Envoi automatique par Email.

### ⚙️ Espace Administrateur
- **Dashboard** : Statistiques en temps réel.
- **Gestion des Questions** : CRUD complet avec gestion dynamique des réponses.
- **Gestion des Créneaux** : Planification des sessions d'examen.
- **Suivi des Résultats** : Recherche, filtre et export des notes.

---

## 🛠️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir :
- **Java JDK 11 ou 17**
- **Maven** (installé et configuré dans le PATH)
- **MySQL** (via XAMPP ou WAMP recommandé)
- **Git**

---

## 📥 Installation & Démarrage Rapide

### 1. Cloner le projet
```bash
git clone https://github.com/Maadalla/Java_projet.git
cd Java_projet
```

### 2. Configuration de la Base de Données
Assurez-vous que votre serveur MySQL est lancé (XAMPP/WAMP en vert ✅).

Lancez simplement le script automatique (double-clic) :
- **`init-data.bat`** : Crée la base de données, les tables et insère les données de test.

> **Note** : Le script détecte automatiquement votre installation MySQL (XAMPP/WAMP). Si une erreur survient, vérifiez que MySQL tourne bien sur le port 3306.

### 3. Lancer l'Application
Lancez le script de démarrage :
- **`start.bat`** : Compile le projet et lance le serveur Payara Micro.

Une fois que vous voyez `Deployed 1/1` dans la console, l'application est prête !

---

## 🔗 Accès à l'Application

### 🌍 Site Public (Candidats)
- **URL** : `http://localhost:8080/`
- **Code de Session de Test** : `TEST-2025`

### 🔒 Administration
- **URL** : `http://localhost:8080/admin/login.xhtml`
- **Identifiants Démo** :
    - **Login** : `admin`
    - **Password** : `admin123`

---

## 📂 Structure du Projet

- `src/main/java` : Code source Java (Entités, Services, Beans).
- `src/main/webapp` : Vues JSF (XHTML), CSS, Images.
- `src/main/resources` : Configuration (persistence.xml).
- `init-data.bat` : Script d'initialisation BDD.
- `start.bat` : Script de lancement serveur.

---


## 📝 Auteurs

- Mohamed Maadalla
- Driss Bouajaja
- Hasnaoui Douaa
- Zirari Nada
- Saad Ouazzani Taibi

**ISGA Management** v1.0
