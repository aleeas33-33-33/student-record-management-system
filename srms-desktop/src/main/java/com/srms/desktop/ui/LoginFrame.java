package com.srms.desktop.ui;

import com.srms.desktop.dao.StudentDAO;
import com.srms.desktop.util.Validator;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Swing login window: validates input and checks the users table through JDBC. */
public class LoginFrame extends JFrame {

    private final JTextField txtUser = new JTextField(18);
    private final JPasswordField txtPass = new JPasswordField(18);
    private final JLabel lblStatus = new JLabel(" ");
    private final StudentDAO dao = new StudentDAO();

    public LoginFrame() {
        super("SRMS Desktop - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        txtUser.setName("username");
        txtPass.setName("password");

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(31, 78, 156));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel t1 = new JLabel("Student Record Management System");
        t1.setFont(t1.getFont().deriveFont(Font.BOLD, 18f));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Swing + JDBC desktop client  |  Admin login");
        t2.setForeground(new Color(220, 230, 255));
        header.add(t1);
        header.add(t2);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        Dimension fieldSize = new Dimension(240, 30);
        txtUser.setPreferredSize(fieldSize); txtUser.setMinimumSize(fieldSize);
        txtPass.setPreferredSize(fieldSize); txtPass.setMinimumSize(fieldSize);
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Username:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; form.add(txtUser, c);
        c.gridx = 0; c.gridy = 1; c.fill = GridBagConstraints.NONE; c.weightx = 0; form.add(new JLabel("Password:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; form.add(txtPass, c);
        c.fill = GridBagConstraints.NONE; c.weightx = 0;

        JButton btnLogin = new JButton("Login");
        btnLogin.setName("loginButton");
        JButton btnExit = new JButton("Exit");
        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnLogin);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnExit);
        buttons.add(btnLogin);
        c.gridx = 1; c.gridy = 2; c.anchor = GridBagConstraints.EAST; form.add(buttons, c);

        lblStatus.setForeground(new Color(198, 40, 40));
        lblStatus.setName("status");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 20));

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
        pack();
        setSize(Math.max(getWidth(), 500), Math.max(getHeight(), 300));
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Username and password are required.");
            return;
        }
        if (pass.length() < 6) {
            lblStatus.setText("Password must be at least 6 characters.");
            return;
        }
        try {
            String fullName = dao.authenticate(user, Validator.sha256(pass));
            if (fullName == null) {
                lblStatus.setText("Invalid username or password.");
                txtPass.setText("");
                return;
            }
            dispose();
            new MainFrame(user, fullName).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, Validator.friendly(ex), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
