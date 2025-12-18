
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;
import javax.swing.*;

public class CO2Server {

    private static final int PORT = 6060;  // Changed from 5050 to 6060
    private static final int MAX_CLIENTS = 4;
    private static final String USERS_CSV = "users.csv";
    private static final String READINGS_CSV = "co2_readings.csv";

    private static final Semaphore clientLimiter = new Semaphore(MAX_CLIENTS, true);

    private static final Object FILE_LOCK = new Object();

    public static void main(String[] args) {
        new CO2Server().start();
    }

    public void start() {
        initialiseCSV();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                if (!clientLimiter.tryAcquire()) {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("ERROR: Server busy (maximum 4 clients allowed)");
                    clientSocket.close();
                    System.out.println("Rejected connection: server full");
                    continue;
                }

                System.out.println("Client accepted: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
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

 /*private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            
            String message = in.readLine();
            if (message != null) {
                System.out.println("Received message: " + message);  // Log the received message
                String response = processMessage(message);
                System.out.println("Sending response: " + response);  // Log the response being sent
                out.println(response);
            } 
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

        try {
            List<String> users = readCSV(USERS_CSV);

            for (String line : uers) {
                
                for (String line : users) {
                if (line.split(",")[0].equals(userId)) {
                    return "ERROR: User ID already exists";
                }
            }
 synchronized (this) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_CSV, true))) {
                    writer.write(userId + "," + name + "," + password + "\n");
                }
            }

            return "OK,User created successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Failed to create user";
        }
    }

    private String loginUser(String[] p) {
        if (p.length < 3) return "ERROR: Invalid login format.";

        String userId = p[1];
        String password = p[2];

        try {
            List<String> users = readCSV(USERS_CSV);
        

            for (String line : users) {
                String[] f = line.split(",");
                if (f[0].equals(userId) && f[2].equals(password)) {
                    System.out.println("User logged in: " + userId);  // Log successful login

                    // Show a pop-up on the server side synchronously so we wait for user to press OK
                   
                        SwingUtilities.invokeAndWait(() -> 
                    
                            JOptionPane.showMessageDialog(
                                null,
                                "User " + userId + " has logged in. Now let's record CO2 readings.",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        );
                     return "OK," + f[1]; // return user name
                
                    }
                }

                    // Send the response to the client only after the pop-up is dismissed
                    return "OK," + f[1];  // Return name after OK
                }
            }

            
            return "ERROR: Invalid credentials.";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Server failed to process login";
        }
    
//CO2 Readings
    private String saveReading(String[] p) {
        if (p.length < 5) return "ERROR: Invalid reading format";

        String userId = p[1];
        String name = p[2];
        String postcode = p[3];
        String ppm = p[4];

        try {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            synchronized (this) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(READINGS_CSV, true))) {
                    writer.write(timestamp + "," + userId + "," + name + "," + postcode + "," + ppm + "\n");
                }
            }

            // Show popup message on the server side
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "CO2 reading saved for user " + userId,
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
            
           if (!new File(USERS_CSV).exists()) {
                System.out.println("Creating users.csv...");
                BufferedWriter writer = new BufferedWriter(new FileWriter(users));
                writer.write("UserID,Name,Password\n");
                writer.close();
            }

            File readings = new File(READINGS_CSV);
            if (!readings.exists()) {
                try (BufferedWriter w = new BufferedWriter(new FileWriter(USERS_CSV))) {
                    w.write("UserID,Name,Password\n");
                }
            }

            if (!new File(READINGS_CSV).exists()) {
                try (BufferedWriter w = new BufferedWriter(new FileWriter(READINGS_CSV))) {
                    w.write("Timestamp,UserID,Name,Postcode,PPM\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Log the exception for debugging
        }
    }

    private List<String> readCSV(String file) throws Exception {
        List<String> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(file));
        String line;
        r.readLine();
        while ((line = r.readLine()) != null) list.add(line);
    
        return list;
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
    private static class ClientHandler implements Runnable {

        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String message = in.readLine();
                if (message != null) {
                    String response = processMessage(message);
                    out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Client error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }

                // 🔓 RELEASE SLOT ONLY WHEN THREAD ENDS
                clientLimiter.release();
                System.out.println("Client disconnected. Slots available: " + clientLimiter.availablePermits());
            }
        }
    }

    // ================= MESSAGE ROUTER =================
    private static String processMessage(String message) {
        String[] p = message.split(",");

        switch (p[0]) {
            case "CREATE_USER":
                return createUser(p);
            case "LOGIN":
                return loginUser(p);
            case "SEND_READING":
                return saveReading(p);
            default:
                return "ERROR: Unknown command";
        }
    }

    // ================= USER CREATION =================
    private static String createUser(String[] p) {
        if (p.length < 4) {
            return "ERROR: Invalid CREATE_USER format";
        }

        String userId = p[1];
        String name = p[2];
        String password = p[3];

        try {
            synchronized (FILE_LOCK) {
                List<String> users = readCSV(USERS_CSV);
                for (String u : users) {
                    if (u.split(",")[0].equals(userId)) {
                        return "ERROR: User ID already exists";
                    }
                }

                BufferedWriter w = new BufferedWriter(new FileWriter(USERS_CSV, true));
                w.write(userId + "," + name + "," + password + "\n");
                w.close();
            }
            return "OK,User created";
        } catch (Exception e) {
            return "ERROR: Server error creating user";
        }
    }

    // ================= LOGIN =================
    private static String loginUser(String[] p) {
        if (p.length < 3) {
            return "ERROR: Invalid LOGIN format";
        }

        String userId = p[1];
        String password = p[2];

        try {
            List<String> users = readCSV(USERS_CSV);
            for (String u : users) {
                String[] f = u.split(",");
                if (f[0].equals(userId) && f[2].equals(password)) {
                    return "OK," + f[1];
                }
            }
            return "ERROR: Invalid credentials";
        } catch (Exception e) {
            return "ERROR: Login failed";
        }
    }

    // ================= SAVE READING =================
    private static String saveReading(String[] p) {
        if (p.length < 5) {
            return "ERROR: Invalid SEND_READING format";
        }

        String userId = p[1];
        String name = p[2];
        String postcode = p[3];
        String ppm = p[4];

        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        try {
            synchronized (FILE_LOCK) {
                BufferedWriter w = new BufferedWriter(new FileWriter(READINGS_CSV, true));
                w.write(timestamp + "," + userId + "," + name + "," + postcode + "," + ppm + "\n");
                w.close();
            }
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "CO2 reading saved for user " + userId,
                    "Reading Saved",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
            
            return "OK: Reading saved at " + timestamp;
        } catch (Exception e) {
            return "ERROR: Failed to save reading";
        }
    }

    // ================= CSV INIT =================
    private static void initialiseCSV() {
        try {
            if (!new File(USERS_CSV).exists()) {
                System.out.println("Creating users.csv...");
                BufferedWriter w = new BufferedWriter(new FileWriter(USERS_CSV));
                w.write("UserID,Name,Password\n");
                w.close();
            }
            if (!new File(READINGS_CSV).exists()) {
                System.out.println("Creating co2_readings.csv...");
                BufferedWriter w = new BufferedWriter(new FileWriter(READINGS_CSV));
                w.write("Timestamp,UserID,Name,Postcode,PPM\n");
                w.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readCSV(String file) throws Exception {
        List<String> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(file));
        r.readLine(); // skip header
        String line;
        while ((line = r.readLine()) != null) {
            list.add(line);
        }
        r.close();
        return list;
    }
}
