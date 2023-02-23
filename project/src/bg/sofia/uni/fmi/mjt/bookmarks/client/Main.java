package bg.sofia.uni.fmi.mjt.bookmarks.client;

import java.util.Scanner;

public class Main {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private static final String PROMPT = ">> ";
    private static final String CONNECT_CMD = "connect";
    private static final String DISCONNECT_CMD = "disconnect";
    private static final String EXIT_CMD = "exit";

    private static final int SINGLE_ARG_LENGTH = 1;
    private static final int THREE_ARG_LENGTH = 3;

    private static void printCommands() {
        System.out.println("Available commands: ");
        System.out.println(
            "connect { <host> <port> } : " +
                "connects to the server either with default credentials or specified host and port");
        System.out.println("disconnect : disconnects from the server");
        System.out.println("exit : exits the client");
        System.out.println();
    }

    public static void main(String... args) throws InterruptedException {
        var scanner = new Scanner(System.in);
        System.out.println("<Bookmarks manager>");
        printCommands();

        while (true) {
            System.out.print(PROMPT);
            String cmd = scanner.nextLine();

            if (cmd.startsWith(CONNECT_CMD)) {
                var split = cmd.split(" ");
                if (split.length == SINGLE_ARG_LENGTH) {
                    var client = new Client(SERVER_HOST, SERVER_PORT);
                    client.start();
                    client.join();
                } else if (split.length == THREE_ARG_LENGTH) {
                    var client = new Client(split[1], Integer.parseInt(split[2]));
                    client.start();
                    client.join();
                } else {
                    System.out.println("Invalid command.");
                }
            } else if (cmd.equals(DISCONNECT_CMD)) {
                System.out.println("Not connected to server.");
            } else if (cmd.equals(EXIT_CMD)) {
                System.out.println("Exiting client...");
                break;
            } else {
                System.out.println("Invalid command.");
                printCommands();
            }
        }
    }
}
