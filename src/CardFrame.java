import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CardFrame extends JFrame implements ActionListener {
    private JLabel label1, label2;
    private JTextField cardNumberField;
    private JPasswordField pinField;
    private JButton loginButton, backButton;

    public CardFrame() {
        super("Card Login");

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image bankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon bankImageIcon = new ImageIcon(bankImage);
        JLabel bankImageLabel = new JLabel(bankImageIcon);
        bankImageLabel.setBounds(350, 10, 100, 100);
        add(bankImageLabel);

        ImageIcon cardIcon = new ImageIcon(ClassLoader.getSystemResource("icon/card.png"));
        Image cardImage = cardIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon cardImageIcon = new ImageIcon(cardImage);
        JLabel cardImageLabel = new JLabel(cardImageIcon);
        cardImageLabel.setBounds(630, 350, 100, 100);
        add(cardImageLabel);

        label1 = new JLabel("Card Number:");
        label1.setFont(new Font("Ralway", Font.BOLD, 28));
        label1.setForeground(Color.WHITE);
        label1.setBounds(150, 200, 200, 30);
        add(label1);

        cardNumberField = new JTextField(15);
        cardNumberField.setBounds(340, 201, 200, 30);
        cardNumberField.setFont(new Font("Arial", Font.BOLD, 14));
        add(cardNumberField);

        label2 = new JLabel("PIN:");
        label2.setFont(new Font("Ralway", Font.BOLD, 28));
        label2.setForeground(Color.WHITE);
        label2.setBounds(280, 250, 200, 30);
        add(label2);

        pinField = new JPasswordField(15);
        pinField.setBounds(340, 250, 200, 30);
        pinField.setFont(new Font("Arial", Font.BOLD, 14));
        add(pinField);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.setBounds(280, 300, 100, 30);
        loginButton.addActionListener(this);
        add(loginButton);

        backButton = new JButton("BACK");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.BLACK);
        backButton.setBounds(400, 300, 100, 30);
        backButton.addActionListener(this);
        add(backButton);

        ImageIcon backgroundImage = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        Image backgroundImageImage = backgroundImage.getImage().getScaledInstance(850, 480, Image.SCALE_DEFAULT);
        ImageIcon backgroundImageIcon = new ImageIcon(backgroundImageImage);
        JLabel backgroundImageLabel = new JLabel(backgroundImageIcon);
        backgroundImageLabel.setBounds(0, 0, 850, 480);
        add(backgroundImageLabel);

        setLayout(null);
        setSize(850, 480);
        setLocationRelativeTo(null); // Center the window on the screen
        setUndecorated(true);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == loginButton) {
                // Handle login action with card number and PIN
                String cardNumber = cardNumberField.getText();
                String pin = String.valueOf(pinField.getPassword());

                String username = authenticate(cardNumber, pin);
                if (username != null) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    DashboardFrame dashboardFrame = new DashboardFrame(username);
                    dashboardFrame.setVisible(true);
                    dispose(); // Close the card login frame
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid card number or PIN!");
                }
            } else if (ae.getSource() == backButton) {
                // Handle back action
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose(); // Close the card login frame
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String authenticate(String cardNumber, String pin) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM users join credit_cards on credit_cards.user_id=users.user_id WHERE credit_cards.card_number = ? AND credit_cards.pin = ?");
            statement.setString(1, cardNumber);
            statement.setString(2, pin);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CardFrame cardFrame = new CardFrame();
                cardFrame.setVisible(true);
            }
        });
    }
}
