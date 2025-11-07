import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;


class Transaction implements Serializable {
    private final Date timestamp;
    private final String type;
    private final double reading;

    public Transaction(String type, double reading) {
        this.timestamp = new Date();
        this.type = type;
        this.reading = reading;
    }

    public String getFormattedTransaction() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %-15s £%.2f", sdf.format(timestamp), type, reading);
    }
}
public class C02 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CO2 reading tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);
            showLoginPage(frame);
            frame.setVisible(true);
        });
    }

    private static void showLoginPage(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel welcomeLabel = new JLabel("Welcome to CO2 reading tracker", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);

        JTextField UserIDfield = new JTextField();
        UserIDfield.setPreferredSize(new Dimension(200, 25));
        JTextField postcodeField = new JTextField();
        postcodeField.setPreferredSize(new Dimension(200, 25));
    JButton loginButton = new JButton("Login");
    JButton createAccountButton = new JButton("Create Account");
    createAccountButton.setPreferredSize(new Dimension(200, 25));
    JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        JLabel copyrightLabel = new JLabel("© 2025 CO2 tracker, Cardiff, UK", JLabel.CENTER);
        copyrightLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        copyrightLabel.setForeground(Color.MAGENTA); // Set color to black

        // Add postcode length validation for login
        loginButton.addActionListener(e -> {
            String postcode = postcodeField.getText().trim();
            int length = postcode.length();
            if (length < 5 || length > 9) {
                errorLabel.setText("Postcode must be between 5 and 9 characters!");
                return;
            }
            errorLabel.setText("");
            // Continue with login process (placeholder)
            JOptionPane.showMessageDialog(frame, "Login clicked", "Login", JOptionPane.INFORMATION_MESSAGE);
        });

        // Simple action for Create Account button
        createAccountButton.addActionListener(e -> {
            // Placeholder action: show a dialog — replace with account creation flow as needed
            JOptionPane.showMessageDialog(frame, "Create account clicked", "Create Account", JOptionPane.INFORMATION_MESSAGE);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("USER ID:"), gbc);

        gbc.gridx = 1;
        panel.add(UserIDfield, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("POSTCODE:"), gbc);

        gbc.gridx = 1;
        panel.add(postcodeField, gbc);

        // Place buttons stacked vertically and centered
        gbc.gridx = 0;
        gbc.gridy = 3;
    gbc.gridwidth = 2; // span both columns so component is centered
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(loginButton, gbc);

    gbc.gridy = 5;
    panel.add(createAccountButton, gbc);

    gbc.gridy = 6;
    panel.add(errorLabel, gbc);

    gbc.gridy = 7;
    panel.add(copyrightLabel, gbc);

        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.pack();
        frame.revalidate();
        frame.repaint();
 
    }
}
