package com.expensetracker.model;

import java.time.LocalDate;

/**
 * Model class representing an Expense record.
 * Demonstrates OOP Encapsulation (private fields with public getters and setters).
 */
public class Expense {
    private int id;
    private String title;
    private double amount;
    private Category category;
    private LocalDate expenseDate;
    private String notes;

    // Default Constructor
    public Expense() {}

    // Constructor without ID (used when creating a new expense before saving to DB)
    public Expense(String title, double amount, Category category, LocalDate expenseDate, String notes) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.notes = notes;
    }

    // Full Constructor (used when fetching existing expense from DB)
    public Expense(int id, String title, double amount, Category category, LocalDate expenseDate, String notes) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Title: %-25s | Amount: $%-8.2f | Category: %-15s | Date: %s",
                id, title, amount, category.getDisplayName(), expenseDate);
    }
}
