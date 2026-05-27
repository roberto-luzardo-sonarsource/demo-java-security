import demo.security.util.SonarIssueDemo;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SonarIssueDemoTest {

  @Test
  public void getDemoConstant_returnsExpectedValue() {
    assertEquals("demo", SonarIssueDemo.getDemoConstant());
  }

  @Test
  public void calculate_whenEqual_returnsSum() {
    SonarIssueDemo demo = new SonarIssueDemo();
    assertEquals(10, demo.calculate(5, 5));
  }

  @Test
  public void calculate_whenLargeValues_returnsProduct() {
    SonarIssueDemo demo = new SonarIssueDemo();
    assertEquals(12000, demo.calculate(150, 80));
  }

  @Test
  public void calculate_whenNoMatch_returnsZero() {
    SonarIssueDemo demo = new SonarIssueDemo();
    assertEquals(0, demo.calculate(10, 20));
  }

  @Test
  public void compareStrings_matchesTestValue() {
    SonarIssueDemo demo = new SonarIssueDemo();
    assertTrue(demo.compareStrings("test"));
    assertFalse(demo.compareStrings("other"));
  }

  @Test
  public void createList_returnsSingleItem() {
    SonarIssueDemo demo = new SonarIssueDemo();
    List<String> items = demo.createList();
    assertEquals(1, items.size());
    assertEquals("item", items.get(0));
  }

  @Test
  public void logMessage_doesNotThrow() {
    SonarIssueDemo demo = new SonarIssueDemo();
    demo.logMessage("coverage");
    assertEquals("demo", SonarIssueDemo.getDemoConstant());
  }

  @Test
  public void riskyOperation_readsFromFile() throws IOException {
    File tempFile = File.createTempFile("sonar-demo", ".txt");
    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
      outputStream.write('x');
    }

    new SonarIssueDemo().riskyOperation(tempFile.getAbsolutePath());
    assertTrue(tempFile.exists());
    assertTrue(tempFile.delete());
  }

  @Test
  public void emptyCatch_handlesMissingFile() {
    String missingPath = "missing-file-" + System.nanoTime() + ".txt";
    new SonarIssueDemo().emptyCatch(missingPath);
    assertFalse(new File(missingPath).exists());
  }
}
