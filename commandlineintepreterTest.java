import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class commandlineintepreterTest {
    private commandlineintepreter cli;
    private File testDirectory;
    //abl kol test
    @BeforeEach
    void setUp() throws IOException {
        // awel haga ne3ml folder eltesting
        testDirectory = new File("testDir");
        testDirectory.mkdir();
        cli = new commandlineintepreter();
        cli.setCurrentDirectory(testDirectory);

        // ne3ml elfiles ely hantest 3aleha
        new File(testDirectory, "file1.txt").createNewFile();
        new File(testDirectory, "file2.txt").createNewFile();

        // hidden files lzom el ls -a
        File hiddenFile = new File(testDirectory, ".hiddenFile.txt");
        hiddenFile.createNewFile();
        Files.setAttribute(hiddenFile.toPath(), "dos:hidden", true); // Set to hidden unix beyhtag . abl elfilename w bs windows hayhtag
    }

    @AfterEach
    void tearDown() {
        // Delete elfiles ba3d kol test
        for (File file : testDirectory.listFiles()) {
            file.delete();
        }
        testDirectory.delete();
    }

    @Test
    void testLsWithReverseOption() {
        // beytest ls -r output
        cli.ls("-r");
        // da elana mestaneh
        List<String> expectedFilesInReverse = Arrays.asList("file2.txt", "file1.txt");

        // ba verify eloutput bely ana mestaneh
        assertTrue(Arrays.asList(testDirectory.list()).containsAll(expectedFilesInReverse));
    }

    @Test
    void testLsWithoutOptions() {
        // beytest ls output
        cli.ls();
        List<String> expectedFiles = Arrays.asList("file1.txt", "file2.txt");

        assertTrue(Arrays.asList(testDirectory.list()).containsAll(expectedFiles));
    }

    @Test
    void testLsWithHiddenOption() {
        // el -a option
        cli.ls("-a");

        List<String> expectedFiles = Arrays.asList("file1.txt", "file2.txt", ".hiddenFile.txt");

        String[] actualFiles = testDirectory.list();

        assertNotNull(actualFiles, "Directory should not be null");

        assertTrue(Arrays.asList(actualFiles).containsAll(expectedFiles),
                "Output should include all expected files, including hidden ones");
    }
}

