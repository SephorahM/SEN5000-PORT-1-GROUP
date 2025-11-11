import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class CO2Analyser {
    private static final String CSV_FILE = "co2_readings.csv";

    public static void showAnalysisPage(JFrame parentFrame) {
    JFrame analysisFrame = new JFrame("CO2 Data Analysis");
    analysisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    analysisFrame.setSize(700, 450);
    analysisFrame.setLocationRelativeTo(parentFrame);

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(new Color(240, 240, 240));

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(240, 240, 240));

    JLabel titleLabel = new JLabel("CO2 Readings Table", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
    titleLabel.setForeground(new Color(0, 102, 204));
    topPanel.add(titleLabel, BorderLayout.CENTER);

    JButton sortButton = new JButton("Sort by Postcode");
    sortButton.setFont(new Font("Arial", Font.PLAIN, 14));
    sortButton.setBackground(new Color(200, 220, 240));
    sortButton.setFocusPainted(false);
    topPanel.add(sortButton, BorderLayout.EAST);

    panel.add(topPanel, BorderLayout.NORTH);

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Timestamp", "User ID", "Name", "Postcode", "CO2 (ppm)"}, 0
    );
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);

    // --- Load all readings directly (raw data) ---
    List<String[]> allReadings = loadAllReadings();
    if (allReadings.isEmpty()) {
        model.addRow(new Object[]{"No data available", "", "", "", ""});
    } else {
        for (String[] row : allReadings) {
            model.addRow(row);
        }
    }

    // --- Sort by Postcode when button clicked ---
    sortButton.addActionListener(e -> {
        List<String[]> sorted = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals("No data available")) continue;
            sorted.add(new String[]{
                model.getValueAt(i, 0).toString(),
                model.getValueAt(i, 1).toString(),
                model.getValueAt(i, 2).toString(),
                model.getValueAt(i, 3).toString(),
                model.getValueAt(i, 4).toString()
            });
        }

        // Sort alphabetically by postcode (column index 3)
        sorted.sort(Comparator.comparing(row -> row[3]));

        // Clear and repopulate table
        model.setRowCount(0);
        for (String[] row : sorted) model.addRow(row);
    });

    analysisFrame.setContentPane(panel);
    analysisFrame.setVisible(true);
}

    /*public static void showAnalysisPage(JFrame parentFrame) {
        JFrame analysisFrame = new JFrame("CO2 Data Analysis");
        analysisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        analysisFrame.setSize(500,400);
        analysisFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(new Color(240, 240, 240));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("CO2 Analysis Board", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton sortButton = new JButton("Sort by Postcode");
        sortButton.setFont(new Font("Arial", Font.PLAIN, 14));
        sortButton.setBackground(new Color(200, 220, 240));
        sortButton.setFocusPainted(false);
        topPanel.add(sortButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Postcode", "Number of Readings", "Average CO2 (ppm)"}, 0);
            
            Map<String, List<Double>> readingsByPostcode = loadReadings();
            if (readingsByPostcode.isEmpty()) {
                model.addRow(new Object[]{"No data", 0, "N/A"});
            }else{
            
            double totalSum = 0;
            int totalCount = 0;

            for (Map.Entry<String, List<Double>> entry : readingsByPostcode.entrySet()) {
                String postcode = entry.getKey();
                List<Double> values = entry.getValue();
                double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                model.addRow(new Object[]{
                    postcode,
                    values.size(),
                    String.format("%.2f", avg)
                });

                totalSum += values.stream().mapToDouble(Double::doubleValue).sum();
                totalCount += values.size();
            }

            double overallAvg = totalSum / totalCount;

            JLabel overallLabel = new JLabel("Overall Average CO₂: " + String.format("%.2f ppm", overallAvg), JLabel.CENTER);
            overallLabel.setFont(new Font("Arial", Font.BOLD, 16));
            overallLabel.setForeground(Color.DARK_GRAY);
            panel.add(overallLabel, BorderLayout.SOUTH);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        analysisFrame.setContentPane(panel);
        analysisFrame.setVisible(true);
    }*/

    private static Map<String, List<Double>> loadReadings() {
        Map<String, List<Double>> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String postcode = parts[3];
                    double co2 = Double.parseDouble(parts[4]);
                    data.computeIfAbsent(postcode, k -> new ArrayList<>()).add(co2);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading data: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return data;
    }

    private static List<String[]> loadAllReadings() {
    List<String[]> rows = new ArrayList<>();
    File file = new File(CSV_FILE);

    if (!file.exists() || file.length() == 0) {
        return rows;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                rows.add(new String[]{
                    parts[0], // Timestamp
                    parts[1], // User ID
                    parts[2], // Name
                    parts[3], // Postcode
                    parts[4]  // CO2
                });
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error reading data: " + e.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
    }

    return rows;
}

}
