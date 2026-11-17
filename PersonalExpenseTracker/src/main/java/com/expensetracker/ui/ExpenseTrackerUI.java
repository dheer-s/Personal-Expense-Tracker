package com.expensetracker.ui;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.ExpenseDAOImpl;
import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.db.DatabaseInitializer;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive Command Line Interface (CLI) for Personal Expense Tracker.
 * Designed for clear demonstration during college presentations.
 */
public class ExpenseTrackerUI {

    private final ExpenseDAO expenseDAO;
    private final Scanner scanner;

    public ExpenseTrackerUI() {
        this.expenseDAO = new ExpenseDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        // Test MySQL Database Connection
        boolean isConnected = DatabaseConnection.testConnection();
        if (!isConnected) {
            System.out.println(" Attempting to auto-create 'expense_tracker_db' and tables...");
            DatabaseInitializer.initialize();
            isConnected = DatabaseConnection.testConnection();
        }

        if (!isConnected) {
            printConnectionTroubleshooting();
        } else {
            // System.out.println(" Connected to MySQL Database successfully!\n");
        }

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readIntInput("Enter your choice (1-8): ");

            switch (choice) {
                case 1 -> addExpense();
                case 2 -> viewAllExpenses();
                case 3 -> viewExpenseById();
                case 4 -> updateExpense();
                case 5 -> deleteExpense();
                case 6 -> viewTotalExpenses();
                case 7 -> viewCategorySummary();
                case 8 -> {
                    System.out.println("\n=======================================================");
                    System.out.println("  Thank you for using Personal Expense Tracker!");
                    System.out.println("=======================================================\n");
                    exit = true;
                }
                default -> System.out.println(" Invalid option! Please choose between 1 and 8.\n");
            }
        }
    }

    private void printBanner() {
        System.out.println("=======================================================");
        System.out.println("                PERSONAL EXPENSE TRACKER               ");
        System.out.println("=======================================================");
    }

    private void printMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("  1. Add New Expense");
        System.out.println("  2. View All Expenses");
        System.out.println("  3. Search Expense by ID");
        System.out.println("  4. Update Existing Expense");
        System.out.println("  5. Delete Expense");
        System.out.println("  6. View Total Expense Amount");
        System.out.println("  7. View Category Summary (Analytics)");
        System.out.println("  8. Exit Program");
        System.out.println("-------------------------------------------");
    }

    private void addExpense() {
        System.out.println("\n--- Add New Expense ---");
        String title = readStringInput("Enter expense title/description: ");
        double amount = readDoubleInput("Enter amount : ");

        Category category = selectCategory();

        LocalDate date = readDateInput("Enter date (YYYY-MM-DD) [Press Enter for Today]: ");
        if (date == null)
            date = LocalDate.now();

        String notes = readStringInput("Enter notes/remarks (optional): ");

        Expense expense = new Expense(title, amount, category, date, notes);

        if (expenseDAO.addExpense(expense)) {
            System.out.println(" Expense added successfully!");
        } else {
            System.out.println(" Failed to add expense.");
        }
    }

    private void viewAllExpenses() {
        System.out.println("\n---  All Recorded Expenses ---");
        List<Expense> expenses = expenseDAO.getAllExpenses();

        if (expenses.isEmpty()) {
            System.out.println(" No expenses found in database.");
            return;
        }

        printExpenseTableHeader();
        for (Expense e : expenses) {
            printExpenseRow(e);
        }
        printTableFooter();
    }

    private void viewExpenseById() {
        System.out.println("\n---  Search Expense by ID ---");
        int id = readIntInput("Enter Expense ID: ");
        Expense e = expenseDAO.getExpenseById(id);

        if (e != null) {
            printExpenseTableHeader();
            printExpenseRow(e);
            printTableFooter();
        } else {
            System.out.println(" Expense with ID " + id + " not found!");
        }
    }

    private void updateExpense() {
        System.out.println("\n---  Update Existing Expense ---");
        int id = readIntInput("Enter Expense ID to update: ");
        Expense existing = expenseDAO.getExpenseById(id);

        if (existing == null) {
            System.out.println(" Expense with ID " + id + " not found!");
            return;
        }

        System.out.println("Found: " + existing.getTitle() + " (Rs. " + existing.getAmount() + ")");

        String newTitle = readStringInput("Enter new title [Leave blank to keep '" + existing.getTitle() + "']: ");
        if (newTitle.trim().isEmpty())
            newTitle = existing.getTitle();

        String amountStr = readStringInput("Enter new amount [Leave blank to keep '" + existing.getAmount() + "']: ");
        double newAmount = existing.getAmount();
        if (!amountStr.trim().isEmpty()) {
            try {
                newAmount = Double.parseDouble(amountStr);
            } catch (NumberFormatException ignored) {
            }
        }

        System.out.println("Current Category: " + existing.getCategory().getDisplayName());
        String changeCat = readStringInput("Do you want to change category? (y/n): ");
        Category newCat = existing.getCategory();
        if (changeCat.equalsIgnoreCase("y")) {
            newCat = selectCategory();
        }

        LocalDate newDate = readDateInput(
                "Enter new date (YYYY-MM-DD) [Leave blank to keep '" + existing.getExpenseDate() + "']: ");
        if (newDate == null)
            newDate = existing.getExpenseDate();

        String newNotes = readStringInput("Enter new notes [Leave blank to keep current notes]: ");
        if (newNotes.trim().isEmpty())
            newNotes = existing.getNotes();

        Expense updated = new Expense(id, newTitle, newAmount, newCat, newDate, newNotes);

        if (expenseDAO.updateExpense(updated)) {
            System.out.println(" Expense ID " + id + " updated successfully!");
        } else {
            System.out.println(" Failed to update expense.");
        }
    }

    private void deleteExpense() {
        System.out.println("\n---  Delete Expense ---");
        int id = readIntInput("Enter Expense ID to delete: ");

        Expense existing = expenseDAO.getExpenseById(id);
        if (existing == null) {
            System.out.println(" Expense with ID " + id + " not found!");
            return;
        }

        String confirm = readStringInput("Are you sure you want to delete '" + existing.getTitle() + "' (Rs. "
                + existing.getAmount() + ")? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            if (expenseDAO.deleteExpense(id)) {
                System.out.println(" Expense ID " + id + " deleted successfully!");
            } else {
                System.out.println(" Failed to delete expense.");
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    private void viewTotalExpenses() {
        System.out.println("\n---  Total Expenses Analytics ---");
        double total = expenseDAO.getTotalExpenses();
        System.out.printf(" Grand Total Expense: Rs. %.2f\n", total);
    }

    private void viewCategorySummary() {
        System.out.println("\n---  Expense Breakdown by Category ---");
        Map<Category, Double> summary = expenseDAO.getCategorySummary();

        if (summary.isEmpty()) {
            System.out.println(" No category data available.");
            return;
        }

        System.out.println("+----------------------+----------------+");
        System.out.println("| Category             | Total Amount (Rs.)|");
        System.out.println("+----------------------+----------------+");

        double grandTotal = 0;
        for (Map.Entry<Category, Double> entry : summary.entrySet()) {
            System.out.printf("| %-20s | Rs. %-14.2f |\n", entry.getKey().getDisplayName(), entry.getValue());
            grandTotal += entry.getValue();
        }
        System.out.println("+----------------------+----------------+");
        System.out.printf("| GRAND TOTAL          | Rs. %-14.2f |\n", grandTotal);
        System.out.println("+----------------------+----------------+");
    }

    private Category selectCategory() {
        System.out.println("Select Category:");
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s\n", (i + 1), categories[i].getDisplayName());
        }

        while (true) {
            int choice = readIntInput("Enter choice (1-" + categories.length + "): ");
            if (choice >= 1 && choice <= categories.length) {
                return categories[choice - 1];
            }
            System.out.println("Invalid selection. Try again.");
        }
    }

    private void printExpenseTableHeader() {
        System.out.println(
                "+------+-------------------------------+------------+--------------------+------------+-------------------------+");
        System.out.println(
                "| ID   | Title                         | Amount (Rs.) | Category           | Date       | Notes                   |");
        System.out.println(
                "+------+-------------------------------+------------+--------------------+------------+-------------------------+");
    }

    private void printExpenseRow(Expense e) {
        String notes = e.getNotes() != null ? e.getNotes() : "";
        if (notes.length() > 23)
            notes = notes.substring(0, 20) + "...";
        String title = e.getTitle();
        if (title.length() > 29)
            title = title.substring(0, 26) + "...";

        System.out.printf("| %-4d | %-29s | %-10.2f | %-18s | %-10s | %-23s |\n",
                e.getId(), title, e.getAmount(), e.getCategory().getDisplayName(), e.getExpenseDate(), notes);
    }

    private void printTableFooter() {
        System.out.println(
                "+------+-------------------------------+------------+--------------------+------------+-------------------------+");
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input! Please enter a valid number.");
            }
        }
    }

    private double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input! Please enter a valid decimal amount.");
            }
        }
    }

    private LocalDate readDateInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty())
            return null;
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println(" Invalid date format. Using today's date instead.");
            return LocalDate.now();
        }
    }

    private void printConnectionTroubleshooting() {
        System.out.println(" WARNING: Could not connect to MySQL server at " + DatabaseConnection.getDbUrl());
        System.out.println(" Quick Setup Checklist for Presentation:");
        System.out.println("   1. Ensure MySQL Server is running (via XAMPP / MySQL Workbench / Services).");
        System.out.println("   2. Run 'schema.sql' in MySQL Workbench or execute: mysql -u root -p < schema.sql");
        System.out.println("   3. Check DatabaseConnection.java to update your DB_PASSWORD if needed.\n");
    }
}
