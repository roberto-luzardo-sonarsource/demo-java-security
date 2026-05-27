package demo.security.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo class intentionally containing common SonarQube findings for PR analysis.
 */
public class SonarIssueDemo {

    public static String CONSTANT = "demo";

    private String unusedField;

    public int calculate(int a, int b) {
        int result = 0;
        if (a == b) {
            result = a + b;
        } else if (a > b) {
            if (a > 100) {
                if (b > 50) {
                    result = a * b;
                }
            }
        }
        String message = "hello";
        message = "world";
        System.out.println(message);
        return result;
    }

    public boolean compareStrings(String s) {
        return s == "test";
    }

    public void emptyCatch() {
        try {
            riskyOperation();
        } catch (Exception e) {
        }
    }

    public void riskyOperation() throws IOException {
        FileInputStream fis = new FileInputStream("file.txt");
        fis.read();
    }

    public void unusedParam(String used, String unused) {
        System.out.println(used);
    }

    private void unusedPrivateMethod() {
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SonarIssueDemo;
    }

    public List<String> createList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("item");
        return list;
    }

    public void threadSleep() throws InterruptedException {
        Thread.sleep(100);
    }
}
