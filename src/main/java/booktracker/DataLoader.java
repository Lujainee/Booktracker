package booktracker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.sql.*;

public class DataLoader {

    public static void loadData(String xlsxPath) {
        try (Connection conn = DatabaseManager.getConnection()) {

            // Skip loading if the database already has data
            if (isDataLoaded(conn)) {
                System.out.println("Data already loaded into database, skipping import.");
                return;
            }

            try (FileInputStream fis = new FileInputStream(xlsxPath);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                loadUsers(workbook, conn);
                loadReadingHabits(workbook, conn);
                System.out.println("Data successfully imported from: " + xlsxPath);
            }

        } catch (Exception e) {
            System.err.println("Error loading data from xlsx: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isDataLoaded(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM User")) {
            return rs.getInt(1) > 0;
        }
    }

    // Loads the "User" sheet: columns [userID, age, gender]
    private static void loadUsers(Workbook workbook, Connection conn) throws SQLException {
        Sheet sheet = workbook.getSheet("User");
        String sql = "INSERT OR IGNORE INTO User (userID, age, gender) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                pstmt.setInt(1, (int) row.getCell(0).getNumericCellValue()); // userID
                pstmt.setInt(2, (int) row.getCell(1).getNumericCellValue()); // age
                pstmt.setString(3, row.getCell(2).getStringCellValue());      // gender
                pstmt.executeUpdate();
            }
        }
        System.out.println("Users loaded.");
    }

    // Loads the "ReadingHabit" sheet: columns [habitID, userID, pagesRead, book, submissionMoment]
    private static void loadReadingHabits(Workbook workbook, Connection conn) throws SQLException {
        Sheet sheet = workbook.getSheet("ReadingHabit");
        String sql = "INSERT OR IGNORE INTO ReadingHabit (habitID, user, pagesRead, book, submissionMoment) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                pstmt.setInt(1, (int) row.getCell(0).getNumericCellValue()); // habitID
                pstmt.setInt(2, (int) row.getCell(1).getNumericCellValue()); // user (FK)
                pstmt.setInt(3, (int) row.getCell(2).getNumericCellValue()); // pagesRead
                pstmt.setString(4, row.getCell(3).getStringCellValue());      // book

                // submissionMoment is stored as a date in xlsx
                Cell dateCell = row.getCell(4);
                if (dateCell != null) {
                    java.util.Date date = dateCell.getDateCellValue();
                    pstmt.setString(5, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                } else {
                    pstmt.setNull(5, Types.VARCHAR);
                }

                pstmt.executeUpdate();
            }
        }
        System.out.println("Reading habits loaded.");
    }
}
