import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServerApp {
    private static Map<String, List<ClientHandler>> topicSubscribers = new HashMap<>();

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
        private String role;
        private String topic;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                role = in.readLine();
                topic = in.readLine();

                if (role.equals("SUBSCRIBER")) {
                    addSubscriber(topic);
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    if (role.equals("PUBLISHER")) {
                        publishMessage(inputLine, topic);
                    }
                    if (inputLine.equals("terminate")) {
                        break;
                    }
                }

                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                out.close();
                in.close();
                clientSocket.close();
                if (role.equals("SUBSCRIBER")) {
                    removeSubscriber(topic);
                }

            } catch (IOException e) {
                System.out.println("ClientHandler error: " + e.getMessage());
            }
        }

        private void addSubscriber(String topic) {
            List<ClientHandler> subscribers = topicSubscribers.getOrDefault(topic, new ArrayList<>());
            subscribers.add(this);
            topicSubscribers.put(topic, subscribers);
        }

        private void removeSubscriber(String topic) {
            List<ClientHandler> subscribers = topicSubscribers.getOrDefault(topic, new ArrayList<>());
            subscribers.remove(this);
            topicSubscribers.put(topic, subscribers);
        }

        private void publishMessage(String message, String topic) {
            List<ClientHandler> subscribers = topicSubscribers.getOrDefault(topic, new ArrayList<>());
            for (ClientHandler subscriber : subscribers) {
                if (!subscriber.equals(this)) {
                    synchronized (subscriber.out) {
                        subscriber.out.println("Topic: " + topic + ", Message: " + message);
                    }
                }
            }
        }
    }
}
