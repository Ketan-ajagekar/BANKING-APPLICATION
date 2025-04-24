
package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FastCash extends JFrame implements ActionListener {

    JButton[] amountButtons = new JButton[6];
    JButton backButton;
    String pin;

    FastCash(String pin) {
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 1550, 830);
        add(background);

        JLabel label = new JLabel("SELECT WITHDRAWAL AMOUNT");
        label.setBounds(445, 180, 700, 35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System", Font.BOLD, 23));
        background.add(label);

        String[] amounts = {"100", "500", "1000", "2000", "5000", "10000"};
        int x1 = 410, x2 = 700, y = 274;
        for (int i = 0; i < amounts.length; i++) {
            JButton btn = new JButton("Rs. " + amounts[i]);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(65, 125, 128));
            btn.setBounds(i % 2 == 0 ? x1 : x2, y, 150, 35);
            btn.addActionListener(this);
            amountButtons[i] = btn;
            background.add(btn);
            if (i % 2 != 0) y += 44;
        }

        backButton = new JButton("BACK");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(65, 125, 128));
        backButton.setBounds(x2, y, 150, 35);
        backButton.addActionListener(this);
        background.add(backButton);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            setVisible(false);
            new main_Class(pin);
            return;
        }

        String amountStr = ((JButton) e.getSource()).getText().replace("Rs. ", "").trim();
        int withdrawAmount = Integer.parseInt(amountStr);

        try {
            Connn connn = new Connn();
            Connection connection = connn.getConnection();

            // Step 1: Validate PIN and Get Card Number
            String cardNumber = null;
            String pinQuery = "SELECT card_number FROM bank WHERE pin = ?";
            try (PreparedStatement stmt = connection.prepareStatement(pinQuery)) {
                stmt.setString(1, pin);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        cardNumber = rs.getString("card_number");
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid PIN!");
                        return;
                    }
                }
            }

            // Step 2: Get Current Balance
            int balance = 0;
            String balanceQuery = "SELECT SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END) AS balance FROM bank WHERE card_number = ?";
            try (PreparedStatement stmt = connection.prepareStatement(balanceQuery)) {
                stmt.setString(1, cardNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getInt("balance");
                    }
                }
            }

            // Step 3: Check balance before withdrawal
            if (balance < withdrawAmount) {
                JOptionPane.showMessageDialog(null, "Insufficient Balance");
                return;
            }

            // Step 4: Record Withdrawal
            String insertQuery = "INSERT INTO bank (pin, card_number, type, amount) VALUES (?, ?, 'Withdrawal', ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, pin);
                stmt.setString(2, cardNumber);
                stmt.setInt(3, withdrawAmount);
                stmt.executeUpdate();
            }

            // Step 5: Show success message
            int remainingBalance = balance - withdrawAmount;
            JOptionPane.showMessageDialog(null, "Withdrawal Successful!\nWithdrawn: Rs. " + withdrawAmount);

            // Redirect to main screen
            setVisible(false);
            new main_Class(pin);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Transaction failed!");
        }
    }

    public static void main(String[] args) {
        new FastCash("");
    }
}