import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Order {

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    private static final AtomicLong lastTimestamp = new AtomicLong(0);

    private final long timestamp;
    private final String username;
    private final Map<String, Integer> items;
    private String status;

    public Order(String username, Map<String, Integer> items) {
        this(nextTimestamp(), username, items, CONFIRMED);
    }

    public Order(long timestamp, String username,
                 Map<String, Integer> items, String status) {

        this.timestamp = timestamp;
        this.username = username;
        this.items = new LinkedHashMap<>(items);
        this.status = status;
    }

    private static long nextTimestamp() {
        long now = System.currentTimeMillis();
        return lastTimestamp.updateAndGet(
                previous -> Math.max(previous + 1, now));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized boolean isCancelled() {
        return CANCELLED.equals(status);
    }

    public synchronized boolean cancel() {

        if (isCancelled()) {
            return false;
        }

        status = CANCELLED;
        return true;
    }


    public String getFormattedTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(timestamp));
    }

    public String getStatusLabel() {
        return isCancelled() ? "Cancelled" : "Confirmed";
    }

    public String getItemsSummary() {

        StringBuilder summary = new StringBuilder();

        for (Map.Entry<String, Integer> item : items.entrySet()) {

            if (summary.length() > 0) {
                summary.append(", ");
            }

            summary.append(item.getKey())
                    .append(" x")
                    .append(item.getValue());
        }

        return summary.toString();
    }


    public String toProtocolString() {

        StringBuilder text = new StringBuilder();

        text.append(timestamp).append(",")
                .append(getStatus()).append(",");

        boolean first = true;

        for (Map.Entry<String, Integer> item : items.entrySet()) {

            if (!first) {
                text.append(";");
            }

            text.append(item.getKey())
                    .append(":")
                    .append(item.getValue());

            first = false;
        }

        return text.toString();
    }

    // Returns null if the text is not a valid order
    public static Order fromProtocolString(String username, String text) {

        try {

            String[] parts = text.split(",", 3);

            if (parts.length != 3) {
                return null;
            }

            long timestamp = Long.parseLong(parts[0].trim());
            String status = parts[1].trim();

            Map<String, Integer> items = new LinkedHashMap<>();

            for (String product : parts[2].split(";")) {

                String[] data = product.split(":");

                if (data.length != 2) {
                    return null;
                }

                items.put(data[0].trim(),
                        Integer.parseInt(data[1].trim()));
            }

            return new Order(timestamp, username, items, status);

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
