package com.expensetracker.dao;

import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC Implementation of the ExpenseDAO interface.
 * Demonstrates:
 * - Polymorphism (implementing ExpenseDAO interface methods)
 * - JDBC PreparedStatements (preventing SQL Injection)
 * - ResultSet processing and object mapping
 * - Try-with-resources for automatic resource closing (Connection, Statement, ResultSet)
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    // SQL Queries
    private static final String INSERT_EXPENSE_SQL =
            "INSERT INTO expenses (title, amount, category, expense_date, notes) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_EXPENSES_SQL =
            "SELECT * FROM expenses ORDER BY expense_date DESC, id DESC";
    private static final String SELECT_EXPENSE_BY_ID_SQL =
            "SELECT * FROM expenses WHERE id = ?";
    private static final String UPDATE_EXPENSE_SQL =
            "UPDATE expenses SET title = ?, amount = ?, category = ?, expense_date = ?, notes = ? WHERE id = ?";
    private static final String DELETE_EXPENSE_SQL =
            "DELETE FROM expenses WHERE id = ?";
    private static final String SELECT_TOTAL_EXPENSE_SQL =
            "SELECT SUM(amount) AS total FROM expenses";
    private static final String SELECT_CATEGORY_SUMMARY_SQL =
            "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category";

    @Override
    public boolean addExpense(Expense expense) {
        // Try-with-resources automatically closes Connection and PreparedStatement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_EXPENSE_SQL)) {

            pstmt.setString(1, expense.getTitle());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory().name());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            pstmt.setString(5, expense.getNotes());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Database Error while adding expense: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_EXPENSES_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Error while fetching expenses: " + e.getMessage());
        }
        return expenses;
    }

    @Override
    public Expense getExpenseById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_EXPENSE_BY_ID_SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Error while fetching expense by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateExpense(Expense expense) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_EXPENSE_SQL)) {

            pstmt.setString(1, expense.getTitle());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory().name());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            pstmt.setString(5, expense.getNotes());
            pstmt.setInt(6, expense.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Database Error while updating expense: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteExpense(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_EXPENSE_SQL)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Database Error while deleting expense: " + e.getMessage());
            return false;
        }
    }

    @Override
    public double getTotalExpenses() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_TOTAL_EXPENSE_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Error while calculating total expenses: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public Map<Category, Double> getCategorySummary() {
        Map<Category, Double> summaryMap = new EnumMap<>(Category.class);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_CATEGORY_SUMMARY_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String categoryStr = rs.getString("category");
                double total = rs.getDouble("total");
                Category cat = Category.fromString(categoryStr);
                summaryMap.put(cat, total);
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Error while fetching category summary: " + e.getMessage());
        }
        return summaryMap;
    }

    /**
     * Helper method to map a single JDBC ResultSet row into an Expense object.
     */
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        double amount = rs.getDouble("amount");
        String categoryStr = rs.getString("category");
        Date sqlDate = rs.getDate("expense_date");
        String notes = rs.getString("notes");

        Category category = Category.fromString(categoryStr);
        LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : LocalDate.now();

        return new Expense(id, title, amount, category, date, notes);
    }
}
