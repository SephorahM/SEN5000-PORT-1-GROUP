import java.io.*;
import java.net.*;

public class CO2Server implements Runnable {
    private static final int PORT = 43;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server started on port " + PORT);
            System.out.println("Waiting for client connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message = in.readLine();
                System.out.println("Received from client: " + message);
                out.println("Server received: " + message);

                clientSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }
}