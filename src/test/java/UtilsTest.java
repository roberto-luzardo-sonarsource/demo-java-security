import demo.security.util.Utils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void generateKey_returnsKeyPair() {
        assertNotNull(Utils.generateKey());
    }

    @Test
    public void executeJs_rejectsUserControlledScript() {
        assertThrows(SecurityException.class, () -> Utils.executeJs("alert(1)"));
    }

    @Test
    public void executeJs_allowsBlankInput() {
        Utils.executeJs("");
        Utils.executeJs(null);
        assertThrows(SecurityException.class, () -> Utils.executeJs("alert(1)"));
    }

    @Test
    public void deleteFile_blocksPathTraversal() {
        assertThrows(SecurityException.class, () -> Utils.deleteFile("../outside-tmp"));
    }

    @Test
    public void deleteFile_deletesFileWithinTempDirectory() throws IOException {
        Path tempFile = Files.createTempFile("utils-test-", ".txt");
        Utils.deleteFile(tempFile.getFileName().toString());
        assertTrue(Files.notExists(tempFile));
    }
}
