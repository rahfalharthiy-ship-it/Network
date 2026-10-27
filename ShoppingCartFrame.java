


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ShoppingCartFrame extends JFrame {

    private final Color pink = new Color(190, 120, 140);
    private final Color cream = new Color(255, 248, 245);
    private final Color borderPink = new Color(235, 215, 220);

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
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(graphics);
        }
    }

    private final List<CartProduct> products;
    private final Consumer<String> onDelete;

    public static class CartProduct {
        private final String name;
        private final String imageFile;
        private final double price;
        private final int quantity;

        public CartProduct(String name, String imageFile,
                           double price, int quantity) {
            this.name = name;
            this.imageFile = imageFile;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public ShoppingCartFrame(
            List<CartProduct> products,
            Consumer<String> onDelete) {

        this.products = products;
        this.onDelete = onDelete;

        setTitle("Beauty Store - Shopping Cart");
        setSize(850, 650);
        setMinimumSize(new Dimension(650, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createCartGUI();
    }

    private void createCartGUI() {
        JPanel mainPanel = new JPanel(
                new BorderLayout(12, 12));
        mainPanel.setBackground(cream);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel heading = new JLabel(
                "Shopping Cart", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(pink);
        mainPanel.add(heading, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(
                new javax.swing.BoxLayout(
                        itemsPanel, javax.swing.BoxLayout.Y_AXIS));
        itemsPanel.setBackground(cream);

        double total = 0.0;

        if (products.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "Your cart is empty.",
                    SwingConstants.CENTER);
            emptyLabel.setFont(
                    new Font("SansSerif", Font.PLAIN, 18));
            emptyLabel.setForeground(pink);
            itemsPanel.add(emptyLabel);
        } else {
            for (CartProduct product : products) {
                double subtotal = product.price * product.quantity;
                total += subtotal;

                JPanel itemPanel = new JPanel(
                        new BorderLayout(15, 8));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(borderPink),
                                BorderFactory.createEmptyBorder(
                                        10, 10, 10, 10)));

                itemPanel.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 125));
                itemPanel.setAlignmentX(
                        java.awt.Component.LEFT_ALIGNMENT);

                JLabel imageLabel = new JLabel(
                        loadImage(product.imageFile, 90, 95));
                imageLabel.setPreferredSize(
                        new Dimension(100, 95));
                imageLabel.setHorizontalAlignment(
                        SwingConstants.CENTER);
                itemPanel.add(imageLabel, BorderLayout.WEST);

                JPanel details = new JPanel();
                details.setOpaque(false);
                details.setLayout(
                        new javax.swing.BoxLayout(
                                details, javax.swing.BoxLayout.Y_AXIS));

                JLabel nameLabel = new JLabel(product.name);
                nameLabel.setFont(
                        new Font("SansSerif", Font.BOLD, 16));
                nameLabel.setForeground(
                        new Color(100, 60, 75));
                details.add(nameLabel);

                details.add(
                        javax.swing.Box.createVerticalStrut(5));

                details.add(new JLabel(
                        "Quantity: " + product.quantity));

                details.add(new JLabel(
                        String.format("Unit price: SAR %.2f",
                                product.price)));

                JLabel subtotalLabel = new JLabel(
                        String.format("Subtotal: SAR %.2f",
                                subtotal));
                subtotalLabel.setFont(
                        new Font("SansSerif", Font.BOLD, 14));
                subtotalLabel.setForeground(pink);
                details.add(subtotalLabel);

                itemPanel.add(details, BorderLayout.CENTER);

                RoundedButton deleteButton = new RoundedButton(
                        "Delete ",
                        pink,
                        Color.WHITE,
                        12);

                deleteButton.setFont(
                        new Font("SansSerif", Font.BOLD, 11));
                deleteButton.setPreferredSize(
                        new Dimension(70, 30));
                deleteButton.setMinimumSize(
                        new Dimension(70, 30));
                deleteButton.setMaximumSize(
                        new Dimension(70, 30));

                deleteButton.addActionListener(e -> {
                    products.remove(product);
                    onDelete.accept(product.name);

                    getContentPane().removeAll();
                    createCartGUI();
                    revalidate();
                    repaint();
                });

                JPanel deletePanel = new JPanel(
                        new java.awt.FlowLayout(
                                java.awt.FlowLayout.CENTER, 0, 0));
                deletePanel.setOpaque(false);
                deletePanel.add(deleteButton);

                itemPanel.add(deletePanel, BorderLayout.EAST);

                itemsPanel.add(itemPanel);
                itemsPanel.add(
                        javax.swing.Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cream);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
                new BorderLayout(10, 10));
        bottomPanel.setBackground(cream);

        JLabel totalLabel = new JLabel(
                String.format("Total: SAR %.2f", total));
        totalLabel.setFont(
                new Font("SansSerif", Font.BOLD, 21));
        totalLabel.setForeground(pink);
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        RoundedButton continueButton = new RoundedButton(
                "Continue Shopping",
                new Color(255, 225, 232),
                new Color(145, 75, 96),
                18);

        continueButton.addActionListener(e -> dispose());

        RoundedButton orderButton = new RoundedButton(
                "Order",
                pink,
                Color.WHITE,
                18);

        orderButton.addActionListener(e -> {
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Your cart is empty.",
                        "Shopping Cart",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Order placed successfully!",
                        "Order Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        JPanel buttonsPanel = new JPanel(
                new java.awt.FlowLayout(
                        java.awt.FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(cream);
        buttonsPanel.add(continueButton);
        buttonsPanel.add(orderButton);

        bottomPanel.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private ImageIcon loadImage(
            String fileName, int width, int height) {

        URL imageURL = getClass().getResource(
                "/images/" + fileName);

        if (imageURL == null) {
            return new ImageIcon(
                    new java.awt.image.BufferedImage(
                            width, height,
                            java.awt.image.BufferedImage.TYPE_INT_ARGB));
        }

        ImageIcon original = new ImageIcon(imageURL);
        Image scaled = original.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShoppingCartFrame(
                    new ArrayList<>(),
                    name -> {
                    }).setVisible(true);
        });
    }
}