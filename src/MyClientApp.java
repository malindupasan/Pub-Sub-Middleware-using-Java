import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClientApp {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java MyClientApp <server_ip> <port>");
            System.exit(1);
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(serverIp, port);
            System.out.println("Connected to server at " + serverIp + ":" + port);

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Thread receiverThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server response: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving server response: " + e.getMessage());
                }
            });
            receiverThread.start();

            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                out.println(userInput);
                if (userInput.equals("terminate")) {
                    break;
                }
            }

            out.close();
            in.close();
            consoleReader.close();
            socket.close();
            receiverThread.join();

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Receiver thread interrupted: " + e.getMessage());
        }
    }
}
