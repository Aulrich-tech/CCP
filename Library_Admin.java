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

        // BOOKS TAB - Updated for new schema
        // Corrected table model to match loadBooks() query (7 columns)
        tbl_books = new JTable(new DefaultTableModel(
                new String[]{"Book Code", "Title", "Author", "Year", "Total Qty", "Available"}, 0));

// Scroll pane for the book table
        JScrollPane scrollPane = new JScrollPane(tbl_books);

// Buttons for managing books
        btn_addBook = new JButton("Add Book");
        btn_deleteBook = new JButton("Delete Book");
        btn_addMultipleBooks = new JButton("Add Multiple Books");
        btn_deleteMultipleBooks = new JButton("Delete Multiple Books");

// Action listeners
        btn_addBook.addActionListener(e -> addBookDialog());
        btn_deleteBook.addActionListener(e -> deleteSelected(tbl_books, "Books", "BookCode")); // Delete by BookCode from Books table
        btn_addMultipleBooks.addActionListener(e -> addMultipleBooksDialog());
        btn_deleteMultipleBooks.addActionListener(e -> deleteMultipleBooks());

// Panel for the buttons
        JPanel bookBtnPanel = new JPanel();
        bookBtnPanel.add(btn_addBook);
        bookBtnPanel.add(btn_deleteBook);
        bookBtnPanel.add(btn_addMultipleBooks);
        bookBtnPanel.add(btn_deleteMultipleBooks);

// Main panel for displaying books and buttons
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.add(scrollPane, BorderLayout.CENTER);
        bookPanel.add(bookBtnPanel, BorderLayout.SOUTH);

        // USERS TAB - Updated for new schema
        lbl_userCount = new JLabel("Total Users: 0");
        lbl_adminCount = new JLabel("Total Admins: 0");

        tbl_users = new JTable(new DefaultTableModel(
                new String[]{"UserID", "Username", "UserType", "Department", "Email"}, 0)); // Added more columns
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

        // BORROWING/OVERDUE TAB - Updated for new schema
        tbl_overdue = new JTable(new DefaultTableModel(
                new String[]{"Borrowing ID", "Book Code", "Book Title", "Borrower ID", "Borrower Name",
                    "Date Borrowed", "Due Date", "Days Overdue", "Surcharge", "Status"}, 0));
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

        // ARCHIVED BOOKS TAB - Updated for new schema
        tbl_archived = new JTable(new DefaultTableModel(
                new String[]{"Archive ID", "Original Book Code", "Title", "Author",
                    "Year", "Archived Quantity", "Archive Reason", "Admin Name", "Archive Date"}, 0));
        JPanel archivePanel = new JPanel(new BorderLayout());
        archivePanel.add(new JScrollPane(tbl_archived), BorderLayout.CENTER);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("CATALOG", bookPanel);
        tabbedPane.addTab("ACCOUNT", userPanel);
        tabbedPane.addTab("BORROWED", overduePanel);
        tabbedPane.addTab("HISTORY", archivePanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        setSize(800, 500);

        loadArchivedBooks();
    }

    private void addBorrowingDialog() {
        // Create fields for the dialog
        JTextField borrowerIDField = new JTextField(15);
        JTextField borrowerNameField = new JTextField(20);
        JTextField contactNoField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        JTextField bookCodeField = new JTextField(10);
        JTextField bookTitleField = new JTextField(25);
        JComboBox<String> borrowerTypeCombo = new JComboBox<>(new String[]{"Student", "Faculty"});

        // Create date fields
        JSpinner borrowedDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner dueDateSpinner = new JSpinner(new SpinnerDateModel());

        // Set current date
        borrowedDateSpinner.setValue(new java.util.Date());

        // Set default due date (7 days from today for Faculty, 3 days for Student)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        dueDateSpinner.setValue(calendar.getTime());

        // Format date spinners
        JSpinner.DateEditor borrowedEditor = new JSpinner.DateEditor(borrowedDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor dueEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd");
        borrowedDateSpinner.setEditor(borrowedEditor);
        dueDateSpinner.setEditor(dueEditor);

        // Update due date based on borrower type
        borrowerTypeCombo.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) borrowedDateSpinner.getValue());
            if ("Faculty".equals(borrowerTypeCombo.getSelectedItem())) {
                cal.add(Calendar.DAY_OF_MONTH, 14); // 14 days for faculty
            } else {
                cal.add(Calendar.DAY_OF_MONTH, 7);  // 7 days for students
            }
            dueDateSpinner.setValue(cal.getTime());
        });

        // Create a book search button
        JButton searchBookBtn = new JButton("Search");
        searchBookBtn.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter Book Code or Title:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT BookCode, BookTitle, Author, AvailableQuantity FROM Books WHERE BookCode = ? OR BookTitle LIKE ?")) {

                    stmt.setString(1, searchTerm);
                    stmt.setString(2, "%" + searchTerm + "%");
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        bookCodeField.setText(rs.getString("BookCode"));
                        bookTitleField.setText(rs.getString("BookTitle"));

                        int available = rs.getInt("AvailableQuantity");
                        if (available <= 0) {
                            JOptionPane.showMessageDialog(this,
                                    "Book found but not available (Quantity: " + available + ")");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching book found.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching for book: " + ex.getMessage());
                }
            }
        });

        // Create borrower search button
        JButton searchBorrowerBtn = new JButton("Search");
        searchBorrowerBtn.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter User ID or Username:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT UserID, Username, UserType, Department, ContactNumber, Email FROM Users WHERE UserID = ? OR Username LIKE ?")) {

                    // Try to parse as UserID first
                    try {
                        long userID = Long.parseLong(searchTerm);
                        stmt.setLong(1, userID);
                        stmt.setString(2, "%" + searchTerm + "%");
                    } catch (NumberFormatException ex) {
                        // If not a number, search by username only
                        stmt.setString(1, "0"); // Invalid UserID to ensure no match
                        stmt.setString(2, "%" + searchTerm + "%");
                    }

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        borrowerIDField.setText(String.valueOf(rs.getLong("UserID")));
                        borrowerNameField.setText(rs.getString("Username"));
                        borrowerTypeCombo.setSelectedItem(rs.getString("UserType"));
                        contactNoField.setText(rs.getString("ContactNumber"));
                        emailField.setText(rs.getString("Email"));
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching user found.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching for user: " + ex.getMessage());
                }
            }
        });

        // Create panel for book search
        JPanel bookSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookSearchPanel.add(bookCodeField);
        bookSearchPanel.add(searchBookBtn);

        // Create panel for borrower search
        JPanel borrowerSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        borrowerSearchPanel.add(borrowerIDField);
        borrowerSearchPanel.add(searchBorrowerBtn);

        // Create the panel with all fields
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Borrower ID:"));
        panel.add(borrowerSearchPanel);
        panel.add(new JLabel("Borrower Name:"));
        panel.add(borrowerNameField);
        panel.add(new JLabel("Borrower Type:"));
        panel.add(borrowerTypeCombo);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactNoField);
        panel.add(new JLabel("Book Code:"));
        panel.add(bookSearchPanel);
        panel.add(new JLabel("Book Title:"));
        panel.add(bookTitleField);
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
                long borrowerID = Long.parseLong(borrowerIDField.getText().trim());
                String borrowerName = borrowerNameField.getText().trim();
                String borrowerType = (String) borrowerTypeCombo.getSelectedItem();
                String email = emailField.getText().trim();
                String contactNo = contactNoField.getText().trim();
                String bookCode = bookCodeField.getText().trim();
                String bookTitle = bookTitleField.getText().trim();

                // Convert JSpinner date to java.sql.Date
                java.sql.Date borrowedDate = new java.sql.Date(((java.util.Date) borrowedDateSpinner.getValue()).getTime());
                java.sql.Date dueDate = new java.sql.Date(((java.util.Date) dueDateSpinner.getValue()).getTime());

                // Validate inputs
                if (borrowerName.isEmpty() || bookCode.isEmpty() || bookTitle.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Borrower name, Book Code, and Book Title are required.");
                    return;
                }

                // Validate borrower ID length (11 digits)
                if (borrowerID < 10000000000L || borrowerID > 99999999999L) {
                    JOptionPane.showMessageDialog(this, "Borrower ID must be exactly 11 digits.");
                    return;
                }

                Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised");
                conn.setAutoCommit(false); // Start transaction

                try {
                    // Check if book is available
                    PreparedStatement checkBookStmt = conn.prepareStatement(
                            "SELECT AvailableQuantity FROM Books WHERE BookCode = ?");
                    checkBookStmt.setString(1, bookCode);
                    ResultSet bookRs = checkBookStmt.executeQuery();

                    if (bookRs.next()) {
                        int availableQty = bookRs.getInt("AvailableQuantity");
                        if (availableQty <= 0) {
                            JOptionPane.showMessageDialog(this, "This book is not available (out of stock).");
                            conn.rollback();
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Book Code not found.");
                        conn.rollback();
                        return;
                    }

                    // Check if user exists
                    PreparedStatement checkUserStmt = conn.prepareStatement(
                            "SELECT UserID FROM Users WHERE UserID = ?");
                    checkUserStmt.setLong(1, borrowerID);
                    ResultSet userRs = checkUserStmt.executeQuery();

                    if (!userRs.next()) {
                        JOptionPane.showMessageDialog(this, "User ID not found. Please register the user first.");
                        conn.rollback();
                        return;
                    }

                    // Get admin info (you'll need to pass this or get current admin)
                    long adminID = SessionManager.getCurrentAdminId(); // Replace with actual admin ID
                    String adminName = SessionManager.getCurrentAdminName(); // Replace with actual admin name

                    // Insert into Borrowings table
                    PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO Borrowings (BookCode, BookTitle, BorrowerID, BorrowerName, BorrowerType, "
                            + "DateBorrowed, DueDate, AdminID, AdminName, IsReturned) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'N')");

                    insertStmt.setString(1, bookCode);
                    insertStmt.setString(2, bookTitle);
                    insertStmt.setLong(3, borrowerID);
                    insertStmt.setString(4, borrowerName);
                    insertStmt.setString(5, borrowerType);
                    insertStmt.setDate(6, borrowedDate);
                    insertStmt.setDate(7, dueDate);
                    insertStmt.setLong(8, adminID);
                    insertStmt.setString(9, adminName);

                    insertStmt.executeUpdate();

                    // Update available quantity
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE Books SET AvailableQuantity = AvailableQuantity - 1 WHERE BookCode = ?");
                    updateStmt.setString(1, bookCode);
                    updateStmt.executeUpdate();

                    conn.commit(); // Commit transaction
                    JOptionPane.showMessageDialog(this, "Borrowing record added successfully!");
                    loadOverdue(); // Refresh the table (you may need to adjust this method name)

                } catch (SQLException ex) {
                    conn.rollback(); // Rollback on error
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                    conn.close();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Borrower ID. Please enter a valid 11-digit number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }

    private void returnBookDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Borrower ID or Book Code:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        input = input.trim();

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {

            String borrowQuery = "SELECT * FROM Borrowings WHERE (BorrowerID = ? OR LOWER(BookCode) = LOWER(?)) AND TRIM(IsReturned) = 'N'";

            try (PreparedStatement stmt = conn.prepareStatement(borrowQuery)) {
                Long borrowerID = null;
                try {
                    borrowerID = Long.parseLong(input);
                } catch (NumberFormatException ignored) {
                }

                stmt.setLong(1, borrowerID != null ? borrowerID : -1);
                stmt.setString(2, input);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int borrowingID = rs.getInt("BorrowingID");
                    String bookCode = rs.getString("BookCode");
                    String bookTitle = rs.getString("BookTitle");
                    long actualBorrowerID = rs.getLong("BorrowerID");
                    String borrowerName = rs.getString("BorrowerName");
                    Date dateBorrowed = rs.getDate("DateBorrowed");
                    Date dueDate = rs.getDate("DueDate");
                    long adminID = rs.getLong("AdminID");
                    String adminName = rs.getString("AdminName");

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Return book for:\nBorrower ID: " + actualBorrowerID
                            + "\nName: " + borrowerName
                            + "\nBook Code: " + bookCode
                            + "\nBook Title: " + bookTitle,
                            "Confirm Return", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        Date currentDate = new Date(System.currentTimeMillis());
                        long daysOverdue = Math.max((currentDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24), 0);
                        double surcharge = daysOverdue * 10.00;

                        try {
                            conn.setAutoCommit(false);

                            // Mark as returned
                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE Borrowings SET IsReturned = 'Y' WHERE BorrowingID = ?")) {
                                updateStmt.setInt(1, borrowingID);
                                updateStmt.executeUpdate();
                            }

                            // Update book quantity
                            try (PreparedStatement bookUpdateStmt = conn.prepareStatement(
                                    "UPDATE Books SET AvailableQuantity = AvailableQuantity + 1 WHERE BookCode = ?")) {
                                bookUpdateStmt.setString(1, bookCode);
                                bookUpdateStmt.executeUpdate();
                            }

                            // Insert into ReturnedBooks
                            try (PreparedStatement insertReturnStmt = conn.prepareStatement(
                                    "INSERT INTO ReturnedBooks (BorrowingID, BookCode, BookTitle, BorrowerID, BorrowerName, "
                                    + "DateBorrowed, DueDate, DateReturned, DaysOverdue, SurchargeAmount, "
                                    + "IsSurchargePaid, AdminID, AdminName) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Y', ?, ?)")) {
                                insertReturnStmt.setInt(1, borrowingID);
                                insertReturnStmt.setString(2, bookCode);
                                insertReturnStmt.setString(3, bookTitle);
                                insertReturnStmt.setLong(4, actualBorrowerID);
                                insertReturnStmt.setString(5, borrowerName);
                                insertReturnStmt.setDate(6, dateBorrowed);
                                insertReturnStmt.setDate(7, dueDate);
                                insertReturnStmt.setDate(8, currentDate);
                                insertReturnStmt.setLong(9, daysOverdue);
                                insertReturnStmt.setDouble(10, surcharge);
                                insertReturnStmt.setLong(11, adminID);
                                insertReturnStmt.setString(12, adminName);
                                insertReturnStmt.executeUpdate();
                            }

                            conn.commit();
                            JOptionPane.showMessageDialog(this, "Book returned successfully!\nOverdue: "
                                    + daysOverdue + " days\nSurcharge: ₱" + String.format("%.2f", surcharge));
                            loadOverdue();

                        } catch (SQLException e) {
                            conn.rollback();
                            throw e;
                        } finally {
                            conn.setAutoCommit(true);
                        }
                        return;
                    }
                }

                // Check Overdues if not found in Borrowings
                String overdueQuery = "SELECT * FROM Overdues WHERE (BorrowerID = ? OR LOWER(BookCode) = LOWER(?)) AND TRIM(IsPaid) = 'N'";

                try (PreparedStatement overdueStmt = conn.prepareStatement(overdueQuery)) {
                    overdueStmt.setLong(1, borrowerID != null ? borrowerID : -1);
                    overdueStmt.setString(2, input);

                    ResultSet overdueRs = overdueStmt.executeQuery();

                    if (overdueRs.next()) {
                        int overdueID = overdueRs.getInt("OverdueID");
                        int borrowingID = overdueRs.getInt("BorrowingID");
                        String bookCode = overdueRs.getString("BookCode");
                        String bookTitle = overdueRs.getString("BookTitle");
                        long actualBorrowerID = overdueRs.getLong("BorrowerID");
                        String borrowerName = overdueRs.getString("BorrowerName");
                        int daysOverdue = overdueRs.getInt("DaysOverdue");
                        double surcharge = overdueRs.getDouble("SurchargeAmount");

                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Return overdue book:\nBorrower: " + borrowerName
                                + "\nBook: " + bookTitle
                                + "\nSurcharge: ₱" + String.format("%.2f", surcharge),
                                "Confirm Return & Payment", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                conn.setAutoCommit(false);

                                // Mark overdue as paid
                                try (PreparedStatement updateOverdueStmt = conn.prepareStatement(
                                        "UPDATE Overdues SET IsPaid = 'Y' WHERE OverdueID = ?")) {
                                    updateOverdueStmt.setInt(1, overdueID);
                                    updateOverdueStmt.executeUpdate();
                                }

                                // Update book availability
                                try (PreparedStatement bookUpdateStmt = conn.prepareStatement(
                                        "UPDATE Books SET AvailableQuantity = AvailableQuantity + 1 WHERE BookCode = ?")) {
                                    bookUpdateStmt.setString(1, bookCode);
                                    bookUpdateStmt.executeUpdate();
                                }

                                // Mark borrowing as returned
                                try (PreparedStatement updateBorrowingStmt = conn.prepareStatement(
                                        "UPDATE Borrowings SET IsReturned = 'Y' WHERE BorrowingID = ?")) {
                                    updateBorrowingStmt.setInt(1, borrowingID);
                                    updateBorrowingStmt.executeUpdate();
                                }

                                // Insert into ReturnedBooks
                                try (PreparedStatement insertReturnStmt = conn.prepareStatement(
                                        "INSERT INTO ReturnedBooks (BorrowingID, BookCode, BookTitle, BorrowerID, BorrowerName, "
                                        + "DateBorrowed, DueDate, DateReturned, DaysOverdue, SurchargeAmount, "
                                        + "IsSurchargePaid, AdminID, AdminName) "
                                        + "SELECT b.BorrowingID, b.BookCode, b.BookTitle, b.BorrowerID, b.BorrowerName, "
                                        + "b.DateBorrowed, b.DueDate, CURRENT_DATE, o.DaysOverdue, o.SurchargeAmount, 'Y', b.AdminID, b.AdminName "
                                        + "FROM Borrowings b JOIN Overdues o ON b.BorrowingID = o.BorrowingID WHERE o.OverdueID = ?")) {
                                    insertReturnStmt.setInt(1, overdueID);
                                    insertReturnStmt.executeUpdate();
                                }

                                conn.commit();
                                JOptionPane.showMessageDialog(this, "Overdue return and surcharge recorded.\nAmount Paid: ₱"
                                        + String.format("%.2f", surcharge));
                                loadOverdue();

                            } catch (SQLException e) {
                                conn.rollback();
                                throw e;
                            } finally {
                                conn.setAutoCommit(true);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No active borrowing or unpaid overdue found.");
                    }
                }

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void markAsOverdueDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Borrower ID or Book Code:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        input = input.trim();

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
            String query = "SELECT b.BorrowingID, b.BookCode, b.BookTitle, b.BorrowerID, b.BorrowerName, "
                    + "b.BorrowerType, b.DateBorrowed, b.DueDate, u.ContactNumber, u.Email "
                    + "FROM Borrowings b "
                    + "JOIN Users u ON b.BorrowerID = u.UserID "
                    + "WHERE TRIM(b.IsReturned) = 'N' AND "
                    + "(b.BorrowerID = ? OR LOWER(b.BookCode) = LOWER(?))";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try {
                    long borrowerID = Long.parseLong(input);
                    stmt.setLong(1, borrowerID);
                    stmt.setString(2, input); // In case input is also a valid book code
                } catch (NumberFormatException e) {
                    // Not a number, so we just use book code and give an invalid borrower ID
                    stmt.setLong(1, -1); // Ensure it won’t match any real BorrowerID
                    stmt.setString(2, input);
                }

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int borrowingID = rs.getInt("BorrowingID");
                    String bookCode = rs.getString("BookCode");
                    String bookTitle = rs.getString("BookTitle");
                    long borrowerID = rs.getLong("BorrowerID");
                    String borrowerName = rs.getString("BorrowerName");
                    String borrowerType = rs.getString("BorrowerType");
                    java.sql.Date borrowedDate = rs.getDate("DateBorrowed");
                    java.sql.Date dueDate = rs.getDate("DueDate");
                    String contactNo = rs.getString("ContactNumber");
                    String email = rs.getString("Email");

                    long currentTimeMillis = System.currentTimeMillis();
                    java.sql.Date currentDate = new java.sql.Date(currentTimeMillis);

                    long diffInMillis = currentDate.getTime() - dueDate.getTime();
                    int daysOverdue = (int) (diffInMillis / (1000 * 60 * 60 * 24));

                    if (daysOverdue <= 0) {
                        JOptionPane.showMessageDialog(this, "This book is not overdue yet.");
                        return;
                    }

                    double surcharge = daysOverdue * 10.0;

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Mark as overdue:\nBorrower ID: " + borrowerID
                            + "\nName: " + borrowerName
                            + "\nBook Code: " + bookCode
                            + "\nBook Title: " + bookTitle
                            + "\nDays Overdue: " + daysOverdue
                            + "\nSurcharge: ₱" + surcharge,
                            "Confirm Overdue", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        conn.setAutoCommit(false);

                        try (PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO Overdues (BorrowingID, BookCode, BookTitle, BorrowerID, BorrowerName, "
                                + "DateBorrowed, DueDate, DaysOverdue, SurchargeAmount, IsPaid) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'N')")) {

                            insertStmt.setInt(1, borrowingID);
                            insertStmt.setString(2, bookCode);
                            insertStmt.setString(3, bookTitle);
                            insertStmt.setLong(4, borrowerID);
                            insertStmt.setString(5, borrowerName);
                            insertStmt.setDate(6, borrowedDate);
                            insertStmt.setDate(7, dueDate);
                            insertStmt.setInt(8, daysOverdue);
                            insertStmt.setDouble(9, surcharge);

                            insertStmt.executeUpdate();
                            conn.commit();

                            JOptionPane.showMessageDialog(this, "Book marked as overdue with surcharge: ₱" + String.format("%.2f", surcharge));
                            loadOverdue();
                        } catch (SQLException ex) {
                            conn.rollback();
                            throw ex;
                        } finally {
                            conn.setAutoCommit(true);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No active borrowing record found for this ID or Book Code.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void markMultipleOverdueDialog() {
        // Define column headers including hidden columns
        String[] columnNames = {
            "", "Borrower ID", "Borrower Name", "Book Code", "Book Title",
            "Date Borrowed", "Due Date", "Days Overdue",
            "Borrowing ID", "Contact No", "Email"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            String query = """
            SELECT b.BorrowingID, b.BookCode, b.BookTitle, b.BorrowerID, b.BorrowerName,
                   b.BorrowerType, b.DateBorrowed, b.DueDate, u.ContactNumber, u.Email
            FROM Borrowings b
            JOIN Users u ON b.BorrowerID = u.UserID
            WHERE b.IsReturned = 'N'
              AND NOT EXISTS (
                  SELECT 1 FROM Overdues o WHERE o.BorrowingID = b.BorrowingID
              )
        """;

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    java.sql.Date dueDate = rs.getDate("DueDate");
                    long diff = currentDate.getTime() - dueDate.getTime();
                    int daysOverdue = (int) (diff / (1000 * 60 * 60 * 24));

                    if (daysOverdue > 0) {
                        model.addRow(new Object[]{
                            false,
                            rs.getLong("BorrowerID"),
                            rs.getString("BorrowerName"),
                            rs.getString("BookCode"),
                            rs.getString("BookTitle"),
                            rs.getDate("DateBorrowed"),
                            dueDate,
                            daysOverdue,
                            rs.getInt("BorrowingID"),
                            rs.getString("ContactNumber"),
                            rs.getString("Email")
                        });
                    }
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No overdue books found.");
                    return;
                }

                JTable selectionTable = new JTable(model);

                // Hide columns 8–10 (Borrowing ID, Contact No, Email)
                for (int i = 8; i <= 10; i++) {
                    selectionTable.getColumnModel().getColumn(i).setMinWidth(0);
                    selectionTable.getColumnModel().getColumn(i).setMaxWidth(0);
                    selectionTable.getColumnModel().getColumn(i).setWidth(0);
                }

                JScrollPane scrollPane = new JScrollPane(selectionTable);
                scrollPane.setPreferredSize(new Dimension(800, 350));

                JCheckBox selectAll = new JCheckBox("Select All");
                selectAll.addActionListener(e -> {
                    boolean selected = selectAll.isSelected();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        model.setValueAt(selected, i, 0);
                    }
                });

                JLabel summaryLabel = new JLabel("Found " + model.getRowCount() + " overdue books");
                summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.add(summaryLabel, BorderLayout.WEST);
                topPanel.add(selectAll, BorderLayout.EAST);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(topPanel, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(this, panel,
                        "Select Books to Mark as Overdue",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    List<Integer> selectedRows = new ArrayList<>();

                    for (int i = 0; i < model.getRowCount(); i++) {
                        if ((Boolean) model.getValueAt(i, 0)) {
                            selectedRows.add(i);
                        }
                    }

                    if (selectedRows.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No books selected.");
                        return;
                    }

                    // Compute total surcharge
                    double totalSurcharge = selectedRows.stream()
                            .mapToInt(row -> (int) model.getValueAt(row, 7))
                            .mapToDouble(daysOverdue -> daysOverdue * 10.0)
                            .sum();

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Mark " + selectedRows.size() + " books as overdue?\n"
                            + "Total surcharge: ₱" + String.format("%.2f", totalSurcharge),
                            "Confirm Mark as Overdue",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        conn.setAutoCommit(false);
                        int successCount = 0;

                        try {
                            for (int row : selectedRows) {
                                try (PreparedStatement insertStmt = conn.prepareStatement("""
                                INSERT INTO Overdues (
                                    BorrowingID, BookCode, BookTitle, BorrowerID, BorrowerName,
                                    DateBorrowed, DueDate, DaysOverdue, SurchargeAmount, IsPaid
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'N')
                            """)) {
                                    int daysOverdue = (int) model.getValueAt(row, 7);
                                    double surcharge = daysOverdue * 10.0;
                                    insertStmt.setInt(1, (int) model.getValueAt(row, 8));
                                    insertStmt.setString(2, (String) model.getValueAt(row, 3));
                                    insertStmt.setString(3, (String) model.getValueAt(row, 4));
                                    insertStmt.setLong(4, (long) model.getValueAt(row, 1));
                                    insertStmt.setString(5, (String) model.getValueAt(row, 2));
                                    insertStmt.setDate(6, (java.sql.Date) model.getValueAt(row, 5));
                                    insertStmt.setDate(7, (java.sql.Date) model.getValueAt(row, 6));
                                    insertStmt.setInt(8, (int) model.getValueAt(row, 7));
                                    insertStmt.setBigDecimal(9, new java.math.BigDecimal((int) model.getValueAt(row, 7) * 10.0));

                                    insertStmt.executeUpdate();
                                    successCount++;
                                }
                            }

                            conn.commit();
                            JOptionPane.showMessageDialog(this,
                                    successCount + " books marked as overdue successfully!\n"
                                    + "Total surcharge: ₱" + String.format("%.2f", totalSurcharge));
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
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT BookCode, BookTitle, Author, BookYear, Quantity, AvailableQuantity "
                + "FROM Books "
                + "ORDER BY BookCode")) {

            DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getString("Author"),
                    rs.getObject("BookYear"), // Use getObject to handle NULL years properly
                    rs.getInt("Quantity"),
                    rs.getInt("AvailableQuantity")
                });
            }

            // Optional: Show count of loaded books
            int rowCount = model.getRowCount();
            if (rowCount == 0) {
                // If no books found, you might want to show a message or placeholder
                System.out.println("No books found in the database.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading books from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // For debugging purposes
        }
    }

    private void loadUsers() {
        // Load user data with updated query for new schema
        loadTableData("SELECT UserID, Username, UserType, Department, Email FROM Users", tbl_users);

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement()) {

            // Count total students
            ResultSet rsStudents = stmt.executeQuery("SELECT COUNT(*) FROM Users WHERE UserType = 'Student'");
            if (rsStudents.next()) {
                lbl_userCount.setText("Total Students: " + rsStudents.getInt(1));
            }

            // Count total faculty
            ResultSet rsFaculty = stmt.executeQuery("SELECT COUNT(*) FROM Users WHERE UserType = 'Faculty'");
            if (rsFaculty.next()) {
                lbl_adminCount.setText("Total Faculty: " + rsFaculty.getInt(1));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Count Error: " + e.getMessage());
        }
    }

    private void loadOverdue() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement()) {

            DefaultTableModel model = (DefaultTableModel) tbl_overdue.getModel();
            model.setRowCount(0);

            // Query for active borrowings (not returned)
            String borrowedQuery
                    = "SELECT BorrowingID, BookCode, BookTitle, BorrowerID, BorrowerName, "
                    + "DateBorrowed, DueDate, 0 as DaysOverdue, 0.00 as SurchargeAmount, 'Borrowed' as Status "
                    + "FROM Borrowings "
                    + "WHERE IsReturned = 'N'";

            ResultSet rsBorrowed = stmt.executeQuery(borrowedQuery);

            // Add borrowed books
            while (rsBorrowed.next()) {
                model.addRow(new Object[]{
                    rsBorrowed.getInt("BorrowingID"),
                    rsBorrowed.getString("BookCode"),
                    rsBorrowed.getString("BookTitle"),
                    rsBorrowed.getLong("BorrowerID"),
                    rsBorrowed.getString("BorrowerName"),
                    rsBorrowed.getDate("DateBorrowed"),
                    rsBorrowed.getDate("DueDate"),
                    rsBorrowed.getInt("DaysOverdue"),
                    rsBorrowed.getBigDecimal("SurchargeAmount"),
                    rsBorrowed.getString("Status")
                });
            }

            // Query for overdue books
            String overdueQuery
                    = "SELECT o.OverdueID, o.BookCode, o.BookTitle, o.BorrowerID, o.BorrowerName, "
                    + "o.DateBorrowed, o.DueDate, o.DaysOverdue, o.SurchargeAmount, "
                    + "CASE WHEN o.IsPaid = 'Y' THEN 'Paid' ELSE 'Overdue' END as Status "
                    + "FROM Overdues o";

            ResultSet rsOverdue = stmt.executeQuery(overdueQuery);

            // Add overdue books
            while (rsOverdue.next()) {
                model.addRow(new Object[]{
                    rsOverdue.getInt("OverdueID"),
                    rsOverdue.getString("BookCode"),
                    rsOverdue.getString("BookTitle"),
                    rsOverdue.getLong("BorrowerID"),
                    rsOverdue.getString("BorrowerName"),
                    rsOverdue.getDate("DateBorrowed"),
                    rsOverdue.getDate("DueDate"),
                    rsOverdue.getInt("DaysOverdue"),
                    rsOverdue.getBigDecimal("SurchargeAmount"),
                    rsOverdue.getString("Status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Borrowed/Overdue Error: " + e.getMessage());
        }
    }

    private void loadArchivedBooks() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT ArchiveID, OriginalBookCode, BookTitle, Author, "
                + "BookYear, ArchivedQuantity, ArchiveReason, AdminName, ArchiveDate "
                + "FROM ArchivedBooks "
                + "ORDER BY ArchiveDate DESC")) {

            DefaultTableModel model = (DefaultTableModel) tbl_archived.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ArchiveID"), // Column 0: Archive ID
                    rs.getString("OriginalBookCode"), // Column 1: Original Book Code
                    rs.getString("BookTitle"), // Column 2: Title
                    rs.getString("Author"), // Column 3: Author
                    rs.getObject("BookYear"), // Column 4: Year
                    rs.getInt("ArchivedQuantity"), // Column 5: Archived Quantity
                    rs.getString("ArchiveReason"), // Column 6: Archive Reason
                    rs.getString("AdminName"), // Column 7: Admin Name (NEW COLUMN)
                    rs.getTimestamp("ArchiveDate") // Column 8: Archive Date
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Archived Books Error: " + e.getMessage());
        }
    }

    private void loadTableData(String query, JTable table) {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

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
        JTextField bookCode = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField deweyClass = new JTextField();
        JTextField year = new JTextField();
        JTextField quantity = new JTextField();

        // Add some helper text for Dewey Classification
        JLabel deweyHelper = new JLabel("<html><small>Examples: 004 (Computer Science), 796 (Sports), 300 (Social Sciences)</small></html>");
        deweyHelper.setFont(new Font("Arial", Font.ITALIC, 10));

        Object[] fields = {
            "Book Code:", bookCode,
            "Title:", title,
            "Author:", author,
            "Dewey Class:", deweyClass,
            deweyHelper,
            "Year:", year,
            "Initial Quantity:", quantity
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            addBook(bookCode.getText(), title.getText(), author.getText(), deweyClass.getText(),
                    year.getText(), quantity.getText());
        }
    }

    private boolean addBook(String bookCode, String title, String author, String deweyClass, String year, String quantity) {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
            conn.setAutoCommit(false);

            try {
                // Step 1: Check if the BookCode already exists in Books or ArchivedBooks
                try (PreparedStatement checkCodeStmt = conn.prepareStatement(
                        "SELECT 1 FROM Books WHERE BookCode = ? UNION SELECT 1 FROM ArchivedBooks WHERE OriginalBookCode = ?")) {
                    checkCodeStmt.setString(1, bookCode);
                    checkCodeStmt.setString(2, bookCode);
                    try (ResultSet rs = checkCodeStmt.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("Book Code already exists in the library or archive.");
                        }
                    }
                }

                // Step 2: Validate Dewey Class format (should be numeric, typically 3 digits)
                try {
                    if (!deweyClass.matches("\\d{1,3}")) {
                        throw new SQLException("Dewey Class must be numeric (1-3 digits). Examples: 004, 796, 300");
                    }
                } catch (Exception e) {
                    throw new SQLException("Invalid Dewey Class format: " + deweyClass);
                }

                // Step 3: Validate year
                int bookYear;
                try {
                    bookYear = Integer.parseInt(year);
                    if (bookYear < 1000 || bookYear > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1) {
                        throw new SQLException("Please enter a valid year.");
                    }
                } catch (NumberFormatException e) {
                    throw new SQLException("Year must be a valid number.");
                }

                // Step 4: Validate quantity
                int bookQuantity;
                try {
                    bookQuantity = Integer.parseInt(quantity);
                    if (bookQuantity < 0) {
                        throw new SQLException("Quantity cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    throw new SQLException("Quantity must be a valid number.");
                }

                // Step 5: Insert into Books table
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Books (BookCode, BookTitle, Author, BookYear, Quantity, AvailableQuantity, DeweyClass) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, bookCode);
                    stmt.setString(2, title);
                    stmt.setString(3, author);
                    stmt.setInt(4, bookYear);
                    stmt.setInt(5, bookQuantity);
                    stmt.setInt(6, bookQuantity); // Initially, all books are available
                    stmt.setString(7, deweyClass);
                    stmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                loadBooks(); // Refresh the books table
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


    private void addMultipleBooksDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(700, 350));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book Code", "Title", "Author", "Dewey Class", "Year", "Quantity"}, 0);

        for (int i = 0; i < 5; i++) {
            model.addRow(new Object[]{"", "", "", "", "", ""});
        }

        JTable inputTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(inputTable);

        // Add some helpful information about Dewey Decimal System
        JLabel infoLabel = new JLabel("<html><b>Dewey Classes:</b> 000-Computer Science, 100-Philosophy, 200-Religion, 300-Social Sciences, 400-Language, 500-Sciences, 600-Technology, 700-Arts, 800-Literature, 900-History</html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> model.addRow(new Object[]{"", "", "", "", "", ""}));

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addRowButton, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Multiple Books",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int successCount = 0;
            int totalCount = 0;
            List<String> skippedBooks = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/LibraryDB", "username", "password")) {
                conn.setAutoCommit(false);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String bookCode = (String) model.getValueAt(i, 0);
                    String title = (String) model.getValueAt(i, 1);
                    String author = (String) model.getValueAt(i, 2);
                    String deweyClass = (String) model.getValueAt(i, 3);
                    String year = (String) model.getValueAt(i, 4);
                    String quantity = (String) model.getValueAt(i, 5);

                    // Skip empty rows
                    if (bookCode == null || bookCode.trim().isEmpty()
                            || title == null || title.trim().isEmpty()) {
                        continue;
                    }

                    totalCount++;

                    try {
                        // Check for existing BookCode or BookTitle in both Books and ArchivedBooks tables
                        try (PreparedStatement checkStmt = conn.prepareStatement(
                                "SELECT 'Books' AS source FROM Books WHERE BookCode = ? OR BookTitle = ? "
                                + "UNION SELECT 'ArchivedBooks' FROM ArchivedBooks WHERE OriginalBookCode = ? OR BookTitle = ?")) {
                            checkStmt.setString(1, bookCode);
                            checkStmt.setString(2, title);
                            checkStmt.setString(3, bookCode);
                            checkStmt.setString(4, title);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    skippedBooks.add("[" + bookCode + "] \"" + title + "\" already exists in " + rs.getString("source"));
                                    continue;
                                }
                            }
                        }

                        // Validate Dewey Class format (should be 3 digits)
                        if (deweyClass == null || deweyClass.trim().isEmpty()) {
                            deweyClass = "000"; // Default to general works
                        } else {
                            deweyClass = deweyClass.trim();
                            // Ensure it's a valid 3-digit Dewey class
                            if (!deweyClass.matches("\\d{3}")) {
                                skippedBooks.add("[" + bookCode + "] Invalid Dewey Class format. Use 3 digits (e.g., 004, 796)");
                                continue;
                            }
                        }

                        // Parse and validate numeric fields
                        int bookYear = 0;
                        int bookQuantity = 0;

                        try {
                            if (year != null && !year.trim().isEmpty()) {
                                bookYear = Integer.parseInt(year.trim());
                            }
                            if (quantity != null && !quantity.trim().isEmpty()) {
                                bookQuantity = Integer.parseInt(quantity.trim());
                                if (bookQuantity < 0) {
                                    skippedBooks.add("[" + bookCode + "] Quantity cannot be negative");
                                    continue;
                                }
                            }
                        } catch (NumberFormatException e) {
                            skippedBooks.add("[" + bookCode + "] Invalid number format for year or quantity");
                            continue;
                        }

                        // Insert into Books table
                        try (PreparedStatement stmt = conn.prepareStatement(
                                "INSERT INTO Books (BookCode, BookTitle, Author, BookYear, Quantity, AvailableQuantity, DeweyClass) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                            stmt.setString(1, bookCode.trim());
                            stmt.setString(2, title.trim());
                            stmt.setString(3, author != null && !author.trim().isEmpty() ? author.trim() : "Unknown");

                            if (bookYear > 0) {
                                stmt.setInt(4, bookYear);
                            } else {
                                stmt.setNull(4, java.sql.Types.INTEGER);
                            }

                            stmt.setInt(5, bookQuantity);
                            stmt.setInt(6, bookQuantity); // Available quantity equals total quantity initially
                            stmt.setString(7, deweyClass);

                            stmt.executeUpdate();
                            successCount++;
                        }

                    } catch (SQLException e) {
                        skippedBooks.add("Error adding [" + bookCode + "]: " + e.getMessage());
                    } catch (Exception e) {
                        skippedBooks.add("Unexpected error for [" + bookCode + "]: " + e.getMessage());
                    }
                }

                try {
                    conn.commit();
                    loadBooks(); // Refresh the books display

                    StringBuilder msg = new StringBuilder("Added " + successCount + " of " + totalCount + " books successfully.");
                    if (!skippedBooks.isEmpty()) {
                        msg.append("\n\nSkipped entries:\n");
                        for (String skipped : skippedBooks) {
                            msg.append("• ").append(skipped).append("\n");
                        }

                        JTextArea textArea = new JTextArea(msg.toString());
                        textArea.setEditable(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                        JScrollPane scrollPaneResult = new JScrollPane(textArea);
                        scrollPaneResult.setPreferredSize(new Dimension(600, 400));

                        JOptionPane.showMessageDialog(this, scrollPaneResult, "Add Books Result", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, msg.toString(), "Add Books Result", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (SQLException e) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Connection Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMultipleBooks() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"", "Book Code", "Title", "Author"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT BookCode, BookTitle, Author FROM Books")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    false,
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getString("Author")
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
                List<String> bookCodesToDelete = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Boolean checked = (Boolean) model.getValueAt(i, 0);
                    if (checked != null && checked) {
                        bookCodesToDelete.add((String) model.getValueAt(i, 1));
                    }
                }

                if (bookCodesToDelete.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No books selected for deletion.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete " + bookCodesToDelete.size() + " selected books?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        conn.setAutoCommit(false);
                        int deletedCount = 0;

                        // Get current admin info (you may need to modify this based on your session management)
                        long currentAdminId = SessionManager.getCurrentAdminId(); // Implement this method
                        String currentAdminName = SessionManager.getCurrentAdminName(); // Implement this method

                        try (
                                PreparedStatement archive = conn.prepareStatement(
                                        "INSERT INTO ArchivedBooks (OriginalBookCode, BookTitle, Author, BookYear, "
                                        + "ArchivedQuantity, DeweyClass, ArchiveReason, AdminID, AdminName) "
                                        + "SELECT BookCode, BookTitle, Author, BookYear, Quantity, DeweyClass, "
                                        + "'Bulk deletion', ?, ? FROM Books WHERE BookCode = ?"); PreparedStatement deleteBook = conn.prepareStatement("DELETE FROM Books WHERE BookCode = ?")) {

                            for (String bookCode : bookCodesToDelete) {
                                // Check if book is currently borrowed
                                try (PreparedStatement checkBorrowed = conn.prepareStatement(
                                        "SELECT COUNT(*) FROM Borrowings WHERE BookCode = ? AND IsReturned = 'N'")) {
                                    checkBorrowed.setString(1, bookCode);
                                    ResultSet borrowedRs = checkBorrowed.executeQuery();
                                    borrowedRs.next();
                                    if (borrowedRs.getInt(1) > 0) {
                                        JOptionPane.showMessageDialog(this,
                                                "Cannot delete book " + bookCode + " - it is currently borrowed.");
                                        continue;
                                    }
                                }

                                // Archive the book
                                archive.setLong(1, currentAdminId);
                                archive.setString(2, currentAdminName);
                                archive.setString(3, bookCode);
                                archive.executeUpdate();

                                // Delete the book
                                deleteBook.setString(1, bookCode);
                                deleteBook.executeUpdate();

                                deletedCount++;
                            }
                        }

                        conn.commit();
                        loadBooks();
                        JOptionPane.showMessageDialog(this, deletedCount + " books deleted successfully.");
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            // Ignore rollback error
                        }
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
        JComboBox<String> userType = new JComboBox<>(new String[]{"Student", "Faculty"});
        JTextField course = new JTextField();
        JTextField department = new JTextField();
        JTextField contactNumber = new JTextField();
        JTextField email = new JTextField();

        Object[] fields = {
            "User ID (11 digits):", userID,
            "Username:", username,
            "User Type:", userType,
            "Course (for students):", course,
            "Department:", department,
            "Contact Number:", contactNumber,
            "Email:", email
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String idText = userID.getText().trim();
            String usernameText = username.getText().trim();
            String selectedUserType = (String) userType.getSelectedItem();
            String courseText = course.getText().trim();
            String departmentText = department.getText().trim();
            String contactText = contactNumber.getText().trim();
            String emailText = email.getText().trim();

            if (!idText.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "User ID must be exactly 11 digits.");
                return;
            }

            if (usernameText.isEmpty() || departmentText.isEmpty() || emailText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, Department, and Email cannot be empty.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Users (UserID, Username, UserType, Course, Department, ContactNumber, Email) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)"
            )) {

                stmt.setLong(1, Long.parseLong(idText));
                stmt.setString(2, usernameText);
                stmt.setString(3, selectedUserType);
                // Set course to null for Faculty
                if ("Faculty".equals(selectedUserType) && courseText.isEmpty()) {
                    stmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(4, courseText.isEmpty() ? null : courseText);
                }
                stmt.setString(5, departmentText);
                stmt.setString(6, contactText.isEmpty() ? null : contactText);
                stmt.setString(7, emailText);

                stmt.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "User added successfully.");
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
                ? "SELECT UserID, Username, UserType FROM Users WHERE UserID = ?"
                : "SELECT UserID, Username, UserType FROM Users WHERE Username LIKE ?";

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); PreparedStatement stmt = conn.prepareStatement(query)) {

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

            long userID = rs.getLong("UserID");
            String username = rs.getString("Username");
            String userType = rs.getString("UserType");

            // Check if user has unreturned books
            try (PreparedStatement checkBorrowed = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Borrowings WHERE BorrowerID = ? AND IsReturned = 'N'")) {
                checkBorrowed.setLong(1, userID);
                ResultSet borrowedRs = checkBorrowed.executeQuery();
                borrowedRs.next();
                if (borrowedRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete user - they have unreturned books.");
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete user:\nID: " + userID + "\nUsername: " + username + "\nType: " + userType,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Users WHERE UserID = ?")) {
                    deleteStmt.setLong(1, userID);
                    deleteStmt.executeUpdate();
                    loadUsers();
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete User Error: " + e.getMessage());
        }
    }

    private void addMultipleUsersDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 400));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"User ID", "Username", "User Type", "Course", "Department", "Contact", "Email"}, 0);

        // Add 5 empty rows to start with
        for (int i = 0; i < 5; i++) {
            model.addRow(new Object[]{"", "", "Student", "", "", "", ""});
        }

        JTable inputTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // All cells are editable
            }
        };

        // Set up combo box for UserType column
        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Student", "Faculty"});
        inputTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(userTypeCombo));

        JScrollPane scrollPane = new JScrollPane(inputTable);

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> model.addRow(new Object[]{"", "", "Student", "", "", "", ""}));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addRowButton, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Multiple Users",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int successCount = 0;
            int totalCount = 0;

            try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
                conn.setAutoCommit(false);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String userID = (String) model.getValueAt(i, 0);
                    String username = (String) model.getValueAt(i, 1);
                    String userType = (String) model.getValueAt(i, 2);
                    String course = (String) model.getValueAt(i, 3);
                    String department = (String) model.getValueAt(i, 4);
                    String contact = (String) model.getValueAt(i, 5);
                    String email = (String) model.getValueAt(i, 6);

                    // Skip empty rows
                    if (userID == null || userID.trim().isEmpty()
                            || username == null || username.trim().isEmpty()
                            || department == null || department.trim().isEmpty()
                            || email == null || email.trim().isEmpty()) {
                        continue;
                    }

                    // Validate User ID format
                    if (!userID.matches("\\d{11}")) {
                        System.out.println("Skipping invalid User ID: " + userID);
                        continue;
                    }

                    totalCount++;

                    try (PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO Users (UserID, Username, UserType, Course, Department, ContactNumber, Email) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        stmt.setLong(1, Long.parseLong(userID));
                        stmt.setString(2, username);
                        stmt.setString(3, userType);
                        // Handle course field (null for Faculty if empty)
                        if ("Faculty".equals(userType) && (course == null || course.trim().isEmpty())) {
                            stmt.setNull(4, java.sql.Types.VARCHAR);
                        } else {
                            stmt.setString(4, course != null && !course.trim().isEmpty() ? course : null);
                        }
                        stmt.setString(5, department);
                        stmt.setString(6, contact != null && !contact.trim().isEmpty() ? contact : null);
                        stmt.setString(7, email);
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
        DefaultTableModel model = new DefaultTableModel(new String[]{"", "ID", "Username", "Type", "Department"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT UserID, Username, UserType, Department FROM Users")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    false,
                    rs.getLong("UserID"),
                    rs.getString("Username"),
                    rs.getString("UserType"),
                    rs.getString("Department")
                });
            }

            JTable selectionTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(selectionTable);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JCheckBox selectAll = new JCheckBox("Select All Users");
            selectAll.addActionListener(e -> {
                boolean selected = selectAll.isSelected();
                for (int i = 0; i < model.getRowCount(); i++) {
                    model.setValueAt(selected, i, 0);
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(selectAll, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, panel, "Select Users to Delete",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                List<Long> userIdsToDelete = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Boolean checked = (Boolean) model.getValueAt(i, 0);
                    if (checked != null && checked) {
                        userIdsToDelete.add((Long) model.getValueAt(i, 1));
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
                        int skippedCount = 0;

                        for (Long userId : userIdsToDelete) {
                            // Check if user has unreturned books
                            try (PreparedStatement checkBorrowed = conn.prepareStatement(
                                    "SELECT COUNT(*) FROM Borrowings WHERE BorrowerID = ? AND IsReturned = 'N'")) {
                                checkBorrowed.setLong(1, userId);
                                ResultSet borrowedRs = checkBorrowed.executeQuery();
                                borrowedRs.next();
                                if (borrowedRs.getInt(1) > 0) {
                                    skippedCount++;
                                    continue; // Skip this user
                                }
                            }

                            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Users WHERE UserID = ?")) {
                                deleteStmt.setLong(1, userId);
                                int deleteResult = deleteStmt.executeUpdate();

                                if (deleteResult > 0) {
                                    deletedCount++;
                                }
                            }
                        }

                        conn.commit();
                        loadUsers();
                        String message = deletedCount + " users deleted successfully.";
                        if (skippedCount > 0) {
                            message += " " + skippedCount + " users skipped (have unreturned books).";
                        }
                        JOptionPane.showMessageDialog(this, message);
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
        String input = JOptionPane.showInputDialog(this, "Enter Book Code or Title to delete:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); PreparedStatement stmt = conn.prepareStatement(
                "SELECT BookCode, BookTitle, Author, DeweyClass, BookYear, Quantity "
                + "FROM Books WHERE BookCode = ? OR BookTitle LIKE ?")) {

            stmt.setString(1, input);
            stmt.setString(2, "%" + input + "%");
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No matching book found.");
                return;
            }

            String bookCode = rs.getString("BookCode");
            String bookTitle = rs.getString("BookTitle");
            String author = rs.getString("Author");
            String deweyClass = rs.getString("DeweyClass");
            int bookYear = rs.getInt("BookYear");
            int quantity = rs.getInt("Quantity");

            // Check if book is currently borrowed
            try (PreparedStatement checkBorrowed = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Borrowings WHERE BookCode = ? AND IsReturned = 'N'")) {
                checkBorrowed.setString(1, bookCode);
                ResultSet borrowedRs = checkBorrowed.executeQuery();
                borrowedRs.next();
                if (borrowedRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete book - it is currently borrowed.");
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete book:\nCode: " + bookCode + "\nTitle: " + bookTitle + "\nAuthor: " + author,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    conn.setAutoCommit(false);

                    // Get current admin info
                    long currentAdminId = SessionManager.getCurrentAdminId();
                    String currentAdminName = SessionManager.getCurrentAdminName();

                    // Archive the book first
                    try (PreparedStatement archiveStmt = conn.prepareStatement(
                            "INSERT INTO ArchivedBooks (OriginalBookCode, BookTitle, Author, BookYear, "
                            + "ArchivedQuantity, DeweyClass, ArchiveReason, AdminID, AdminName) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                        archiveStmt.setString(1, bookCode);
                        archiveStmt.setString(2, bookTitle);
                        archiveStmt.setString(3, author);
                        archiveStmt.setInt(4, bookYear);
                        archiveStmt.setInt(5, quantity);
                        archiveStmt.setString(6, deweyClass);
                        archiveStmt.setString(7, "Single book deletion");
                        archiveStmt.setLong(8, currentAdminId);
                        archiveStmt.setString(9, currentAdminName);
                        archiveStmt.executeUpdate();
                    }

                    // Delete the book
                    try (PreparedStatement deleteBook = conn.prepareStatement("DELETE FROM Books WHERE BookCode = ?")) {
                        deleteBook.setString(1, bookCode);
                        deleteBook.executeUpdate();
                    }

                    conn.commit();
                    loadBooks();
                    JOptionPane.showMessageDialog(this, "Book deleted and archived successfully.");
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        // Ignore rollback error
                    }
                    JOptionPane.showMessageDialog(this, "Delete Error: " + e.getMessage());
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
