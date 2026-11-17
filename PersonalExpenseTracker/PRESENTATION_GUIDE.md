# 🎓 Personal Expense Tracker - College Presentation & Viva Guide

This document is your complete study and presentation cheat-sheet for explaining the **Personal Expense Tracker** project to your college professors, external examiners, or classmates.

---

## 📌 1. Project Overview & Features

The **Personal Expense Tracker** is a Java application designed to help individuals record, track, and analyze their personal daily expenses.

### Key Features Demonstrated:
1. **CRUD Operations**: Add, View, Search, Update, and Delete Expense records in a MySQL Database.
2. **Expense Analytics**: Instant calculation of total expenses and aggregate breakdowns by category (`FOOD`, `TRANSPORT`, `UTILITIES`, etc.) using SQL aggregate functions (`SUM`, `GROUP BY`).
3. **Robust Input Validation**: Interactive command-line UI with error handling and user-friendly table formatting.
4. **Automated Schema Setup**: Automatically creates database and tables on launch.

---

## 🏗️ 2. Architectural Design & OOP Concepts

The application follows the **Layered Architecture (DAO Pattern)** to achieve separation of concerns:

```
                  +--------------------------+
                  |  Main / Presentation UI  |
                  |   (ExpenseTrackerUI)     |
                  +------------+-------------+
                               |
                               v
                  +--------------------------+
                  |   DAO Interface (OOP)    |
                  |       (ExpenseDAO)       |
                  +------------+-------------+
                               | (Implements)
                               v
                  +--------------------------+
                  |    DAO Implementation    |
                  |     (ExpenseDAOImpl)     |
                  +------------+-------------+
                               | (JDBC Calls)
                               v
                  +--------------------------+
                  |    Database Layer        |
                  |   (DatabaseConnection)   |
                  +------------+-------------+
                               | (TCP/IP 3306)
                               v
                  +--------------------------+
                  |  MySQL DB Server         |
                  |   (expense_tracker_db)   |
                  +--------------------------+
```

### 💡 Core OOP Principles Implemented:
1. **Encapsulation**:
   - The `Expense` class encapsulates private attributes (`id`, `title`, `amount`, `category`, `expenseDate`, `notes`).
   - Access is restricted using `public` getters and setters.

2. **Abstraction**:
   - The `ExpenseDAO` interface hides database implementation details from the UI layer.
   - The UI layer only interacts with contract methods like `addExpense()` or `getAllExpenses()`.

3. **Polymorphism**:
   - `ExpenseDAOImpl` implements `ExpenseDAO`.
   - The UI references the interface (`ExpenseDAO expenseDAO = new ExpenseDAOImpl()`), enabling flexibility if switching database implementations in the future.

4. **Composition & Enums**:
   - Uses Java `Category` Enum to enforce clean domain categories.

---

## 🔌 3. How JDBC Works in This Project (Step-by-Step)

JDBC (**Java Database Connectivity**) is an API that allows Java programs to execute SQL statements.

Here is the exact **5-Step JDBC Process** used in `DatabaseConnection.java` and `ExpenseDAOImpl.java`:

### Step 1: Load and Register the JDBC Driver (MySQL Connector/J)
```java
Class.forName("com.mysql.cj.jdbc.Driver");
```
*Loads the MySQL Connector/J driver class into memory.*

### Step 2: Establish the Connection
```java
Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/expense_tracker_db", "root", "password"
);
```
*Connects to MySQL server running locally on port 3306.*

### Step 3: Create a PreparedStatement
```java
String sql = "INSERT INTO expenses (title, amount, category, expense_date, notes) VALUES (?, ?, ?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, expense.getTitle());
pstmt.setDouble(2, expense.getAmount());
pstmt.setString(3, expense.getCategory().name());
pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
pstmt.setString(5, expense.getNotes());
```
*Uses `?` placeholders for security and performance.*

### Step 4: Execute SQL Query & Process Results
```java
// For SELECT queries:
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
    int id = rs.getInt("id");
    String title = rs.getString("title");
    double amount = rs.getDouble("amount");
    // Map to Expense object
}

// For INSERT, UPDATE, DELETE queries:
int rowsAffected = pstmt.executeUpdate();
```

### Step 5: Close Connection & Resources
```java
// Try-with-resources syntax automatically closes Connection, Statement, and ResultSet
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {
    // Operations...
}
```

---

## ❓ 4. Top 10 Viva Questions & Answers

### Q1: What is JDBC?
**Answer**: JDBC (Java Database Connectivity) is a standard Java API that enables Java applications to connect to relational databases (like MySQL, PostgreSQL, Oracle) and execute SQL statements.

### Q2: What is MySQL Connector/J?
**Answer**: MySQL Connector/J is the official Type 4 JDBC driver provided by MySQL. It converts Java JDBC API calls directly into MySQL network protocol requests.

### Q3: Why use `PreparedStatement` instead of regular `Statement`?
**Answer**: 
1. **Prevents SQL Injection attacks** by escaping input variables automatically.
2. **Better Performance**: Pre-compiles SQL queries on the database server.
3. **Clean Code**: Eliminates messy string concatenation (e.g. `' + title + '`).

### Q4: What is the DAO Pattern?
**Answer**: Data Access Object (DAO) pattern separates business logic from data access logic. It provides an interface (`ExpenseDAO`) defining database operations without exposing how the data is retrieved.

### Q5: How do you handle database resource leaks?
**Answer**: We use Java's **try-with-resources** statement (`AutoCloseable`). It ensures `Connection`, `PreparedStatement`, and `ResultSet` are closed automatically when the block finishes, even if an exception occurs.

### Q6: What is a `ResultSet`?
**Answer**: A `ResultSet` is a table of data representing a database result set, generated by executing a SQL query. We iterate over it using `rs.next()` to map SQL columns to Java objects.

### Q7: How does category summation work in SQL?
**Answer**: We execute a SQL query using the `GROUP BY` clause and aggregate function `SUM()`:
```sql
SELECT category, SUM(amount) AS total FROM expenses GROUP BY category;
```

### Q8: What happens if the database is not running?
**Answer**: The application catches `SQLException`, displays a friendly error message, and provides quick troubleshooting instructions without crashing unexpectedly.

### Q9: What is `AUTO_INCREMENT` in MySQL?
**Answer**: It automatically generates a unique sequential integer (1, 2, 3...) for the `id` column whenever a new expense row is inserted.

### Q10: How do you convert Java `LocalDate` to SQL `Date`?
**Answer**: Using `java.sql.Date.valueOf(localDate)` and `sqlDate.toLocalDate()`.

---

## 🚀 5. How to Run the Project for Presentation

1. **Start MySQL Server** (XAMPP / MySQL Workbench / Services).
2. **Execute `schema.sql`** (or let the app auto-create database on first run).
3. **Compile & Run via Maven**:
   ```bash
   mvn clean compile exec:java
   ```
4. Perform demo: Add expenses, view all, update, delete, and show category totals!
