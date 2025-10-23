import demo.security.util.Utils;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UtilsSecurityTest {

    @Test
    public void testGenerateKey_Uses2048() {
        KeyPair kp = Utils.generateKey();
        assertNotNull(kp.getPublic());
        assertTrue("Public key size appears too small", kp.getPublic().getEncoded().length > 200);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptRejectsBadKey() throws Exception {
        byte[] badKey = new byte[5];
        Utils.encrypt(badKey, "hello".getBytes());
    }

    @Test
    public void testEncryptProducesCiphertext() throws Exception {
        byte[] key = new byte[16];
        byte[] ct1 = Utils.encrypt(key, "hello".getBytes());
        byte[] ct2 = Utils.encrypt(key, "hello".getBytes());
        assertNotNull(ct1);
        assertNotNull(ct2);
        assertFalse("Ciphertexts should differ due to random nonce", Arrays.equals(ct1, ct2));
    }
}
