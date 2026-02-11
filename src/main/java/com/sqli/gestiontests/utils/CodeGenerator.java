package com.sqli.gestiontests.utils;

import java.security.SecureRandom;
import java.util.Random;

public class CodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();

    /**
     * Génère un code unique alphanumérique
     * Format: XXXX-XXXX (8 caractères)
     */
    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH + 1);

        for (int i = 0; i < CODE_LENGTH; i++) {
            if (i == 4) {
                code.append('-');
            }
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    /**
     * Génère un code unique en vérifiant qu'il n'existe pas déjà
     */
    public static String generateUniqueCode(java.util.Set<String> existingCodes) {
        String code;
        do {
            code = generateCode();
        } while (existingCodes.contains(code));

        return code;
    }
}
