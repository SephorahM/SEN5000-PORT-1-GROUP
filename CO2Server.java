import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;

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
        stopButton.setEnable(false);

        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitleBorder("Server Log"));
        add(logScroll, BorderLayout.Center);

        tableModel = new DefaultTableModel(
            new String[]{"Timestamp", "UserID", "Name", "Postcode", "CO2 (ppm)"}, 0);
        dataTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(dataTable);
        add(tableScroll, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        refreshButton.addActionListener(e -> loadCSVData());

        loadCSVData();

    }

    public void run() {
        initialiseCSV{};

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("CO2 Server started on port " + PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getIntAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    private void handleClient
}
