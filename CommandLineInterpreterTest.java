import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineInterpreterTest {
    private commandlineintepreter cli;
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        cli = new commandlineintepreter();
        testDir = Files.createTempDirectory("testDir").toFile();
        cli.setCurrentDirectory(testDir); // Ensure you have this method

        // Create test files
        new File(testDir, "file1.txt").createNewFile();
        new File(testDir, "file2.txt").createNewFile();
        new File(testDir, ".hiddenfile").createNewFile();
    }

    @AfterEach
    public void tearDown() {
        for (File file : testDir.listFiles()) {
            file.delete();
        }
        testDir.delete();
    }

    @Test
    public void testLs() {
        String output = captureOutput(() -> cli.ls(new String[]{"ls"}));
        assertFalse(output.contains(".hiddenfile"));
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("file2.txt"));
    }

    @Test
    public void testLsWithA() {
        String output = captureOutput(() -> cli.ls(new String[]{"ls", "-a"}));
        assertTrue(output.contains(".hiddenfile"));
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("file2.txt"));
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        action.run();

        System.setOut(originalOut);
        return outputStream.toString().trim();
    }
}
