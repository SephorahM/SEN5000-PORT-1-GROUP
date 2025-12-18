/*import java.io.*;
import java.net.*;

//Connects to server on port 6060 and sends one CSV reading.
 
public class CO2ClientSocket extends Thread implements Runnable {

    private final String messageToSend;
    private final String host;
    private final int port;

    // Use port 6060 instead of 43 (43 is privileged and often blocked)
    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 6060;

    public static void setServerConfig(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
    }

    // Add these methods to retrieve the host and port
    public static String getHost() {
        return SERVER_HOST;
    }

    public static int getPort() {
        return SERVER_PORT;
    }

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

            // Increase timeout to 10 seconds for debugging
            socket.setSoTimeout(10000);

            System.out.println("Attempting to connect to server at " + SERVER_HOST + ":" + SERVER_PORT);
            System.out.println("Sending message: " + message);  // Log the message being sent
            out.println(message);

            String response = in.readLine();
            System.out.println("Received response: " + response);  // Log the server's response

            // Removed automatic popup for ERROR responses. The server will handle pop-ups.
            return response;

        } catch (SocketTimeoutException e) {
            System.out.println("Error: Server took too long to respond.");
            return "ERROR: Server took too long to respond.";
        } catch (ConnectException e) {
            System.out.println("Error: Could not connect to server. Is it running on port " + SERVER_PORT + "?");
            return "ERROR: Could not connect to server. Is it running on port " + SERVER_PORT + "?";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    // the client connects to server.
     
    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream()), true);

            writer.println(messageToSend);
            System.out.println("Client connected and holding connection...");
            Thread.sleep(10000);
            sentSuccessfully = true;

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
            sentSuccessfully = false;
        } catch (InterruptedException e) {
        System.out.println("Client interrupted: " + e.getMessage());
        Thread.currentThread().interrupt(); // restore interrupt status
        sentSuccessfully = false;
    } finally {
        if (socket != null) {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
    }

    public boolean wasSentSuccessfully() {
        return sentSuccessfully;
    }
}*/

import java.io.*;
import java.net.*;

/**
 * Handles all client-server communication.
 */
public class CO2ClientSocket {

    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 6060;

    public static void setServerConfig(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
    }

    public static String getHost() {
        return SERVER_HOST;
    }

    public static int getPort() {
        return SERVER_PORT;
    }

    // Generic send-to-server function for ALL messages
    public static String sendToServer(String message) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT); BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true)) {

            socket.setSoTimeout(10000);

            out.println(message);
            return in.readLine();

        } catch (SocketTimeoutException e) {
            return "ERROR: Server took too long to respond";
        } catch (ConnectException e) {
            return "ERROR: Could not connect to server";
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
