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
    }

    private void loadBooks() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT BookCode, BookTitle, Author,BookYear, Quantity, AvailableQuantity "
                + "FROM Books ORDER BY BookCode")) {

            // Make sure your table model has the required columns
            DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BookCode"),
                    rs.getString("BookTitle"),
                    rs.getString("Author"),
                    rs.getInt("BookYear"),
                    rs.getInt("AvailableQuantity") + "/" + rs.getInt("Quantity") // Show available/total
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Books Error: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Revised"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT UserID, Username, UserType, Course, Department, ContactNumber, Email "
                + "FROM Users ORDER BY UserID")) {

            // Assuming you have a users table - adjust table model as needed
            DefaultTableModel model = (DefaultTableModel) tbl_users.getModel(); // You'll need this table
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getLong("UserID"),
                    rs.getString("Username"),
                    rs.getString("UserType"),
                    rs.getString("Course") != null ? rs.getString("Course") : "N/A",
                    rs.getString("Department"),
                    rs.getString("ContactNumber") != null ? rs.getString("ContactNumber") : "N/A",
                    rs.getString("Email")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Users Error: " + e.getMessage());
        }
    }

    private void refreshTable(ArrayList<String[]> bookList) {
        DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
        model.setRowCount(0); // Clear existing rows
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
                // Handle numeric field - convert to string for LIKE comparison
                sql = "SELECT BookCode, BookTitle, Author,BookYear, Quantity, AvailableQuantity "
                        + "FROM Books WHERE CAST(" + columnName + " AS VARCHAR(20)) LIKE ?";
            } else {
                // Regular string fields - use UPPER for case-insensitive search in Derby
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

        // Close button
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
        lbl_title = new javax.swing.JLabel();
        JPanel topPanel = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Library Management System");
        //Dewey Information
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
        topPanel.add(btn_logout, BorderLayout.EAST);

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
            loadBooks(); // Reset to full catalog
        });

        btn_logout.setText("Log Out");
        btn_logout.addActionListener(evt -> {
            JOptionPane.showMessageDialog(null, "Logging out...");
            new LoginForm().setVisible(true);
            this.dispose();
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
