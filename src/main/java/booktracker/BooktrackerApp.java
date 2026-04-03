package booktracker;

import java.sql.*;
import java.util.Scanner;

public class BooktrackerApp {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        DatabaseManager.initializeDatabase();
        DataLoader.loadData("Data/reading_habits_dataset.xlsx");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();

            if (choice.equals("1")) {
                addUser();
            } else if (choice.equals("2")) {
                getReadingHabitsForUser();
            } else if (choice.equals("3")) {
                changeBookTitle();
            } else if (choice.equals("4")) {
                deleteReadingHabit();
            } else if (choice.equals("5")) {
                getMeanAge();
            } else if (choice.equals("6")) {
                getUsersForBook();
            } else if (choice.equals("7")) {
                getTotalPagesRead();
            } else if (choice.equals("8")) {
                getUsersWithMultipleBooks();
            } else if (choice.equals("9")) {
                addNameColumn();
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid choice. Please enter a number from 0-9.");
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("=== Booktracker Application ===");
        System.out.println("1. Add a user");
        System.out.println("2. View all reading habits for a user");
        System.out.println("3. Change a book title");
        System.out.println("4. Delete a reading habit record");
        System.out.println("5. Show mean age of all users");
        System.out.println("6. Show number of users that read a specific book");
        System.out.println("7. Show total pages read by all users");
        System.out.println("8. Show number of users that read more than one book");
        System.out.println("9. Add 'Name' column to User table");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    // Functionality 1: Add a user to the database
    private void addUser() {
        System.out.print("Enter age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Please enter a number.");
            return;
        }

        System.out.print("Enter gender (m/f): ");
        String gender = scanner.nextLine().trim();

        String sql = "INSERT INTO User (age, gender) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, age);
            pstmt.setString(2, gender);
            pstmt.executeUpdate();
            System.out.println("User added successfully.");

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    // Functionality 2: Provide all reading habit data for a certain user
    private void getReadingHabitsForUser() {
        System.out.print("Enter user ID: ");
        int userID;
        try {
            userID = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID.");
            return;
        }

        String sql = "SELECT habitID, book, pagesRead, submissionMoment FROM ReadingHabit WHERE user = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("HabitID | Book | Pages Read | Submission Moment");
            System.out.println("--------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getInt("habitID") + " | " + rs.getString("book") + " | " +
                        rs.getInt("pagesRead") + " | " + rs.getString("submissionMoment"));
            }

            if (!found) {
                System.out.println("No reading habits found for user ID: " + userID);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving reading habits: " + e.getMessage());
        }
    }

    // Functionality 3: Change the title of a book in the database
    private void changeBookTitle() {
        System.out.print("Enter the current book title: ");
        String oldTitle = scanner.nextLine().trim();
        System.out.print("Enter the new book title: ");
        String newTitle = scanner.nextLine().trim();

        String sql = "UPDATE ReadingHabit SET book = ? WHERE book = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setString(2, oldTitle);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Book title updated in " + rowsUpdated + " record(s).");
            } else {
                System.out.println("No records found with title: \"" + oldTitle + "\"");
            }

        } catch (SQLException e) {
            System.err.println("Error updating book title: " + e.getMessage());
        }
    }

    // Functionality 4: Delete a record from the ReadingHabit table
    private void deleteReadingHabit() {
        System.out.print("Enter the habit ID to delete: ");
        int habitID;
        try {
            habitID = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid habit ID.");
            return;
        }

        String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitID);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Reading habit record with ID " + habitID + " deleted.");
            } else {
                System.out.println("No record found with habit ID: " + habitID);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
        }
    }

    // Functionality 5: Provide the mean age of the users (calculated in SQL)
    private void getMeanAge() {
        String sql = "SELECT AVG(age) AS meanAge FROM User";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.printf("Mean age of all users: %.2f years%n", rs.getDouble("meanAge"));
            }

        } catch (SQLException e) {
            System.err.println("Error calculating mean age: " + e.getMessage());
        }
    }

    // Functionality 6: Total number of users that have read pages from a specific book
    private void getUsersForBook() {
        System.out.print("Enter the book title: ");
        String book = scanner.nextLine().trim();

        String sql = "SELECT COUNT(DISTINCT user) AS userCount FROM ReadingHabit WHERE book = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Number of users that read \"" + book + "\": " + rs.getInt("userCount"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user count: " + e.getMessage());
        }
    }

    // Functionality 7: Total number of pages read by all users (calculated in SQL)
    private void getTotalPagesRead() {
        String sql = "SELECT SUM(pagesRead) AS totalPages FROM ReadingHabit";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Total pages read by all users: " + rs.getInt("totalPages"));
            }

        } catch (SQLException e) {
            System.err.println("Error calculating total pages: " + e.getMessage());
        }
    }

    // Functionality 8: Total number of users that have read more than one book (calculated in SQL)
    private void getUsersWithMultipleBooks() {
        String sql = "SELECT COUNT(*) AS userCount FROM (" +
                "SELECT user FROM ReadingHabit " +
                "GROUP BY user " +
                "HAVING COUNT(DISTINCT book) > 1)";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Number of users that have read more than one book: " + rs.getInt("userCount"));
            }

        } catch (SQLException e) {
            System.err.println("Error calculating user count: " + e.getMessage());
        }
    }

    // Functionality 9: Add a "Name" column (TEXT) to the User table
    private void addNameColumn() {
        String sql = "ALTER TABLE User ADD COLUMN Name TEXT";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Column 'Name' (TEXT) successfully added to the User table.");

        } catch (SQLException e) {
            // SQLite throws an error if the column already exists
            if (e.getMessage() != null && e.getMessage().contains("duplicate column name")) {
                System.out.println("Column 'Name' already exists in the User table.");
            } else {
                System.err.println("Error adding column: " + e.getMessage());
            }
        }
    }
}
