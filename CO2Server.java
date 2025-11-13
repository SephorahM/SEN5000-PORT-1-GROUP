import java.io.*;
import java.net.*;

public class CO2Server {

    private static final int PORT = 43; 
    private static boolean running = true;

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server started on port " + PORT + "...");
            System.out.println("Waiting for clients to connect...");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create reader and writer for client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                
                String clientMessage = in.readLine();
                if (clientMessage != null) {
                    System.out.println("Received from client: " + clientMessage);
                    out.println("Server received: " + clientMessage);
                }

                
                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void stopServer() {
        running = false;
    }
}
