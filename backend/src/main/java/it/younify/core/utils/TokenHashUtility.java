package it.younify.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * The {@code TokenHashUtility} class provides a utility method for hashing tokens.
 * This is used to securely store tokens in the database.
 */
public class TokenHashUtility {

    /**
     * Hashes a raw token using the SHA-256 algorithm and encodes it in Base64.
     *
     * @param rawToken The raw token to be hashed.
     * @return The Base64-encoded SHA-256 hash of the token.
     * @throws RuntimeException if the SHA-256 algorithm is not available, which is a critical error.
     */
    public static String hashToken(String rawToken) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encodedHash);
        }
        catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Critical error: SHA-256 not found", e);
        }
    }
}