import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {
    private JLabel label1, label2, label3;
    private JTextField textField2;
    private JPasswordField passwordField3;
    private JButton button1, button2, button3, button4, button5; // New button for login using Card Number and PIN

    public LoginFrame() {
        super("Credit Card Management System");

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

        label1 = new JLabel("CREDIT CARD");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("AvantGarde", Font.BOLD, 38));
        label1.setBounds(280, 125, 450, 40);
        add(label1);

        label2 = new JLabel("Username: ");
        label2.setFont(new Font("Ralway", Font.BOLD, 28));
        label2.setForeground(Color.WHITE);
        label2.setBounds(150, 190, 375, 30);
        add(label2);

        textField2 = new JTextField(15);
        textField2.setBounds(325, 190, 230, 30);
        textField2.setFont(new Font("Arial", Font.BOLD, 14));
        add(textField2);

        label3 = new JLabel("Password: ");
        label3.setFont(new Font("Ralway", Font.BOLD, 28));
        label3.setForeground(Color.WHITE);
        label3.setBounds(150, 250, 375, 30);
        add(label3);

        passwordField3 = new JPasswordField(15);
        passwordField3.setBounds(325, 250, 230, 30);
        passwordField3.setFont(new Font("Arial", Font.BOLD, 14));
        add(passwordField3);

        button1 = new JButton("SIGN IN");
        button1.setFont(new Font("Arial", Font.BOLD, 14));
        button1.setForeground(Color.WHITE);
        button1.setBackground(Color.BLACK);
        button1.setBounds(300, 300, 100, 30);
        button1.addActionListener(this);
        add(button1);

        button2 = new JButton("CLEAR");
        button2.setFont(new Font("Arial", Font.BOLD, 14));
        button2.setForeground(Color.WHITE);
        button2.setBackground(Color.BLACK);
        button2.setBounds(430, 300, 100, 30);
        button2.addActionListener(this);
        add(button2);

        button3 = new JButton("SIGN UP");
        button3.setFont(new Font("Arial", Font.BOLD, 14));
        button3.setForeground(Color.WHITE);
        button3.setBackground(Color.BLACK);
        button3.setBounds(300, 340, 230, 30);
        button3.addActionListener(this);
        add(button3);

        button5 = new JButton("RESET PASSWORD");
        button5.setFont(new Font("Arial", Font.BOLD, 14));
        button5.setForeground(Color.WHITE);
        button5.setBackground(Color.BLACK);
        button5.setBounds(300, 380, 230, 30);
        button5.addActionListener(this);
        add(button5);

        button4 = new JButton("SIGN IN WITH CARD");
        button4.setFont(new Font("Arial", Font.BOLD, 14));
        button4.setForeground(Color.WHITE);
        button4.setBackground(Color.BLACK);
        button4.setBounds(300, 420, 230, 30);
        button4.addActionListener(this);
        add(button4);

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
            if (ae.getSource() == button1) {
                // Handle login action with username and password
                String username = textField2.getText();
                String password = String.valueOf(passwordField3.getPassword());

                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    DashboardFrame dashboardFrame = new DashboardFrame(username);
                    dashboardFrame.setVisible(true);
                    dispose(); // Close the login frame
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password!");
                }
            } else if (ae.getSource() == button4) {
                // Handle login action with card number and PIN
                CardFrame cardFrame = new CardFrame();
                cardFrame.setVisible(true);
                dispose(); // Close the login frame
            } else if (ae.getSource() == button2) {
                // Handle clear action
                textField2.setText("");
                passwordField3.setText("");
            } else if (ae.getSource() == button3) {
                // Handle signup action
                SignupFrame signupFrame = new SignupFrame();
                signupFrame.setVisible(true);
                dispose(); // Close the login frame
            } else if (ae.getSource() == button5) {
                // Handle signup action
                ResetPasswordFrame Resetpassword = new ResetPasswordFrame();
                Resetpassword.setVisible(true);
                dispose(); // Close the login frame
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate(String username, String password) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If result set is not empty, authentication successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
