package com.expensetracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for establishing JDBC Database Connection to MySQL.
 * Demonstrates JDBC Driver registration, Connection management, and exception
 * handling.
 */
public class DatabaseConnection {

    // MySQL Database Configuration (Change credentials if your MySQL password is
    // different)
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "qwerty"; // Change to your MySQL password

    // Base URL without database name (used to auto-create database if missing)
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

    static {
        try {
            // Step 1: Load and register MySQL JDBC Driver (Connector/J)
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: MySQL JDBC Driver not found! Ensure mysql-connector-j dependency is added.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a new connection to the MySQL expense_tracker_db.
     * 
     * @return Connection object
     * @throws SQLException if database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        // Step 2: Establish connection via DriverManager
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Establishes a raw connection to MySQL server (without specifying DB name).
     */
    public static Connection getRawConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Tests if MySQL database connection is working.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Getters for configuration (useful for UI/debugging)
    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }
}
