package Library_System;

import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Connect extends javax.swing.JFrame {

    public Connection con;
    public Statement stmt;
    ResultSet rs;
    DefaultTableModel LoginModel = new DefaultTableModel();

    public void DoConnect() {
        try {
            // MySQL connection string
            String host = "jdbc:mysql://localhost:3306/accountt";
            String uName = "root"; // Replace with your MySQL username
            String uPass = "@Isekan09172713252"; // Replace with your MySQL password

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            con = DriverManager.getConnection(host, uName, uPass);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM ACCOUNT";
            rs = stmt.executeQuery(sql);
            System.out.println("MySQL database connection successful");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(Connect.this, "MySQL JDBC Driver not found: " + e.getMessage(), "Driver Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(Connect.this, "Database Connection Error: " + err.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void Refresh_RS_STMT() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM ACCOUNT";
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error refreshing database connection: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void closeConnection() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
            System.out.println("Database connection closed");
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean userExists(int userId) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM ACCOUNT WHERE USERID = ?");
            pstmt.setInt(1, userId);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getNextUserId() {
        int nextId = 1;
        try {
            Statement st = con.createStatement();
            ResultSet result = st.executeQuery("SELECT MAX(USERID) FROM ACCOUNT");
            if (result.next()) {
                nextId = result.getInt(1) + 1;
            }
            result.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nextId;
    }

}
