package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class Deposit extends JFrame implements ActionListener {
    String pin;
    TextField textField;
    JPasswordField passwordField4;
    JButton b1, b2;

    Deposit(String pin) {
        super("Bank Application");
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0, 0, 1550, 830);
        add(l3);

        JLabel label1 = new JLabel("ENTER AMOUNT THAT YOU WANT TO DEPOSIT");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(430, 180, 400, 35);
        l3.add(label1);

        textField = new TextField();
        textField.setBackground(new Color(65, 125, 128));
        textField.setForeground(Color.WHITE);
        textField.setBounds(460, 230, 320, 25);
        textField.setFont(new Font("Raleway", Font.BOLD, 22));
        l3.add(textField);

        JLabel label3 = new JLabel("PLEASE ENTER PIN");
        label3.setForeground(Color.WHITE);
        label3.setFont(new Font("System", Font.BOLD, 16));
        label3.setBounds(460, 280, 350, 35);
        l3.add(label3);

        passwordField4 = new JPasswordField(15);
        passwordField4.setBackground(new Color(65, 125, 128));
        passwordField4.setForeground(Color.WHITE);
        passwordField4.setBounds(460, 320, 320, 25);
        passwordField4.setFont(new Font("Raleway", Font.BOLD, 22));
        l3.add(passwordField4);

        b1 = new JButton("DEPOSIT");
        b1.setBounds(700, 362, 150, 35);
        b1.setBackground(new Color(65, 125, 128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        l3.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(700, 406, 150, 35);
        b2.setBackground(new Color(65, 125, 128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        l3.add(b2);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String amount = textField.getText();
            String enteredPin = new String(passwordField4.getPassword());
            Date date = new Date();

            if (e.getSource() == b1) {
                if (amount.isEmpty() || enteredPin.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both Amount and PIN!");
                    return;
                }

                int depositAmount = Integer.parseInt(amount);
                if (depositAmount <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid deposit amount!");
                    return;
                }

                Connn connn = new Connn();
                Connection connection = connn.getConnection();

                // ✅ Step 1: Validate PIN before deposit
                PreparedStatement pinStmt = connection.prepareStatement("SELECT card_number FROM login WHERE pin = ?");
                pinStmt.setString(1, enteredPin);
                ResultSet pinResult = pinStmt.executeQuery();

                if (!pinResult.next()) {
                    JOptionPane.showMessageDialog(null, "Invalid PIN!");
                    return;
                }

                String cardNumber = pinResult.getString("card_number");

                // ✅ Step 2: Insert deposit transaction
                PreparedStatement depositStmt = connection.prepareStatement(
                        "INSERT INTO bank (pin,card_number, date, type, amount) VALUES (?,?, ?, ?, ?)"
                );
                depositStmt.setString(1, enteredPin);
                depositStmt.setString(2, cardNumber);
                depositStmt.setTimestamp(3, new java.sql.Timestamp(date.getTime()));
                depositStmt.setString(4, "Deposit");
                depositStmt.setInt(5, depositAmount);
                int rowsInserted = depositStmt.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Rs. " + amount + " Deposited Successfully");
                    setVisible(false);
                    new main_Class(enteredPin);
                } else {
                    JOptionPane.showMessageDialog(null, "Deposit Failed. Try again.");
                }

            } else if (e.getSource() == b2) {
                setVisible(false);
                new main_Class(pin);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid amount format!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Transaction failed!");
        }
    }

    public static void main(String[] args) {
        new Deposit("");
    }
}
