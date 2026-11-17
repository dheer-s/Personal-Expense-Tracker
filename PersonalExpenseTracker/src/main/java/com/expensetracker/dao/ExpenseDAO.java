package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Category;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) Interface for Expense operations.
 * Demonstrates Abstraction in Object-Oriented Programming (OOP).
 */
public interface ExpenseDAO {

    /** Adds a new expense record to the database */
    boolean addExpense(Expense expense);

    /** Retrieves all expense records from the database */
    List<Expense> getAllExpenses();

    /** Retrieves a specific expense by ID */
    Expense getExpenseById(int id);

    /** Updates an existing expense record */
    boolean updateExpense(Expense expense);

    /** Deletes an expense record by ID */
    boolean deleteExpense(int id);

    /** Calculates total sum of all expenses */
    double getTotalExpenses();

    /** Calculates total expenses grouped by Category */
    Map<Category, Double> getCategorySummary();
}
