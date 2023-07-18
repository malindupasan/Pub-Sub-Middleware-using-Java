Client-Server Socket Application
This repository contains a client-server socket application implemented in Java. The application allows communication between a server and multiple clients over sockets using a command line interface (CLI). The primary objective is to demonstrate client-server communication where messages typed on the client CLI are displayed on the server's CLI.

Task 1: Basic Client-Server Communication
The first task involves implementing a basic client-server socket application. The server listens for connections on a pre-defined port, and clients connect to the server using the server's IP address and port number. After connecting, any text typed on the client CLI is displayed on the server's CLI as standard system output text.

Task 2: Improved Client-Server Communication
The second task enhances the client-server implementation to handle multiple concurrent client connections. Multiple client applications can connect to the server simultaneously, and their typed messages are displayed on the server's CLI. The clients are classified as either "Publisher" or "Subscriber" based on a command line argument. Publishers can send messages to the server, and the server echoes these messages to all the subscriber clients' terminals.

Task 3: Topic-Based Filtering
Task 3 further improves the implementation by introducing topic-based filtering of messages among publishers and subscribers. The client application is updated to include an additional command line argument for the topic or subject. Publishers send messages to the server on a specific topic, and subscribers interested in that topic receive the messages. Publishers and subscribers interested in different messages can connect concurrently to the server.

Each task includes both server and client code. To run the server, pass the desired port number as a command line argument. To run the client, provide the server's IP address and port number as command line arguments.

Feel free to explore and enhance the code as needed for your specific requirements.
