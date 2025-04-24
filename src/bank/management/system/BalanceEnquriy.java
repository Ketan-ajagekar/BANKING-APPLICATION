package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceEnquriy extends JFrame implements ActionListener {

    String pin;
    JLabel label2;
    JButton b1;

    BalanceEnquriy(String pin) {
        super("Bank Application");
        this.pin = pin;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0, 0, 1550, 830);
        add(l3);

        JLabel label1 = new JLabel("Your Current Balance is Rs ");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(430, 180, 700, 35);
        l3.add(label1);

        label2 = new JLabel();
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(430, 220, 400, 35);
        l3.add(label2);

        b1 = new JButton("Back");
        b1.setBounds(700, 406, 150, 35);
        b1.setBackground(new Color(65, 125, 128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        l3.add(b1);

        // ✅ Fetch balance from database
        int balance = getBalance(pin);
        label2.setText("Rs. " + balance);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    // ✅ Function to get balance from database
    private int getBalance(String pin) {
        int balance = 0;
        try {
            Connn connn = new Connn();
            Connection connection = connn.getConnection();

            String query = "SELECT type, amount FROM bank WHERE pin = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, pin);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                String type = resultSet.getString("type");
                int amt = resultSet.getInt("amount");

                if (type.equals("Deposit")) {
                    balance += amt;
                } else {
                    balance -= amt;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        new main_Class(pin);
    }

    public static void main(String[] args) {
        new BalanceEnquriy("");
    }
}
