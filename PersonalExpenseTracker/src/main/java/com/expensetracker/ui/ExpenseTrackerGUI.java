package com.expensetracker.ui;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.ExpenseDAOImpl;
import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.db.DatabaseInitializer;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Swing GUI for Personal Expense Tracker.
 *
 * Swing Components Used:
 *   JFrame, JPanel, JLabel, JTextField, JComboBox,
 *   JButton, JTable, JScrollPane, JOptionPane,
 *   DefaultTableModel, BorderLayout, GridBagLayout
 */
public class ExpenseTrackerGUI extends JFrame {

    // ── Colour palette ────────────────────────────────────────
    private static final Color CLR_HEADER  = new Color(28, 78, 128);
    private static final Color CLR_ADD     = new Color(34, 139, 34);
    private static final Color CLR_UPDATE  = new Color(30, 100, 200);
    private static final Color CLR_DELETE  = new Color(180, 35, 35);
    private static final Color CLR_REFRESH = new Color(80, 80, 80);
    private static final Color CLR_SUMMARY = new Color(120, 50, 180);
    private static final Color CLR_CLEAR   = new Color(110, 110, 110);
    private static final Color CLR_ROW_ODD = new Color(245, 248, 255);
    private static final Color CLR_ROW_EVN = Color.WHITE;
    private static final Color CLR_SEL     = new Color(180, 210, 255);
    private static final Color CLR_FOOTER  = new Color(25, 130, 60);
    private static final Color CLR_BG_FORM = new Color(250, 251, 255);

    // ── Fonts ─────────────────────────────────────────────────
    private static final Font FNT_HEADER  = new Font("SansSerif", Font.BOLD,  18);
    private static final Font FNT_LABEL   = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FNT_FIELD   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FNT_BTN     = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FNT_TABLE   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FNT_THEAD   = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FNT_FOOTER  = new Font("SansSerif", Font.BOLD,  13);
    private static final Font FNT_SECTION = new Font("SansSerif", Font.BOLD,  14);

    // ── DAO ───────────────────────────────────────────────────
    private final ExpenseDAO expenseDAO = new ExpenseDAOImpl();

    // ── Form fields ───────────────────────────────────────────
    private JTextField      txtTitle;
    private JTextField      txtAmount;
    private JComboBox<Category> cmbCategory;
    private JTextField      txtDate;
    private JTextField      txtNotes;

    // ── Table ─────────────────────────────────────────────────
    private JTable           expenseTable;
    private DefaultTableModel tableModel;

    // ── Status bar ────────────────────────────────────────────
    private JLabel lblTotal;
    private JLabel lblStatus;

    private static final String[] COLUMNS =
        {"ID", "Title", "Amount (Rs.)", "Category", "Date", "Notes"};

    // ─────────────────────────────────────────────────────────
    public ExpenseTrackerGUI() {
        initUI();
        loadExpenses();
    }

    // =========================================================
    // UI CONSTRUCTION
    // =========================================================
    private void initUI() {
        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 660);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        // Root panel with padding
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildFooter(),    BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_HEADER);
        panel.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("  Personal Expense Tracker");
        title.setFont(FNT_HEADER);
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.WEST);

        lblStatus = new JLabel("Ready");
        lblStatus.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(180, 215, 255));
        panel.add(lblStatus, BorderLayout.EAST);

        return panel;
    }

    // ── Center (table + form side-by-side) ───────────────────
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 8, 12));

        panel.add(buildTablePanel(), BorderLayout.CENTER);
        panel.add(buildFormPanel(),  BorderLayout.EAST);

        return panel;
    }

    // ── Table panel (left/center) ─────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);

        // Section label
        JLabel lbl = new JLabel("All Expenses");
        lbl.setFont(FNT_SECTION);
        lbl.setForeground(CLR_HEADER);
        lbl.setBorder(new EmptyBorder(0, 4, 6, 0));
        panel.add(lbl, BorderLayout.NORTH);

        // Table model - cells not editable (edits go through the form)
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        expenseTable = new JTable(tableModel) {
            // Alternating row colours
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CLR_ROW_EVN : CLR_ROW_ODD);
                } else {
                    c.setBackground(CLR_SEL);
                }
                c.setForeground(Color.DARK_GRAY);
                return c;
            }
        };

        expenseTable.setFont(FNT_TABLE);
        expenseTable.setRowHeight(28);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));
        expenseTable.setSelectionBackground(CLR_SEL);
        expenseTable.setSelectionForeground(Color.DARK_GRAY);

        // Table header styling
        JTableHeader header = expenseTable.getTableHeader();
        header.setFont(FNT_THEAD);
        header.setBackground(new Color(220, 230, 245));
        header.setForeground(CLR_HEADER);
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CLR_HEADER));

        // Column widths
        int[] widths = {45, 200, 110, 140, 105, 200};
        for (int i = 0; i < widths.length; i++)
            expenseTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Center-align ID and Amount columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        // Row-click fills the form automatically
        expenseTable.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) fillFormFromRow();
        });

        return panel;
    }

    // ── Form panel (right side) ───────────────────────────────
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setPreferredSize(new Dimension(270, 0));
        outer.setBackground(CLR_BG_FORM);
        outer.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
            new EmptyBorder(16, 16, 16, 16)));

        // -- Section label --
        JLabel sec = new JLabel("Add / Edit Expense");
        sec.setFont(FNT_SECTION);
        sec.setForeground(CLR_HEADER);
        outer.add(sec, BorderLayout.NORTH);

        // -- Fields (GridBagLayout for clean label+field rows) --
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.insets  = new Insets(4, 0, 4, 0);
        gc.weightx = 1.0;

        txtTitle    = styledField();
        txtAmount   = styledField();
        cmbCategory = new JComboBox<>(Category.values());
        styleCmb(cmbCategory);
        txtDate     = styledField();
        txtDate.setText(LocalDate.now().toString());
        txtNotes    = styledField();

        addFormRow(fields, gc, 0, "Title *",             txtTitle);
        addFormRow(fields, gc, 1, "Amount (Rs.) *",      txtAmount);
        addFormRow(fields, gc, 2, "Category",            cmbCategory);
        addFormRow(fields, gc, 3, "Date (YYYY-MM-DD)",   txtDate);
        addFormRow(fields, gc, 4, "Notes",               txtNotes);

        // Hint label
        JLabel hint = new JLabel("* required fields");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        fields.add(hint, gc);

        outer.add(fields, BorderLayout.CENTER);

        // -- Buttons (stacked vertically) --
        JPanel btns = new JPanel(new GridLayout(6, 1, 0, 7));
        btns.setOpaque(false);
        btns.add(solidBtn("+ Add Expense",       CLR_ADD,     e -> onAdd()));
        btns.add(solidBtn("✎ Update Selected",   CLR_UPDATE,  e -> onUpdate()));
        btns.add(solidBtn("✕ Delete Selected",   CLR_DELETE,  e -> onDelete()));
        btns.add(solidBtn("↺ Refresh Table",     CLR_REFRESH, e -> loadExpenses()));
        btns.add(solidBtn("≡ Category Summary",  CLR_SUMMARY, e -> showCategorySummary()));
        btns.add(solidBtn("✗ Clear Form",        CLR_CLEAR,   e -> clearForm()));
        outer.add(btns, BorderLayout.SOUTH);

        return outer;
    }

    /** Adds a label row + component row into the GridBag form */
    private void addFormRow(JPanel p, GridBagConstraints gc, int row,
                            String labelText, JComponent field) {
        gc.gridy   = row * 2;
        gc.gridx   = 0;
        gc.insets  = new Insets(row == 0 ? 0 : 8, 0, 2, 0);
        gc.weighty = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FNT_LABEL);
        lbl.setForeground(new Color(50, 70, 100));
        p.add(lbl, gc);

        gc.gridy  = row * 2 + 1;
        gc.insets = new Insets(0, 0, 0, 0);
        p.add(field, gc);
    }

    // ── Footer ────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_FOOTER);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblTotal = new JLabel("Total: Rs. 0.00   |   Records: 0");
        lblTotal.setFont(FNT_FOOTER);
        lblTotal.setForeground(Color.WHITE);
        panel.add(lblTotal, BorderLayout.WEST);

        JLabel tip = new JLabel("Tip: Click a row to load it into the form for editing");
        tip.setFont(new Font("SansSerif", Font.ITALIC, 12));
        tip.setForeground(new Color(180, 240, 200));
        panel.add(tip, BorderLayout.EAST);

        return panel;
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    private void onAdd() {
        String title  = txtTitle.getText().trim();
        String amtStr = txtAmount.getText().trim();
        if (title.isEmpty() || amtStr.isEmpty()) {
            showWarn("Title and Amount are required fields.");
            return;
        }
        double amount;
        try { amount = Double.parseDouble(amtStr); }
        catch (NumberFormatException ex) { showWarn("Amount must be a number (e.g. 150 or 99.50)."); return; }

        LocalDate date = parseDate();
        Expense e = new Expense(title, amount, (Category) cmbCategory.getSelectedItem(), date, txtNotes.getText().trim());
        if (expenseDAO.addExpense(e)) {
            setStatus("Expense \"" + title + "\" added.");
            clearForm(); loadExpenses();
        } else showErr("Failed to save expense. Check your database connection.");
    }

    private void onUpdate() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) { showWarn("Select a row from the table first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);

        String title  = txtTitle.getText().trim();
        String amtStr = txtAmount.getText().trim();
        if (title.isEmpty() || amtStr.isEmpty()) { showWarn("Title and Amount are required fields."); return; }
        double amount;
        try { amount = Double.parseDouble(amtStr); }
        catch (NumberFormatException ex) { showWarn("Amount must be a number."); return; }

        LocalDate date = parseDate();
        Expense e = new Expense(id, title, amount, (Category) cmbCategory.getSelectedItem(), date, txtNotes.getText().trim());
        if (expenseDAO.updateExpense(e)) {
            setStatus("Expense ID " + id + " updated.");
            clearForm(); loadExpenses();
        } else showErr("Failed to update. Check your database connection.");
    }

    private void onDelete() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) { showWarn("Select a row from the table first."); return; }
        int id    = (int) tableModel.getValueAt(row, 0);
        String title = tableModel.getValueAt(row, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this,
            "Delete expense: \"" + title + "\"?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            if (expenseDAO.deleteExpense(id)) {
                setStatus("Expense \"" + title + "\" deleted.");
                loadExpenses(); clearForm();
            } else showErr("Failed to delete. Check your database connection.");
        }
    }

    // =========================================================
    // DATA LOADING
    // =========================================================

    private void loadExpenses() {
        tableModel.setRowCount(0);
        List<Expense> list = expenseDAO.getAllExpenses();
        for (Expense e : list) {
            tableModel.addRow(new Object[]{
                e.getId(),
                e.getTitle(),
                String.format("%.2f", e.getAmount()),
                e.getCategory().getDisplayName(),
                e.getExpenseDate().toString(),
                e.getNotes() != null ? e.getNotes() : ""
            });
        }
        double total = expenseDAO.getTotalExpenses();
        lblTotal.setText(String.format("Total Spent: Rs. %.2f   |   Records: %d", total, list.size()));
    }

    private void fillFormFromRow() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) return;
        txtTitle.setText(tableModel.getValueAt(row, 1).toString());
        txtAmount.setText(tableModel.getValueAt(row, 2).toString());
        String cat = tableModel.getValueAt(row, 3).toString();
        for (Category c : Category.values())
            if (c.getDisplayName().equals(cat)) { cmbCategory.setSelectedItem(c); break; }
        txtDate.setText(tableModel.getValueAt(row, 4).toString());
        txtNotes.setText(tableModel.getValueAt(row, 5).toString());
        setStatus("Row loaded into form. Edit fields and click Update.");
    }

    // =========================================================
    // CATEGORY SUMMARY DIALOG
    // =========================================================

    private void showCategorySummary() {
        Map<Category, Double> summary = expenseDAO.getCategorySummary();
        if (summary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses recorded yet.", "Summary", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"Category", "Total (Rs.)"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        double grand = 0;
        for (Map.Entry<Category, Double> en : summary.entrySet()) {
            m.addRow(new Object[]{en.getKey().getDisplayName(), String.format("%.2f", en.getValue())});
            grand += en.getValue();
        }
        m.addRow(new Object[]{"─── GRAND TOTAL ───", String.format("%.2f", grand)});

        JTable t = new JTable(m);
        t.setEnabled(false);
        t.setRowHeight(28);
        t.setFont(FNT_TABLE);
        t.getTableHeader().setFont(FNT_THEAD);

        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(340, Math.min(50 + m.getRowCount() * 30, 400)));

        JOptionPane.showMessageDialog(this, sp, "Category Summary", JOptionPane.PLAIN_MESSAGE);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void clearForm() {
        txtTitle.setText(""); txtAmount.setText("");
        txtDate.setText(LocalDate.now().toString()); txtNotes.setText("");
        cmbCategory.setSelectedIndex(0);
        expenseTable.clearSelection();
        setStatus("Form cleared.");
    }

    private LocalDate parseDate() {
        try { return LocalDate.parse(txtDate.getText().trim()); }
        catch (DateTimeParseException ex) { return LocalDate.now(); }
    }

    private void setStatus(String msg) { lblStatus.setText(msg + "   "); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Notice",   JOptionPane.WARNING_MESSAGE); }
    private void showErr (String msg)  { JOptionPane.showMessageDialog(this, msg, "Error",    JOptionPane.ERROR_MESSAGE); }

    // ── Styled component factories ───────────────────────────

    /** Creates a styled JTextField with border and proper font */
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FNT_FIELD);
        f.setPreferredSize(new Dimension(0, 32));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1),
            new EmptyBorder(4, 8, 4, 8)));
        return f;
    }

    /** Creates a styled JComboBox */
    private void styleCmb(JComboBox<Category> c) {
        c.setFont(FNT_FIELD);
        c.setPreferredSize(new Dimension(0, 32));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        c.setBorder(BorderFactory.createLineBorder(new Color(180, 195, 220), 1));
        c.setBackground(Color.WHITE);
    }

    /**
     * Creates a solid-coloured button with white text.
     * Does NOT use the system L&F colour override because we paint it manually.
     */
    private JButton solidBtn(String label, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Darken slightly on hover/press
                Color draw = getModel().isPressed()
                    ? bg.darker().darker()
                    : getModel().isRollover() ? bg.darker() : bg;
                g2.setColor(draw);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* no default border */ }
        };
        btn.setFont(FNT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);  // We handle painting ourselves
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 36));
        btn.addActionListener(action);
        return btn;
    }

    // =========================================================
    // LAUNCHER
    // =========================================================
    public static void launch() {
        // Use cross-platform Metal L&F so colours render consistently
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        DatabaseInitializer.initialize();

        if (!DatabaseConnection.testConnection()) {
            int choice = JOptionPane.showConfirmDialog(null,
                "Cannot connect to MySQL database!\n\n" +
                "Please check:\n" +
                "  1. MySQL Server is running (XAMPP / Workbench)\n" +
                "  2. You have run schema.sql to create the database\n" +
                "  3. DB_PASSWORD is correct in DatabaseConnection.java\n\n" +
                "Continue anyway (table will be empty)?",
                "Database Connection Failed",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) System.exit(0);
        }

        SwingUtilities.invokeLater(() -> new ExpenseTrackerGUI().setVisible(true));
    }
}
