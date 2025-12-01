import java.io.*;
import java.net.Socket;

//Connects to server on port 43 and sends one CSV reading.
 
public class CO2ClientSocket extends Thread implements Runnable {

    private final String messageToSend;
    private final String host;
    private final int port;

    private boolean sentSuccessfully = false;

    // Constructor (required – GUI will create an object with the CSV line)
    public CO2ClientSocket(String messageToSend) {
        this(messageToSend, "localhost", 43);
    }

    public CO2ClientSocket(String messageToSend, String host, int port) {
        this.messageToSend = messageToSend;
        this.host = host;
        this.port = port;
    }
    
    public static boolean sendReadingToServer(String csvLine) {
        CO2ClientSocket client = new CO2ClientSocket(csvLine);
        client.start();  // starts the thread
        try {
            client.join(); // wait until thread finishes sending
        } catch (InterruptedException e) {
            return false;
        }
        return client.sentSuccessfully;
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
