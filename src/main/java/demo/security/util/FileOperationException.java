package demo.security.util;

/**
 * Custom exception for file operation security violations.
 * Thrown when file operations fail due to security constraints.
 */
public class FileOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
