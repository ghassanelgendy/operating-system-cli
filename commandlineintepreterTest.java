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

    @Test
    void testPwd() {
        String expectedPath = testDirectory.getAbsolutePath();
        assertEquals(expectedPath, cli.getCurrentDirectory().getAbsolutePath(),
                "pwd should return the current directory path");
    }

    @Test
    void testCdValidDirectory() throws IOException {
        File subDirectory = new File(testDirectory, "subDir");
        subDirectory.mkdir();
        cli.cd("subDir");

        assertEquals(subDirectory.getCanonicalPath(), cli.getCurrentDirectory().getCanonicalPath(),
                "cd should change to subDir ");
    }

    @Test
    void testCdInvalidDirectory() {
        cli.cd("nonExistentDir");
        assertEquals(testDirectory.getAbsolutePath(), cli.getCurrentDirectory().getAbsolutePath(),
                "cd should not change directory ");
    }

    @Test
    void testMvFile() throws IOException {
        File sourceFile = new File(testDirectory, "file1.txt");
        File destinationFile = new File(testDirectory, "movedFile.txt");

        cli.mv("file1.txt", "movedFile.txt");

        assertFalse(sourceFile.exists(), "Original file should no longer exist after mv");
        assertTrue(destinationFile.exists(), "Destination file should exist after mv");
    }

    @Test
    void testMvToDirectory() throws IOException {
        File sourceFile = new File(testDirectory, "file1.txt");
        File destDir = new File(testDirectory, "destDir");
        destDir.mkdir();

        cli.mv("file1.txt", "destDir");

        File movedFile = new File(destDir, "file1.txt");

        assertTrue(movedFile.exists(), "File should be moved into the destination directory");
    }
    @Test
    void testRedirectOutputToFile() throws IOException {
        String outputFileName = "output.txt";
        File outputFile = new File(testDirectory, outputFileName);

        cli.redirectOutput(outputFileName);
        System.out.println("Redirected output test");
        cli.resetOutput();

        String fileContent = Files.readString(outputFile.toPath()).trim();
        assertEquals("Redirected output test", fileContent,
                "Output should be redirected to the specified file");
    }
}

