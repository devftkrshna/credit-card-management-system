import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private JLabel nameLabel;
    private JLabel userDetailsLabel;
    private JLabel availableCreditLabel;
    private JButton payButton;
    private JButton transactionButton;
    private JButton refreshButton; // New refresh button
    private JButton logoutButton; // New logout button

    private String username;

    public DashboardFrame(String username) {
        this.username = username;

        setTitle("Dashboard");
        setSize(850, 480); // Set size to match LoginFrame
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background Image
        ImageIcon backgroundImage = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 850, 480);
        add(backgroundLabel);

        // Bank Icon
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image bankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel bankImageLabel = new JLabel(new ImageIcon(bankImage));
        bankImageLabel.setBounds(350, 10, 100, 100);
        backgroundLabel.add(bankImageLabel);

        // Label for Welcome message
        nameLabel = new JLabel("Welcome, " + username);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, 150, 300, 30);
        backgroundLabel.add(nameLabel);

        // Label for User Details
        userDetailsLabel = new JLabel();
        userDetailsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userDetailsLabel.setForeground(Color.WHITE);
        userDetailsLabel.setBounds(20, 200, 800, 90); // Increased height to display details on separate lines
        backgroundLabel.add(userDetailsLabel);

        // Label for Available Credit
        availableCreditLabel = new JLabel();
        availableCreditLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        availableCreditLabel.setForeground(Color.WHITE);
        availableCreditLabel.setBounds(20, 290, 300, 30);
        backgroundLabel.add(availableCreditLabel);

        // Pay Button
        payButton = new JButton("Pay");
        payButton.setFont(new Font("Arial", Font.BOLD, 16));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(Color.BLACK);
        payButton.setBounds(20, 340, 100, 30);
        backgroundLabel.add(payButton);

        // Transaction Button
        transactionButton = new JButton("Transactions");
        transactionButton.setFont(new Font("Arial", Font.BOLD, 16));
        transactionButton.setForeground(Color.WHITE);
        transactionButton.setBackground(Color.BLACK);
        transactionButton.setBounds(140, 340, 150, 30);
        backgroundLabel.add(transactionButton);

        // Refresh Button
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(Color.BLACK);
        refreshButton.setBounds(300, 340, 100, 30);
        backgroundLabel.add(refreshButton);

        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(Color.BLACK);
        logoutButton.setBounds(420, 340, 100, 30);
        backgroundLabel.add(logoutButton);

        // ActionListener for Pay Button
        payButton.addActionListener(e -> {
            // Open payment frame
            PaymentFrame paymentFrame = new PaymentFrame(username);
            paymentFrame.setVisible(true);
        });

        // ActionListener for Transaction Button
        transactionButton.addActionListener(e -> {
            // Open transactions frame
            TransactionFrame transactionFrame = new TransactionFrame(username);
            transactionFrame.setVisible(true);
        });

        // ActionListener for Refresh Button
        refreshButton.addActionListener(e -> updateUserDetailsAndCredit(username));

        // ActionListener for Logout Button
        logoutButton.addActionListener(e -> {
            // Close the DashboardFrame
            dispose();
            // Open the LoginFrame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        // Retrieve and display user details, available credit, and credit limit
        updateUserDetailsAndCredit(username);
    }

    private void updateUserDetailsAndCredit(String username) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Retrieve user details
            PreparedStatement userDetailsStatement = connection.prepareStatement("SELECT name, email, address, phone FROM users WHERE username = ?");
            userDetailsStatement.setString(1, username);
            ResultSet userDetailsResultSet = userDetailsStatement.executeQuery();

            if (userDetailsResultSet.next()) {
                String name = userDetailsResultSet.getString("name");
                String email = userDetailsResultSet.getString("email");
                String address = userDetailsResultSet.getString("address");
                String phone = userDetailsResultSet.getString("phone");
                userDetailsLabel.setText("<html>Name: " + name + "<br>Email: " + email + "<br>Address: " + address + "<br>Phone: " + phone + "</html>");
            }

            // Retrieve available credit from the credit_cards table
            PreparedStatement creditStatement = connection.prepareStatement("SELECT credit_limit FROM credit_cards cc INNER JOIN users u ON cc.user_id = u.user_id WHERE u.username = ?");
            creditStatement.setString(1, username);
            ResultSet creditResultSet = creditStatement.executeQuery();

            if (creditResultSet.next()) {
                double creditLimit = creditResultSet.getDouble("credit_limit");
                availableCreditLabel.setText("Credit Limit: $" + String.format("%.2f", creditLimit));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboardFrame = new DashboardFrame("TestUser");
            dashboardFrame.setVisible(true);
        });
    }
}
