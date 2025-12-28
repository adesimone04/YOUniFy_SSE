package it.younify.config;

import java.security.SecureRandom;

public class TwoFactorUtility {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateCode() {

        int number = SECURE_RANDOM.nextInt(1000000);
        return String.format("%06d", number);
    }
}
