package com.expensetracker.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Automatically initializes database schema (creates database & tables if missing).
 * Uses JDBC Statement execution.
 */
public class DatabaseInitializer {

    /**
     * Creates database and expenses table if they do not exist.
     */
    public static boolean initialize() {
        try {
            // First, try creating the database using raw MySQL server connection
            try (Connection rawConn = DatabaseConnection.getRawConnection();
                 Statement stmt = rawConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS expense_tracker_db");
            } catch (SQLException e) {
                // Ignore if raw connection fails (user might not have root privileges to create DB)
            }

            // Next, connect to expense_tracker_db and create table
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                String createTableSQL = "CREATE TABLE IF NOT EXISTS expenses ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "title VARCHAR(100) NOT NULL, "
                        + "amount DECIMAL(10, 2) NOT NULL, "
                        + "category VARCHAR(50) NOT NULL, "
                        + "expense_date DATE NOT NULL, "
                        + "notes VARCHAR(255)"
                        + ")";

                stmt.executeUpdate(createTableSQL);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Auto-initialization failed. Details: " + e.getMessage());
            return false;
        }
    }
}
