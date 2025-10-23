package demo.security.util;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

public class Utils {

    // Restrictive filename pattern: letters, digits, dot, underscore, dash; 1-100 chars
    private static final Pattern SAFE_FILENAME = Pattern.compile("^[A-Za-z0-9._-]{1,100}$");

    public static KeyPair generateKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // Use a modern key size
            keyPairGen.initialize(2048);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA algorithm not available", e);
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        validateFilename(fileName);
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "app-uploads");
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IOException("Unable to create base directory");
        }
        File target = new File(baseDir, fileName);
        ensureWithinBase(baseDir, target);
        if (target.isDirectory()) {
            throw new FileOperationException("Refusing to delete directory");
        }
        if (!target.exists()) {
            throw new IOException("File does not exist: " + fileName);
        }
        FileUtils.forceDelete(target);
    }

    public static void executeJs(String input) {
        // Further lockdown: no dynamic execution. Only accept console.log and treat as a log line.
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Script input cannot be empty");
        }
        String trimmed = input.trim();
        if (!trimmed.startsWith("console.log(") || !trimmed.endsWith(")")) {
            throw new FileOperationException("Dynamic execution blocked");
        }
        // Log safely without including user-controlled data directly
        java.util.logging.Logger.getLogger(Utils.class.getName())
            .info(() -> "Script execution attempted (blocked for security)");
    }

    public static byte[] encrypt(byte[] key, byte[] ptxt) throws Exception {
        if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
            throw new IllegalArgumentException("AES key must be 16, 24, or 32 bytes");
        }
        if (ptxt == null) {
            throw new IllegalArgumentException("Plaintext must not be null");
        }
        // Generate a fresh 12 byte nonce for every encryption as recommended for GCM
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        return cipher.doFinal(ptxt);
    }

    // ---------------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------------
    private static void validateFilename(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileOperationException("Filename is blank");
        }
        // Reject path separators or null bytes explicitly
        if (fileName.contains("/") || fileName.contains("\\") || fileName.indexOf('\0') >= 0) {
            throw new FileOperationException("Illegal characters in filename");
        }
        if (!SAFE_FILENAME.matcher(fileName).matches()) {
            throw new FileOperationException("Filename fails whitelist pattern");
        }
    }

    private static void ensureWithinBase(File baseDir, File target) throws IOException {
        String baseCanonical = baseDir.getCanonicalPath();
        String targetCanonical = target.getCanonicalPath();
        if (!targetCanonical.startsWith(baseCanonical + File.separator)) {
            throw new FileOperationException("Attempted path escape");
        }
    }

    // Expose safe filename check for other classes (e.g., servlets)
    public static boolean isSafeFilename(String fileName) {
        try {
            validateFilename(fileName);
            return true;
        } catch (FileOperationException e) {
            return false;
        }
    }
}
