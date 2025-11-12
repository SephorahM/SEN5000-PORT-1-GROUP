import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CO2Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }
}

class ClientHandler extends Thread {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Transaction) {
                    Transaction t = (Transaction) obj;
                    System.out.println("Received: " + t.getFormattedTransaction());
                    out.println("Server received transaction at " + new Date());
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}

class Transaction implements Serializable {
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
        return String.format("[%s] %-15s %.2f ppm", sdf.format(timestamp), type, reading);
    }
}
