import java.util.Arrays;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        commandlineintepreter cli = new commandlineintepreter();
        Scanner scanner = new Scanner(System.in); // object el cin bta3t java
        System.out.println("Welcome to the CLI. Type 'help' for available commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            //3shan law el inout fyh > aw >> aw |
            String[] redirectionParts = input.split(">"); //by2sm el input 3la asas el >
            //3shan lawa el input command b argument yt2asem sah
            String commandInput = redirectionParts[0].trim();
            String outputFile = redirectionParts.length > 1 ? redirectionParts[1].trim() : null;


            String[] parts = input.split(" "); //ba7ot el input f array mt2asem bel spaces 3shan law arguements b3d el command
            String command = parts[0];

            if (outputFile != null) {
                cli.redirectOutput(outputFile);
            }

            switch (command) {
                case "pwd":
                    cli.pwd();
                    break;
                case "cd":
                    if (parts.length < 2) {
                        System.out.println("cd: missing argument");
                    } else {
                        cli.cd(parts[1]);
                    }
                    break;
                case "ls":
                    cli.ls(parts); //bab3at le ls func. - handling the options there
                    break;
                case "mkdir":
                    break;
                case "rmdir":
                    break;
                case "touch":
                    break;
                case "mv":
                    if (parts.length < 3) {
                        System.out.println("mv: missing source or destination argument");
                    } else {
                        String source = parts[1];
                        String destination = parts[2];
                        cli.mv(source, destination);
                    }
                    break;
                case "cat":
                    break;
                case "help":
                    cli.help();
                    break;
                case "exit":
                    cli.exit();
                    scanner.close();
                    return;
                default:
                    System.out.println("Unknown command: " + command);
            }
            if (outputFile != null) {
                cli.resetOutput(); // Reset System.out to console after command
            }
        }
    }
}
