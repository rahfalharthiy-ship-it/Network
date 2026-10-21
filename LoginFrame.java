import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class LoginFrame extends JFrame {
    private static final String Server_IP = "localhost";
    private static final int Server_port = 5000;
    private final JTextField usernameField = new JTextField(15);
    private final JButton connectButton = new JButton("Connect");
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public LoginFrame() {
        setTitle("Online Ordering System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(connectButton);
        panel.add(statusLabel);
        add(panel);
        connectButton.addActionListener(e -> connect());
    }
    private void connect() {
        String username = usernameField.getText().trim();
        connectButton.setEnabled(false);
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText("Connecting...");
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                socket = new Socket(Server_IP, Server_port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(username);
                String reply = in.readLine();
                return reply != null;
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setForeground(new Color(0, 130, 0));
                        statusLabel.setText("Connected successfully!");
                        ServerConnection servcon = new ServerConnection(in);
                        new Thread(servcon).start();
                        new BrowseItemsFrame(username, socket, out).setVisible(true);
                        dispose();
                    } else {
                        fail();
                    }
                } catch (Exception ex) {
                    fail();
                }
            }
            private void fail() {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Connection failed");
                connectButton.setEnabled(true);
            }
        }.execute();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
class ServerConnection implements Runnable {
    private final BufferedReader in;
    public ServerConnection(BufferedReader in) {
        this.in = in;
    }
    @Override
    public void run() {
        try {
            while (true) {
                String serverResponse = in.readLine();
                if (serverResponse == null) break;
                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}


