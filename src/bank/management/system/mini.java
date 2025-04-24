package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mini extends JFrame implements ActionListener {
    String pin;
    JButton button;

    mini(String pin) {
        super("Bank Application");
        this.pin = pin;
        getContentPane().setBackground(new Color(170, 230, 246));
        setSize(400, 600);
        setLocation(440, 20);
        setLayout(null);

        JLabel label2 = new JLabel("MINI STATEMENT");
        label2.setFont(new Font("System", Font.BOLD, 18));
        label2.setBounds(120, 20, 300, 30);
        add(label2);

        JLabel label3 = new JLabel();
        label3.setBounds(20, 80, 300, 20);
        add(label3);

        JLabel label4 = new JLabel();
        label4.setBounds(20, 450, 300, 30);
        add(label4);

        // Create a JTextArea for displaying the mini statement
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Add JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 140, 340, 280);
        add(scrollPane);

        try {
            Connn connn = new Connn(); // Create database connection
            Connection connection = connn.getConnection();

            //Get Card Number from `login` Table
            PreparedStatement ps1 = connection.prepareStatement("SELECT card_number FROM login WHERE pin = ?");
            ps1.setString(1, pin);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                label3.setText("Card Number: " + rs1.getString("card_number").substring(0, 4) + "XXXXXXXX" + rs1.getString("card_number").substring(12));
            }

            //Get Mini Statement from `bank` Table
            int balance = 0;
            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM bank WHERE pin = ?");
            ps2.setString(1, pin);
            ResultSet rs2 = ps2.executeQuery();

            StringBuilder miniStatement = new StringBuilder();
            while (rs2.next()) {
                miniStatement.append(rs2.getString("date")).append("   ")
                        .append(rs2.getString("type")).append("   Rs.")
                        .append(rs2.getString("amount")).append("\n");

                if (rs2.getString("type").equalsIgnoreCase("Deposit")) {
                    balance += rs2.getInt("amount");
                } else {
                    balance -= rs2.getInt("amount");
                }
            }
            textArea.setText(miniStatement.toString());
            label4.setText("Your Total Balance is Rs " + balance);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Exit Button
        button = new JButton("Exit");
        button.setBounds(20, 500, 100, 30);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        add(button);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    public static void main(String[] args) {
        new mini("");
    }
}
