import java.io.*;
import java.net.*;

//Connects to server on port 5000 and sends one CSV reading.
 
public class CO2ClientSocket extends Thread implements Runnable {

    private final String messageToSend;
    private final String host;
    private final int port;

    // Use port 5000 instead of 43 (43 is privileged and often blocked)
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private boolean sentSuccessfully = false;

    // Constructor (required – GUI will create an object with the CSV line)
    public CO2ClientSocket(String messageToSend) {
        this(messageToSend, SERVER_HOST, SERVER_PORT);
    }

    public CO2ClientSocket(String messageToSend, String host, int port) {
        this.messageToSend = messageToSend;
        this.host = host;
        this.port = port;
    }
    
    // Generic send-to-server function for ALL messages
    public static String sendToServer(String message) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Prevent freezing: Timeout after 5 seconds if no response
            socket.setSoTimeout(5000);

            out.println(message);
            return in.readLine();

        } catch (ConnectException e) {
            return "ERROR: Connection refused. Is server running on port " + SERVER_PORT + "?";
        } catch (SocketTimeoutException e) {
            return "ERROR: Server took too long to respond.";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    // the client connects to server.
     
    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream()), true)) {

            writer.println(messageToSend);
            sentSuccessfully = true;

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
            sentSuccessfully = false;
        }
    }

    public boolean wasSentSuccessfully() {
        return sentSuccessfully;
    }
}
