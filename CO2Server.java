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
    if (running) {
        log("Server already running");
        return;
    }

    running = true;
    startButton.setEnabled(false);
    stopButton.setEnabled(true);

    serverThread = new Thread(this);  // THIS STARTS run()
    serverThread.start();

    log("Server started.");
}

    }

    public void run() {
        initialiseCSV();

        try (ServerSocket server = new ServerSocket(PORT)) {
            this.serverSocket = server;
            log("Waiting for client connections");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log("Client connected: " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException e) {
            log("Server Error: " + e.getMessage());
        }
    }

    private void stopServer() {
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
        log("Server stopped.");
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message = in.readLine();
            if (message != null) {
                log("Received: " + message);
                saveReading(message);
                out.println("Server received: " + message);
                SwingUtilities.invokeLater(this::loadCSVData);
            }
        } catch (IOException e) {
            log("Client Error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void saveReading(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SERVER_CSV, true))) {
            if (new File(SERVER_CSV).length() == 0) {
                writer.write("Timestamp, UserID, Name, Postcode, CO2_PPM\n");
            } 
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            writer.write(timestamp + "," + data + "\n");
        }catch (IOException e) {
                log("Error saving data: " + e.getMessage());
        }
    }

    private void initialiseCSV() {
        File file = new File(SERVER_CSV);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Timestamp, UserID, Name, Postcode, CO2_PPM\n");
            } catch (IOException e) {
                log("Error initialising CSV: " + e.getMessage());
            }
        }
    }

    private void loadCSVData() {
        tableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVER_CSV))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                tableModel.addRow(parts);
            }
            log("Data refreshed");
        } catch (IOException e) {
            log("Error loading data: " + e.getMessage());
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CO2Server().setVisible(true));
    }
}
