import java.io.*;
import java.net.*;

// Thread class using Runnable 
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = 
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = 
                new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Connected to Server. Type 'bye' to exit.");

            String message;

            // Communication loop
            while ((message = in.readLine()) != null) {
                System.out.println("Client says: " + message);

                if (message.equalsIgnoreCase("bye")) {
                    out.println("Goodbye!");
                    break;
                }

                out.println("Server received: " + message);
            }

            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }
}

public class CO2ServerSocket {

    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server running on port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected.");

                // Create a handler for each client
                ClientHandler handler = new ClientHandler(client);

                // Assign to a thread
                Thread t = new Thread(handler);
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}
