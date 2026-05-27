package demo.security.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Demo class that was introduced with intentional SonarQube findings and later cleaned up.
 */
public class SonarIssueDemo {

    private static final Logger LOGGER = Logger.getLogger(SonarIssueDemo.class.getName());
    private static final String DEMO_CONSTANT = "demo";
    private static final String TEST_VALUE = "test";

    public static String getDemoConstant() {
        return DEMO_CONSTANT;
    }

    public int calculate(int a, int b) {
        if (a == b) {
            return a + b;
        }
        if (a > b && a > 100 && b > 50) {
            return a * b;
        }
        LOGGER.fine("calculate using default path");
        return 0;
    }

    public boolean compareStrings(String value) {
        return TEST_VALUE.equals(value);
    }

    public void emptyCatch(String filePath) {
        try {
            riskyOperation(filePath);
        } catch (IOException e) {
            LOGGER.warning("Operation failed: " + e.getMessage());
        }
    }

    public void riskyOperation(String filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            inputStream.read();
        }
    }

    public void logMessage(String message) {
        LOGGER.info(message);
    }

    public List<String> createList() {
        List<String> items = new ArrayList<>();
        items.add("item");
        return items;
    }
}
