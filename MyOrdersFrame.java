import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class MyOrdersFrame extends JFrame {

    private final Color pink = new Color(190, 120, 140);
    private final Color cream = new Color(255, 248, 245);
    private final Color borderPink = new Color(235, 215, 220);


    private static MyOrdersFrame current;

    private final String username;
    private final PrintWriter out;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel emptyLabel;

    
    private final List<Order> orders = new ArrayList<>();

    private static class RoundedButton extends JButton {

        private final int radius;
        private final Color fillColor;

        RoundedButton(String text, Color fillColor,
                      Color textColor, int radius) {

            super(text);

            this.fillColor = fillColor;
            this.radius = radius;

            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR));
            setMargin(new java.awt.Insets(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D) graphics.create();

            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = getModel().isPressed()
                    ? fillColor.darker()
                    : getModel().isRollover()
                    ? fillColor.brighter()
                    : fillColor;

            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                    radius, radius);
            g2.dispose();

            super.paintComponent(graphics);
        }
    }

    public static void open(String username, PrintWriter out) {

        SwingUtilities.invokeLater(() -> {

            if (current == null || !current.isDisplayable()) {
                current = new MyOrdersFrame(username, out);
                current.setVisible(true);
            } else {
                current.toFront();
            }

            current.requestOrders();
        });
    }

    public static void showOrders(String message) {

        SwingUtilities.invokeLater(() -> {

            if (current != null && current.isDisplayable()) {
                current.updateTable(message);
            }
        });
    }

    

    private MyOrdersFrame(String username, PrintWriter out) {

        this.username = username;
        this.out = out;

        setTitle("Glowzy - My Orders");
        setSize(850, 550);
        setMinimumSize(new Dimension(650, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(cream);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBackground(cream);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel heading = new JLabel("My Orders", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(pink);
        mainPanel.add(heading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"Timestamp", "Products", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(borderPink);
        table.setSelectionBackground(new Color(255, 225, 232));
        table.setSelectionForeground(new Color(100, 60, 75));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        
        table.getColumnModel().getColumn(2).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object value, boolean selected,
                            boolean focus, int row, int column) {

                        Component c = super
                                .getTableCellRendererComponent(
                                        t, value, selected, focus,
                                        row, column);

                        setHorizontalAlignment(SwingConstants.CENTER);

                        if ("Cancelled".equals(value)) {
                            c.setForeground(new Color(190, 50, 50));
                        } else {
                            c.setForeground(new Color(0, 130, 0));
                        }

                        return c;
                    }
                });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(borderPink));
        scrollPane.getViewport().setBackground(Color.WHITE);

        emptyLabel = new JLabel(
                "You have no orders yet.", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emptyLabel.setForeground(pink);
        emptyLabel.setVisible(false);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(cream);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(emptyLabel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        RoundedButton refreshButton = new RoundedButton(
                "Refresh", new Color(255, 225, 232),
                new Color(145, 75, 96), 18);
        refreshButton.addActionListener(e -> requestOrders());

        RoundedButton closeButton = new RoundedButton(
                "Close", pink, Color.WHITE, 18);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonsPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(cream);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(closeButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    
    public void requestOrders() {

        if (out == null) {
            return;
        }

        out.println("GET_ORDERS");
    }

    public Order getSelectedOrder() {

        int row = table.getSelectedRow();

        if (row < 0 || row >= orders.size()) {
            return null;
        }

        return orders.get(row);
    }

    private void updateTable(String message) {

        orders.clear();
        tableModel.setRowCount(0);

        String[] parts = message.split("\\|", 2);

        if (parts.length == 2 && !parts[1].trim().isEmpty()) {

            for (String text : parts[1].split("#")) {

                Order order = Order.fromProtocolString(username, text);

                if (order != null) {
                    orders.add(order);
                }
            }
        }
        
        orders.sort((a, b) ->
                Long.compare(b.getTimestamp(), a.getTimestamp()));

        for (Order order : orders) {

            tableModel.addRow(new Object[]{
                    order.getFormattedTime(),
                    order.getItemsSummary(),
                    order.getStatusLabel()
            });
        }

        emptyLabel.setVisible(orders.isEmpty());
    }
}
