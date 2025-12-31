package it.younify.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TokenHashUtility {

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