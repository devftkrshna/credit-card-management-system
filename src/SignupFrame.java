import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField phoneNumberField;
    private JTextField addressField;
    private JTextField nameField;
    private JTextField emailField;

    public SignupFrame() {
        setTitle("Signup");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JButton signupButton = new JButton("Signup");

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String phoneNumber = phoneNumberField.getText();
                String address = addressField.getText();
                String name = nameField.getText();
                String email = emailField.getText();

                if (signup(username, password, phoneNumber, address, name, email)) {
                    JOptionPane.showMessageDialog(null, "Signup successful!");
                    dispose(); // Close the SignupFrame
                } else {
                    JOptionPane.showMessageDialog(null, "Signup failed!");
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(phoneNumberLabel);
        panel.add(phoneNumberField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(signupButton);

        add(panel);
    }

    private boolean signup(String username, String password, String phoneNumber, String address, String name, String email) {
    try {
        // Generate a 16-digit credit card number
        String creditCardNumber = generateCreditCardNumber();
        // Generate a random 4-digit pin for the credit card
        String pin = generatePin();

        // Connect to the database
        Connection connection = DatabaseConnection.getConnection();

        // Insert user's information into the users table
        PreparedStatement userStatement = connection.prepareStatement(
            "INSERT INTO users (username, password, name, email, address, phone) VALUES (?, ?, ?, ?, ?, ?)");
        userStatement.setString(1, username);
        userStatement.setString(2, password);
        userStatement.setString(3, name);
        userStatement.setString(4, email);
        userStatement.setString(5, address);
        userStatement.setString(6, phoneNumber);
        userStatement.executeUpdate();

        // Get the user_id of the newly inserted user
        PreparedStatement getUserIdStatement = connection.prepareStatement(
            "SELECT user_id FROM users WHERE username = ?");
        getUserIdStatement.setString(1, username);
        ResultSet userIdResult = getUserIdStatement.executeQuery();
        int userId = 0;
        if (userIdResult.next()) {
            userId = userIdResult.getInt("user_id");
        }

        // Insert credit card details into the credit_cards table
        PreparedStatement creditCardStatement = connection.prepareStatement(
            "INSERT INTO credit_cards (user_id, card_number, pin, credit_limit) VALUES (?, ?, ?, ?)");
        creditCardStatement.setInt(1, userId);
        creditCardStatement.setString(2, creditCardNumber);
        creditCardStatement.setString(3, pin);
        creditCardStatement.setDouble(4, 50000.00);
        creditCardStatement.executeUpdate();

        // Display name, card number, and PIN in a dialog box
        JOptionPane.showMessageDialog(this,
                "Name: " + name + "\n" +
                "Card Number: " + creditCardNumber + "\n" +
                "PIN: " + pin,
                "Signup Successful", JOptionPane.INFORMATION_MESSAGE);

        // Dispose of the signup frame and return to the login frame
        dispose();
        return true;
    } catch (SQLException e) {
        // If SQLException occurs, it means the username already exists
        JOptionPane.showMessageDialog(this,
                "Username already exists. Please choose a different username.",
                "Signup Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}






    private String generateCreditCardNumber() {
        StringBuilder creditCardNumber = new StringBuilder();
        Random random = new Random();
        // Generate 16 random digits
        for (int i = 0; i < 16; i++) {
            int digit = random.nextInt(10);
            creditCardNumber.append(digit);
        }
        return creditCardNumber.toString();
    }
    private String generatePin() {
    // Generate a random 4-digit PIN
    Random random = new Random();
    int pin = 1000 + random.nextInt(9000);
    return String.valueOf(pin);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SignupFrame signupFrame = new SignupFrame();
                signupFrame.setVisible(true);
            }
        });
    }
}
