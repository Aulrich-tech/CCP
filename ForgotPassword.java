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
        userId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        userType = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        newPass = new javax.swing.JPasswordField();
        btnReset = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 0, 0));
        jLabel1.setFont(new java.awt.Font("Stencil", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Forgot Password");
        jPanel1.add(jLabel1);
        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 680, 60));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("UserID:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));
        jPanel2.add(userId, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 200, -1));

        jLabel3.setText("User Type:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, -1, -1));

        userType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user" }));
        jPanel2.add(userType, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 200, -1));

        jLabel4.setText("New Password:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));
        jPanel2.add(newPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 200, -1));

        btnReset.setText("Reset Password");
        btnReset.addActionListener(evt -> resetPassword());
        jPanel2.add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 140, -1));

        btnBack.setText("Back");
        btnBack.addActionListener(evt -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
        jPanel2.add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 160, 70, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 680, 240));

        pack();
        setLocationRelativeTo(null);
    }

    private void resetPassword() {
        String id = userId.getText().trim();
        String type = userType.getSelectedItem().toString();
        String password = new String(newPass.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            long uid = Long.parseLong(id);
            String query = "UPDATE ACCOUNT SET PASSWORD = ? WHERE USERID = ? AND USERTYPE = ?";
            PreparedStatement ps = dbConnect.con.prepareStatement(query);
            ps.setString(1, password);
            ps.setLong(2, uid);
            ps.setString(3, type);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User not found or type mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "UserID must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField userId;
    private javax.swing.JComboBox<String> userType;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnBack;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new ForgotPassword().setVisible(true);
        });
    }
}
