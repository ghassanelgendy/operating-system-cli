import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        commandlineintepreter cli = new commandlineintepreter();
        Scanner scanner = new Scanner(System.in); // object el cin bta3t java
        System.out.println("Welcome to the CLI. Type 'help' for available commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] parts = input.split(" "); //ba7ot el input f array mt2asem bel spaces 3shan law arguements b3d el command
            String command = parts[0];

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
        }
    }
}
