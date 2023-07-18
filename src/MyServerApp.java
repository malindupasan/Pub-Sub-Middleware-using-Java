import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServerApp {

    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java MyServerApp <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean isPublisher;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String role = in.readLine();
                isPublisher = role.equals("PUBLISHER");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    if (isPublisher) {
                        broadcastToSubscribers(inputLine);
                    }
                    if (inputLine.equals("terminate")) {
                        break;
                    }
                }

                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                out.close();
                in.close();
                clientSocket.close();
                clients.remove(this);
            } catch (IOException e) {
                System.out.println("ClientHandler error: " + e.getMessage());
            }
        }

        private synchronized void broadcastToSubscribers(String message) {
            for (ClientHandler client : clients) {
                if (!client.isPublisher) {
                    synchronized (client.out) {
                        client.out.println("Broadcast: " + message);
                    }
                }
            }
        }
    }
}
