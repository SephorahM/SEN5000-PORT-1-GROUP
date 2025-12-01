/*public class commented_code {

/*class Transaction implements Serializable {
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

class User implements Serializable {
    private final String userId;
    private final String name;
    private final String password;

    public User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
}*/

    //private static Set<String> existingUserIds = new HashSet<>();
    //private static Map<String, User> users = new HashMap<>();
   // private static final String CSV_FILE = "co2_readings.csv";
   // private static final String USERS_CSV = "users.csv";  // New constant

    // Add new method to save users
    /*private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_CSV))) {
            writer.write("UserID,Name,Password\n");
            for (User user : users.values()) {
                writer.write(String.format("%s,%s,%s%n", 
                    user.getUserId(), 
                    user.getName(), 
                    user.getPassword()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Add new method to load users
    /*private static void loadUsers() {
        File file = new File(USERS_CSV);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("UserID,Name,Password\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    User user = new User(parts[0], parts[1], parts[2]);
                    users.put(parts[0], user);
                    existingUserIds.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Add this method to initialize CSV file
    /*private static void initializeCSV() {
        File file = new File(CSV_FILE);
        System.out.println("CSV File path: " + file.getAbsolutePath());
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Timestamp,UserID,Name,Postcode,CO2_PPM\n");
                    writer.flush();
                }
                System.out.println("Created new CSV file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error creating CSV file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }*/


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

            //User user = users.get(userId);
        /*sendToServer("LOGIN:" + userId + ";" + password);
        String userName = user != null ? user.getName() : userId;

        JFrame co2Frame = new JFrame("CO2 Reading Input");
        co2Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        co2Frame.setSize(500, 400);

        /*try {
                double co2Value = Double.parseDouble(co2Reading);
                if (co2Value <= 0) {
                    errorLabel.setText("CO2 reading must be positive!");
                    return;
                }

                String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                
                // Write directly to CSV file
String csvLine = String.format("%s,%s,%s,%s,%.2f",
        timestamp, submittedUserId, userName, postcode, co2Value);

}*/
