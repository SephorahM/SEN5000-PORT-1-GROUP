import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CO2Server {
    private static final int PORT = 43;
    private static final String SERVER_CSV = "server_readings.csv";

    public void run() {
        initialiseCSV{};

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server started on port " + PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getIntAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    private void handleClient
}
