import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;


public class CO2 {

    // Update main method
    public static void main(String[] args) {
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

            // Buttons
        JButton clientButton = new JButton("Go to Login");
        
        // Style buttons
        Dimension buttonSize = new Dimension(200, 40);
        clientButton.setPreferredSize(buttonSize);
        clientButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Keep client button action listener to show login page
        clientButton.addActionListener(e -> showLoginPage(frame));

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(clientButton,gbc);

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
            
            String response = CO2ClientSocket.sendToServer("LOGIN," + userId + "," + password);

            if (response != null && response.startsWith("OK")) {
                String userName = response.split(",", 2)[1];
                showCO2ReadingPage(frame, userId, userName);
            } else {
                errorLabel.setText(response == null ? "No response from server." : response);
            }
        });

            /*if (userId.isEmpty() || password.length == 0) {
                errorLabel.setText("Please enter both User ID and Password!");
                return;
            }
            
            User user = users.get(userId);
            if (user == null) {
                errorLabel.setText("User ID not found!");
                return;
            }
            
            if (password.length < 8) {
                errorLabel.setText("Invalid password!");
                return;
            }
            
            errorLabel.setText("");
            // Clear sensitive data
            Arrays.fill(password, '\0');
            
            // Clear login fields before opening new window
            UserIDfield.setText("");
            passwordField.setText("");
            
            // Open CO2 reading window
            showCO2ReadingPage(frame, userId);
        });*/

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
            String pwd = new String(passwordField.getPassword());
            String confirm = new String (confirmPasswordField.getPassword());
            
            if (name.isEmpty() || newUser.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "All fields required");
                return;
            }

            if (!pwd.equals(confirm)) {
                JOptionPane.showMessageDialog(createFrame, "Passwords do not match");
                return;
            }

            String message = "CREATE_USER," + newUser + "," + name + "," + pwd;

            String response = CO2ClientSocket.sendToServer(message);
    
            /*if (name.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "Please enter your name.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if user ID exists and belongs to a different name
            User existingUser = users.get(newUser);
            if (existingUser != null && !existingUser.getName().equals(name)) {
                JOptionPane.showMessageDialog(createFrame, 
                    "This User ID is already registered to a different name.", 
                    "Validation", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Rest of validation
            if (newUser.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "Please enter a user id.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newUser.matches("\\d{7,9}")) {
                JOptionPane.showMessageDialog(createFrame, "User ID must be 7 to 9 digits.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Add check for existing user ID
            if (existingUserIds.contains(newUser)) {
                JOptionPane.showMessageDialog(createFrame, "This User ID is already taken. Please choose another.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pwd == null || pwd.length < 8) {
                JOptionPane.showMessageDialog(createFrame, "Password must be at least 8 characters long.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!Arrays.equals(pwd, confirm)) {
                JOptionPane.showMessageDialog(createFrame, "Password and confirm password do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
                Arrays.fill(pwd, '\0');
                Arrays.fill(confirm, '\0');
                return;
            }
            // Add the user ID to the set of existing IDs
            User newUserObj = new User(newUser, name, new String(pwd));
            users.put(newUser, newUserObj);
            existingUserIds.add(newUser);
            //saveUsers(); // Save updated users list
            String command = "CREATE_USER;" + newUser + ";" + name + ";" + new String(pwd);
            String response = sendToServer(command);
            
            if (response.startsWith("OK")) {
                JOptionPane.showMessageDialog(createFrame, "Account created!");
                createFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(createFrame, "Error: " + response);
            }*/

            // Success - placeholder action
            if (response.startsWith("OK")) {
                JOptionPane.showMessageDialog(createFrame, "Account created for: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
            // clear password arrays for security
            //Arrays.fill(pwd, '\0');
            //Arrays.fill(confirm, '\0');
                createFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(createFrame, response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> createFrame.dispose());

        // Add document listener to newUserField to only accept numbers
        newUserField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void update(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = newUserField.getText();
                    if (!text.matches("\\d*")) {
                        newUserField.setText(text.replaceAll("[^\\d]", ""));
                    }
                });
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        });

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

                String message = "SEND_READING,"
                    + userId + ","
                    + userName + ","
                    + postcode + ","
                    + co2Value;
        
                String response = CO2ClientSocket.sendToServer(message);

                JOptionPane.showMessageDialog(
                    parentFrame,
                    response,
                    "Server Response",
                    JOptionPane.INFORMATION_MESSAGE
                );

                postcodeField.setText("");
                co2Field.setText("");
                errorLabel.setText("");

             } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid CO2 reading format!");
            }
        
        });
            // Send to server using socket client
            /*boolean ok = CO2ClientSocket.sendReadingToServer(csvLine);

            if (ok) {
                JOptionPane.showMessageDialog(co2Frame,
                        "Reading sent to server successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                postcodeField.setText("");
                co2Field.setText("");
                postcodeField.requestFocus();
            } else {
                errorLabel.setText("Could not connect to server!");
            }*/

        
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

