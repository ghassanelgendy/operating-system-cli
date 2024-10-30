import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Scanner;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class commandlineintepreter {
    private File currentDirectory;
    private PrintStream originalOut;

    public commandlineintepreter() {
        currentDirectory = new File(System.getProperty("user.dir"));
        originalOut = System.out;
    }

    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void help() {
        System.out.println("""
                Available commands:
                pwd - Print working directory
                cd <dir> - Change directory
                ls [-a] [-r] - List directory contents
                mkdir <dir> - Create directory
                rmdir <dir> - Remove directory
                touch <file> - Create empty file
                mv <source> <destination> - Move/rename file
                cat <file> - Show file contents
                > <file> - Redirect output to file
                >> <file> - Append output to file
                | <command> - Pipe output to another command
                help - Show this help
                exit - Exit CLI
                """);
    }

    public void exit() {
        System.out.println("Exiting CLI.");
    }

    public void pwd() {
        System.out.println(currentDirectory.getAbsolutePath());
    }

    public void cd(String dir) {
        File newDir = new File(currentDirectory, dir);

        if (newDir.exists() && newDir.isDirectory()) {
            try {
                currentDirectory = newDir.getCanonicalFile();
                System.out.println("Changed directory to " + currentDirectory.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("cd: " + e.getMessage());
            }
        } else {
            System.out.println("cd: " + dir + " No such file or directory");
        }
    }

    public void ls(String... options) {
        File[] files = currentDirectory.listFiles();
        boolean showHidden = false;
        boolean reverse = false;

        for (String option : options) {
            if ("-a".equals(option)) {
                showHidden = true;
            } else if ("-r".equals(option)) {
                reverse = true;
            }
        }

        if (files != null) {
            if (!showHidden) {
                files = Arrays.stream(files).filter(file -> !file.isHidden()).toArray(File[]::new);
            }

            boolean finalReverse = reverse;
            Arrays.sort(files, (f1, f2) -> {
                if (finalReverse) {
                    return f2.getName().compareTo(f1.getName());
                } else {
                    return f1.getName().compareTo(f2.getName());
                }
            });

            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("Error reading directory");
        }
    }

    public void mkdir(String dir) {
        File newDir = new File(currentDirectory, dir);
        if (newDir.mkdir()) {
            System.out.println("Directory created: " + newDir.getAbsolutePath());
        } else {
            System.out.println("mkdir: " + dir + ": Failed to create directory or directory already exists");
        }
    }

    public void rmdir(String dir) {
        File dirToRemove = new File(currentDirectory, dir);
        if (dirToRemove.exists() && dirToRemove.isDirectory()) {
            if (dirToRemove.list().length == 0) {
                if (dirToRemove.delete()) {
                    System.out.println("Directory removed: " + dirToRemove.getAbsolutePath());
                } else {
                    System.out.println("rmdir: " + dir + ": Failed to remove directory");
                }
            } else {
                System.out.println("rmdir: " + dir + ": Directory not empty");
            }
        } else {
            System.out.println("rmdir: " + dir + ": No such directory");
        }
    }

    public void rm(String file) {
        File fileToRemove = new File(currentDirectory, file);
        if (fileToRemove.exists()) {
            if (fileToRemove.delete()) {
                System.out.println("File removed: " + fileToRemove.getAbsolutePath());
            } else {
                System.out.println("rm: " + file + ": Failed to remove file");
            }
        } else {
            System.out.println("rm: " + file + ": No such file");
        }
    }

    public void touch(String filename) {
        File file = new File(currentDirectory, filename);
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + filename);
            } else {
                System.out.println("File already exists: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public void mv(String source, String destination) {
        File sourceFile = new File(currentDirectory, source);

        if (!sourceFile.exists()) {
            System.out.println("mv: " + source + " No such file or directory");
            return;
        }

        File destinationFile = new File(currentDirectory, destination);

        if (destinationFile.isDirectory()) {
            destinationFile = new File(destinationFile, sourceFile.getName());
        }
        if (sourceFile.renameTo(destinationFile)) {
            System.out.println("Moved/Renamed " + source + " to " + destinationFile.getPath());
        } else {
            System.out.println("mv: Failed to move/rename " + source + " to " + destination);
        }
    }

    public void cat(String[] cmnd) {
        if (cmnd.length == 1) {
            System.out.println("cat: Missing argument");
        } else if (cmnd.length == 2) {
            String filename = cmnd[1];
            File file = new File(currentDirectory, filename);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    int data;
                    while ((data = fis.read()) != -1) {
                        System.out.print((char) data);
                    }
                    System.out.println();
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                }
            } else {
                System.out.println("cat: " + filename + " No such file or directory");
            }
        } else if (cmnd.length == 3) {
            if (Objects.equals(cmnd[1], "-n")) {
                String filename = cmnd[2];
                File file = new File(currentDirectory, filename);
                if (file.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        int lineNumber = 1;
                        while ((line = br.readLine()) != null) {
                            System.out.println(lineNumber++ + " " + line);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + e.getMessage());
                    }
                } else {
                    System.out.println("cat: " + filename + ": No such file or directory");
                }
            } else if (Objects.equals(cmnd[1], ">>")) {
                String filename = cmnd[2];
                File file = new File(currentDirectory, filename);
                if (file.exists()) {
                    try (FileOutputStream fos = new FileOutputStream(file, true)) {
                        System.out.println("Enter text to append (press Ctrl+D to finish):");
                        Scanner scanner = new Scanner(System.in);
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            fos.write(line.getBytes());
                            fos.write('\n');
                        }
                    } catch (IOException e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }
                }
            } else if (Objects.equals(cmnd[1], ">")) {
                String filename = cmnd[2];
                File file = new File(currentDirectory, filename);
                try {
                    if (file.createNewFile()) {
                        System.out.println("File created: " + filename);
                    } else {
                        System.out.println("File already exists: " + filename);
                    }
                } catch (IOException e) {
                    System.out.println("Error creating file: " + e.getMessage());
                }
            } else {
                String firstname = cmnd[1], secondname = cmnd[2];
                String com = "cat " + firstname;
                String com2 = "cat " + secondname;
                cat(com.split(" "));
                cat(com2.split(" "));
            }
        } else if (cmnd.length == 5) {
            if (Objects.equals(cmnd[3], ">")) {
                String firstname = cmnd[1], secondname = cmnd[2], thirdname = cmnd[4];
                File file1 = new File(currentDirectory, firstname),
                        file2 = new File(currentDirectory, secondname),
                        file3 = new File(currentDirectory, thirdname);
                if (file1.exists() && file2.exists() && file3.exists()) {
                    try (FileOutputStream fos = new FileOutputStream(file3)) {
                        try (FileInputStream fis1 = new FileInputStream(file1)) {
                            int data;
                            while ((data = fis1.read()) != -1) {
                                fos.write(data);
                            }
                            fos.write('\n');
                        }
                        try (FileInputStream fis2 = new FileInputStream(file2)) {
                            int data;
                            while ((data = fis2.read()) != -1) {
                                fos.write(data);
                            }
                        } catch (IOException e) {
                            System.out.println("Error reading file: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.out.println("Error writing file: " + e.getMessage());
                    }
                } else {
                    if (!file1.exists())
                        System.out.println("cat: " + firstname + " No such file or directory");
                    if (!file2.exists())
                        System.out.println("cat: " + secondname + " No such file or directory");
                    if (!file3.exists())
                        System.out.println("cat: " + thirdname + " No such file or directory");
                }
            } else {
                System.out.println("cat: Invalid command");
            }
        }
    }

    public void redirectOutput(String file) {
        File outputFile = new File(currentDirectory, file);

        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile, false));
            System.out.println("Output redirected to " + outputFile.getAbsolutePath());
            System.setOut(fileOut);

        } catch (IOException e) {
            System.out.println("redirectOutput: Unable to write to " + file + " - " + e.getMessage());
        }
    }

    // Reset System.out to the console
    public void resetOutput() {
        System.out.flush();
        System.setOut(originalOut);
    }

    public void appendOutput(String file) {
        File outputFile = new File(currentDirectory, file);

        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile, true));
            System.out.println("Appending output to " + outputFile.getAbsolutePath());
            System.setOut(fileOut);
        } catch (IOException e) {
            System.out.println("appendOutput: Unable to append to " + file + " - " + e.getMessage());
        }
    }

    public void pipe(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) {
            System.out.println("pipe: Invalid command");
            return;
        }

        String commandToExecute = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (commandToExecute) {
            case "ls" -> ls(args);
            case "pwd" -> pwd();
            case "cat" -> cat(args);
            case "help" -> help();
            default -> System.out.println("pipe: Unsupported command '" + commandToExecute + "'");
        }
    }
}
