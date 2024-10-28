import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class commandlineintepreter {
    private File currentDirectory;
    public commandlineintepreter() {
        currentDirectory = new File(System.getProperty("user.dir"));
    }

    public void help() {
        System.out.println("Available commands:");
        System.out.println("pwd - Print working directory");
        System.out.println("cd <dir> - Change directory");
        System.out.println("ls [-a] [-r] - List directory contents");
        System.out.println("mkdir <dir> - Create directory");
        System.out.println("rmdir <dir> - Remove directory");
        System.out.println("touch <file> - Create empty file");
        System.out.println("mv <source> <destination> - Move/rename file");
        System.out.println("cat <file> - Show file contents");
        System.out.println("help - Show this help");
        System.out.println("exit - Exit CLI");
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

    public void ls() {
        //
    }

    public void ls_a() {
        //
    }

    public void ls_r() {
        //
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


    }

    public void appendOutput(String file) {
        // Used for >>
    }

    public void pipe(String command) {
        // Used for |
    }
}