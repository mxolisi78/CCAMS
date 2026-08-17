package ccams.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    /**
     * Generate SHA-256 hash.
     */
    public static String hashPassword(String password) {

        if (password == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(
                password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexString = new StringBuilder(64);

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                "SHA-256 algorithm not available", e
            );
        }
    }

    /**
     * Verify plain password against stored SHA-256 hash.
     */
    public static boolean verifyPassword(
            String plainPassword,
            String storedHash) {

        if (plainPassword == null || storedHash == null) {
            return false;
        }

        String hashedPassword = hashPassword(plainPassword);

        return hashedPassword.equalsIgnoreCase(storedHash.trim());
    }

    /**
     * Check whether a value looks like a SHA-256 hash.
     */
    public static boolean isHashed(String password) {

        return password != null
                && password.matches("^[a-fA-F0-9]{64}$");
    }
}