package demo.security.util;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Utils {

    private Utils() {
    }

    private static final Path SAFE_UPLOAD_BASE = Paths.get("/var/data/uploads").normalize();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static KeyPair generateKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        Path resolved = SAFE_UPLOAD_BASE.resolve(fileName).normalize();
        if (!resolved.startsWith(SAFE_UPLOAD_BASE)) {
            throw new SecurityException("Path traversal attempt blocked");
        }
        FileUtils.forceDelete(resolved.toFile());
    }

    public static void rejectDynamicScriptExecution(String input) throws ScriptException {
        if (input != null && !input.isEmpty()) {
            throw new ScriptException("Dynamic script execution is not permitted");
        }
    }

    public static byte[] encrypt(byte[] key, byte[] plaintext) throws GeneralSecurityException {
        byte[] nonce = new byte[12];
        SECURE_RANDOM.nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        return cipher.doFinal(plaintext);
    }
}
