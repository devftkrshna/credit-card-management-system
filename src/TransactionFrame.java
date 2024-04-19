import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TransactionFrame extends JFrame {
    private JTable transactionTable;
    private String username;

    public TransactionFrame(String username) {
        this.username = username;

        setTitle("Transaction History");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background Image
        ImageIcon backgroundImage = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 800, 600);
        add(backgroundLabel);

        // Panel with spacing
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245, 200)); // Set panel background color with alpha for transparency

        // Title label with modern font, dark text, and centered alignment
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20)); // Modern font
        titleLabel.setForeground(new Color(52, 52, 52)); // Dark text
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Adjust padding
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Transaction ID");
        model.addColumn("Source");
        model.addColumn("Debit");
        model.addColumn("Credit");
        model.addColumn("Date");

        // Table with custom renderer for alternating row colors
        transactionTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(SwingConstants.LEFT); // Left align text
                    if (row % 2 == 0) {
                        label.setBackground(new Color(250, 250, 250)); // Alternate row color
                    } else {
                        label.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        transactionTable.setFillsViewportHeight(true);
        transactionTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14)); // Modern font
        transactionTable.setFont(new Font("Roboto", Font.PLAIN, 12)); // Modern font
        transactionTable.setRowHeight(25);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Table header with light background, dark text, and modern font
        JTableHeader header = transactionTable.getTableHeader();
        header.setBackground(new Color(232, 232, 232)); // Light header background
        header.setForeground(Color.BLACK);
        // Modern font
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adjust padding

        // Scroll pane with removed border
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        backgroundLabel.add(panel);

        // Set panel location
        panel.setBounds(100, 100, 600, 400);

        // Load transaction history
        loadTransactionHistory(username);
    }

    private void loadTransactionHistory(String username) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Retrieve transaction history for the user
            PreparedStatement statement = connection.prepareStatement("SELECT transaction_id, source1, debit, credit, transaction_date FROM transactions WHERE user_id = (SELECT user_id FROM users WHERE username = ?)");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            // Add rows to the table model
            DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
            while (resultSet.next()) {
                String transactionId = resultSet.getString("transaction_id");
                String source = resultSet.getString("source1");
                double debit = resultSet.getDouble("debit");
                double credit = resultSet.getDouble("credit");
                String transactionDate = resultSet.getString("transaction_date");

                model.addRow(new Object[]{transactionId, source, debit, credit, transactionDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TransactionFrame transactionFrame = new TransactionFrame("TestUser");
            transactionFrame.setVisible(true);
        });
    }
}
