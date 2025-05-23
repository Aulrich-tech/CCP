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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252"); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT C.BOOKID, C.BOOKTITLE, C.AUTHOR, G.GENRENAME, C.BOOKYR, S.STOCK " +
                "FROM CATALOG C " +
                "LEFT JOIN GENRES G ON C.GENRE = G.GENREID " +
                "LEFT JOIN STOCK S ON C.BOOKID = S.BOOKID")) {

            // Make sure your table model has the required columns including "Available"
            DefaultTableModel model = (DefaultTableModel) tbl_books.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("BOOKID"),
                    rs.getString("BOOKTITLE"),
                    rs.getString("AUTHOR"),
                    rs.getString("GENRENAME"),
                    rs.getInt("BOOKYR"),
                    rs.getInt("STOCK") // Add stock information
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Books Error: " + e.getMessage());
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
        int fieldIndex;
        String columnName;

        switch (fieldLabel) {
            case "Book ID":
                fieldIndex = 0;
                columnName = "bookid";
                break;
            case "Title":
                fieldIndex = 1;
                columnName = "booktitle";
                break;
            case "Author":
                fieldIndex = 2;
                columnName = "author";
                break;
            case "Genre":
                fieldIndex = 3;
                columnName = "genre";
                break;
            case "Yr":
                fieldIndex = 4;
                columnName = "bookyr";
                break;
            default:
                fieldIndex = 1;
                columnName = "booktitle";
                break;
        }

        ArrayList<String[]> filtered = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountt", "root", "@Isekan09172713252");

            String sql;
            if (columnName.equals("bookyr") || columnName.equals("bookid")) {
                // Handle numeric fields - convert to string for LIKE comparison
                sql = "SELECT * FROM CATALOG WHERE CAST(" + columnName + " AS CHAR(20)) LIKE ?";
            } else {
                // Regular string fields
                sql = "SELECT * FROM CATALOG WHERE LOWER(" + columnName + ") LIKE ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookId = rs.getString("bookid");
                String title = rs.getString("booktitle");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                String yr = String.valueOf(rs.getInt("bookyr"));
                filtered.add(new String[]{bookId, title, author, genre, yr});
            }

            rs.close();
            stmt.close();
            conn.close();

            refreshTable(filtered);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching books in the database.");
        }
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
        setTitle("Library Book Catalog");

        // Panel customization
        topPanel.setBackground(Color.RED);
        topPanel.setLayout(new BorderLayout());

        lbl_title.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 24));
        lbl_title.setForeground(Color.WHITE);
        lbl_title.setText("   Welcome User!");
        topPanel.add(lbl_title, BorderLayout.WEST);
        topPanel.add(btn_logout, BorderLayout.EAST);

        tbl_books = new JTable(new DefaultTableModel(
                new String[]{"ID", "Title", "Author", "Genre", "Year", "Available"}, 0));
        jScrollPane1.setViewportView(tbl_books);

        cmb_searchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Title", "Author", "Genre", "Yr", "Book ID"}));

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
                                .addGap(18)
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
}
