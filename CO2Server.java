
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
            return "OK,User created successfully";
        } catch (Exception e) {
            e.printStackTrace();
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
                    System.out.println("OK, user logged in:" + f[1]);
                }
            }
            return "ERROR: Invalid credentials";
        } catch (Exception e) {
            System.out.println("ERROR: Login failed");
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
            e.printStackTrace();
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
