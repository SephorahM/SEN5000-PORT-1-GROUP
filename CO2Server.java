import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.*; // Import for JOptionPane

public class CO2Server {

    private static final int PORT = 6060;  // Changed from 5050 to 6060
    private static final int MAX_CLIENTS = 4;

    private static final String USERS_CSV = "users.csv";
    private static final String READINGS_CSV = "co2_readings.csv";

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running = false;

    private final Semaphore clientLimiter = new Semaphore(MAX_CLIENTS);

    public static void main(String[] args) {
        new CO2Server().start();
    }
    
    public void start() {
        initialiseCSV();
        running = true;

        try {
            ServerSocket server = new ServerSocket(PORT);
            this.serverSocket = server;
            System.out.println("CO2 Server running on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();

                // Limit the number of concurrent clients to MAX_CLIENTS (4)
                if (!clientLimiter.tryAcquire()) {
                    System.out.println("Connection denied (server full)");
                    clientSocket.close();
                    continue;
                }

                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Start a new thread to handle the client
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server stopped due to an error.");
            e.printStackTrace();  // Log the exception for debugging
        }
    }

    /*public void startServer() {
    if (running) {
        log("Server already running");
        return;
    }

    running = true;
    startButton.setEnabled(false);
    stopButton.setEnabled(true);

    serverThread = new Thread(this);  // THIS STARTS run()
    serverThread.start();

    log("Server started.");
    }

    private void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("Error stopping server: " + e.getMessage());
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        log("Server stopped.");
    }*/

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            System.out.println("Client connected: " + clientSocket.getInetAddress());  // Log client connection

            String message = in.readLine();
            if (message != null) {
                System.out.println("Received message: " + message);  // Log the received message
                String response = processMessage(message);
                System.out.println("Sending response: " + response);  // Log the response being sent
                out.println(response);
            } else {
                System.out.println("No message received from client.");  // Log if no message is received
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            clientLimiter.release();
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private String processMessage(String message) {
        String[] p = message.split(",");

        switch (p[0]) {
            case "CREATE_USER":
                return createUser(p);

            case "LOGIN":
                return loginUser(p);

            case "SEND_READING":
                return saveReading(p);

            default:
                // Add a default case to handle unknown commands
                return "ERROR: Unknown command";
        }
    }

    private String createUser(String[] p) {
        if (p.length < 4) return "ERROR: Invalid user format";

        String userId = p[1];
        String name = p[2];
        String password = p[3];

        // Validate User ID length
        if (userId.length() < 6 || userId.length() > 8) {
            System.out.println("Invalid User ID: " + userId); // Log the issue on the server
            return "ERROR: User ID must be between 6 and 8 digits.";
        }

        try {
            List<String> lines = readCSV(USERS_CSV);

            for (String line : lines) {
                String[] fields = line.split(",");
                if (fields[0].equals(userId)) {
                    System.out.println("User ID already exists: " + userId); // Log the issue on the server
                    return "ERROR: User ID already exists.";
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_CSV, true));
            writer.write(userId + "," + name + "," + password + "\n");
            writer.close();

            System.out.println("User created: " + userId + " - " + name);
            return "OK,User created successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Server failed to create user";
        }
    }

    private String loginUser(String[] p) {
        if (p.length < 3) return "ERROR: Invalid login format.";

        String userId = p[1];
        String password = p[2];

        try {
            List<String> lines = readCSV(USERS_CSV);

            for (String line : lines) {
                String[] fields = line.split(",");
                if (fields[0].equals(userId) && fields[2].equals(password)) {
                    System.out.println("User logged in: " + userId);  // Log successful login

                    // Return a success message with the user name and instructions
                    return "OK," + fields[1] + ",User has logged in. Now let's record CO2 readings.";
                }
            }

            return "ERROR: Invalid credentials.";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Server failed to process login";
        }
    }

    private String saveReading(String[] p) {
        if (p.length < 5) return "ERROR: Invalid reading format";

        String userId = p[1];
        String name = p[2];
        String postcode = p[3];
        String ppm = p[4];

        try {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            BufferedWriter writer = new BufferedWriter(new FileWriter(READINGS_CSV, true));
            writer.write(timestamp + "," + userId + "," + name + "," + postcode + "," + ppm + "\n");
            writer.close();

            System.out.println("Reading saved: " + userId + " - " + postcode + " - " + ppm + " ppm");  // Added logging

            // Show popup message on the server side
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Reading for User ID " + userId + " has been saved successfully!",
                    "Reading Saved",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });

            return "OK: Reading saved successfully at " + timestamp;

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Server failed to save reading";
        }
    }

    private void initialiseCSV() {
        try {
            File users = new File(USERS_CSV);
            if (!users.exists()) {
                System.out.println("Creating users.csv...");
                BufferedWriter writer = new BufferedWriter(new FileWriter(users));
                writer.write("UserID,Name,Password\n");
                writer.close();
            }

            File readings = new File(READINGS_CSV);
            if (!readings.exists()) {
                System.out.println("Creating co2_readings.csv...");
                BufferedWriter writer = new BufferedWriter(new FileWriter(readings));
                writer.write("Timestamp,UserID,Name,Postcode,PPM\n");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error initializing CSV files.");
            e.printStackTrace();  // Log the exception for debugging
        }
    }

    private List<String> readCSV(String file) throws Exception {
        List<String> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(file));
        String line;
        r.readLine();
        while ((line = r.readLine()) != null) list.add(line);
        r.close();
        return list;
    }
}
    /*private void loadCSVData() {
        tableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVER_CSV))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                tableModel.addRow(parts);
            }
            log("Data refreshed");
        } catch (IOException e) {
            log("Error loading data: " + e.getMessage());
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CO2Server().setVisible(true));
    }
}*/
