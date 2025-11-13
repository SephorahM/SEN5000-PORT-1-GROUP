import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CO2Server extends JFrame implements Runnable {

    private JTextArea logArea;
    private JButton startButton, stopButton, refreshButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running = false;
    private static final int PORT = 43;
    private static final String SERVER_CSV = "server_readings.csv";

    public CO2Server() {

        setTitle("CO2 Server Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        startButton = new JButton("Start server");
        stopButton = new JButton("Stop server");
        refreshButton = new JButton("Refresh Data");
        stopButton.setEnabled(false);

        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Server Log"));
        add(logScroll, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(
            new String[]{"Timestamp", "UserID", "Name", "Postcode", "CO2 (ppm)"}, 0);
        dataTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(dataTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("CO2 readings"));
        add(tableScroll, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        refreshButton.addActionListener(e -> loadCSVData());

        initialiseCSV();
        loadCSVData();
        
    }

    public void startServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("Error stopping server: " + e.getMessage());
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        log("Server stopped");
    }

    public void run() {
        initializeCSV();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            this.serverSocket = server;
            log("Waiting for client connections...");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log("Client connected: " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (SocketException se) {
                    break;
                }
            
        } catch (IOException e) {
            log("Server Error: " + e.getMessage());
        }
    }

    private void handleClient
}
