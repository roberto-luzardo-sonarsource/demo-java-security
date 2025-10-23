package demo.security.util;

/**
 * Custom exception for encryption/decryption operations.
 * Provides specific exception type instead of generic Exception (Sonar rule S112).
 */
public class EncryptionException extends Exception {
    
    public EncryptionException(String message) {
        super(message);
    }
    
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
