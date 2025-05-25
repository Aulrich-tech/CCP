package Library_System;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Library_User extends javax.swing.JFrame {

    private ArrayList<String[]> books;

    public Library_User() {
        initComponents();
        setLocationRelativeTo(null);
        tbl_books.setRowHeight(25);
        loadBooks();
        updateWelcomeLabel();
    }

    private void updateWelcomeLabel() {
        if (SessionManager.isUserLoggedIn()) {
            String userType = SessionManager.getCurrentUserType();
            String username = SessionManager.getCurrentUsername();
            lbl_title.setText("   Welcome " + userType + " - " + username + "!");
        } else {
            lbl_title.setText("   Welcome User!");
        }
    }

    private void loadBooks() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT BookCode, BookTitle, Author,BookYear, Quantity, AvailableQuantity "
                + "FROM Books ORDER BY BookCode")) {

            DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getString("Author"),
                    rs.getInt("BookYear"),
                    rs.getInt("AvailableQuantity") + "/" + rs.getInt("Quantity")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Books Error: " + e.getMessage());
        }
    }

    private void showUserProfile() {
        if (!SessionManager.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(this, "No user is currently logged in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog profileDialog = new JDialog(this, "User Profile", true);
        profileDialog.setSize(900, 650);
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setResizable(false);

        // Main content panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel similar to login form style
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 0, 0)); // Red background like login form
        headerPanel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED,
                Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE));
        headerPanel.setPreferredSize(new Dimension(900, 60));

        JLabel headerLabel = new JLabel("User Profile - " + SessionManager.getCurrentUsername());
        headerLabel.setFont(new Font("Stencil", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // User Details Tab
        JPanel userDetailsPanel = createStyledUserDetailsPanel();
        tabbedPane.addTab("My Details", userDetailsPanel);

        // Active Borrowings Tab
        JPanel activeBorrowingsPanel = createStyledActiveBorrowingsPanel();
        tabbedPane.addTab("Active Borrowings", activeBorrowingsPanel);

        // Past Borrowings Tab
        JPanel pastBorrowingsPanel = createStyledPastBorrowingsPanel();
        tabbedPane.addTab("Borrowing History", pastBorrowingsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with styled close button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnClose = new JButton("CLOSE");
        btnClose.setBackground(Color.BLACK);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnClose.setPreferredSize(new Dimension(100, 30));
        btnClose.addActionListener(e -> profileDialog.dispose());

        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        profileDialog.add(mainPanel);
        profileDialog.setVisible(true);
    }

    private JPanel createStyledUserDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create a centered panel for the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // User details with styling similar to login form
        String[][] userDetails = {
            {"User ID:", String.valueOf(SessionManager.getCurrentUserId())},
            {"Username:", SessionManager.getCurrentUsername()},
            {"User Type:", SessionManager.getCurrentUserType()},
            {"Course:", SessionManager.getCurrentCourse() != null ? SessionManager.getCurrentCourse() : "N/A"},
            {"Department:", SessionManager.getCurrentUserDepartment()},
            {"Contact Number:", SessionManager.getCurrentUserContactNumber() != null ? SessionManager.getCurrentUserContactNumber() : "N/A"},
            {"Email:", SessionManager.getCurrentUserEmail()},
            {"Account Created:", SessionManager.getUserCreatedDate() != null ? SessionManager.getUserCreatedDate().toString().substring(0, 10) : "N/A"}
        };

        for (int i = 0; i < userDetails.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;

            JLabel label = new JLabel(userDetails[i][0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(new Color(51, 51, 51));
            centerPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;

            // Create styled text field for values (read-only)
            JTextField valueField = new JTextField(userDetails[i][1]);
            valueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valueField.setEditable(false);
            valueField.setBorder(new javax.swing.border.LineBorder(new Color(153, 153, 153), 1, true));
            valueField.setBackground(new Color(245, 245, 245));
            valueField.setPreferredSize(new Dimension(300, 25));

            centerPanel.add(valueField, gbc);
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        return mainPanel;
    }

// Replace the existing createStyledActiveBorrowingsPanel method with this updated version
    private JPanel createStyledActiveBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("Your Active Book Borrowings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create styled table for active borrowings with updated columns
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book Code", "Book Title", "Date Borrowed", "Due Date", "Status", "Surcharge"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable activeBorrowingsTable = new JTable(model);
        activeBorrowingsTable.setRowHeight(30);
        activeBorrowingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activeBorrowingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeBorrowingsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        activeBorrowingsTable.setGridColor(new Color(200, 200, 200));
        activeBorrowingsTable.setSelectionBackground(new Color(230, 230, 250));

        // Load active borrowings
        loadActiveBorrowings(model);

        JScrollPane scrollPane = new JScrollPane(activeBorrowingsTable);
        scrollPane.setBorder(new javax.swing.border.LineBorder(new Color(153, 153, 153), 1, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add styled refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setBackground(new Color(255, 0, 0));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.addActionListener(e -> loadActiveBorrowings(model));

        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

// Replace the existing createStyledPastBorrowingsPanel method with this updated version
    private JPanel createStyledPastBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("Your Borrowing History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create styled table for past borrowings with updated columns
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book Code", "Book Title", "Date Borrowed", "Due Date", "Date Returned", "Status", "Surcharge"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable pastBorrowingsTable = new JTable(model);
        pastBorrowingsTable.setRowHeight(30);
        pastBorrowingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pastBorrowingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pastBorrowingsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        pastBorrowingsTable.setGridColor(new Color(200, 200, 200));
        pastBorrowingsTable.setSelectionBackground(new Color(230, 230, 250));

        // Load past borrowings
        loadPastBorrowings(model);

        JScrollPane scrollPane = new JScrollPane(pastBorrowingsTable);
        scrollPane.setBorder(new javax.swing.border.LineBorder(new Color(153, 153, 153), 1, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add styled refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setBackground(new Color(255, 0, 0));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.addActionListener(e -> loadPastBorrowings(model));

        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUserDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // User details
        String[][] userDetails = {
            {"User ID:", String.valueOf(SessionManager.getCurrentUserId())},
            {"Username:", SessionManager.getCurrentUsername()},
            {"User Type:", SessionManager.getCurrentUserType()},
            {"Course:", SessionManager.getCurrentCourse() != null ? SessionManager.getCurrentCourse() : "N/A"},
            {"Department:", SessionManager.getCurrentUserDepartment()},
            {"Contact Number:", SessionManager.getCurrentUserContactNumber() != null ? SessionManager.getCurrentUserContactNumber() : "N/A"},
            {"Email:", SessionManager.getCurrentUserEmail()},
            {"Account Created:", SessionManager.getUserCreatedDate() != null ? SessionManager.getUserCreatedDate().toString() : "N/A"}
        };

        for (int i = 0; i < userDetails.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(userDetails[i][0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panel.add(label, gbc);

            gbc.gridx = 1;
            JLabel value = new JLabel(userDetails[i][1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(value, gbc);
        }

        return panel;
    }

    private JPanel createActiveBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table for active borrowings
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book Code", "Book Title", "Date Borrowed", "Due Date", "Days Left"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable activeBorrowingsTable = new JTable(model);
        activeBorrowingsTable.setRowHeight(25);

        // Load active borrowings
        loadActiveBorrowings(model);

        JScrollPane scrollPane = new JScrollPane(activeBorrowingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadActiveBorrowings(model));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPastBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table for past borrowings
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Book Code", "Book Title", "Date Borrowed", "Due Date", "Date Returned", "Days Overdue", "Surcharge"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable pastBorrowingsTable = new JTable(model);
        pastBorrowingsTable.setRowHeight(25);

        // Load past borrowings
        loadPastBorrowings(model);

        JScrollPane scrollPane = new JScrollPane(pastBorrowingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadPastBorrowings(model));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadActiveBorrowings(DefaultTableModel model) {
        model.setRowCount(0);

        if (!SessionManager.isUserLoggedIn()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
            String sql = "SELECT b.BookCode, b.BookTitle, b.DateBorrowed, b.DueDate, b.BorrowingID "
                    + "FROM Borrowings b WHERE b.BorrowerID = ? AND b.IsReturned = 'N' "
                    + "ORDER BY b.DateBorrowed DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, SessionManager.getCurrentUserId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date dueDate = rs.getDate("DueDate");
                Date currentDate = new Date(System.currentTimeMillis());
                int borrowingId = rs.getInt("BorrowingID");

                // Calculate days overdue
                long diffInMillies = currentDate.getTime() - dueDate.getTime();
                long daysOverdue = diffInMillies / (1000 * 60 * 60 * 24);

                String status;
                String surcharge = "₱0.00";

                if (daysOverdue > 0) {
                    // Book is overdue
                    status = daysOverdue + " days overdue";

                    // Check if there's an overdue record with surcharge
                    String overdueSQL = "SELECT SurchargeAmount FROM Overdues WHERE BorrowingID = ?";
                    try (PreparedStatement overdueStmt = conn.prepareStatement(overdueSQL)) {
                        overdueStmt.setInt(1, borrowingId);
                        ResultSet overdueRs = overdueStmt.executeQuery();
                        if (overdueRs.next()) {
                            double surchargeAmount = overdueRs.getDouble("SurchargeAmount");
                            surcharge = "₱" + String.format("%.2f", surchargeAmount);
                        } else {
                            // Calculate surcharge if not in Overdues table yet
                            // Assuming ₱5.00 per day overdue (adjust as needed)
                            double calculatedSurcharge = daysOverdue * 5.00;
                            surcharge = "₱" + String.format("%.2f", calculatedSurcharge);
                        }
                    }
                } else if (daysOverdue == 0) {
                    status = "Due today";
                } else {
                    // Book is not yet due
                    long daysLeft = Math.abs(daysOverdue);
                    status = daysLeft + " days left";
                }

                model.addRow(new Object[]{
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getDate("DateBorrowed"),
                    dueDate,
                    status,
                    surcharge
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading active borrowings: " + e.getMessage());
        }
    }

// Replace the existing loadPastBorrowings method with this updated version
    private void loadPastBorrowings(DefaultTableModel model) {
        model.setRowCount(0);

        if (!SessionManager.isUserLoggedIn()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {
            String sql = "SELECT BookCode, BookTitle, DateBorrowed, DueDate, DateReturned, "
                    + "DaysOverdue, SurchargeAmount, IsSurchargePaid "
                    + "FROM ReturnedBooks WHERE BorrowerID = ? "
                    + "ORDER BY DateReturned DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, SessionManager.getCurrentUserId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int daysOverdue = rs.getInt("DaysOverdue");
                double surcharge = rs.getDouble("SurchargeAmount");
                String isSurchargePaid = rs.getString("IsSurchargePaid");

                String status;
                String surchargeDisplay;

                if (daysOverdue > 0) {
                    // Book was returned late
                    if (surcharge > 0) {
                        if ("Y".equals(isSurchargePaid)) {
                            status = "Returned/Paid (" + daysOverdue + " days late)";
                            surchargeDisplay = "₱" + String.format("%.2f", surcharge) + " (Paid)";
                        } else {
                            status = "Returned/Unpaid (" + daysOverdue + " days late)";
                            surchargeDisplay = "₱" + String.format("%.2f", surcharge) + " (Unpaid)";
                        }
                    } else {
                        status = "Returned (" + daysOverdue + " days late)";
                        surchargeDisplay = "₱0.00";
                    }
                } else {
                    // Book was returned on time
                    status = "Returned";
                    surchargeDisplay = "₱0.00";
                }

                model.addRow(new Object[]{
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getDate("DateBorrowed"),
                    rs.getDate("DueDate"),
                    rs.getDate("DateReturned"),
                    status,
                    surchargeDisplay
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading borrowing history: " + e.getMessage());
        }
    }

    private void refreshTable(ArrayList<String[]> bookList) {
        DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
        model.setRowCount(0);
        for (String[] book : bookList) {
            model.addRow(book);
        }
    }

    private void searchBooks() {
        String keyword = txt_search.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a search term.");
            return;
        }

        String fieldLabel = cmb_searchBy.getSelectedItem().toString();
        String columnName;

        switch (fieldLabel) {
            case "Book Code":
                columnName = "BookCode";
                break;
            case "Title":
                columnName = "BookTitle";
                break;
            case "Author":
                columnName = "Author";
                break;
            case "Year":
                columnName = "BookYear";
                break;
            default:
                columnName = "BookTitle";
                break;
        }

        ArrayList<String[]> filtered = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised")) {

            String sql;
            PreparedStatement stmt;

            if (columnName.equals("BookYear")) {
                sql = "SELECT BookCode, BookTitle, Author,BookYear, Quantity, AvailableQuantity "
                        + "FROM Books WHERE CAST(" + columnName + " AS VARCHAR(20)) LIKE ?";
            } else {
                sql = "SELECT BookCode, BookTitle, Author, BookYear, Quantity, AvailableQuantity "
                        + "FROM Books WHERE UPPER(" + columnName + ") LIKE UPPER(?)";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookCode = rs.getString("BookCode");
                String title = rs.getString("BookTitle");
                String author = rs.getString("Author");
                String year = String.valueOf(rs.getInt("BookYear"));
                String availability = rs.getInt("AvailableQuantity") + "/" + rs.getInt("Quantity");

                filtered.add(new String[]{bookCode, title, author, year, availability});
            }

            refreshTable(filtered);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching books in the database: " + e.getMessage());
        }
    }

    private void showDeweyInfo() {
        JDialog dialog = new JDialog(this, "Dewey Decimal Classification", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(
                "Dewey Decimal Classification (DDC) organizes books by subject:\n\n"
                + "000-099: Computer Science, Information, and General Works\n"
                + "100-199: Philosophy and Psychology\n"
                + "200-299: Religion\n"
                + "300-399: Social sciences\n"
                + "400-499: Language\n"
                + "500-599: Pure sciences (mathematics, physics, chemistry, etc.)\n"
                + "600-699: Technology and applied sciences\n"
                + "700-799: Arts and recreation\n"
                + "800-899: Literature\n"
                + "900-999: History and geography"
        );
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnClose);

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_books = new javax.swing.JTable();
        txt_search = new javax.swing.JTextField();
        cmb_searchBy = new javax.swing.JComboBox<>();
        btn_search = new javax.swing.JButton();
        btn_clear = new javax.swing.JButton();
        btn_logout = new javax.swing.JButton();
        btn_profile = new javax.swing.JButton(); // New profile button
        lbl_title = new javax.swing.JLabel();
        JPanel topPanel = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Library Management System");

        // Dewey Information
        btn_deweyInfo = new javax.swing.JButton();
        btn_deweyInfo.setText("What is Dewey Class?");
        btn_deweyInfo.addActionListener(evt -> showDeweyInfo());
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(btn_deweyInfo);

        // Panel customization
        topPanel.setBackground(Color.RED);
        topPanel.setLayout(new BorderLayout());

        lbl_title.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 24));
        lbl_title.setForeground(Color.WHITE);
        lbl_title.setText("   Welcome User!");
        topPanel.add(lbl_title, BorderLayout.WEST);

        // Create right panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setBackground(Color.RED);

        // Profile button
        btn_profile.setText("Profile");
        btn_profile.addActionListener(evt -> showUserProfile());
        rightPanel.add(btn_profile);

        // Logout button
        btn_logout.setText("Log Out");
        btn_logout.addActionListener(evt -> {
            SessionManager.logout(); // Clear session
            JOptionPane.showMessageDialog(null, "Logging out...");
            new LoginForm().setVisible(true);
            this.dispose();
        });
        rightPanel.add(btn_logout);

        topPanel.add(rightPanel, BorderLayout.EAST);

        // Updated table model for new schema
        tbl_books = new JTable(new DefaultTableModel(
                new String[]{"Book Code", "Title", "Author", "Year", "Available"}, 0));
        jScrollPane1.setViewportView(tbl_books);

        // Updated search options for new schema
        cmb_searchBy.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Title", "Author", "Year", "Book Code"}));

        btn_search.setText("Search");
        btn_search.addActionListener(evt -> searchBooks());

        btn_clear.setText("Clear");
        btn_clear.addActionListener(evt -> {
            txt_search.setText("");
            cmb_searchBy.setSelectedIndex(0);
            loadBooks();
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24)
                                .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmb_searchBy, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_clear)
                                .addContainerGap(60, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24)
                                .addComponent(infoPanel)
                                .addContainerGap(60, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24)
                                .addComponent(jScrollPane1)
                                .addGap(24))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmb_searchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btn_search)
                                        .addComponent(btn_clear))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addGap(24))
        );

        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Library_User().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton btn_logout;
    private javax.swing.JButton btn_profile; // New profile button
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_clear;
    private javax.swing.JComboBox<String> cmb_searchBy;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_title;
    private javax.swing.JTable tbl_books;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTable tbl_users;
    private javax.swing.JButton btn_deweyInfo;
}
