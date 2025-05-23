package Library_System;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class Library_Admin extends JFrame {

    private JTable tbl_books, tbl_users, tbl_overdue;
    private JButton btn_addBook, btn_deleteBook, btn_addMultipleBooks, btn_deleteMultipleBooks;
    private JButton btn_addUser, btn_deleteUser, btn_addMultipleUsers, btn_deleteMultipleUsers, btn_logout;
    private JButton btn_addBorrowing, btn_returnBook, btn_markOverdue;
    private JLabel lbl_title, lbl_userCount, lbl_adminCount;
    private JTabbedPane tabbedPane;
    private JButton btn_refresh;
    private JTable tbl_archived;

    public Library_Admin() {
        initComponents();
        setLocationRelativeTo(null);
        loadBooks();
        loadUsers();
        loadOverdue();
    }

    private void initComponents() {
        setTitle("Library Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        lbl_title = new JLabel("Welcome Admin!");
        lbl_title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl_title.setForeground(Color.WHITE);

        btn_logout = new JButton("Logout");
        btn_logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...");
            new LoginForm().setVisible(true);
            this.dispose();
        });
        btn_refresh = new JButton("Refresh");
        btn_refresh.setFont(new Font("Metal", Font.BOLD, 10));

        btn_refresh.setPreferredSize(btn_logout.getPreferredSize());

        btn_refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();               // Close current window
                Library_Admin refreshed = new Library_Admin(); // Create new instance
                refreshed.setVisible(true); // Show it    // Re-open a fresh one
                btn_refresh.setFont(new Font("Segoe UI", Font.PLAIN, 7));
            }

        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.RED);

        topPanel.add(lbl_title, BorderLayout.WEST);
        JPanel rightButtons = new JPanel();
        rightButtons.setLayout(new BoxLayout(rightButtons, BoxLayout.Y_AXIS));
        rightButtons.add(btn_logout);
        rightButtons.add(Box.createVerticalStrut(5));
        rightButtons.add(btn_refresh);
        topPanel.add(rightButtons, BorderLayout.EAST);

        // BOOKS TAB
        tbl_books = new JTable(new DefaultTableModel(
                new String[]{"ID", "Title", "Author", "Genre", "Year", "Available"}, 0));
        btn_addBook = new JButton("Add Book");
        btn_deleteBook = new JButton("Delete Book");
        btn_addMultipleBooks = new JButton("Add Multiple Books");
        btn_deleteMultipleBooks = new JButton("Delete Multiple Books");

        btn_addBook.addActionListener(e -> addBookDialog());
        btn_deleteBook.addActionListener(e -> deleteSelected(tbl_books, "CATALOG", "BOOKID"));
        btn_addMultipleBooks.addActionListener(e -> addMultipleBooksDialog());
        btn_deleteMultipleBooks.addActionListener(e -> deleteMultipleBooks());

        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.add(new JScrollPane(tbl_books), BorderLayout.CENTER);
        JPanel bookBtnPanel = new JPanel();
        bookBtnPanel.add(btn_addBook);
        bookBtnPanel.add(btn_deleteBook);
        bookBtnPanel.add(btn_addMultipleBooks);
        bookBtnPanel.add(btn_deleteMultipleBooks);
        bookPanel.add(bookBtnPanel, BorderLayout.SOUTH);

        // USERS TAB
        lbl_userCount = new JLabel("Total Users: 0");
        lbl_adminCount = new JLabel("Total Admins: 0");

        tbl_users = new JTable(new DefaultTableModel(new String[]{"UserID", "Username", "UserType"}, 0));
        btn_addUser = new JButton("Add User");
        btn_deleteUser = new JButton("Delete User");
        btn_addMultipleUsers = new JButton("Add Multiple Users");
        btn_deleteMultipleUsers = new JButton("Delete Multiple Users");

        btn_addUser.addActionListener(e -> addUserDialog());
        btn_deleteUser.addActionListener(e -> deleteUser());
        btn_addMultipleUsers.addActionListener(e -> addMultipleUsersDialog());
        btn_deleteMultipleUsers.addActionListener(e -> deleteMultipleUsers());

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(new JScrollPane(tbl_users), BorderLayout.CENTER);

        JPanel userBtnPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btn_addUser);
        btnPanel.add(btn_deleteUser);
        btnPanel.add(btn_addMultipleUsers);
        btnPanel.add(btn_deleteMultipleUsers);

        userBtnPanel.add(btnPanel, BorderLayout.WEST);
        userBtnPanel.add(lbl_userCount, BorderLayout.EAST);

        userPanel.add(userBtnPanel, BorderLayout.SOUTH);

        // OVERDUE TAB
        tbl_overdue = new JTable(new DefaultTableModel(
                new String[]{"Borrower ID", "Borrower Name", "Book ID", "Book Title", "Date Borrowed", "Due Date", "Surcharge"}, 0));
        JPanel overduePanel = new JPanel(new BorderLayout());
        overduePanel.add(new JScrollPane(tbl_overdue), BorderLayout.CENTER);
        btn_addBorrowing = new JButton("Add Borrowing");
        btn_returnBook = new JButton("Return Book");
        btn_markOverdue = new JButton("Mark as Overdue");
        JButton btn_markMultipleOverdue = new JButton("Mark Multiple as Overdue");
        btn_markMultipleOverdue.addActionListener(e -> markMultipleOverdueDialog());

        btn_addBorrowing.addActionListener(e -> addBorrowingDialog());
        btn_returnBook.addActionListener(e -> returnBookDialog());
        btn_markOverdue.addActionListener(e -> markAsOverdueDialog());
        JPanel borrowingBtnPanel = new JPanel();
        borrowingBtnPanel.add(btn_addBorrowing);
        borrowingBtnPanel.add(btn_returnBook);
        borrowingBtnPanel.add(btn_markOverdue);
        borrowingBtnPanel.add(btn_markMultipleOverdue);
        overduePanel.add(borrowingBtnPanel, BorderLayout.SOUTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("CATALOG", bookPanel);
        tabbedPane.addTab("ACCOUNT", userPanel);
        tabbedPane.addTab("BORROWED", overduePanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        setSize(800, 500);

        tbl_archived = new JTable(new DefaultTableModel(
                new String[]{"Book ID", "Title", "Author", "Genre", "Year", "Deleted At"}, 0));

        JPanel archivePanel = new JPanel(new BorderLayout());
        archivePanel.add(new JScrollPane(tbl_archived), BorderLayout.CENTER);

        tabbedPane.addTab("HISTORY", archivePanel);
        loadArchivedBooks();
    }

    private void loadArchivedBooks() {
        DefaultTableModel model = (DefaultTableModel) tbl_archived.getModel();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM archived_books")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BOOKID"),
                    rs.getString("BOOKTITLE"),
                    rs.getString("AUTHOR"),
                    rs.getString("GENRENAME"),
                    rs.getInt("BOOKYR"),
                    rs.getTimestamp("DELETED_AT")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading archive: " + e.getMessage());
        }
    }

    private void addBorrowingDialog() {
        // Create fields for the dialog
        JTextField customerIDField = new JTextField(10);
        JTextField customerNameField = new JTextField(20);
        JTextField contactNoField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        JTextField bookIDField = new JTextField(10);

        // Create date fields
        JSpinner borrowedDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner dueDateSpinner = new JSpinner(new SpinnerDateModel());

        // Set current date
        borrowedDateSpinner.setValue(new java.util.Date());

        // Set default due date (7 days from today)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        dueDateSpinner.setValue(calendar.getTime());

        // Format date spinners
        JSpinner.DateEditor borrowedEditor = new JSpinner.DateEditor(borrowedDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor dueEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd");
        borrowedDateSpinner.setEditor(borrowedEditor);
        dueDateSpinner.setEditor(dueEditor);

        // Create a book search button
        JButton searchBookBtn = new JButton("Search");
        searchBookBtn.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter Book ID or Title:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT BOOKID, BOOKTITLE FROM CATALOG WHERE BOOKID = ? OR BOOKTITLE LIKE ?")) {

                    stmt.setString(1, searchTerm);
                    stmt.setString(2, "%" + searchTerm + "%");
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        bookIDField.setText(rs.getString("BOOKID"));
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching book found.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching for book: " + ex.getMessage());
                }
            }
        });

        // Create customer search button
        JButton searchCustomerBtn = new JButton("Search");
        searchCustomerBtn.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter Customer ID or Name:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT customerID, customerName, email, contactNo FROM Borrowed WHERE customerID = ? OR customerName LIKE ? "
                        + "UNION "
                        + "SELECT customerID, customerName, email, contactNo FROM Overdue WHERE customerID = ? OR customerName LIKE ?")) {

                    stmt.setString(1, searchTerm);
                    stmt.setString(2, "%" + searchTerm + "%");
                    stmt.setString(3, searchTerm);
                    stmt.setString(4, "%" + searchTerm + "%");
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        customerIDField.setText(rs.getString("customerID"));
                        customerNameField.setText(rs.getString("customerName"));
                        emailField.setText(rs.getString("email"));
                        contactNoField.setText(rs.getString("contactNo"));
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching customer found.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching for customer: " + ex.getMessage());
                }
            }
        });

        // Create panel for book search
        JPanel bookSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookSearchPanel.add(bookIDField);
        bookSearchPanel.add(searchBookBtn);

        // Create panel for customer search
        JPanel customerSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerSearchPanel.add(customerIDField);
        customerSearchPanel.add(searchCustomerBtn);

        // Create the panel with all fields
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Borrower ID:"));
        panel.add(customerSearchPanel);
        panel.add(new JLabel("Borrower Name:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactNoField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookSearchPanel);
        panel.add(new JLabel("Date Borrowed:"));
        panel.add(borrowedDateSpinner);
        panel.add(new JLabel("Due Date:"));
        panel.add(dueDateSpinner);

        // Add padding
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add New Borrowing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int customerID = Integer.parseInt(customerIDField.getText().trim());
                String customerName = customerNameField.getText().trim();
                String email = emailField.getText().trim();
                String contactNo = contactNoField.getText().trim();
                String bookID = bookIDField.getText().trim();

                // Convert JSpinner date to java.sql.Date
                java.sql.Date borrowedDate = new java.sql.Date(((java.util.Date) borrowedDateSpinner.getValue()).getTime());
                java.sql.Date dueDate = new java.sql.Date(((java.util.Date) dueDateSpinner.getValue()).getTime());

                // Validate inputs
                if (customerName.isEmpty() || bookID.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Borrower name and Book ID are required.");
                    return;
                }

                // Check if book is available
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement checkStmt = conn.prepareStatement("SELECT STOCK FROM STOCK WHERE BOOKID = ?")) {

                    checkStmt.setString(1, bookID);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        int stock = rs.getInt("STOCK");
                        if (stock <= 0) {
                            JOptionPane.showMessageDialog(this, "This book is not available (out of stock).");
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found in stock.");
                        return;
                    }

                    // Insert into Borrowed table
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO Borrowed (customerID, customerName, email, contactNo, bookID, dateBorrowed, dueDate) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                        insertStmt.setInt(1, customerID);
                        insertStmt.setString(2, customerName);
                        insertStmt.setString(3, email);
                        insertStmt.setString(4, contactNo);
                        insertStmt.setString(5, bookID);
                        insertStmt.setDate(6, borrowedDate);
                        insertStmt.setDate(7, dueDate);

                        insertStmt.executeUpdate();

                        // Update stock count
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE STOCK SET STOCK = STOCK - 1 WHERE BOOKID = ?")) {
                            updateStmt.setString(1, bookID);
                            updateStmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Borrowing record added successfully!");
                        loadOverdue(); // Refresh the table
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Customer ID. Please enter a number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }

    private void returnBookDialog() {
        // Ask for customer ID or book ID
        String input = JOptionPane.showInputDialog(this, "Enter Borrower ID or Book ID:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
            // Try to find in Borrowed table
            String query = "SELECT customerID, customerName, bookID FROM Borrowed WHERE customerID = ? OR bookID = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, input);
                stmt.setString(2, input);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int customerID = rs.getInt("customerID");
                    String customerName = rs.getString("customerName");
                    String bookID = rs.getString("bookID");

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Return book for:\nBorrower ID: " + customerID + "\nName: " + customerName + "\nBook ID: " + bookID,
                            "Confirm Return", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // Delete from Borrowed
                        try (PreparedStatement deleteStmt = conn.prepareStatement(
                                "DELETE FROM Borrowed WHERE customerID = ? AND bookID = ?")) {

                            deleteStmt.setInt(1, customerID);
                            deleteStmt.setString(2, bookID);
                            deleteStmt.executeUpdate();

                            // Update stock
                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE STOCK SET STOCK = STOCK + 1 WHERE BOOKID = ?")) {
                                updateStmt.setString(1, bookID);
                                updateStmt.executeUpdate();
                            }

                            JOptionPane.showMessageDialog(this, "Book returned successfully!");
                            loadOverdue(); // Refresh the table
                        }
                    }
                } else {
                    // Try to find in Overdue table
                    query = "SELECT customerID, customerName, bookID, surcharge FROM Overdue WHERE customerID = ? OR bookID = ?";

                    try (PreparedStatement overdueStmt = conn.prepareStatement(query)) {
                        overdueStmt.setString(1, input);
                        overdueStmt.setString(2, input);

                        ResultSet overdueRs = overdueStmt.executeQuery();

                        if (overdueRs.next()) {
                            int customerID = overdueRs.getInt("customerID");
                            String customerName = overdueRs.getString("customerName");
                            String bookID = overdueRs.getString("bookID");
                            double surcharge = overdueRs.getDouble("surcharge");

                            int confirm = JOptionPane.showConfirmDialog(this,
                                    "Return overdue book for:\nBorrower ID: " + customerID
                                    + "\nName: " + customerName
                                    + "\nBook ID: " + bookID
                                    + "\nSurcharge: ₱" + surcharge,
                                    "Confirm Return", JOptionPane.YES_NO_OPTION);

                            if (confirm == JOptionPane.YES_OPTION) {
                                // Delete from Overdue
                                try (PreparedStatement deleteStmt = conn.prepareStatement(
                                        "DELETE FROM Overdue WHERE customerID = ? AND bookID = ?")) {

                                    deleteStmt.setInt(1, customerID);
                                    deleteStmt.setString(2, bookID);
                                    deleteStmt.executeUpdate();

                                    // Update stock
                                    try (PreparedStatement updateStmt = conn.prepareStatement(
                                            "UPDATE STOCK SET STOCK = STOCK + 1 WHERE BOOKID = ?")) {
                                        updateStmt.setString(1, bookID);
                                        updateStmt.executeUpdate();
                                    }

                                    JOptionPane.showMessageDialog(this,
                                            "Book returned successfully! Surcharge: ₱" + surcharge);
                                    loadOverdue(); // Refresh the table
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No borrowing record found for this ID.");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void markAsOverdueDialog() {
        // Ask for customer ID or book ID
        String input = JOptionPane.showInputDialog(this, "Enter Borrower ID");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
            // Find in Borrowed table
            String query = "SELECT b.customerID, b.customerName, b.email, b.contactNo, b.bookID, b.dateBorrowed, b.dueDate "
                    + "FROM Borrowed b WHERE b.customerID = ? OR b.bookID = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, input);
                stmt.setString(2, input);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int customerID = rs.getInt("customerID");
                    String customerName = rs.getString("customerName");
                    String email = rs.getString("email");
                    String contactNo = rs.getString("contactNo");
                    String bookID = rs.getString("bookID");
                    java.sql.Date borrowedDate = rs.getDate("dateBorrowed");
                    java.sql.Date dueDate = rs.getDate("dueDate");

                    // Calculate number of days overdue
                    long currentTimeMillis = System.currentTimeMillis();
                    java.sql.Date currentDate = new java.sql.Date(currentTimeMillis);

                    long diffInMillis = currentDate.getTime() - dueDate.getTime();
                    int daysOverdue = (int) (diffInMillis / (1000 * 60 * 60 * 24));

                    if (daysOverdue <= 0) {
                        JOptionPane.showMessageDialog(this, "This book is not overdue yet.");
                        return;
                    }

                    // Calculate surcharge (10 pesos per day)
                    double surcharge = daysOverdue * 10.0;

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Mark as overdue:\nBorrower ID: " + customerID
                            + "\nName: " + customerName
                            + "\nBook ID: " + bookID
                            + "\nDays Overdue: " + daysOverdue
                            + "\nSurcharge: ₱" + surcharge,
                            "Confirm Overdue", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // Begin transaction
                        conn.setAutoCommit(false);

                        try {
                            // Insert into Overdue
                            try (PreparedStatement insertStmt = conn.prepareStatement(
                                    "INSERT INTO Overdue (customerID, customerName, email, contactNo, bookID, dateBorrowed, dueDate, surcharge) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                                insertStmt.setInt(1, customerID);
                                insertStmt.setString(2, customerName);
                                insertStmt.setString(3, email);
                                insertStmt.setString(4, contactNo);
                                insertStmt.setString(5, bookID);
                                insertStmt.setDate(6, borrowedDate);
                                insertStmt.setDate(7, dueDate);
                                insertStmt.setDouble(8, surcharge);

                                insertStmt.executeUpdate();
                            }

                            // Delete from Borrowed
                            try (PreparedStatement deleteStmt = conn.prepareStatement(
                                    "DELETE FROM Borrowed WHERE customerID = ? AND bookID = ?")) {

                                deleteStmt.setInt(1, customerID);
                                deleteStmt.setString(2, bookID);
                                deleteStmt.executeUpdate();
                            }

                            conn.commit();
                            JOptionPane.showMessageDialog(this, "Book marked as overdue with surcharge: ₱" + surcharge);
                            loadOverdue(); // Refresh the table

                        } catch (SQLException ex) {
                            conn.rollback();
                            throw ex;
                        } finally {
                            conn.setAutoCommit(true);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No borrowing record found for this ID.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void markMultipleOverdueDialog() {
        // Create a multi-select dialog with all currently borrowed books
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"", "Customer ID", "Customer Name", "Book ID", "Book Title", "Date Borrowed", "Due Date", "Days Overdue"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
            // Get current date
            long currentTimeMillis = System.currentTimeMillis();
            java.sql.Date currentDate = new java.sql.Date(currentTimeMillis);

            // Query borrowed books
            String query = "SELECT b.customerID, b.customerName, b.email, b.contactNo, b.bookID, "
                    + "c.BOOKTITLE, b.dateBorrowed, b.dueDate "
                    + "FROM Borrowed b JOIN CATALOG c ON b.bookID = c.BOOKID";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    int customerID = rs.getInt("customerID");
                    String customerName = rs.getString("customerName");
                    String bookID = rs.getString("bookID");
                    String bookTitle = rs.getString("BOOKTITLE");
                    java.sql.Date borrowedDate = rs.getDate("dateBorrowed");
                    java.sql.Date dueDate = rs.getDate("dueDate");

                    // Calculate days overdue
                    long diffInMillis = currentDate.getTime() - dueDate.getTime();
                    int daysOverdue = (int) (diffInMillis / (1000 * 60 * 60 * 24));

                    // Only add overdue books to the table
                    if (daysOverdue > 0) {
                        model.addRow(new Object[]{
                            false, // Checkbox initially unchecked
                            customerID,
                            customerName,
                            bookID,
                            bookTitle,
                            borrowedDate,
                            dueDate,
                            daysOverdue
                        });
                    }
                }

                // If no overdue books found
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No overdue books found.");
                    return;
                }

                JTable selectionTable = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(selectionTable);
                scrollPane.setPreferredSize(new Dimension(700, 300));

                // Add a "Select All" checkbox
                JCheckBox selectAll = new JCheckBox("Select All");
                selectAll.addActionListener(e -> {
                    boolean selected = selectAll.isSelected();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        model.setValueAt(selected, i, 0);
                    }
                });

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(selectAll, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(this, panel, "Select Books to Mark as Overdue",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    List<Integer> selectedRows = new ArrayList<>();

                    for (int i = 0; i < model.getRowCount(); i++) {
                        Boolean checked = (Boolean) model.getValueAt(i, 0);
                        if (checked) {
                            selectedRows.add(i);
                        }
                    }

                    if (selectedRows.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No books selected.");
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Mark " + selectedRows.size() + " books as overdue?",
                            "Confirm Mark as Overdue", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        conn.setAutoCommit(false);
                        int successCount = 0;

                        try {
                            for (int rowIndex : selectedRows) {
                                int customerID = (int) model.getValueAt(rowIndex, 1);
                                String bookID = (String) model.getValueAt(rowIndex, 3);
                                int daysOverdue = (int) model.getValueAt(rowIndex, 7);
                                double surcharge = daysOverdue * 10.0; // 10 pesos per day

                                // Get additional customer details
                                String getCustomerQuery = "SELECT email, contactNo FROM Borrowed WHERE customerID = ? AND bookID = ?";
                                try (PreparedStatement custStmt = conn.prepareStatement(getCustomerQuery)) {
                                    custStmt.setInt(1, customerID);
                                    custStmt.setString(2, bookID);
                                    ResultSet custRs = custStmt.executeQuery();

                                    if (custRs.next()) {
                                        String email = custRs.getString("email");
                                        String contactNo = custRs.getString("contactNo");
                                        String customerName = (String) model.getValueAt(rowIndex, 2);
                                        java.sql.Date borrowedDate = (java.sql.Date) model.getValueAt(rowIndex, 5);
                                        java.sql.Date dueDate = (java.sql.Date) model.getValueAt(rowIndex, 6);

                                        // Insert into Overdue
                                        try (PreparedStatement insertStmt = conn.prepareStatement(
                                                "INSERT INTO Overdue (customerID, customerName, email, contactNo, bookID, dateBorrowed, dueDate, surcharge) "
                                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                                            insertStmt.setInt(1, customerID);
                                            insertStmt.setString(2, customerName);
                                            insertStmt.setString(3, email);
                                            insertStmt.setString(4, contactNo);
                                            insertStmt.setString(5, bookID);
                                            insertStmt.setDate(6, borrowedDate);
                                            insertStmt.setDate(7, dueDate);
                                            insertStmt.setDouble(8, surcharge);

                                            insertStmt.executeUpdate();
                                        }

                                        // Delete from Borrowed
                                        try (PreparedStatement deleteStmt = conn.prepareStatement(
                                                "DELETE FROM Borrowed WHERE customerID = ? AND bookID = ?")) {

                                            deleteStmt.setInt(1, customerID);
                                            deleteStmt.setString(2, bookID);
                                            deleteStmt.executeUpdate();

                                            successCount++;
                                        }
                                    }
                                }
                            }

                            conn.commit();
                            JOptionPane.showMessageDialog(this, successCount + " books marked as overdue successfully!");
                            loadOverdue(); // Refresh the table

                        } catch (SQLException ex) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Error marking books as overdue: " + ex.getMessage());
                        } finally {
                            conn.setAutoCommit(true);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void loadBooks() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT C.BOOKID, C.BOOKTITLE, C.AUTHOR, G.GENRENAME, C.BOOKYR, S.STOCK "
                + "FROM CATALOG C "
                + "LEFT JOIN GENRES G ON C.GENRE = G.GENREID "
                + "LEFT JOIN STOCK S ON C.BOOKID = S.BOOKID")) {

            DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BOOKID"),
                    rs.getString("BOOKTITLE"),
                    rs.getString("AUTHOR"),
                    rs.getString("GENRENAME"),
                    rs.getInt("BOOKYR"),
                    rs.getInt("STOCK")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Books Error: " + e.getMessage());
        }
    }

    private void loadUsers() {
        loadTableData("SELECT USERID, USERNAME, USERTYPE FROM ACCOUNT", tbl_users);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement()) {

            // Count total users (non-admin)
            ResultSet rsUsers = stmt.executeQuery("SELECT COUNT(*) FROM ACCOUNT WHERE USERTYPE = 'user'");
            if (rsUsers.next()) {
                lbl_userCount.setText("Total Users: " + rsUsers.getInt(1));
            }

            // Count total admins
            ResultSet rsAdmins = stmt.executeQuery("SELECT COUNT(*) FROM ACCOUNT WHERE USERTYPE = 'admin'");
            if (rsAdmins.next()) {
                lbl_adminCount.setText("Total Admins: " + rsAdmins.getInt(1));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Count Error: " + e.getMessage());
        }
    }

    private void loadOverdue() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement()) {

            DefaultTableModel model = (DefaultTableModel) tbl_overdue.getModel();
            model.setRowCount(0);

            // Query for borrowed books
            ResultSet rsBorrowed = stmt.executeQuery(
                    "SELECT b.customerID, b.customerName, b.bookID, c.BOOKTITLE, b.dateBorrowed, b.dueDate, 0 as surcharge "
                    + "FROM Borrowed b "
                    + "JOIN CATALOG c ON b.bookID = c.BOOKID");

            // Add borrowed books
            while (rsBorrowed.next()) {
                model.addRow(new Object[]{
                    rsBorrowed.getInt("customerID"),
                    rsBorrowed.getString("customerName"),
                    rsBorrowed.getString("bookID"),
                    rsBorrowed.getString("BOOKTITLE"),
                    rsBorrowed.getDate("dateBorrowed"),
                    rsBorrowed.getDate("dueDate"),
                    rsBorrowed.getDouble("surcharge")
                });
            }

            // Query for overdue books
            ResultSet rsOverdue = stmt.executeQuery(
                    "SELECT o.customerID, o.customerName, o.bookID, c.BOOKTITLE, o.dateBorrowed, o.dueDate, o.surcharge "
                    + "FROM Overdue o "
                    + "JOIN CATALOG c ON o.bookID = c.BOOKID");

            // Add overdue books
            while (rsOverdue.next()) {
                model.addRow(new Object[]{
                    rsOverdue.getInt("customerID"),
                    rsOverdue.getString("customerName"),
                    rsOverdue.getString("bookID"),
                    rsOverdue.getString("BOOKTITLE"),
                    rsOverdue.getDate("dateBorrowed"),
                    rsOverdue.getDate("dueDate"),
                    rsOverdue.getDouble("surcharge")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Borrowed/Overdue Error: " + e.getMessage());
        }
    }

    private void loadTableData(String query, JTable table) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Error: " + e.getMessage());
        }
    }

    private void addBookDialog() {
        JTextField bookID = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField genre = new JTextField();
        JTextField year = new JTextField();
        JTextField stock = new JTextField();

        Object[] fields = {
            "Book ID:", bookID,
            "Title:", title,
            "Author:", author,
            "Genre:", genre,
            "Year:", year,
            "Initial Stock:", stock
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            addBook(bookID.getText(), title.getText(), author.getText(), genre.getText(),
                    year.getText(), stock.getText());
        }
    }

    private boolean addBook(String bookID, String title, String author, String genreName, String year, String stock) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
            conn.setAutoCommit(false);

            try {
                // Step 1: Check if the BOOKID already exists in CATALOG or ARCHIVED_BOOKS
                try (PreparedStatement checkIdStmt = conn.prepareStatement(
                        "SELECT 1 FROM catalog WHERE BOOKID = ? UNION SELECT 1 FROM archived_books WHERE BOOKID = ?")) {
                    checkIdStmt.setString(1, bookID);
                    checkIdStmt.setString(2, bookID);
                    try (ResultSet rs = checkIdStmt.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("Book ID already exists in the library or archive.");
                        }
                    }
                }

                // Step 2: Check if the book title already exists in catalog
                try (PreparedStatement checkBookStmt = conn.prepareStatement(
                        "SELECT 1 FROM catalog WHERE BOOKTITLE = ?")) {
                    checkBookStmt.setString(1, title);
                    try (ResultSet rs = checkBookStmt.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("Book title already exists. Duplicate not allowed.");
                        }
                    }
                }

                // Step 3: Get or insert genre
                int genreId = getOrInsertGenre(conn, genreName);

                // Step 4: Insert into CATALOG
                try (PreparedStatement stmt1 = conn.prepareStatement(
                        "INSERT INTO catalog (BOOKID, BOOKTITLE, AUTHOR, GENRE, BOOKYR) VALUES (?, ?, ?, ?, ?)")) {
                    stmt1.setString(1, bookID);
                    stmt1.setString(2, title);
                    stmt1.setString(3, author);
                    stmt1.setInt(4, genreId);
                    stmt1.setInt(5, Integer.parseInt(year));
                    stmt1.executeUpdate();
                }

                // Step 5: Insert into STOCK
                try (PreparedStatement stmt2 = conn.prepareStatement(
                        "INSERT INTO stock (BOOKID, BOOKTITLE, STOCK) VALUES (?, ?, ?)")) {
                    stmt2.setString(1, bookID);
                    stmt2.setString(2, title);
                    stmt2.setInt(3, Integer.parseInt(stock));
                    stmt2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Insert Error: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            return false;
        }
    }

    private int getOrInsertGenre(Connection conn, String genreName) throws SQLException {
        // Check if genre exists
        try (PreparedStatement checkStmt = conn.prepareStatement("SELECT GENREID FROM genres WHERE GENRENAME = ?")) {
            checkStmt.setString(1, genreName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("GENREID");
                }
            }
        }

        // Insert new genre
        try (PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO genres (GENRENAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, genreName);
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating genre failed, no ID obtained.");
                }
            }
        }
    }

    private void addMultipleBooksDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 300));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book ID", "Title", "Author", "Genre", "Year", "Stock"}, 0);

        for (int i = 0; i < 5; i++) {
            model.addRow(new Object[]{"", "", "", "", "", ""});
        }

        JTable inputTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(inputTable);

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> model.addRow(new Object[]{"", "", "", "", "", ""}));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addRowButton, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Multiple Books",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int successCount = 0;
            int totalCount = 0;
            List<String> skippedBooks = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
                conn.setAutoCommit(false);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String bookID = (String) model.getValueAt(i, 0);
                    String title = (String) model.getValueAt(i, 1);
                    String author = (String) model.getValueAt(i, 2);
                    String genreName = (String) model.getValueAt(i, 3);
                    String year = (String) model.getValueAt(i, 4);
                    String stock = (String) model.getValueAt(i, 5);

                    if (bookID == null || bookID.trim().isEmpty()
                            || title == null || title.trim().isEmpty()) {
                        continue;
                    }

                    totalCount++;

                    try {
                        // Check for existing BOOKID or BOOKTITLE
                        try (PreparedStatement checkStmt = conn.prepareStatement(
                                "SELECT 'catalog' AS source FROM catalog WHERE BOOKID = ? OR BOOKTITLE = ? "
                                + "UNION SELECT 'archived_books' FROM archived_books WHERE BOOKID = ?")) {
                            checkStmt.setString(1, bookID);
                            checkStmt.setString(2, title);
                            checkStmt.setString(3, bookID);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    skippedBooks.add("[" + bookID + "] \"" + title + "\" already exists in " + rs.getString("source"));
                                    continue;
                                }
                            }
                        }

                        int genreId = getOrInsertGenre(conn, genreName);

                        try (PreparedStatement stmt1 = conn.prepareStatement(
                                "INSERT INTO CATALOG (BOOKID, BOOKTITLE, AUTHOR, GENRE, BOOKYR) VALUES (?, ?, ?, ?, ?)"); PreparedStatement stmt2 = conn.prepareStatement(
                                        "INSERT INTO STOCK (BOOKID, BOOKTITLE, STOCK) VALUES (?, ?, ?)")) {

                            stmt1.setString(1, bookID);
                            stmt1.setString(2, title);
                            stmt1.setString(3, author != null ? author : "");
                            stmt1.setInt(4, genreId);
                            stmt1.setInt(5, year != null && !year.trim().isEmpty() ? Integer.parseInt(year) : 0);
                            stmt1.executeUpdate();

                            stmt2.setString(1, bookID);
                            stmt2.setString(2, title);
                            stmt2.setInt(3, stock != null && !stock.trim().isEmpty() ? Integer.parseInt(stock) : 0);
                            stmt2.executeUpdate();

                            successCount++;
                        }

                    } catch (SQLException e) {
                        skippedBooks.add("Error adding [" + bookID + "]: " + e.getMessage());
                    }
                }

                try {
                    conn.commit();
                    loadBooks();

                    StringBuilder msg = new StringBuilder("Added " + successCount + " of " + totalCount + " books successfully.");
                    if (!skippedBooks.isEmpty()) {
                        msg.append("\n\nSkipped entries:\n");
                        for (String skipped : skippedBooks) {
                            msg.append(skipped).append("\n");
                        }

                        JTextArea textArea = new JTextArea(msg.toString());
                        textArea.setEditable(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        JScrollPane scrollPaneResult = new JScrollPane(textArea);
                        scrollPaneResult.setPreferredSize(new Dimension(500, 300));

                        JOptionPane.showMessageDialog(this, scrollPaneResult, "Add Book Result", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, msg.toString(), "Add Book Result", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (SQLException e) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction Error: " + e.getMessage());
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }

    private void deleteMultipleBooks() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"", "ID", "Title", "Author"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT BOOKID, BOOKTITLE, AUTHOR FROM CATALOG")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    false,
                    rs.getString("BOOKID"),
                    rs.getString("BOOKTITLE"),
                    rs.getString("AUTHOR")
                });
            }

            JTable selectionTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(selectionTable);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JCheckBox selectAll = new JCheckBox("Select All");
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                for (int i = 0; i < model.getRowCount(); i++) {
                    model.setValueAt(selected, i, 0);
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(selectAll, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, panel, "Select Books to Delete",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                List<String> bookIdsToDelete = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Boolean checked = (Boolean) model.getValueAt(i, 0);
                    if (checked != null && checked) {
                        bookIdsToDelete.add((String) model.getValueAt(i, 1));
                    }
                }

                if (bookIdsToDelete.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No books selected for deletion.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete " + bookIdsToDelete.size() + " selected books?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        conn.setAutoCommit(false);
                        int deletedCount = 0;

                        try (
                                PreparedStatement archive = conn.prepareStatement(
                                        "INSERT INTO archived_books (BOOKID, BOOKTITLE, AUTHOR, GENRENAME, BOOKYR, DELETED_AT) "
                                        + "SELECT c.BOOKID, c.BOOKTITLE, c.AUTHOR, g.GENRENAME, c.BOOKYR, CURRENT_TIMESTAMP "
                                        + "FROM catalog c JOIN genres g ON c.GENRE = g.GENREID WHERE c.BOOKID = ?"); PreparedStatement deleteStock = conn.prepareStatement("DELETE FROM STOCK WHERE BOOKID = ?"); PreparedStatement deleteCatalog = conn.prepareStatement("DELETE FROM CATALOG WHERE BOOKID = ?")) {
                            for (String bookId : bookIdsToDelete) {
                                archive.setString(1, bookId);
                                archive.executeUpdate();

                                deleteStock.setString(1, bookId);
                                deleteStock.executeUpdate();

                                deleteCatalog.setString(1, bookId);
                                deleteCatalog.executeUpdate();

                                deletedCount++;
                            }
                        }

                        conn.commit();
                        loadBooks();
                        JOptionPane.showMessageDialog(this, deletedCount + " books deleted successfully.");
                    } catch (SQLException e) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Error deleting books: " + e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void addUserDialog() {
        JTextField userID = new JTextField();
        JTextField username = new JTextField();

        Object[] fields = {
            "User ID (11 digits):", userID,
            "Username:", username
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String idText = userID.getText().trim();
            String usernameText = username.getText().trim();

            if (!idText.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "User ID must be exactly 11 digits.");
                return;
            }

            if (usernameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ACCOUNT (USERID, USERNAME, PASSWORD, USERTYPE) VALUES (?, ?, ?, 'user')"
            )) {

                stmt.setString(1, idText);
                stmt.setString(2, usernameText);
                stmt.setString(3, "user123"); // Default password

                stmt.executeUpdate();
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Insert Error: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        String input = JOptionPane.showInputDialog(this, "Enter User ID or Username to delete:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        boolean isNumeric = input.matches("\\d+");
        String query = isNumeric
                ? "SELECT USERID, USERNAME, USERTYPE FROM ACCOUNT WHERE USERID = ?"
                : "SELECT USERID, USERNAME, USERTYPE FROM ACCOUNT WHERE USERNAME LIKE ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement stmt = conn.prepareStatement(query)) {

            if (isNumeric) {
                stmt.setLong(1, Long.parseLong(input));
            } else {
                stmt.setString(1, "%" + input + "%");
            }

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No matching user found.");
                return;
            }

            String userID = rs.getString("USERID");
            String username = rs.getString("USERNAME");
            String userType = rs.getString("USERTYPE");

            if ("admin".equalsIgnoreCase(userType)) {
                JOptionPane.showMessageDialog(this, "Cannot delete an admin account.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete user:\nID: " + userID + "\nUsername: " + username,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM ACCOUNT WHERE USERID = ?")) {
                    deleteStmt.setString(1, userID);
                    deleteStmt.executeUpdate();
                    loadUsers();
                    JOptionPane.showMessageDialog(this, "User deleted.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete User Error: " + e.getMessage());
        }
    }

    private void addMultipleUsersDialog() {
        // Create a panel with a table to input multiple users
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 300));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"User ID", "Username"}, 0);

        // Add 5 empty rows to start with
        for (int i = 0; i < 5; i++) {
            model.addRow(new Object[]{"", ""});
        }

        JTable inputTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(inputTable);

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> model.addRow(new Object[]{"", ""}));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addRowButton, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Multiple Users",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int successCount = 0;
            int totalCount = 0;

            // Start a transaction for all additions
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252")) {
                conn.setAutoCommit(false);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String userID = (String) model.getValueAt(i, 0);
                    String username = (String) model.getValueAt(i, 1);

                    // Skip empty rows
                    if (userID == null || userID.trim().isEmpty()
                            || username == null || username.trim().isEmpty()) {
                        continue;
                    }

                    totalCount++;

                    try (PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO ACCOUNT (USERID, USERNAME, USERTYPE) VALUES (?, ?, 'user')")) {
                        stmt.setString(1, userID);
                        stmt.setString(2, username);
                        stmt.executeUpdate();
                        successCount++;
                    } catch (SQLException e) {
                        System.out.println("Error adding user: " + userID + " - " + e.getMessage());
                    }
                }

                try {
                    conn.commit();
                    loadUsers();
                    JOptionPane.showMessageDialog(this,
                            "Added " + successCount + " of " + totalCount + " users successfully.");
                } catch (SQLException e) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction Error: " + e.getMessage());
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }

    private void deleteMultipleUsers() {
        // Create a multi-select dialog with all non-admin users
        DefaultTableModel model = new DefaultTableModel(new String[]{"", "ID", "Username", "Type"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT USERID, USERNAME, USERTYPE FROM ACCOUNT")) {

            while (rs.next()) {
                String userType = rs.getString("USERTYPE");
                // Don't allow selection of admin accounts
                boolean isAdmin = "admin".equalsIgnoreCase(userType);

                model.addRow(new Object[]{
                    !isAdmin, // Checkbox only enabled for non-admin accounts
                    rs.getString("USERID"),
                    rs.getString("USERNAME"),
                    userType
                });
            }

            JTable selectionTable = new JTable(model) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Disable checkbox for admin users
                    if (column == 0) {
                        String userType = (String) getValueAt(row, 3);
                        return !"admin".equalsIgnoreCase(userType);
                    }
                    return false;
                }
            };

            JScrollPane scrollPane = new JScrollPane(selectionTable);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            // Add a "Select All Non-Admin" checkbox
            JCheckBox selectAll = new JCheckBox("Select All Non-Admin Users");
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String userType = (String) model.getValueAt(i, 3);
                    if (!"admin".equalsIgnoreCase(userType)) {
                        model.setValueAt(selected, i, 0);
                    }
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(selectAll, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, panel, "Select Users to Delete",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                List<String> userIdsToDelete = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Boolean checked = (Boolean) model.getValueAt(i, 0);
                    String userType = (String) model.getValueAt(i, 3);

                    // Double-check to make sure we're not deleting admin accounts
                    if (checked && !"admin".equalsIgnoreCase(userType)) {
                        userIdsToDelete.add((String) model.getValueAt(i, 1));
                    }
                }

                if (userIdsToDelete.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No users selected for deletion.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete " + userIdsToDelete.size() + " selected users?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        conn.setAutoCommit(false);
                        int deletedCount = 0;

                        for (String userId : userIdsToDelete) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM ACCOUNT WHERE USERID = ?")) {
                                deleteStmt.setString(1, userId);
                                int result2 = deleteStmt.executeUpdate();

                                if (result2 > 0) {
                                    deletedCount++;
                                }
                            }
                        }

                        conn.commit();
                        loadUsers();
                        JOptionPane.showMessageDialog(this, deletedCount + " users deleted successfully.");
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            // Ignore rollback error
                        }
                        JOptionPane.showMessageDialog(this, "Error deleting users: " + e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void deleteSelected(JTable table, String tableName, String idCol) {
        String input = JOptionPane.showInputDialog(this, "Enter Book ID or Title to delete:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); PreparedStatement stmt = conn.prepareStatement(
                "SELECT c.BOOKID, c.BOOKTITLE, c.AUTHOR, g.GENRENAME, c.BOOKYR "
                + "FROM CATALOG c JOIN GENRES g ON c.GENRE = g.GENREID "
                + "WHERE c.BOOKID = ? OR c.BOOKTITLE LIKE ?")) {

            stmt.setString(1, input);
            stmt.setString(2, "%" + input + "%");
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No matching book found.");
                return;
            }

            String bookID = rs.getString("BOOKID");
            String bookTitle = rs.getString("BOOKTITLE");
            String author = rs.getString("AUTHOR");
            String genreName = rs.getString("GENRENAME");
            int bookYr = rs.getInt("BOOKYR");

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete book:\nID: " + bookID + "\nTitle: " + bookTitle,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (
                        PreparedStatement deleteStock = conn.prepareStatement("DELETE FROM STOCK WHERE BOOKID = ?"); PreparedStatement deleteCatalog = conn.prepareStatement("DELETE FROM CATALOG WHERE BOOKID = ?"); PreparedStatement archiveStmt = conn.prepareStatement(
                        "INSERT INTO ARCHIVED_BOOKS (BOOKID, BOOKTITLE, AUTHOR, GENRENAME, BOOKYR, DELETED_AT) "
                        + "VALUES (?, ?, ?, ?, ?, NOW())")) {

                    // Archive first
                    archiveStmt.setString(1, bookID);
                    archiveStmt.setString(2, bookTitle);
                    archiveStmt.setString(3, author);
                    archiveStmt.setString(4, genreName);
                    archiveStmt.setInt(5, bookYr);
                    archiveStmt.executeUpdate();

                    // Delete STOCK
                    deleteStock.setString(1, bookID);
                    deleteStock.executeUpdate();

                    // Delete CATALOG
                    deleteCatalog.setString(1, bookID);
                    deleteCatalog.executeUpdate();

                    loadBooks();
                    JOptionPane.showMessageDialog(this, "Book and stock deleted, archived in history.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search/Delete Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Library_Admin().setVisible(true));
    }
}
