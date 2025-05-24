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
            // Derby database connection string
            String host = "jdbc:derby://localhost:1527/Revised";
            
            // For Derby, you can either:
            // Option 1: Use default credentials (empty strings)
            String uName = "";
            String uPass = "";
            
            // Option 2: Or use specific Derby credentials if you set them up
            // String uName = "app";  // Default Derby user
            // String uPass = "app";  // Default Derby password
            
            // Try connection with empty credentials first
            try {
                con = DriverManager.getConnection(host, uName, uPass);
            } catch (SQLException e) {
                // If empty credentials fail, try with default Derby credentials
                System.out.println("Trying with default Derby credentials...");
                con = DriverManager.getConnection(host, "app", "app");
            }
            
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            // Test the connection by running a simple query
            String sql = "SELECT COUNT(*) FROM Users";
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                System.out.println("Database connection to 'Revised' successful");
                System.out.println("Total users in database: " + rs.getInt(1));
            }
            
        } catch (SQLException err) {
            System.err.println("SQL Error Details:");
            System.err.println("Error Code: " + err.getErrorCode());
            System.err.println("SQL State: " + err.getSQLState());
            System.err.println("Message: " + err.getMessage());
            
            // More specific error handling
            if (err.getMessage().contains("User id length")) {
                JOptionPane.showMessageDialog(Connect.this, 
                    "Database Connection Error: Invalid credentials.\n" +
                    "Please check your Derby database setup.\n" +
                    "Error: " + err.getMessage(), 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
            } else if (err.getMessage().contains("Connection refused") || err.getMessage().contains("could not listen")) {
                JOptionPane.showMessageDialog(Connect.this, 
                    "Database Connection Error: Cannot connect to Derby server.\n" +
                    "Please ensure Derby server is running on localhost:1527\n" +
                    "Error: " + err.getMessage(), 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Connect.this, 
                    "Database Connection Error: " + err.getMessage(), 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            // Don't exit immediately, allow user to retry
            // System.exit(0);
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
            String sql = "SELECT * FROM Users";
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                "Error refreshing database connection: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean userExists(long userId) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM Users WHERE UserID = ?");
            pstmt.setLong(1, userId);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public long getNextUserId() {
        long nextId = 10000000000L; // Start from the minimum UserID constraint
        try {
            Statement st = con.createStatement();
            ResultSet result = st.executeQuery("SELECT MAX(UserID) FROM Users");
            if (result.next()) {
                long max = result.getLong(1);
                if (max > 0) {
                    nextId = max + 1;
                }
            }
            result.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nextId;
    }
    
    // Method to test database connection
    public boolean testConnection() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException ex) {
            return false;
        }
    }
    
    // Method to close connection properly
    public void closeConnection() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
            System.out.println("Database connection closed successfully");
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}