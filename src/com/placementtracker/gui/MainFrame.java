package com.placementtracker.gui;

import com.placementtracker.model.Student;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private Student student;
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel lblBreadcrumb;

    private Map<String, JButton> navButtons = new HashMap<>();
    private String currentCard = "DASHBOARD";

    // Panel References
    private DashboardPanel dashboardPanel;
    private CompanyPanel companyPanel;
    private AptitudePanel aptitudePanel;
    private DSAPanel dsaPanel;
    private InterviewPanel interviewPanel;
    private ReportsPanel reportsPanel;
    private DatabaseOperationsPanel dbPanel;

    public MainFrame(Student student) {
        super("Placement Preparation Tracker — Full Stack Java Development");
        this.student = student;
        initComponents();
        showCard("DASHBOARD");
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.APP_BG);
        setLayout(new BorderLayout());

        // 1. WEST: Modern Dark Sidebar
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 780));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        // Sidebar Brand Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.SIDEBAR_BG);
        brandPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel lblLogo = new JLabel("PLACEMENT TRACKER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("APSCHE • Full Stack Java");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(UITheme.TEXT_LIGHT);

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(3));
        brandPanel.add(lblSub);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Sidebar Navigation Buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UITheme.SIDEBAR_BG);
        navPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        navPanel.add(createNavButton("Dashboard", "DASHBOARD"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("Company Management", "COMPANY"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("Aptitude Tracker", "APTITUDE"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("DSA Progress Tracker", "DSA"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("Interview Schedule", "INTERVIEW"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("Progress Reports", "REPORTS"));
        navPanel.add(Box.createVerticalStrut(4));
        navPanel.add(createNavButton("Database Operations", "DATABASE"));

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Sidebar Footer: User Profile & Logout
        JPanel userFooter = new JPanel(new BorderLayout(5, 5));
        userFooter.setBackground(new Color(30, 41, 59));
        userFooter.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(new Color(30, 41, 59));

        JLabel lblUserName = new JLabel(student != null ? student.getName() : "Student");
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        JLabel lblUserEmail = new JLabel(student != null ? student.getEmail() : "");
        lblUserEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserEmail.setForeground(UITheme.TEXT_LIGHT);

        userInfo.add(lblUserName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(lblUserEmail);
        userFooter.add(userInfo, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogout.setBackground(new Color(239, 68, 68));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Are you sure you want to log out from Placement Preparation Tracker?",
                        "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        userFooter.add(btnLogout, BorderLayout.EAST);

        sidebar.add(userFooter, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        // 2. CENTER: Top Header Bar + Card Layout
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.APP_BG);

        // Top Header Bar
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Color.WHITE);
        topHeader.setPreferredSize(new Dimension(800, 52));
        topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER));

        lblBreadcrumb = new JLabel("  Dashboard Overview");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBreadcrumb.setForeground(UITheme.TEXT_DARK);
        topHeader.add(lblBreadcrumb, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        rightTop.setBackground(Color.WHITE);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
        JLabel lblDate = new JLabel("Date: " + dateStr);
        lblDate.setFont(UITheme.FONT_SMALL);
        lblDate.setForeground(UITheme.TEXT_MUTED);

        JLabel lblDbStatus = new JLabel("[MySQL Connected]");
        lblDbStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDbStatus.setForeground(UITheme.SUCCESS);

        rightTop.add(lblDate);
        rightTop.add(new JSeparator(SwingConstants.VERTICAL));
        rightTop.add(lblDbStatus);

        topHeader.add(rightTop, BorderLayout.EAST);
        mainArea.add(topHeader, BorderLayout.NORTH);

        // Cards Panel
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(UITheme.APP_BG);

        dashboardPanel = new DashboardPanel(student, this);
        companyPanel = new CompanyPanel();
        aptitudePanel = new AptitudePanel();
        dsaPanel = new DSAPanel();
        interviewPanel = new InterviewPanel();
        reportsPanel = new ReportsPanel();
        dbPanel = new DatabaseOperationsPanel();

        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(companyPanel, "COMPANY");
        contentCards.add(aptitudePanel, "APTITUDE");
        contentCards.add(dsaPanel, "DSA");
        contentCards.add(interviewPanel, "INTERVIEW");
        contentCards.add(reportsPanel, "REPORTS");
        contentCards.add(dbPanel, "DATABASE");

        mainArea.add(contentCards, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, final String cardName) {
        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UITheme.SIDEBAR_TEXT);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(216, 40));
        btn.setPreferredSize(new Dimension(216, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(cardName);
            }
        });

        navButtons.put(cardName, btn);
        return btn;
    }

    public void showCard(String cardName) {
        this.currentCard = cardName;
        cardLayout.show(contentCards, cardName);

        // Update active button styling
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton b = entry.getValue();
            if (entry.getKey().equals(cardName)) {
                b.setBackground(UITheme.PRIMARY);
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                b.setBackground(UITheme.SIDEBAR_BG);
                b.setForeground(UITheme.SIDEBAR_TEXT);
                b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }

        // Update Breadcrumb & refresh data
        switch (cardName) {
            case "DASHBOARD":
                lblBreadcrumb.setText("  Dashboard Overview");
                dashboardPanel.refreshData();
                break;
            case "COMPANY":
                lblBreadcrumb.setText("  Company Management — Campus Opportunities");
                break;
            case "APTITUDE":
                lblBreadcrumb.setText("  Aptitude Tracker — Practice Tests & Mock Assessments");
                break;
            case "DSA":
                lblBreadcrumb.setText("  DSA Progress Tracker — Coding Problems & Algorithms");
                break;
            case "INTERVIEW":
                lblBreadcrumb.setText("  Interview Scheduler — Technical Rounds & Results");
                break;
            case "REPORTS":
                lblBreadcrumb.setText("  Consolidated Placement Reports & Metrics");
                reportsPanel.generateReports();
                break;
            case "DATABASE":
                lblBreadcrumb.setText("  Database Operations — MySQL 9.7 Diagnostics");
                dbPanel.runDiagnosticCheck();
                break;
        }
    }

    public DashboardPanel getDashboardPanel() { return dashboardPanel; }
    public CompanyPanel getCompanyPanel() { return companyPanel; }
    public AptitudePanel getAptitudePanel() { return aptitudePanel; }
    public DSAPanel getDsaPanel() { return dsaPanel; }
    public InterviewPanel getInterviewPanel() { return interviewPanel; }
    public ReportsPanel getReportsPanel() { return reportsPanel; }
    public DatabaseOperationsPanel getDbPanel() { return dbPanel; }
}
