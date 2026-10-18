
package com.mycompany.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
private static final int PORT = 5000;
private static final int MAX_CLIENTS = 4;

private static final List<ClientHandler> clients =
        new ArrayList<>();

public static void main(String[] args) {

    try (ServerSocket serverSocket =
                 new ServerSocket(PORT)) {

        System.out.println("Shopping Server started.");
        System.out.println("Waiting for clients...");

        while (true) {

            Socket socket = serverSocket.accept();
            ClientHandler handler;

            synchronized (clients) {

                if (clients.size() >= MAX_CLIENTS) {
                    socket.close();
                    continue;
                }

                handler = new ClientHandler(socket, clients);
                clients.add(handler);
            }

            Thread thread = new Thread(handler);
            thread.start();
        }

    } catch (IOException e) {
        System.out.println(
                "Server error: " + e.getMessage());
    }
}

public static void broadcast(
        String message, ClientHandler sender) {

    List<ClientHandler> snapshot;

    synchronized (clients) {
        snapshot = new ArrayList<>(clients);
    }

    for (ClientHandler client : snapshot) {
        client.sendMessage(message);
    }

    System.out.println(message);
}

public static void removeClient(ClientHandler client) {

    boolean removed;

    synchronized (clients) {
        removed = clients.remove(client);
    }

    if (removed) {
        broadcast(
                client.getUsername() + " left the chat.",
                null);

        showConnectedClients();
    }
}

public static void showConnectedClients() {

    synchronized (clients) {

        System.out.println("Connected clients:");

        if (clients.isEmpty()) {
            System.out.println("No clients connected.");
        }

        for (ClientHandler client : clients) {
            if (client.getUsername() != null) {
                System.out.println(
                        "- " + client.getUsername());
            }
        }

        System.out.println("--------------------");
    }
}
}  

