package booktracker;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:booktracker.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Create User table (ERD: userID, age, gender)
            stmt.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "userID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "age INTEGER, " +
                    "gender TEXT)");

            // Create ReadingHabit table (ERD: habitID, book, pagesRead, submissionMoment, user FK)
            stmt.execute("CREATE TABLE IF NOT EXISTS ReadingHabit (" +
                    "habitID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book TEXT, " +
                    "pagesRead INTEGER, " +
                    "submissionMoment DATETIME, " +
                    "user INTEGER, " +
                    "FOREIGN KEY (user) REFERENCES User(userID))");

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
