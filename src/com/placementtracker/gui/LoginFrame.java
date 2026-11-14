package com.placementtracker.gui;

import com.placementtracker.model.Student;
import com.placementtracker.service.LoginService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private LoginService loginService;

    public LoginFrame() {
        super("Placement Preparation Tracker - Login");
        this.loginService = new LoginService();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.APP_BG);
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.APP_BG);
        rootPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Center Login Card
        JPanel card = UITheme.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Top Brand Icon / Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        brandPanel.setBackground(UITheme.CARD_BG);
        JLabel iconBadge = new JLabel("PPT");
        iconBadge.setFont(new Font("Segoe UI", Font.BOLD, 22));
        iconBadge.setForeground(Color.WHITE);
        iconBadge.setOpaque(true);
        iconBadge.setBackground(UITheme.PRIMARY);
        iconBadge.setHorizontalAlignment(SwingConstants.CENTER);
        iconBadge.setPreferredSize(new Dimension(65, 65));
        iconBadge.setBorder(new LineBorder(UITheme.PRIMARY_DARK, 2, true));
        brandPanel.add(iconBadge);

        JLabel lblTitle = new JLabel("Placement Preparation Tracker");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(UITheme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("APSCHE • Full Stack Java Development");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSignIn = new JLabel("Student Authentication");
        lblSignIn.setFont(UITheme.FONT_SUBTITLE);
        lblSignIn.setForeground(UITheme.PRIMARY);
        lblSignIn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JLabel lblEmail = new JLabel("Student Email");
        lblEmail.setFont(UITheme.FONT_BODY_BOLD);
        lblEmail.setForeground(UITheme.TEXT_DARK);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtEmail = new JTextField("kphani8937@gmail.com");
        txtEmail.setFont(UITheme.FONT_BODY);
        txtEmail.setMaximumSize(new Dimension(360, 38));
        txtEmail.setPreferredSize(new Dimension(360, 38));
        txtEmail.setBorder(new CompoundBorder(
                new LineBorder(UITheme.CARD_BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtPassword = new JPasswordField("Phani@123");
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setMaximumSize(new Dimension(360, 38));
        txtPassword.setPreferredSize(new Dimension(360, 38));
        txtPassword.setBorder(new CompoundBorder(
                new LineBorder(UITheme.CARD_BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton btnLogin = UITheme.createStyledButton("Login to Portal", UITheme.PRIMARY, Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(360, 42));
        btnLogin.setPreferredSize(new Dimension(360, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnClear = UITheme.createStyledButton("Clear", new Color(241, 245, 249), UITheme.TEXT_MUTED);
        btnClear.setMaximumSize(new Dimension(360, 36));
        btnClear.setPreferredSize(new Dimension(360, 36));
        btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(brandPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(2));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(15));
        card.add(lblSignIn);
        card.add(Box.createVerticalStrut(15));

        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailLabelPanel.setBackground(UITheme.CARD_BG);
        emailLabelPanel.setMaximumSize(new Dimension(360, 20));
        emailLabelPanel.add(lblEmail);
        card.add(emailLabelPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(txtEmail);

        card.add(Box.createVerticalStrut(12));

        JPanel passLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passLabelPanel.setBackground(UITheme.CARD_BG);
        passLabelPanel.setMaximumSize(new Dimension(360, 20));
        passLabelPanel.add(lblPass);
        card.add(passLabelPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);

        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(12));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnClear);

        rootPanel.add(card, BorderLayout.CENTER);

        // Footer note
        JLabel lblFooter = new JLabel("Placement Preparation Tracker • MySQL JDBC Connected", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(UITheme.TEXT_MUTED);
        lblFooter.setBorder(new EmptyBorder(12, 0, 0, 0));
        rootPanel.add(lblFooter, BorderLayout.SOUTH);

        add(rootPanel, BorderLayout.CENTER);

        // Action Listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEmail.setText("");
                txtPassword.setText("");
                lblStatus.setText(" ");
                txtEmail.requestFocus();
            }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    private void performLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Email and password fields are required.");
            lblStatus.setForeground(UITheme.DANGER);
            return;
        }

        Student student = loginService.login(email, password);

        if (student != null) {
            lblStatus.setText("Login successful! Loading dashboard...");
            lblStatus.setForeground(UITheme.SUCCESS);

            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainFrame mainFrame = new MainFrame(student);
                    mainFrame.setVisible(true);
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            lblStatus.setText("Invalid email or password. Access denied.");
            lblStatus.setForeground(UITheme.DANGER);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
