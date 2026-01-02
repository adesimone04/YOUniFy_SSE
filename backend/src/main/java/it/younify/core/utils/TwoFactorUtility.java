package it.younify.core.utils;

import java.security.SecureRandom;

/**
 * The {@code TwoFactorUtility} class provides a utility method for generating
 * two-factor authentication (2FA) codes.
 */
public class TwoFactorUtility {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a 6-digit 2FA code.
     * The code is a number between 0 and 999,999, padded with leading zeros if necessary.
     *
     * @return A 6-digit string representing the 2FA code.
     */
    public static String generateCode() {

        int number = SECURE_RANDOM.nextInt(1000000);
        return String.format("%06d", number);
    }
}
