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
        File file = new File(fileName);
        FileUtils.forceDelete(file);
    }

    public static void executeJs(String input) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(input);
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
