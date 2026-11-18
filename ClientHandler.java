
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final List<ClientHandler> clients;

    private BufferedReader input;
    private PrintWriter output;
    private String username;

    // Shared stock used to check orders
    private static final Map<String, Integer> stock =
            new HashMap<>();

    static {
        stock.put("Facial Cleanser", 12);
        stock.put("Moisturizer", 8);
        stock.put("Face Serum", 10);
        stock.put("Sunscreen", 15);

        stock.put("Shampoo", 10);
        stock.put("Conditioner", 9);
        stock.put("Hair Mask", 6);
        stock.put("Hair Oil", 11);

        stock.put("Body Lotion", 7);
        stock.put("Body Wash", 14);
        stock.put("Body Scrub", 5);
        stock.put("Body Cream", 8);
    }

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

            // Receive username
            username = input.readLine();

            if (username == null || username.trim().isEmpty()) {
                return;
            }

            username = username.trim();

            // Confirm connection to client
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

                // Order request
                if (message.startsWith("ORDER|")) {

                    handleOrder(message);
                }

                // Other messages
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

            // Format:
            // ORDER|username|Product:Quantity;Product:Quantity

            String[] messageParts =
                    message.split("\\|", 3);

            if (messageParts.length != 3) {

                sendMessage(
                        "ORDER_RESULT|CANCELLED|Invalid order format");

                return;
            }

            String orderUsername = messageParts[1];

            String orderData = messageParts[2];

            // Check username
            if (!orderUsername.equals(username)) {

                sendMessage(
                        "ORDER_RESULT|CANCELLED|Username mismatch");

                return;
            }

            Map<String, Integer> order =
                    new HashMap<>();

            String[] products = orderData.split(";");

            // Read products and quantities
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

            // Check stock and confirm or cancel order
            String result = processOrder(order);

            sendMessage("ORDER_RESULT|" + result);

        } catch (Exception e) {

            sendMessage(
                    "ORDER_RESULT|CANCELLED|Error processing order");
        }
    }

    // Check stock and process the order
    private static synchronized String processOrder(
            Map<String, Integer> order) {

        // Check all quantities first
        for (String product : order.keySet()) {

            int requested = order.get(product);

            int available = stock.getOrDefault(product, 0);

            if (requested > available) {

                return "CANCELLED|Insufficient stock for "
                        + product
                        + ". Available quantity: "
                        + available;
            }
        }

        // All products are available
        // Update stock after successful validation
        for (String product : order.keySet()) {

            int requested = order.get(product);

            int available = stock.get(product);

            stock.put(product, available - requested);
        }

        return "CONFIRMED|Order placed successfully!";
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
