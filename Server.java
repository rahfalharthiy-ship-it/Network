
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Server {
private static final int PORT = 5001;
private static final int MAX_CLIENTS = 4;

private static final List<ClientHandler> clients =
        new ArrayList<>();


private static final Map<String, List<Order>> ordersByUser =
        new HashMap<>();

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

public static void saveOrder(Order order) {
    synchronized (ordersByUser) {
        ordersByUser
                .computeIfAbsent(order.getUsername(),
                        key -> new ArrayList<>())
                .add(order);
    }
}

public static List<Order> getOrdersByUser(String username) {
    synchronized (ordersByUser) {
        List<Order> orders = ordersByUser.get(username);
        if (orders == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(orders);
    }
}


public static Order findOrder(String username, long timestamp) {
    for (Order order : getOrdersByUser(username)) {
        if (order.getTimestamp() == timestamp) {
            return order;
        }
    }
    return null;
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


public static void broadcastStockUpdate(String productName) {

    int quantity = InventoryManager.getStock(productName);

    List<ClientHandler> snapshot;

    synchronized (clients) {
        snapshot = new ArrayList<>(clients);
    }

    for (ClientHandler client : snapshot) {
        client.sendMessage(
                "STOCK_RESULT|" + productName + "|" + quantity
        );
    }
}


public static int getStock(String productName) {
    return InventoryManager.getStock(productName);
}

}  
