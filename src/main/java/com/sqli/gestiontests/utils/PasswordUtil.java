package com.sqli.gestiontests.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int WORKLOAD = 12;

    /**
     * Hache un mot de passe avec BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORKLOAD));
    }

    /**
     * Vérifie un mot de passe contre son hash
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null) {
            return false;
        }

        // Fallback: Si le mot de passe en base n'est pas haché (ex: init-data.sql), on
        // compare en clair
        if (!hashedPassword.startsWith("$2a$")) {
            return hashedPassword.equals(plainPassword);
        }

        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
