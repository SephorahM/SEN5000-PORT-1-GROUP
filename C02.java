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

        // Open the Create Account window when the button is clicked
        createAccountButton.addActionListener(e -> showCreateAccountPage(frame));

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

        gbc.gridy = 4;
        panel.add(createAccountButton, gbc);

        gbc.gridy = 5;
        panel.add(errorLabel, gbc);

        gbc.gridy = 6;
        panel.add(copyrightLabel, gbc);

        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.pack();
        frame.revalidate();
        frame.repaint();
 
    }

    private static void showCreateAccountPage(JFrame parentFrame) {
        JFrame createFrame = new JFrame("Create Account");
        createFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createFrame.setSize(420, 260);
        createFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("lets create an account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        // Name
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("NAME:"), gbc);
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // User ID
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("USER ID:"), gbc);
        JTextField newUserField = new JTextField();
        newUserField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        panel.add(newUserField, gbc);

        // Password
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("PASSWORD:"), gbc);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

    // Password strength label + bar (below password field)
    gbc.gridy = 4;
    gbc.gridx = 1;
    JLabel pwdStrengthLabel = new JLabel("");
    pwdStrengthLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    JProgressBar pwdStrengthBar = new JProgressBar(0, 100);
    pwdStrengthBar.setStringPainted(true);
    pwdStrengthBar.setValue(0);
    pwdStrengthBar.setPreferredSize(new Dimension(200, 12));
    JPanel strengthPanel = new JPanel();
    strengthPanel.setLayout(new BorderLayout(0, 4));
    strengthPanel.setOpaque(false);
    // hide the textual label — only show percentage on the bar
    pwdStrengthLabel.setVisible(false);
    // (label kept in layout but hidden to avoid layout shift)
    strengthPanel.add(pwdStrengthLabel, BorderLayout.NORTH);
    strengthPanel.add(pwdStrengthBar, BorderLayout.SOUTH);
    panel.add(strengthPanel, gbc);

        // Confirm Password
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("CONFIRM PASSWORD:"), gbc);
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Buttons
        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(createBtn);
        btnPanel.add(cancelBtn);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnPanel, gbc);

        // Update password strength as user types — show percent on bar only
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String pwd = new String(passwordField.getPassword());
                // compute score (same rules as before) and map to percent
                int score = 0;
                if (pwd.length() >= 8) score++;
                if (pwd.matches(".*\\d.*")) score++;
                if (pwd.matches(".*[a-z].*") && pwd.matches(".*[A-Z].*")) score++;
                if (pwd.matches(".*[^a-zA-Z0-9].*")) score++;
                int val;
                if (pwd.isEmpty()) {
                    val = 0;
                    pwdStrengthBar.setForeground(Color.LIGHT_GRAY);
                } else if (score <= 1) {
                    val = 33;
                    pwdStrengthBar.setForeground(Color.RED);
                } else if (score == 2) {
                    val = 66;
                    pwdStrengthBar.setForeground(new Color(255, 140, 0)); // dark orange
                } else {
                    val = 100;
                    pwdStrengthBar.setForeground(new Color(0, 128, 0)); // green
                }
                pwdStrengthBar.setValue(val);
                pwdStrengthBar.setString(val + "%");
             }

             public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
             public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
             public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
         });

        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String newUser = newUserField.getText().trim();
            char[] pwd = passwordField.getPassword();
            char[] confirm = confirmPasswordField.getPassword();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "Please enter your name.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newUser.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "Please enter a user id.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // User ID must be numeric and between 7 and 9 digits
            if (!newUser.matches("\\d{7,9}")) {
                JOptionPane.showMessageDialog(createFrame, "User ID must be 7 to 9 digits.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pwd == null || pwd.length == 0) {
                JOptionPane.showMessageDialog(createFrame, "Please enter a password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!Arrays.equals(pwd, confirm)) {
                JOptionPane.showMessageDialog(createFrame, "Password and confirm password do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
                // clear sensitive data
                Arrays.fill(pwd, '\0');
                Arrays.fill(confirm, '\0');
                return;
            }
            // Success - placeholder action
            JOptionPane.showMessageDialog(createFrame, "Account created for: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
            // clear password arrays for security
            Arrays.fill(pwd, '\0');
            Arrays.fill(confirm, '\0');
            createFrame.dispose();
        });

        cancelBtn.addActionListener(e -> createFrame.dispose());

        createFrame.setContentPane(panel);
        createFrame.pack();
        createFrame.setVisible(true);
    }

    private static String passwordStrength(String pwd) {
        if (pwd == null || pwd.isEmpty()) return "";
        int score = 0;
        if (pwd.length() >= 8) score++;
        if (pwd.matches(".*\\d.*")) score++;
        if (pwd.matches(".*[a-z].*") && pwd.matches(".*[A-Z].*")) score++;
        if (pwd.matches(".*[^a-zA-Z0-9].*")) score++;
        if (score <= 1) return "Weak";
        if (score == 2) return "Medium";
        return "Strong";
    }
}
