package Library_System;

import java.sql.*;
import javax.swing.*;

public class ForgotPassword extends javax.swing.JFrame {

    Connect dbConnect = new Connect();

    public ForgotPassword() {
        initComponents();
        dbConnect.DoConnect();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        adminId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        newPass = new javax.swing.JPasswordField();
        btnReset = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 0, 0));
        jLabel1.setFont(new java.awt.Font("Stencil", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Admin Password Reset");
        jPanel1.add(jLabel1);
        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 680, 60));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Admin ID:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));
        jPanel2.add(adminId, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 200, -1));

        jLabel4.setText("New Password:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, -1, -1));
        jPanel2.add(newPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 200, -1));

        btnReset.setText("Reset Password");
        btnReset.addActionListener(evt -> resetAdminPassword());
        jPanel2.add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 140, -1));

        btnBack.setText("Back");
        btnBack.addActionListener(evt -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
        jPanel2.add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 70, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 680, 240));

        pack();
        setLocationRelativeTo(null);
    }

    private void resetAdminPassword() {
        String id = adminId.getText().trim();
        String password = new String(newPass.getPassword());

        // Input validation
        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Admin ID format (exactly 11 digits)
        if (!isValidAdminId(id)) {
            JOptionPane.showMessageDialog(this, "Admin ID must be exactly 11 digits", "Invalid Admin ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Admin PIN Verification for security
        String pin = JOptionPane.showInputDialog(this, "Enter IT Department PIN Code:");
        if (pin == null || !pin.equals("1234")) {
            JOptionPane.showMessageDialog(this, "Invalid PIN. Password reset denied.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            long adminIdLong = Long.parseLong(id);
            
            // First check if admin exists
            if (!adminExists(adminIdLong)) {
                JOptionPane.showMessageDialog(this, "Admin ID not found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update admin password
            String query = "UPDATE Admins SET Password = ? WHERE AdminID = ?";
            PreparedStatement ps = dbConnect.con.prepareStatement(query);
            ps.setString(1, password);
            ps.setLong(2, adminIdLong);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Admin password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                ps.close();
                this.dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ps.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Admin ID must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQL Error in resetAdminPassword: " + e.getMessage());
        }
    }

    private boolean isValidAdminId(String adminId) {
        if (adminId == null || adminId.length() != 11) {
            return false;
        }
        
        // Check if all characters are digits
        for (char c : adminId.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        
        // Additional check for range constraint from database
        long id = Long.parseLong(adminId);
        return id >= 10000000000L && id <= 99999999999L;
    }

    private boolean adminExists(long adminId) {
        try {
            String query = "SELECT AdminID FROM Admins WHERE AdminID = ?";
            PreparedStatement ps = dbConnect.con.prepareStatement(query);
            ps.setLong(1, adminId);
            
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            
            rs.close();
            ps.close();
            return exists;
            
        } catch (SQLException e) {
            System.err.println("Error checking admin existence: " + e.getMessage());
            return false;
        }
    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField adminId;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnBack;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new ForgotPassword().setVisible(true);
        });
    }
}