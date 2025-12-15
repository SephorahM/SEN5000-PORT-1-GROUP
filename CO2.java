import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;


public class CO2 {

    // Update main method
    public static void main(String[] args) {
        String host = "localhost"; // Default IP address
        int port = 6060;           // Default port number

        if (args.length >= 2) {
            host = args[0];        // Use provided IP address
            port = Integer.parseInt(args[1]); // Use provided port number
        }

        CO2ClientSocket.setServerConfig(host, port); // Connect to the server
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CO2 reading tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);
            showInitialPage(frame);  // Changed from showLoginPage to showInitialPage
            frame.setVisible(true);
        });
    }

    private static void showInitialPage(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to CO2 Tracker", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0, 102, 204));

        // Connection Status Label
        JLabel connectionLabel = new JLabel("Connected to server at " + CO2ClientSocket.getHost() + ":" + CO2ClientSocket.getPort(), JLabel.CENTER);
        connectionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        connectionLabel.setForeground(new Color(0, 128, 0));

        // Buttons
        JButton clientButton = new JButton("Go to Login");
        Dimension buttonSize = new Dimension(200, 40);
        clientButton.setPreferredSize(buttonSize);
        clientButton.setFont(new Font("Arial", Font.BOLD, 16));
        clientButton.addActionListener(e -> showLoginPage(frame));

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(connectionLabel, gbc); // Add connection status label

        gbc.gridy = 2;
        panel.add(clientButton, gbc);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
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
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(errorLabel, gbc);


        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setPreferredSize(new Dimension(200, 25));
        
        JLabel copyrightLabel = new JLabel("© 2025 CO2 tracker, Cardiff, UK", JLabel.CENTER);
        copyrightLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        copyrightLabel.setForeground(Color.MAGENTA);

        // Update login validation
        loginButton.addActionListener(e -> {
            String userId = UserIDfield.getText().trim();
            String password = new String(passwordField.getPassword());
            
            // Send login request to the server
            String response = CO2ClientSocket.sendToServer("LOGIN," + userId + "," + password);

            if (response != null && response.startsWith("OK")) {
                // Extract user name and server message from the response
                String[] parts = response.split(",", 3);
                String userName = parts.length > 1 ? parts[1] : "User";
                String serverMessage = parts.length > 2 ? parts[2] : "Login successful.";

                // Show the server's message as a pop-up
                JOptionPane.showMessageDialog(
                    frame,
                    serverMessage,
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Open the CO2 reading input window
                showCO2ReadingPage(frame, userId, userName);
            }   // Clear the text fields for User ID and Password
                UserIDfield.setText("");
                passwordField.setText("");
            });
    

        // Open the Create Account window when the button is clicked
        createAccountButton.addActionListener(e ->
           // UserIDfield.setText("");  // Clear user ID field
           // passwordField.setText(""); // Clear password field
           // errorLabel.setText("");    // Clear any error messages
            showCreateAccountPage(frame));
        

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
        panel.add(new JLabel("PASSWORD:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

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

        // Add document listener to UserIDfield to only accept numbers
        UserIDfield.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void update(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = UserIDfield.getText();
                    if (!text.matches("\\d*")) {
                        UserIDfield.setText(text.replaceAll("[^\\d]", ""));
                    }
                });
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        });

        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.pack();
        frame.revalidate();
        frame.repaint();
 
    }

    private static void showCreateAccountPage(JFrame parentFrame) {
        JFrame createFrame = new JFrame("Create Account");
        createFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createFrame.setSize(420, 300); // Adjusted size to fit the password strength bar
        createFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("Let's create an account", JLabel.CENTER);
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

        // Password strength label + bar
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

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(errorLabel, gbc);

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

        // Add validation for User ID length
        newUserField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void update(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = newUserField.getText();
                    if (!text.matches("\\d*")) {
                        newUserField.setText(text.replaceAll("[^\\d]", ""));
                    }
                    if (text.length() > 8) {
                        newUserField.setText(text.substring(0, 8)); // Limit to 8 digits
                    }
                });
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        });

        // Update password strength as user types
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String pwd = new String(passwordField.getPassword());
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
                    pwdStrengthBar.setForeground(new Color(255, 140, 0)); // Dark orange
                } else {
                    val = 100;
                    pwdStrengthBar.setForeground(new Color(0, 128, 0)); // Green
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
            String pwd = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());

            if (name.isEmpty() || newUser.isEmpty() || pwd.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            if (newUser.length() < 6 || newUser.length() > 8) {
                errorLabel.setText("User ID must be between 6 and 8 digits.");
                return;
            }

            if (!pwd.equals(confirm)) {
                // Show a popup message for mismatched passwords
                JOptionPane.showMessageDialog(
                    createFrame,
                    "Passwords do not match. Please try again.",
                    "Password Mismatch",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Send create user request to the server
            String message = "CREATE_USER," + newUser + "," + name + "," + pwd;
            String response = CO2ClientSocket.sendToServer(message);

            // Display server response in the error label
            if (response != null && response.startsWith("OK")) {
                errorLabel.setForeground(Color.GREEN);
                errorLabel.setText("Account created successfully!");
            } else {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(response != null ? response : "No response from server.");
            }
        });

        cancelBtn.addActionListener(e -> createFrame.dispose());

        createFrame.setContentPane(panel);
        createFrame.pack();
        createFrame.setVisible(true);
    }

    private static void showCO2ReadingPage(JFrame parentFrame, String userId, String userName) {
        //User user = users.get(userId);
        /*sendToServer("LOGIN:" + userId + ";" + password);
        String userName = user != null ? user.getName() : userId;*/

        JFrame co2Frame = new JFrame("CO2 Reading Input");
        co2Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        co2Frame.setSize(500, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 20, 15);
        panel.add(welcomeLabel, gbc);

        // Reset insets
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridwidth = 1;

        Dimension fieldSize = new Dimension(250, 30);

        // Add User ID field back (now editable)
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(userIdLabel, gbc);
        
        gbc.gridx = 1;
        JTextField userIdField = new JTextField(userId);
        userIdField.setPreferredSize(fieldSize);
        panel.add(userIdField, gbc);

        // Postcode (now at position 1 instead of 2)
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel postcodeLabel = new JLabel("Postcode:");
        postcodeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(postcodeLabel, gbc);
        
        gbc.gridx = 1;
        JTextField postcodeField = new JTextField();
        postcodeField.setPreferredSize(fieldSize);
        panel.add(postcodeField, gbc);

        // CO2 Reading (now at position 2 instead of 3)
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel co2Label = new JLabel("CO2 (ppm):");
        co2Label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(co2Label, gbc);
        
        gbc.gridx = 1;
        JTextField co2Field = new JTextField();
        co2Field.setPreferredSize(fieldSize);
        panel.add(co2Field, gbc);

        // Submit button (now at position 3 instead of 4)
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 10, 15);
        JButton submitButton = new JButton("Submit Reading");
        submitButton.setPreferredSize(new Dimension(200, 35));
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(submitButton, gbc);

        // Error label (now at position 4 instead of 5)
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 15, 10, 15);
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(errorLabel, gbc);

        // Add document listener to CO2 field to only accept numbers and decimal point
        co2Field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void update(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = co2Field.getText();
                    if (!text.matches("\\d*\\.?\\d*")) {
                        co2Field.setText(text.replaceAll("[^\\d.]", ""));
                    }
                });
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        });

        // Add document listener to postcode field to only accept numbers with max length
        postcodeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void update(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = postcodeField.getText();
                    if (!text.matches("\\d{0,9}")) {
                        postcodeField.setText(text.replaceAll("[^\\d]", "").substring(0, Math.min(text.length(), 9)));
                    }
                });
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        });

        // Modify submit button action listener
        submitButton.addActionListener(e -> {
            try {
                String postcode = postcodeField.getText().trim();
                String co2Reading = co2Field.getText().trim();

                if (postcode.isEmpty() || co2Reading.isEmpty()) {
                    errorLabel.setText("Please fill in all fields.");
                    return;
                }

                double co2Value = Double.parseDouble(co2Reading);

                if (co2Value <= 0) {
                    errorLabel.setText("CO2 value must be positive.");
                    return;
                }

                // Send CO2 reading to the server
                String message = "SEND_READING," + userId + "," + userName + "," + postcode + "," + co2Value;
                String response = CO2ClientSocket.sendToServer(message);

                // Display server response in the error label
                if (response.startsWith("OK")) {
                    errorLabel.setForeground(Color.GREEN);
                    errorLabel.setText("Reading submitted successfully!");
                } else {
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setText(response);
                }

                // Clear input fields
                postcodeField.setText("");
                co2Field.setText("");
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid CO2 reading format!");
            }
        });

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 20, 15);

        JButton viewAnalysisButton = new JButton("View CO2 Analysis");
        viewAnalysisButton.setPreferredSize(new Dimension(200, 35));
        viewAnalysisButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(viewAnalysisButton, gbc);

        viewAnalysisButton.addActionListener(e -> {
            CO2Analyser.showAnalysisPage(co2Frame);
        });

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 20, 15);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(200, 35));
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(255, 0, 0));
        logoutButton.setForeground(Color.WHITE);
        panel.add(logoutButton, gbc);

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                co2Frame,
                "Are you sure you want to log out?",
                "Logout Confirmed",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                co2Frame.dispose();
                showLoginPage(parentFrame);
            }
        });

        co2Frame.setContentPane(panel);
        co2Frame.setLocationRelativeTo(parentFrame);
        co2Frame.pack();
        co2Frame.setVisible(true);
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

