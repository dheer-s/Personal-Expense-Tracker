-- ======================================================
-- Personal Expense Tracker Database Schema (MySQL)
-- ======================================================

-- 1. Create Database if it does not exist
CREATE DATABASE IF NOT EXISTS expense_tracker_db;

-- 2. Switch to using expense_tracker_db
USE expense_tracker_db;

-- 3. Create Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    notes VARCHAR(255)
);

-- 4. Sample Seed Data for testing
INSERT INTO expenses (title, amount, category, expense_date, notes) VALUES
('Groceries at Supermarket', 45.50, 'FOOD', CURDATE(), 'Milk, eggs, and bread'),
('Bus Pass Monthly', 30.00, 'TRANSPORT', CURDATE(), 'Monthly public transit'),
('Electricity Bill', 75.20, 'UTILITIES', CURDATE(), 'July bill payment'),
('Movie Ticket', 15.00, 'ENTERTAINMENT', CURDATE(), 'Weekend movie with friends');

-- 5. Verification Query
SELECT * FROM expenses;
