import java.io.*;
import java.net.*;

public class CO2ServerSocket extends Thread {

    private ServerSocket serverSocket;
    private boolean running = true;

    public CO2ServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
    }

    @Override
    public void run() {
        try {
            while (running) {
                System.out.println("Waiting for client...");
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());

                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            System.out.println("Server stopped.");
        }
    }

    public void stopServer() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }

    // Thread to handle each client
    private static class ClientHandler extends Thread {
        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true)
            ) {
                String message = in.readLine();
                System.out.println("Received: " + message);

                out.println("Server received: " + message);

            } catch (IOException e) {
                System.out.println("Client error: " + e.getMessage());
            } finally {
                try { client.close(); } 
                catch (IOException ignored) {}
            }
        }
    }
}