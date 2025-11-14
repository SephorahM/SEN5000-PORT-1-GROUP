import java.io.*;
import java.net.*;

public class CO2ServerSocket extends Thread {

    private ServerSocket serverSocket;
    private boolean running = true;

    public static final int PORT = 43;

    public CO2ServerSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                System.out.println("Waiting for clients...");
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());

                new ClientHandler(client).start();

            } catch (IOException e) {
                if (running) {
                    System.out.println("Server error: " + e.getMessage());
                }
            }
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
        System.out.println("Server stopped.");
    }

    
    // Inner Class: Client Handler
    
    class ClientHandler extends Thread {

        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(
                        client.getOutputStream(), true)
            ) {
                String msg = in.readLine();
                System.out.println("Received from client: " + msg);

                out.println("Server received: " + msg);

            } catch (IOException e) {
                System.out.println("Client error: " + e.getMessage());
            } finally {
                try { client.close(); } catch (IOException ignored) {}
            }
        }
    }

    public static void main(String[] args) {
        CO2ServerSocket server = new CO2ServerSocket();
        server.start();
    }
}
