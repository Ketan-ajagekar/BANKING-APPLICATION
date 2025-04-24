package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Withdrawal extends JFrame implements ActionListener {

    private final String pin;
    private JTextField amountField;
    private JPasswordField pinField;
    private JButton withdrawButton, backButton;

    Withdrawal(String pin) {
        super("Bank Application");
        this.pin = pin;

        // Background Image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 1550, 830);
        add(background);

        // Labels
        JLabel maxLabel = new JLabel("MAXIMUM WITHDRAWAL IS RS.10,000");
        maxLabel.setForeground(Color.WHITE);
        maxLabel.setFont(new Font("System", Font.BOLD, 16));
        maxLabel.setBounds(460, 180, 700, 35);
        background.add(maxLabel);

        JLabel enterAmountLabel = new JLabel("PLEASE ENTER YOUR AMOUNT");
        enterAmountLabel.setForeground(Color.WHITE);
        enterAmountLabel.setFont(new Font("System", Font.BOLD, 16));
        enterAmountLabel.setBounds(460, 220, 400, 35);
        background.add(enterAmountLabel);

        amountField = new JTextField();
        amountField.setBackground(new Color(65, 125, 128));
        amountField.setForeground(Color.WHITE);
        amountField.setBounds(460, 260, 320, 25);
        amountField.setFont(new Font("Raleway", Font.BOLD, 22));
        background.add(amountField);

        JLabel pinLabel = new JLabel("PLEASE ENTER PIN");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("System", Font.BOLD, 16));
        pinLabel.setBounds(460, 300, 400, 35);
        background.add(pinLabel);

        pinField = new JPasswordField(15);
        pinField.setBackground(new Color(65, 125, 128));
        pinField.setForeground(Color.WHITE);
        pinField.setBounds(460, 340, 320, 25);
        pinField.setFont(new Font("Raleway", Font.BOLD, 22));
        background.add(pinField);

        // Buttons
        withdrawButton = new JButton("WITHDRAW");
        withdrawButton.setBounds(700, 380, 150, 35);
        withdrawButton.setBackground(new Color(65, 125, 128));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.addActionListener(this);
        background.add(withdrawButton);

        backButton = new JButton("BACK");
        backButton.setBounds(700, 430, 150, 35);
        backButton.setBackground(new Color(65, 125, 128));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        background.add(backButton);

        // Frame Settings
        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == withdrawButton) {
            handleWithdrawal();
        } else if (e.getSource() == backButton) {
            setVisible(false);
            new main_Class(pin);
        }
    }

    private void handleWithdrawal() {
        String amountText = amountField.getText().trim();
        String enteredPin = new String(pinField.getPassword()).trim();

        if (amountText.isEmpty() || enteredPin.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both Amount and PIN!");
            return;
        }

        try {
            int withdrawAmount = Integer.parseInt(amountText);
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive amount.");
                return;
            }
            if (withdrawAmount > 10000) {
                JOptionPane.showMessageDialog(null, "Maximum withdrawal limit is Rs. 10,000");
                return;
            }

            Connn connn = new Connn();
            Connection connection = connn.getConnection();

            // Step 1: Validate PIN and get card number
            String getCardQuery = "SELECT card_number FROM bank WHERE pin = ?";
            PreparedStatement cardStmt = connection.prepareStatement(getCardQuery);
            cardStmt.setString(1, enteredPin);
            ResultSet rs = cardStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Invalid PIN!");
                return;
            }

            String cardNumber = rs.getString("card_number");

            // Step 2: Calculate current balance
            String balanceQuery = "SELECT SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END) AS balance FROM bank WHERE card_number = ?";
            PreparedStatement balanceStmt = connection.prepareStatement(balanceQuery);
            balanceStmt.setString(1, cardNumber);
            ResultSet balanceResult = balanceStmt.executeQuery();

            int balance = 0;
            if (balanceResult.next()) {
                balance = balanceResult.getInt("balance");
            }

            // Step 3: Check balance
            if (balance < withdrawAmount) {
                JOptionPane.showMessageDialog(null, "Insufficient Balance. Your balance is Rs. " + balance);
                return;
            }

            // Step 4: Insert withdrawal record
            String insertQuery = "INSERT INTO bank (pin, card_number, type, amount) VALUES (?, ?, 'Withdrawal', ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, enteredPin);
            insertStmt.setString(2, cardNumber);
            insertStmt.setInt(3, withdrawAmount);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Withdrawal Successful!\nWithdrawn: Rs. " + withdrawAmount);

            setVisible(false);
            new main_Class(enteredPin);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Transaction failed! Please try again.");
        }
    }

    public static void main(String[] args) {
        new Withdrawal("");
    }
}