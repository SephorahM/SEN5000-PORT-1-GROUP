import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class C02Analyser {
    private static final String CSV_FILE = "co2_readings.csv";

    public static void showAnalysisPage(JFrame parentFrame) {
        JFrame analysisFrame = new JFrame("CO2 Data Analysis");
        analysisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        analysisFrame.setSize(700,500);
        analysisFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("CO2 Analysis Board", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Postcode", "Number of Readings", "Average CO2 (ppm)"}, 0);
            
            Map<String, List<Double>> readingsByPostcode = loadReadings();
            if (readingsByPostcode.isEmpty()) {
                JOptionPane.showMessageDialog(analysisFrame, "No readings available to analyse.", "Data Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double totalSum = 0;
            int totalCount = 0;
            for (String postcode : readingsByPostcode.keySet()) {
            List<Double> list = readingsByPostcode.get(postcode);
            double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            model.addRow(new Object[]{postcode, list.size(), String.format("%.2f", avg)});
            totalSum += list.stream().mapToDouble(Double::doubleValue).sum();
            totalCount += list.size();
        }

        double overallAvg = totalSum / totalCount;

        // Table view
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom label
        JLabel overallLabel = new JLabel("Overall Average CO₂: " + String.format("%.2f ppm", overallAvg), JLabel.CENTER);
        overallLabel.setFont(new Font("Arial", Font.BOLD, 16));
        overallLabel.setForeground(Color.DARK_GRAY);
        panel.add(overallLabel, BorderLayout.SOUTH);

        analysisFrame.setContentPane(panel);
        analysisFrame.setVisible(true);
    }

    private static Map<String, List<Double>> loadReadings() {
        Map<String, List<Double>> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            reader.readLine(); // skip header
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
}
