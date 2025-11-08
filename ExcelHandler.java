import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelHandler {
    private static final String EXCEL_FILE = "co2_readings.xlsx";
    
    public static void initializeExcel() {
        File file = new File(EXCEL_FILE);
        if (!file.exists()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("CO2 Readings");
                
                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Timestamp", "UserID", "Name", "Postcode", "CO2_PPM"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
                
                // Save the workbook
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendReading(String timestamp, String userId, String name, 
                                   String postcode, double co2Value) throws IOException {
        File file = new File(EXCEL_FILE);
        Workbook workbook;
        
        if (file.exists()) {
            workbook = WorkbookFactory.create(file);
        } else {
            workbook = new XSSFWorkbook();
        }

        try {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            newRow.createCell(0).setCellValue(timestamp);
            newRow.createCell(1).setCellValue(userId);
            newRow.createCell(2).setCellValue(name);
            newRow.createCell(3).setCellValue(postcode);
            newRow.createCell(4).setCellValue(co2Value);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } finally {
            workbook.close();
        }
    }
}
