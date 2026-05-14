import demo.security.util.AsymmetricEncryptionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AsymmetricEncryptionUtilTest {
    private AsymmetricEncryptionUtil util;

    @BeforeEach
    void setUp() {
        util = new AsymmetricEncryptionUtil();
    }

    @Test
    void testGenerateKeys() {
        assertDoesNotThrow(() -> util.generateKeys(2048));
    }

    @Test
    void testEncryptDecrypt() throws Exception {
        util.generateKeys(2048);
        String plaintext = "Hello, World!";
        String encrypted = util.encrypt(plaintext);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);

        String decrypted = util.decrypt(encrypted);
        assertNotNull(decrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptWithoutKeyPair() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            util.encrypt("Hello, World!");
        });
        assertEquals("Key pair not generated. Call generateKeys() first.", exception.getMessage());
    }

    @Test
    void testDecryptWithoutKeyPair() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            util.decrypt("someEncryptedText");
        });
        assertEquals("Key pair not generated. Call generateKeys() first.", exception.getMessage());
    }
}