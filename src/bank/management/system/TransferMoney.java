package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TransferMoney extends JFrame implements ActionListener {
    JTextField txtPayeeCardNumber, txtAmount;
    JPasswordField txtPin;
    JButton btnTransfer, btnBack;
    String pin, senderCard;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Ketan@01114";

    public TransferMoney(String pin) {
        super("Bank Application");
        this.pin = pin;

        setLayout(null);

        // Background Image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel l3 = new JLabel(new ImageIcon(i2));
        l3.setBounds(0, 0, 1550, 830);
        add(l3);

        JLabel lblTitle = new JLabel("TRANSFER MONEY");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setBounds(520, 180, 400, 35);
        l3.add(lblTitle);

        JLabel lblPayeeCardNumber = new JLabel("Payee Card Number:");
        lblPayeeCardNumber.setForeground(Color.WHITE);
        lblPayeeCardNumber.setFont(new Font("Arial", Font.BOLD, 16));
        lblPayeeCardNumber.setBounds(410, 274, 180, 25);
        l3.add(lblPayeeCardNumber);

        txtPayeeCardNumber = new JTextField();
        txtPayeeCardNumber.setBackground(new Color(65, 125, 128));
        txtPayeeCardNumber.setForeground(Color.WHITE);
        txtPayeeCardNumber.setBounds(640, 274, 200, 25);
        txtPayeeCardNumber.setFont(new Font("Raleway", Font.BOLD, 18));
        l3.add(txtPayeeCardNumber);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setBounds(410, 318, 150, 35);
        lblAmount.setFont(new Font("Arial", Font.BOLD, 16));
        l3.add(lblAmount);

        txtAmount = new JTextField();
        txtAmount.setBackground(new Color(65, 125, 128));
        txtAmount.setForeground(Color.WHITE);
        txtAmount.setBounds(640, 320, 200, 25);
        txtAmount.setFont(new Font("Raleway", Font.BOLD, 18));
        l3.add(txtAmount);

        JLabel lblPin = new JLabel("Enter Your Card PIN:");
        lblPin.setForeground(Color.WHITE);
        lblPin.setBounds(410, 362, 200, 35);
        lblPin.setFont(new Font("Arial", Font.BOLD, 16));
        l3.add(lblPin);

        txtPin = new JPasswordField();
        txtPin.setBackground(new Color(65, 125, 128));
        txtPin.setForeground(Color.WHITE);
        txtPin.setBounds(640, 370, 200, 25);
        txtPin.setFont(new Font("Raleway", Font.BOLD, 18));
        l3.add(txtPin);

        btnTransfer = new JButton("Transfer Money");
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setBackground(new Color(65, 125, 128));
        btnTransfer.setBounds(410, 410, 150, 35);
        btnTransfer.addActionListener(this);
        l3.add(btnTransfer);

        btnBack = new JButton("Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(65, 125, 128));
        btnBack.setBounds(700, 410, 150, 35);
        btnBack.addActionListener(this);
        l3.add(btnBack);

        setSize(1550, 1080);
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        fetchSenderCard();
    }

    private void fetchSenderCard() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement("SELECT card_number FROM login WHERE pin = ?");
            ps.setString(1, pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                senderCard = rs.getString("card_number");
            } else {
                showMessageAndDispose("Sender card not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Error fetching sender card details!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTransfer) {
            transferMoney();
        } else if (e.getSource() == btnBack) {
            setVisible(false);
            new main_Class(pin);
        }
    }

    private void transferMoney() {
        String payeeCard = txtPayeeCardNumber.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String enteredPin = new String(txtPin.getPassword()).trim();

        if (payeeCard.isEmpty() || amountStr.isEmpty() || enteredPin.isEmpty()) {
            showMessage("Please enter all details!");
            return;
        }

        if (!enteredPin.equals(pin)) {
            showMessage("Incorrect PIN!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                showMessage("Enter a valid amount!");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Invalid amount entered!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            // 1. Get Sender Balance
            int senderBalance = getSenderBalance(conn);
            if (senderBalance < amount) {
                showMessage("Insufficient balance!");
                return;
            }

            // 2. Check if Payee Exists
            if (!checkPayeeExists(conn, payeeCard)) {
                showMessage("Payee card not found!");
                return;
            }

            // 3. Perform Transaction
            performTransaction(conn, amount, payeeCard);

            conn.commit();
            showMessage("Rs. " + amount + " transferred successfully!");
            setVisible(false);
            new main_Class(pin);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessage("Transaction failed!");
        }
    }

    private int getSenderBalance(Connection conn) throws SQLException {
        PreparedStatement senderStmt = conn.prepareStatement(
                "SELECT SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END) AS balance FROM bank WHERE card_number = ?"
        );
        senderStmt.setString(1, senderCard);
        ResultSet rsSender = senderStmt.executeQuery();
        return rsSender.next() ? rsSender.getInt("balance") : 0;
    }

    private boolean checkPayeeExists(Connection conn, String payeeCard) throws SQLException {
        PreparedStatement checkPayee = conn.prepareStatement("SELECT card_number FROM login WHERE card_number = ?");
        checkPayee.setString(1, payeeCard);
        ResultSet rsPayee = checkPayee.executeQuery();
        return rsPayee.next();
    }

    // Helper method to retrieve the payee's PIN from the login table
    private String getPayeePin(String payeeCard) {
        String payeePin = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT pin FROM login WHERE card_number = ?");
            stmt.setString(1, payeeCard);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                payeePin = rs.getString("pin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payeePin;
    }

    private void performTransaction(Connection conn, int amount, String payeeCard) throws SQLException {
        // Withdraw from sender's account
        PreparedStatement withdraw = conn.prepareStatement(
                "INSERT INTO bank (pin, card_number, date, type, amount) VALUES (?, ?, NOW(), 'Withdrawal', ?)"
        );
        withdraw.setString(1, pin);
        withdraw.setString(2, senderCard);
        withdraw.setInt(3, amount);
        withdraw.executeUpdate();

        // Deposit to payee's account (using their actual PIN)
        String payeePin = getPayeePin(payeeCard);
        if (payeePin.isEmpty()) {
            throw new SQLException("Payee PIN not found");
        }
        PreparedStatement deposit = conn.prepareStatement(
                "INSERT INTO bank (pin, card_number, date, type, amount) VALUES (?, ?, NOW(), 'Deposit', ?)"
        );
        deposit.setString(1, payeePin);
        deposit.setString(2, payeeCard);
        deposit.setInt(3, amount);
        deposit.executeUpdate();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void showMessageAndDispose(String message) {
        showMessage(message);
        dispose();
    }

    public static void main(String[] args) {
        new TransferMoney("your_pin_here"); // Replace with actual PIN as needed
    }
}
