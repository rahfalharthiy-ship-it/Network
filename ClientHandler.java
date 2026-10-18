package com.mycompany.network;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket; 
import java.util.List;

public class ClientHandler implements Runnable {
private final Socket socket;
private final List<ClientHandler> clients;

private BufferedReader input;
private PrintWriter output;
private String username;

public ClientHandler(
        Socket socket,
        List<ClientHandler> clients) {

    this.socket = socket;
    this.clients = clients;
}

@Override
public void run() {

    try {
        input = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        output = new PrintWriter(
                socket.getOutputStream(), true);

        // First line must contain the username
        username = input.readLine();

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        username = username.trim();

        Server.broadcast(
                username + " joined the shopping system.",
                this);

        Server.showConnectedClients();

        String message;

        while ((message = input.readLine()) != null) {

            if (message.trim().isEmpty()) {
                continue;
            }

            Server.broadcast(
                    "[" + username + "]: " + message,
                    this);
        }

    } catch (IOException e) {
        System.out.println(
                "Connection closed for " + username);

    } finally {
        Server.removeClient(this);

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing socket.");
        }
    }
}

public synchronized void sendMessage(String message) {

    if (output != null) {
        output.println(message);
    }
}

public String getUsername() {
    return username;
}
}  

