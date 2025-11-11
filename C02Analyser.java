import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class C02Analyser {
    private static final String CSV_FILE = "co2_readings.csv";

    public static void showAnalysisPage(JFrame parentFrame)
        JFrame analysisFrame = new JFrame("CO2 Data Analysis");
        analysisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        analysisFrame.setSize(700,500);
        analysisFrame.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(new Colour(240, 240, 240));

        JLabel titleLabel = new JLabel("CO2")
}
