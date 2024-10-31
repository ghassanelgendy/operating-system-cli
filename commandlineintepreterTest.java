import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
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


    @Test
    void testAppendOutput() throws IOException {
        String outputFileName = "appendOutput.txt";
        File outputFile = new File(testDirectory, outputFileName);
        // Execute the appendOutput method
        cli.appendOutput(outputFileName);
        System.out.println("AppendOutput test");
        cli.resetOutput(); // Resetting the output to the original output stream

        // Read the content of the output file
        String fileContent = Files.readString(outputFile.toPath()).trim();
        // Assert that the content of the output file is the correct output
        assertEquals("AppendOutput test", fileContent, "Output should be appended to the specified file");
    }

    @Test
    void testCatWithOneArgumentFileExists() throws IOException {
        String filename = "file1.txt";
        File testFile = new File(testDirectory, filename);
        Files.writeString(testFile.toPath(), "Testing cat method");

        String expectedOutput = "Testing cat method";
        String actualOutput = getLatestSystemOutput(() -> cli.cat(new String[]{"cat", filename}));

        assertEquals(expectedOutput, actualOutput, "cat should print the content of the file to the console");
    }

    @Test
    void testCatWithNoArguments() {
        String expectedOutput = "cat: Missing argument";
        String actualOutput = getLatestSystemOutput(() -> cli.cat(new String[]{"cat"}));
        assertEquals(expectedOutput, actualOutput, "cat with no arguments should print 'cat: Missing argument'");
    }

    @Test
    void testCatWithOneArgumentFileNotExists() {
        String filename = "nonExistentFile.txt";
        String expectedErrorMessage = "cat: " + filename + " No such file or directory";
        String actualOutput = getLatestSystemOutput(() -> cli.cat(new String[]{"cat", filename}));
        assertEquals(expectedErrorMessage, actualOutput,
                "cat should print an error message when the file does not exist");
    }

    @Test
    void testCatWithTwoArgumentsTwoFiles() throws IOException {
        String filename = "file1.txt";
        String filename2 = "file2.txt";
        File testFile = new File(testDirectory, filename);
        File testFile2 = new File(testDirectory, filename2);
        Files.writeString(testFile.toPath(), "File1");
        Files.writeString(testFile2.toPath(), "File2");

        String actualOutput = getLatestSystemOutput(() -> cli.cat(new String[]{"cat", filename, filename2}));

        String expectedOutput = "File1\r\nFile2";
        assertEquals(expectedOutput, actualOutput,
                "cat -n should print the content of the two files to the console");
    }

    @Test
    void testCatWithTwoArgumentsShowingLineNumbers() throws IOException {
        //Text from the cat command is CRLF so I'm using \r\n
        // we can use \n only (LF) and replace every \r\n in the actual output to \n
        String filename = "file1.txt";
        File testFile = new File(testDirectory, filename);
        Files.writeString(testFile.toPath(), "Testing\r\ncat\r\nmethod");

        cli.cat(new String[]{"cat", "-n", filename});

        String expectedOutput = "1 Testing\r\n2 cat\r\n3 method";
        String actualOutput = getLatestSystemOutput(() -> cli.cat(new String[]{"cat", "-n", filename}));
        assertEquals(expectedOutput, actualOutput,
                "cat -n should print the content of the file with line numbers");
    }

    @Test
    void testCatWithFiveArgumentsConcatenateTwoFiles() throws IOException {
        String file1Name = "file1.txt";
        String file2Name = "file2.txt";
        String outputFilename = "output.txt";
        File file1 = new File(testDirectory, file1Name);
        File file2 = new File(testDirectory, file2Name);
        File outputFile = new File(testDirectory, outputFilename);
        outputFile.createNewFile();

        Files.writeString(file1.toPath(), "File 1 Content");
        Files.writeString(file2.toPath(), "File 2 Content");
        cli.cat(new String[]{"cat", file1Name, file2Name, ">", outputFilename});

        String expectedOutput = "File 1 Content\nFile 2 Content";

        assertEquals(expectedOutput, Files.readString(outputFile.toPath()),
                "cat file1 file2 > output should concatenate the contents of file1 and file2 into output.txt");
    }

    @Test
    void testAppendToFileWithInput() throws IOException {
        String filename = "appendTestFile.txt";
        File testFile = new File(testDirectory, filename);
        Files.writeString(testFile.toPath(), "Existing content\n");

        // Simulate the input text to append (replace "your appended text" with the actual content)
        String simulatedInput = "First line to append\nSecond line to append\n";

        // Simulate the input text to append by redirecting System.in
        InputStream originalIn = System.in;
        try (ByteArrayInputStream simulatedInputStream = new ByteArrayInputStream(simulatedInput.getBytes())) {
            System.setIn(simulatedInputStream);
            cli.cat(new String[]{"cat", ">>", filename}); // Assuming 'cat >> filename' is the command syntax
        } finally {
            System.setIn(originalIn); // Reset to the original System.in
        }
        // Read the contents of the file after appending
        String fileContent = Files.readString(testFile.toPath());

        // Expected content after appending
        String expectedContent = "Existing content\nFirst line to append\nSecond line to append\n";

        assertEquals(expectedContent, fileContent, "File content should match after appending input text");
    }


    private String getLatestSystemOutput(Runnable task) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outStream));

        try {
            task.run(); // Run the command line method that outputs to System.out
        } finally {
            System.setOut(originalOut); // Reset to the original System.out
        }

        return outStream.toString(StandardCharsets.UTF_8).trim();
    }

    @Test
    void testTouchNewFile() {
        // Given
        String filename = "newfile.txt";

        // When
        cli.touch(filename);

        // Then
        File newFile = new File(testDirectory, filename);
        assertTrue(newFile.exists(), "File should exist after touch command");
        assertEquals(0, newFile.length(), "New file should be empty");
    }

    @Test
    void testTouchExistingFile() throws IOException {
        // Given
        String filename = "existingfile.txt";
        File existingFile = new File(testDirectory, filename);
        Files.writeString(existingFile.toPath(), "Initial content");

        // When
        cli.touch(filename);

        // Then
        assertTrue(existingFile.exists(), "File should still exist after touch command");
        assertEquals("Initial content", Files.readString(existingFile.toPath()),
                "Existing file content should not be changed by touch command");
    }
    @Test
    void testMkdirCreatesNewDirectory() {
        String dirName = "newDir";
        cli.mkdir(dirName);
        File newDir = new File(testDirectory, dirName);
        assertTrue(newDir.exists() && newDir.isDirectory(), "Directory should be created successfully");
    }

    @Test
    void testMkdirExistingDirectory() {
        String dirName = "existingDir";
        File existingDir = new File(testDirectory, dirName);
        existingDir.mkdir();

        String expectedOutput = "mkdir: " + dirName + ": Failed to create directory or directory already exists";
        String actualOutput = getLatestSystemOutput(() -> cli.mkdir(dirName));
        assertEquals(expectedOutput, actualOutput, "Should notify that directory creation failed because it already exists");
    }

    @Test
    void testRmdirRemovesEmptyDirectory() {
        String dirName = "emptyDir";
        File emptyDir = new File(testDirectory, dirName);
        emptyDir.mkdir();
        cli.rmdir(dirName);
        assertFalse(emptyDir.exists(), "Directory should be removed successfully");
    }

    @Test
    void testRmdirNonExistentDirectory() {
        String dirName = "nonExistentDir";
        String expectedOutput = "rmdir: " + dirName + ": No such directory";
        String actualOutput = getLatestSystemOutput(() -> cli.rmdir(dirName));
        assertEquals(expectedOutput, actualOutput, "Should notify that the directory does not exist");
    }

    @Test
    void testRmdirNonEmptyDirectory() throws IOException {
        String dirName = "nonEmptyDir";
        File nonEmptyDir = new File(testDirectory, dirName);
        nonEmptyDir.mkdir();
        new File(nonEmptyDir, "fileInsideDir.txt").createNewFile();

        String expectedOutput = "rmdir: " + dirName + ": Directory not empty";
        String actualOutput = getLatestSystemOutput(() -> cli.rmdir(dirName));
        assertEquals(expectedOutput, actualOutput, "Should notify that directory is not empty");
        assertTrue(nonEmptyDir.exists(), "Directory should not be removed if it is not empty");
    }

    @Test
    void testRmRemovesFile() throws IOException {
        String fileName = "fileToRemove.txt";
        File fileToRemove = new File(testDirectory, fileName);
        fileToRemove.createNewFile();
        cli.rm(fileName);
        assertFalse(fileToRemove.exists(), "File should be removed successfully");
    }

    @Test
    void testRmNonExistentFile() {
        String fileName = "nonExistentFile.txt";
        String expectedOutput = "rm: " + fileName + ": No such file";
        String actualOutput = getLatestSystemOutput(() -> cli.rm(fileName));
        assertEquals(expectedOutput, actualOutput, "Should notify that the file does not exist");
    }

}



