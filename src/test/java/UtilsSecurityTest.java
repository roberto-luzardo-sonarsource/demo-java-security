import demo.security.util.EncryptionException;
import demo.security.util.Utils;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UtilsSecurityTest {

    @Test
    void testGenerateKey_Uses2048() {
        KeyPair kp = Utils.generateKey();
        assertNotNull(kp.getPublic());
        assertTrue(kp.getPublic().getEncoded().length > 200, "Public key size appears too small");
    }

    @Test
    void testEncryptRejectsBadKey() {
        byte[] badKey = new byte[5];
        assertThrows(IllegalArgumentException.class, () -> {
            Utils.encrypt(badKey, "hello".getBytes());
        });
    }

    @Test
    void testEncryptProducesCiphertext() throws EncryptionException {
        byte[] key = new byte[16];
        byte[] ct1 = Utils.encrypt(key, "hello".getBytes());
        byte[] ct2 = Utils.encrypt(key, "hello".getBytes());
        assertNotNull(ct1);
        assertNotNull(ct2);
        assertFalse(Arrays.equals(ct1, ct2), "Ciphertexts should differ due to random nonce");
    }

    @Test
    void testEncryptRejectsNullPlaintext() {
        byte[] key = new byte[16];
        assertThrows(IllegalArgumentException.class, () -> Utils.encrypt(key, null));
    }

    @Test
    void testEncryptWith24ByteKey() throws EncryptionException {
        byte[] key = new byte[24];
        byte[] ciphertext = Utils.encrypt(key, "test".getBytes());
        assertNotNull(ciphertext);
        assertTrue(ciphertext.length > 0, "Ciphertext should be non-empty");
    }

    @Test
    void testEncryptWith32ByteKey() throws EncryptionException {
        byte[] key = new byte[32];
        byte[] ciphertext = Utils.encrypt(key, "test".getBytes());
        assertNotNull(ciphertext);
        assertTrue(ciphertext.length > 0, "Ciphertext should be non-empty");
    }
}