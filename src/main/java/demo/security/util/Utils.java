package demo.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Utils {

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
        // Prevent deletion of directories and restrict to canonical path inside temp/app-uploads
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "app-uploads");
        File target = new File(baseDir, fileName);
        String baseCanonical = baseDir.getCanonicalPath();
        String targetCanonical = target.getCanonicalPath();
        if (!targetCanonical.startsWith(baseCanonical + File.separator)) {
            throw new SecurityException("Attempted path escape");
        }
        if (target.isDirectory()) {
            throw new SecurityException("Refusing to delete directory");
        }
        if (!target.exists()) {
            throw new IOException("File does not exist: " + fileName);
        }
        FileUtils.forceDelete(target);
    }

    public static void executeJs(String input) throws ScriptException {
        // Reject null/empty input early
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Script input cannot be empty");
        }
        // Basic allow-list: only permit simple console.log statements for demo (very restrictive)
        String trimmed = input.trim();
        if (!trimmed.startsWith("console.log(") || !trimmed.endsWith(")")) {
            throw new SecurityException("Dynamic execution blocked");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(trimmed);
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
}
