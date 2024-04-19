import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class ResetPasswordFrame extends JFrame {
    private JTextField usernameField;
    private JTextField otpField;
    private JPasswordField newPasswordField;
    private JButton sendOtpButton;
    private JButton resetPasswordButton;

    private String generatedOTP;
    private String username;

    public ResetPasswordFrame() {
        setTitle("Reset Password");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10)); // GridLayout with spacing

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel otpLabel = new JLabel("OTP:");
        otpField = new JTextField();
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordField = new JPasswordField();
        sendOtpButton = new JButton("Send OTP");
        resetPasswordButton = new JButton("Reset Password");

        sendOtpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                if (!username.isEmpty()) {
                    generatedOTP = generateOTP();
                    // Simulate sending OTP (display it in console for testing)
                    System.out.println("OTP Sent: " + generatedOTP);
                    otpField.setEditable(true); // Make OTP field editable
                    otpField.requestFocusInWindow(); // Set focus to OTP field
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter username!");
                }
            }
        });

        resetPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String enteredOTP = otpField.getText();
                String newPassword = String.valueOf(newPasswordField.getPassword());

                if (enteredOTP.equals(generatedOTP)) {
                    if (!newPassword.isEmpty()) {
                        if (resetPassword(username, newPassword)) {
                            JOptionPane.showMessageDialog(null, "Password reset successful!");
                            dispose(); // Close the reset password frame
                        } else {
                            JOptionPane.showMessageDialog(null, "Password reset failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a new password!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid OTP!");
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(otpLabel);
        panel.add(otpField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(sendOtpButton);
        panel.add(resetPasswordButton);

        add(panel);
    }

    private String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private boolean resetPassword(String username, String newPassword) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Update user's password in the database
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0; // Return true if password reset successful
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ResetPasswordFrame resetPasswordFrame = new ResetPasswordFrame();
                resetPasswordFrame.setVisible(true);
            }
        });
    }
}
