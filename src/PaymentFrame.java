import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.math.BigInteger;
import java.util.Random;

public class PaymentFrame extends JFrame {
     private JTextField recipientField;
    private JTextField amountField;
    private JButton payButton;
    private JButton backButton; // New back button

    private String username;

    public PaymentFrame(String username) {
        this.username = username;

        setTitle("Make Payment");
        setSize(400, 300); // Adjusted size to match DashboardFrame
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background Image
        ImageIcon backgroundImage = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 400, 300);
        add(backgroundLabel);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.setOpaque(false); // Set panel background to transparent

        JLabel recipientLabel = new JLabel("Recipient:");
        recipientLabel.setForeground(Color.WHITE); // Set label text color
        recipientField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.WHITE); // Set label text color
        amountField = new JTextField();
        payButton = new JButton("Pay");
        payButton.setForeground(Color.WHITE); // Set button text color
        payButton.setBackground(Color.BLACK); // Set button background color
        backButton = new JButton("Back");
        backButton.setForeground(Color.WHITE); // Set button text color
        backButton.setBackground(Color.BLACK); // Set button background color

        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String recipient = recipientField.getText();
                double amount = Double.parseDouble(amountField.getText());

                if (makePayment(recipient, amount)) {
                    JOptionPane.showMessageDialog(null, "Payment successful!");
                    dispose(); // Close the payment frame
                } else {
                    JOptionPane.showMessageDialog(null, "Payment failed!");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close the payment frame
                dispose();
            }
        });

        panel.add(recipientLabel);
        panel.add(recipientField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(backButton); // Add back button to panel
        panel.add(payButton);

        backgroundLabel.add(panel);

        // Set panel location
        panel.setBounds(50, 50, 300, 150);
    }

    private boolean makePayment(String recipient, double amount) {
    try {
        Connection connection = DatabaseConnection.getConnection();

        // Generate transaction id
        String transactionId = generateTransactionId();

        // Retrieve user's credit limit from the credit_cards table
        double creditLimit = getCreditLimit(username, connection);

        // Check if the user has sufficient available credit (credit limit)
        if (amount > creditLimit) {
            JOptionPane.showMessageDialog(null, "Exceeded credit limit!");
            return false;
        }

        // Deduct the amount from the user's credit limit
        PreparedStatement deductStatement = connection.prepareStatement("UPDATE credit_cards SET credit_limit = credit_limit - ? WHERE user_id = (SELECT user_id FROM users WHERE username = ?)");
        deductStatement.setDouble(1, amount);
        deductStatement.setString(2, username);
        deductStatement.executeUpdate();

        // Add the amount to the recipient's credit limit
        PreparedStatement addStatement = connection.prepareStatement("UPDATE credit_cards SET credit_limit = credit_limit + ? WHERE user_id = (SELECT user_id FROM users WHERE username = ?)");
        addStatement.setDouble(1, amount);
        addStatement.setString(2, recipient);
        addStatement.executeUpdate();

        // Log the transaction for the recipient
        PreparedStatement logStatementForRecipient = connection.prepareStatement("INSERT INTO transactions (transaction_id, source1, debit, credit, user_id, transaction_date) VALUES (?, ?, ?, ?, (SELECT user_id FROM users WHERE username = ?), CURRENT_TIMESTAMP)");
        logStatementForRecipient.setString(1, transactionId);
        logStatementForRecipient.setString(2, username); // Set source as sender
        logStatementForRecipient.setDouble(3, 0); // Since money is received, debit is 0
        logStatementForRecipient.setDouble(4, amount);
        logStatementForRecipient.setString(5, recipient);
        logStatementForRecipient.executeUpdate();

        // Log the transaction for the sender
        PreparedStatement logStatementForSender = connection.prepareStatement("INSERT INTO transactions (transaction_id, source1, debit, credit, user_id, transaction_date) VALUES (?, ?, ?, ?, (SELECT user_id FROM users WHERE username = ?), CURRENT_TIMESTAMP)");
        logStatementForSender.setString(1, transactionId);
        logStatementForSender.setString(2, recipient); // Set source as recipient
        logStatementForSender.setDouble(3, amount);
        logStatementForSender.setDouble(4, 0); // Since money is sent, credit is 0
        logStatementForSender.setString(5, username);
        logStatementForSender.executeUpdate();

        return true;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}


    // Method to retrieve user's credit limit from the credit_cards table
    private double getCreditLimit(String username, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT credit_limit FROM credit_cards WHERE user_id = (SELECT user_id FROM users WHERE username = ?)");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("credit_limit");
        }
        return 0; // Return 0 if no credit limit found
    }

    // Method to generate transaction id
    private String generateTransactionId() {
        // Generate a random 17-digit transaction id using BigInteger
        Random random = new Random();
        int transactionId = 100000000 + random.nextInt(900000000);
        return String.valueOf(transactionId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PaymentFrame paymentFrame = new PaymentFrame("TestUser");
                paymentFrame.setVisible(true);
            }
        });
    }
}
