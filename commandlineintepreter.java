import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;


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
        File newDir = new File(currentDirectory, dir);// el file ely hanmashwaro m3ana

        if (newDir.exists() && newDir.isDirectory()) {
            try {
                currentDirectory = newDir.getCanonicalFile(); //btgeeb absolute path thoto fyh
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

        // bashof mab3ota be options eh
        for (String option : options) {
            if ("-a".equals(option)) {
                showHidden = true;
            } else if ("-r".equals(option)) {
                reverse = true;
            }
        }

        if (files != null) {
            // bacheck elhidden
            if (!showHidden) {
                //ba3mlo stream 3shan afilter 3la hassab elhidden
                files = Arrays.stream(files).filter(file -> !file.isHidden()).toArray(File[]::new);
            }

            // Sort files
            boolean finalReverse = reverse;
            Arrays.sort(files, (f1, f2) -> {
                if (finalReverse) {
                    return f2.getName().compareTo(f1.getName());
                } else {
                    return f1.getName().compareTo(f2.getName());
                }
            });

            // Print the file names
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("Error reading directory");
        }
    }

    public void mkdir(String dir) {
        //
    }

    public void rmdir(String dir) {
        //
    }

    public void touch(String file) {
        //
    }

    public void mv(String source, String destination) {
        File sourceFile = new File(currentDirectory, source);

        if (!sourceFile.exists()) {
            System.out.println("mv: " + source + " No such file or directory");
            return;
        }


        File destinationFile = new File(currentDirectory, destination);


        if (destinationFile.isDirectory()) {
            destinationFile = new File(destinationFile, sourceFile.getName()); // eza kan el destination dy directory mesh file ermyh gowaha
        }
        if (sourceFile.renameTo(destinationFile)) {
            System.out.println("Moved/Renamed " + source + " to " + destinationFile.getPath());
        } else {
            System.out.println("mv: Failed to move/rename " + source + " to " + destination);
        }
    }

    public void rm(String file) {
        //
    }

    public void cat(String file) {
        //
    }

    public void redirectOutput(String file) {
        File outputFile = new File(currentDirectory, file);

        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile, false)); // 3shan a-overwrite
            System.out.println("Output redirected to " + outputFile.getAbsolutePath());
            System.setOut(fileOut);  // bn2l el output fel file

        } catch (IOException e) {
            System.out.println("redirectOutput: Unable to write to " + file + " - " + e.getMessage());
        }
    }

    // Reset System.out to the console
    public void resetOutput() {
        System.out.flush();
        System.setOut(originalOut);  // brg3 og system.out lel console
    }



    public void appendOutput(String file) {
        // Used for >>
    }

    public void pipe(String command) {
        // Used for |
    }
}
