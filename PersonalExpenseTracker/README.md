# Personal Expense Tracker (Java + MySQL JDBC)

A clean, easy-to-understand **Personal Expense Tracker** project in Java implementing Object-Oriented Programming (OOP) concepts, Data Access Object (DAO) pattern, MySQL Database integration using **MySQL Connector/J**, and interactive CLI menus. Ideal for college presentations and viva demonstrations.

---

## 📁 Project Structure

```
PersonalExpenseTracker/
├── pom.xml                                   # Maven dependencies (mysql-connector-j)
├── schema.sql                                # MySQL Database creation script
├── README.md                                 # Quickstart guide
├── PRESENTATION_GUIDE.md                     # College presentation & Viva Q&A sheet
└── src/main/java/com/expensetracker/
    ├── Main.java                             # Application Entry Point
    ├── model/
    │   ├── Category.java                     # Expense Category Enum
    │   └── Expense.java                      # Expense Domain Model (Encapsulation)
    ├── db/
    │   ├── DatabaseConnection.java           # JDBC Connection Manager
    │   └── DatabaseInitializer.java          # Automatic DB DDL setup
    ├── dao/
    │   ├── ExpenseDAO.java                   # DAO Interface (Abstraction)
    │   └── ExpenseDAOImpl.java               # JDBC DAO Implementation (PreparedStatements)
    └── ui/
        └── ExpenseTrackerUI.java             # Interactive CLI Dashboard & Menus
```

---

## ⚡ Quick Setup Guide

### 1. Requirements
- JDK 17 (or JDK 11+)
- Apache Maven
- MySQL Server (installed locally or via XAMPP)

### 2. Configure Database Connection (Optional)
If your MySQL password is not `root`, open `DatabaseConnection.java` and update:
```java
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_mysql_password";
```

### 3. Run Database Script
Open MySQL Workbench / Terminal and run `schema.sql`, or launch the application directly (it will automatically attempt to create `expense_tracker_db` and tables for you).

### 4. Build and Run Application
Run the following command from the project root folder:

```bash
mvn clean compile exec:java
```

---

## 🎓 College Presentation Sheet
Check out [PRESENTATION_GUIDE.md](file:///C:/Users/Dheer/Desktop/PersonalExpenseTracker/PRESENTATION_GUIDE.md) for architecture diagrams, step-by-step JDBC explanation, and top 10 college viva questions & answers.
