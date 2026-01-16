package com.sqli.gestiontests.services;

import com.sqli.gestiontests.dao.AdministrateurDAO;
import com.sqli.gestiontests.entities.Administrateur;
import com.sqli.gestiontests.utils.PasswordUtil;

public class AdminService {

    private final AdministrateurDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdministrateurDAO();
    }

    /**
     * Authentifie un administrateur
     */
    public Administrateur authentifier(String login, String password) {
        System.out.println("=== DEBUG AUTH ===");
        System.out.println("Login reçu: " + login);
        System.out.println("Password reçu: " + password);

        Administrateur admin = adminDAO.findByLogin(login);

        if (admin == null) {
            System.out.println("Admin NOT FOUND pour login: " + login);
            return null;
        }

        System.out.println("Admin trouvé - ID: " + admin.getId());
        System.out.println("Password en base: " + admin.getPassword());
        System.out.println("Est BCrypt? " + admin.getPassword().startsWith("$2a$"));

        boolean passwordMatch = PasswordUtil.checkPassword(password, admin.getPassword());
        System.out.println("Password match? " + passwordMatch);

        if (passwordMatch) {
            System.out.println("AUTH SUCCESS!");
            return admin;
        }

        System.out.println("AUTH FAILED - Wrong password");
        return null;
    }

    /**
     * Crée un nouvel administrateur
     */
    public void creerAdmin(Administrateur admin) {
        // Hasher le mot de passe
        String hashedPassword = PasswordUtil.hashPassword(admin.getPassword());
        admin.setPassword(hashedPassword);

        adminDAO.create(admin);
    }

    /**
     * Met à jour le mot de passe d'un admin
     */
    public void updatePassword(Long adminId, String newPassword) throws Exception {
        Administrateur admin = adminDAO.findById(adminId);
        if (admin == null) {
            throw new Exception("Administrateur introuvable");
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        admin.setPassword(hashedPassword);
        adminDAO.update(admin);
    }
}
