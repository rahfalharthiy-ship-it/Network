
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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

            
            username = input.readLine();

            if (username == null || username.trim().isEmpty()) {
                return;
            }

            username = username.trim();

            
            sendMessage("CONNECTED");

            Server.broadcast(
                    username + " joined the shopping system.",
                    this);

            Server.showConnectedClients();

            String message;

            while ((message = input.readLine()) != null) {

                if (message.trim().isEmpty()) {
                    continue;
                }

                if (message.startsWith("STOCK|")) {

                    String productName = message.substring(6).trim();

                    int availableStock = Server.getStock(productName);

                    sendMessage(
                            "STOCK_RESULT|" + productName + "|" + availableStock);

                    continue;
                }
                if (message.startsWith("ORDER|")) {
                    System.out.println("Received order: " + message);
                    handleOrder(message);
                }

                else if (message.equals("GET_ORDERS")) {
                    handleGetOrders();
                }
                else if (message.startsWith("CANCEL_ORDER|")) {
                    handleCancelOrder(message);
                }
               
                else {

                    Server.broadcast(
                            "[" + username + "]: " + message,
                            this);
                }
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

    private void handleOrder(String message) {

        try {

            

            String[] messageParts =
                    message.split("\\|", 3);

            if (messageParts.length != 3) {

                sendMessage(
                        "ORDER_RESULT|CANCELLED|Invalid order format");

                return;
            }

            String orderUsername = messageParts[1];

            String orderData = messageParts[2];

           
            if (!orderUsername.equals(username)) {

                sendMessage(
                        "ORDER_RESULT|CANCELLED|Username mismatch");

                return;
            }

            Map<String, Integer> order =
                    new LinkedHashMap<>();

            String[] products = orderData.split(";");

            
            for (String productData : products) {

                String[] parts = productData.split(":");

                if (parts.length != 2) {

                    sendMessage(
                            "ORDER_RESULT|CANCELLED|Invalid order format");

                    return;
                }

                String productName = parts[0].trim();

                int quantity = Integer.parseInt(
                        parts[1].trim());

                if (quantity <= 0) {

                    sendMessage(
                            "ORDER_RESULT|CANCELLED|Invalid quantity");

                    return;
                }

                order.put(productName, quantity);
            }

            
            String result = processOrder(order);
            
            if (result.startsWith("CONFIRMED")) {
                Server.saveOrder(new Order(username, order));

                for (String productName : order.keySet()) {
                    Server.broadcastStockUpdate(productName);
                }
            }

            sendMessage("ORDER_RESULT|" + result);

        } catch (Exception e) {

            sendMessage(
                    "ORDER_RESULT|CANCELLED|Error processing order");
        }
    }

    private void handleGetOrders() {
        List<Order> orders = Server.getOrdersByUser(username);
        StringBuilder response = new StringBuilder("MY_ORDERS|");
        for (int i = 0; i < orders.size(); i++) {
            if (i > 0) {
                response.append("#");
            }
            response.append(orders.get(i).toProtocolString());
        }
        sendMessage(response.toString());
    }
    
    private void handleCancelOrder(String message) {

        try {
            String[] parts = message.split("\\|", 2);

            if (parts.length != 2) {
                return;
            }

            long timestamp = Long.parseLong(parts[1]);

            Order order = Server.findOrder(username, timestamp);

            if (order == null || order.isCancelled()) {
                handleGetOrders();
                return;
            }

            synchronized (order) {

                if (order.isCancelled()) {
                    handleGetOrders();
                    return;
                }

                for (Map.Entry<String, Integer> item
                        : order.getItems().entrySet()) {

                    InventoryManager.restoreStock(
                            item.getKey(), item.getValue());
                }

                order.cancel();
            }

           
            for (String productName : order.getItems().keySet()) {
                Server.broadcastStockUpdate(productName);
            }

            
            handleGetOrders();

        } catch (Exception e) {
            System.out.println("Error cancelling order: " + e.getMessage());
        }
    }
    
    
    private static String processOrder(
            Map<String, Integer> order) {

        
        boolean success = InventoryManager.deductOrder(order);

        if (success) {
            return "CONFIRMED|Order placed successfully!";
        }

        return "CANCELLED|Insufficient stock or invalid product";
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
