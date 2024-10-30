import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        commandlineintepreter cli = new commandlineintepreter();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the CLI. Type 'help' for available commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            // Handle redirection and piping
            String[] redirectionParts = input.split(">");
            String commandInput = redirectionParts[0].trim();
            String outputFile = redirectionParts.length > 1 ? redirectionParts[1].trim() : null;

            // Handle piping
            String[] pipeParts = commandInput.split("\\|");
            String commandWithArgs = pipeParts[0].trim();
            String[] parts = commandWithArgs.split(" ");
            String command = parts[0];

            if (outputFile != null) {
                cli.redirectOutput(outputFile);
            }

            // Process each command part in case of piping
            for (String cmd : pipeParts) {
                String trimmedCmd = cmd.trim();
                String[] cmdParts = trimmedCmd.split(" ");
                String cmdName = cmdParts[0];

                switch (cmdName) {
                    case "pwd":
                        cli.pwd();
                        break;
                    case "cd":
                        if (cmdParts.length < 2) {
                            System.out.println("cd: missing argument");
                        } else {
                            cli.cd(cmdParts[1]);
                        }
                        break;
                    case "ls":
                        cli.ls(Arrays.copyOfRange(cmdParts, 1, cmdParts.length));
                        break;
                    case "mkdir":
                        if (cmdParts.length < 2) {
                            System.out.println("mkdir: missing directory name");
                        } else {
                            cli.mkdir(cmdParts[1]);
                        }
                        break;
                    case "rmdir":
                        if (cmdParts.length < 2) {
                            System.out.println("rmdir: missing directory name");
                        } else {
                            cli.rmdir(cmdParts[1]);
                        }
                        break;
                    case "touch":
                        if (cmdParts.length < 2) {
                            System.out.println("touch: missing filename");
                        } else {
                            cli.touch(cmdParts[1]);
                        }
                        break;
                    case "mv":
                        if (cmdParts.length < 3) {
                            System.out.println("mv: missing source or destination argument");
                        } else {
                            cli.mv(cmdParts[1], cmdParts[2]);
                        }
                        break;
                    case "rm":
                        if (cmdParts.length < 2) {
                            System.out.println("rm: missing filename");
                        } else {
                            cli.rm(cmdParts[1]);
                        }
                        break;
                    case "cat":
                        cli.cat(cmdParts);
                        break;
                    case "help":
                        cli.help();
                        break;
                    case "exit":
                        cli.exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command: " + cmdName);
                }
            }

            if (outputFile != null) {
                cli.resetOutput(); // Reset System.out to console after command
            }
        }
    }
}
