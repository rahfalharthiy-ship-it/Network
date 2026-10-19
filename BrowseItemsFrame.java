
package com.networkproject.networkproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BrowseItemsFrame extends JFrame {
    private final Color pink = new Color(190, 120, 140);
    private final Color cream = new Color(255, 248, 245);
    private final Color borderPink = new Color(235, 215, 220);
    private final JPanel contentPanel = new JPanel(new BorderLayout());

    private final Map<String, CartItem> cart = new LinkedHashMap<>();
    private JButton cartButton;
    private JButton headerBackButton;

    private static class RoundedButton extends JButton {
        private final int radius;
        private final Color fillColor;

        RoundedButton(String text, Color fillColor, Color textColor, int radius) {
            super(text);
            this.fillColor = fillColor;
            this.radius = radius;
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            setMargin(new java.awt.Insets(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = getModel().isPressed() ? fillColor.darker()
                    : getModel().isRollover() ? fillColor.brighter() : fillColor;
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class CartItem {
        private final String name;
        private final double price;
        private final int stock;
        private int quantity;

        private CartItem(String name, double price, int stock, int quantity) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.quantity = quantity;
        }
    }

    private final String[] skincareNames = {
        "Facial Cleanser", "Moisturizer", "Face Serum", "Sunscreen"
    };
    private final String[] skincareImages = {
        "Facial_Cleanser.png", "Moisturizer.png", "Face_Serum.png", "Sunscreen.png"
    };
    private final double[] skincarePrices = {35.00, 48.00, 65.00, 42.00};
    private final int[] skincareStock = {12, 8, 10, 15};

    private final String[] haircareNames = {
        "Shampoo", "Conditioner", "Hair Mask", "Hair Oil"
    };
    private final String[] haircareImages = {
        "Shampoo.png", "Conditioner.png", "Hair_Mask.png", "Hair_Oil.png"
    };
    private final double[] haircarePrices = {32.00, 34.00, 55.00, 47.00};
    private final int[] haircareStock = {10, 9, 6, 11};

    private final String[] bodyNames = {
        "Body Lotion", "Body Wash", "Body Scrub", "Body Cream"
    };
    private final String[] bodyImages = {
        "Body_Lotion.png", "Body_Wash.png", "Body_Scrub.png", "Body_Cream.png"
    };
    private final double[] bodyPrices = {39.00, 28.00, 45.00, 41.00};
    private final int[] bodyStock = {7, 14, 5, 8};

    public BrowseItemsFrame() {
        setTitle("Beauty Store");
        setSize(1000, 720);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(cream);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cream);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        headerBackButton = new RoundedButton(
                "\u2190", new Color(255, 225, 232), new Color(145, 75, 96), 22);
        headerBackButton.setFont(new Font("SansSerif", Font.BOLD, 23));
        headerBackButton.setToolTipText("Back to Categories");
        headerBackButton.setPreferredSize(new Dimension(48, 40));
        headerBackButton.addActionListener(e -> showCategories());
        headerBackButton.setVisible(false);

        JPanel leftSlot = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        leftSlot.setOpaque(false);
        leftSlot.add(headerBackButton);

        JLabel title = new JLabel("BEAUTY STORE", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(pink);

        cartButton = new RoundedButton(
                "Cart (0)", new Color(255, 225, 232), new Color(145, 75, 96), 18);
        cartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cartButton.setPreferredSize(new Dimension(112, 40));
        cartButton.addActionListener(e -> showCart());

        JPanel rightSlot = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        rightSlot.setOpaque(false);
        rightSlot.add(cartButton);

        header.setLayout(new GridLayout(1, 3));
        header.add(leftSlot);
        header.add(title);
        header.add(rightSlot);

        contentPanel.setBackground(cream);
        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        showCategories();
    }

    private void showCategories() {
        if (headerBackButton != null) {
            headerBackButton.setVisible(false);
        }
        contentPanel.removeAll();
        contentPanel.setBackground(cream);
        contentPanel.setLayout(new BorderLayout());

        JLabel heading = new JLabel("Choose a Category", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(pink);
        heading.setBorder(BorderFactory.createEmptyBorder(18, 10, 12, 10));

        JPanel categories = new JPanel(new GridLayout(1, 3, 15, 15));
        categories.setBackground(cream);
        categories.setBorder(BorderFactory.createEmptyBorder(15, 25, 30, 25));

        addCategoryCard(categories, "Skincare", "category_Skincare.png");
        addCategoryCard(categories, "Haircare", "category_Haircare.png");
        addCategoryCard(categories, "Body Care", "category_Body_Care.png");

        contentPanel.add(heading, BorderLayout.NORTH);
        contentPanel.add(categories, BorderLayout.CENTER);
        refreshContent();
    }

    private void addCategoryCard(JPanel panel, String category, String imageFile) {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(500, 285));
        card.setMinimumSize(new Dimension(260, 260));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderPink),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel imageLabel = new JLabel(loadImage(imageFile, 230, 300), SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(255, 248, 245));
        imageLabel.setPreferredSize(new Dimension(250, 300));
        card.add(imageLabel, BorderLayout.CENTER);

        JButton button = new RoundedButton(
                "<html><center><b>" + category
                + "</b><br>Explore Products</center></html>",
                new Color(255, 225, 232), new Color(145, 75, 96), 24);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(250, 58));
        button.setMinimumSize(new Dimension(180, 58));
        button.addActionListener(e -> showProducts(category));
        card.add(button, BorderLayout.SOUTH);
        panel.add(card);
    }

    private void showProducts(String category) {
        if (headerBackButton != null) {
            headerBackButton.setVisible(true);
        }
        String[] names;
        String[] images;
        double[] prices;
        int[] stock;

        switch (category) {
            case "Skincare":
                names = skincareNames;
                images = skincareImages;
                prices = skincarePrices;
                stock = skincareStock;
                break;
            case "Haircare":
                names = haircareNames;
                images = haircareImages;
                prices = haircarePrices;
                stock = haircareStock;
                break;
            default:
                names = bodyNames;
                images = bodyImages;
                prices = bodyPrices;
                stock = bodyStock;
                break;
        }

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(cream);

        JLabel heading = new JLabel(category + " Products", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(pink);
        heading.setBorder(BorderFactory.createEmptyBorder(18, 10, 12, 10));

        JPanel products = new JPanel(new GridLayout(0, 2, 12, 8));
        products.setBackground(cream);
        products.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        for (int i = 0; i < names.length; i++) {
            products.add(createProductCard(names[i], images[i], prices[i], stock[i]));
        }

        JScrollPane scrollPane = new JScrollPane(products);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cream);

        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        scrollPane.getVerticalScrollBar().setBlockIncrement(180);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane.setWheelScrollingEnabled(true);

        contentPanel.add(heading, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        refreshContent();
    }

    private JPanel createProductCard(String name, String imageFile, double price, int stock) {
        JPanel card = new JPanel();
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderPink),
                BorderFactory.createEmptyBorder(8, 9, 9, 9)));

        JLabel imageLabel = new JLabel(loadImage(imageFile, 320, 175), SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(255, 248, 245));
        imageLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(320, 145));
        imageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        imageLabel.setMinimumSize(new Dimension(100, 145));
        card.add(imageLabel);

        JLabel details = new JLabel(
                "<html><center><b>" + name + "</b><br>"
                + String.format("Price: SAR %.2f", price)
                + "<br>Stock available: " + stock + "</center></html>",
                SwingConstants.CENTER);
        details.setFont(new Font("SansSerif", Font.PLAIN, 14));
        details.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        details.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        card.add(details);

        JPanel quantityRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 7, 2));
        quantityRow.setBackground(Color.WHITE);
        quantityRow.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        int minQuantity = stock > 0 ? 1 : 0;
        JSpinner quantitySpinner = new JSpinner(
                new SpinnerNumberModel(minQuantity, minQuantity, Math.max(stock, 0), 1));
        quantitySpinner.setPreferredSize(new Dimension(65, 27));
        quantityRow.add(new JLabel("Qty:"));
        quantityRow.add(quantitySpinner);
        card.add(quantityRow);

        RoundedButton addButton = new RoundedButton(
                "Add to Cart", new Color(93, 70, 125), Color.WHITE, 24);
        addButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        addButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        addButton.setPreferredSize(new Dimension(250, 42));
        addButton.setMinimumSize(new Dimension(150, 42));
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        addButton.setEnabled(stock > 0);
        addButton.addActionListener(e -> {
            int quantity = (Integer) quantitySpinner.getValue();
            if (quantity < 1 || quantity > stock) {
                JOptionPane.showMessageDialog(this,
                        "Please choose a quantity from 1 to " + stock + ".",
                        "Check quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CartItem existing = cart.get(name);
            int alreadyInCart = existing == null ? 0 : existing.quantity;
            if (alreadyInCart + quantity > stock) {
                JOptionPane.showMessageDialog(this,
                        "You already have " + alreadyInCart + " of this item in your cart.\n"
                        + "The maximum available quantity is " + stock + ".",
                        "Stock limit reached", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (existing == null) {
                cart.put(name, new CartItem(name, price, stock, quantity));
            } else {
                existing.quantity += quantity;
            }
            updateCartButton();
            JOptionPane.showMessageDialog(this,
                    "Lovely choice!\n" + quantity + " × " + name + " added to your cart.",
                    "Added to your cart!", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel buttonRow = new JPanel(new java.awt.BorderLayout());
        buttonRow.setBackground(Color.WHITE);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        buttonRow.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        buttonRow.add(addButton, java.awt.BorderLayout.CENTER);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 47));
        card.add(buttonRow);

        return card;
    }

    private void updateCartButton() {   
        int totalQuantity = 0;
        for (CartItem item : cart.values()) {
            totalQuantity += item.quantity;
        }
        cartButton.setText("Cart (" + totalQuantity + ")");
    }

    private void showCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty for now.\n\nExplore our products and add your favourites!",
                    "Your Beauty Cart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder summary = new StringBuilder("<html><body style='width:320px'>");
        double total = 0.0;

        for (CartItem item : cart.values()) {
            double lineTotal = item.price * item.quantity;
            total += lineTotal;
            summary.append(item.name)
                    .append(" — Qty: ").append(item.quantity)
                    .append(" — SAR ").append(String.format("%.2f", lineTotal))
                    .append("<br>");
        }

        summary.append("<br><b>Total: SAR ")
                .append(String.format("%.2f", total))
                .append("</b></body></html>");

        Object[] options = {"Continue Shopping", "Empty My Cart"};
        int choice = JOptionPane.showOptionDialog(
                this, summary.toString(), "Shopping Cart",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 1) {
            cart.clear();
            updateCartButton();
        }
    }

    private ImageIcon loadImage(String fileName, int width, int height) {
        URL imageUrl = getClass().getResource("/images/" + fileName);
        if (imageUrl == null) {
            return createPlaceholderIcon(width, height, fileName);
        }
        ImageIcon original = new ImageIcon(imageUrl);
        int originalWidth = original.getIconWidth();
        int originalHeight = original.getIconHeight();

        if (originalWidth <= 0 || originalHeight <= 0) {
            return createPlaceholderIcon(width, height, fileName);
        }

        double scale = Math.min(
                (double) width / originalWidth,
                (double) height / originalHeight);
        int scaledWidth = Math.max(1, (int) Math.round(originalWidth * scale));
        int scaledHeight = Math.max(1, (int) Math.round(originalHeight * scale));

        Image scaled = original.getImage().getScaledInstance(
                scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon createPlaceholderIcon(int width, int height, String fileName) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = image.createGraphics();
        g.setColor(cream);
        g.fillRect(0, 0, width, height);
        g.setColor(pink);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Image not found:", 10, Math.max(20, height / 2 - 8));
        g.drawString(fileName, 10, Math.max(38, height / 2 + 12));
        g.dispose();
        return new ImageIcon(image);
    }

    private void refreshContent() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BrowseItemsFrame().setVisible(true));
    }
}
