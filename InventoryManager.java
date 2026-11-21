
import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

   
    private static final Map<String, Integer> stock =
            new HashMap<>();

    
    static {
        stock.put("Facial Cleanser", 20);
        stock.put("Moisturizer", 30);
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

    
    public static synchronized int getStock(String productName) {
        return stock.getOrDefault(productName, 0);
    }

    
    public static synchronized boolean isAvailable(
            String productName, int quantity) {

        if (quantity <= 0) {
            return false;
        }

        return stock.getOrDefault(productName, 0) >= quantity;
    }

   
    public static synchronized boolean deductStock(
            String productName, int quantity) {

        if (!isAvailable(productName, quantity)) {
            return false;
        }

        stock.put(productName,
                stock.get(productName) - quantity);

        return true;
    }

    
    public static synchronized boolean restoreStock(
            String productName, int quantity) {

        if (quantity <= 0 || !stock.containsKey(productName)) {
            return false;
        }

        stock.put(productName,
                stock.get(productName) + quantity);

        return true;
    }
    
    
    public static synchronized boolean deductOrder(
            Map<String, Integer> order) {

        
        for (Map.Entry<String, Integer> item : order.entrySet()) {

            String productName = item.getKey();
            int quantity = item.getValue();

            if (quantity <= 0 ||
                    !stock.containsKey(productName) ||
                    stock.get(productName) < quantity) {
                return false;
            }
        }

        
        for (Map.Entry<String, Integer> item : order.entrySet()) {

            String productName = item.getKey();
            int quantity = item.getValue();

            stock.put(productName, stock.get(productName) - quantity);
        }

        return true;
    }
    
    public static synchronized Map<String, Integer> getAllStock() {
        return new HashMap<>(stock);
    }
}
