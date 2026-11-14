package com.placementtracker.gui;

import com.placementtracker.model.*;
import com.placementtracker.service.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardPanel extends JPanel {

    private Student student;
    private MainFrame mainFrame;

    private CompanyService companyService;
    private AptitudeService aptitudeService;
    private DSAProgressService dsaService;
    private InterviewService interviewService;

    // Stat Labels
    private JLabel lblTotalCompanies;
    private JLabel lblAptitudeTests;
    private JLabel lblDsaSolved;
    private JLabel lblInterviews;

    // Detailed metrics
    private JLabel lblCompanyDetails;
    private JLabel lblDsaDetails;
    private JLabel lblAptitudeDetails;
    private JLabel lblInterviewDetails;

    public DashboardPanel(Student student, MainFrame mainFrame) {
        this.student = student;
        this.mainFrame = mainFrame;
        this.companyService = new CompanyService();
        this.aptitudeService = new AptitudeService();
        this.dsaService = new DSAProgressService();
        this.interviewService = new InterviewService();

        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.APP_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Welcome Card
        JPanel welcomeCard = UITheme.createCard();
        welcomeCard.setLayout(new BorderLayout());

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setBackground(UITheme.CARD_BG);

        JLabel lblWelcome = new JLabel("Welcome back, " + student.getName() + "!");
        lblWelcome.setFont(UITheme.FONT_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Track your placement readiness, mock tests, problem-solving, and scheduled company interviews.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        welcomeTextPanel.add(lblWelcome);
        welcomeTextPanel.add(Box.createVerticalStrut(4));
        welcomeTextPanel.add(lblSub);

        welcomeCard.add(welcomeTextPanel, BorderLayout.WEST);

        JButton btnRefresh = UITheme.createStyledButton("↻ Refresh Live Stats", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        welcomeCard.add(btnRefresh, BorderLayout.EAST);

        // Center Content: Stats + Detailed Panels
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.APP_BG);

        // 4 KPI Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statsGrid.setBackground(UITheme.APP_BG);

        statsGrid.add(createKpiCard("TOTAL COMPANIES", lblTotalCompanies = new JLabel("0"), "Applications logged", UITheme.PRIMARY));
        statsGrid.add(createKpiCard("APTITUDE TESTS", lblAptitudeTests = new JLabel("0"), "Practice assessments", UITheme.INFO));
        statsGrid.add(createKpiCard("DSA SOLVED", lblDsaSolved = new JLabel("0"), "Coding problems", UITheme.SUCCESS));
        statsGrid.add(createKpiCard("INTERVIEWS", lblInterviews = new JLabel("0"), "Rounds scheduled", UITheme.WARNING));

        centerPanel.add(statsGrid, BorderLayout.NORTH);

        // Two Breakdown Cards Side by Side
        JPanel breakdownGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        breakdownGrid.setBackground(UITheme.APP_BG);

        // Card 1: Placement Pipeline Summary
        JPanel pnlPipeline = UITheme.createCard();
        pnlPipeline.setLayout(new BorderLayout(10, 10));
        JLabel lblPipeTitle = new JLabel("Application & Recruitment Pipeline");
        lblPipeTitle.setFont(UITheme.FONT_SUBTITLE);
        lblPipeTitle.setForeground(UITheme.TEXT_DARK);
        pnlPipeline.add(lblPipeTitle, BorderLayout.NORTH);

        lblCompanyDetails = new JLabel();
        lblCompanyDetails.setFont(UITheme.FONT_BODY);
        pnlPipeline.add(lblCompanyDetails, BorderLayout.CENTER);

        JPanel pnlPipeAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlPipeAction.setBackground(UITheme.CARD_BG);
        JButton btnGoCompany = UITheme.createStyledButton("Manage Companies →", UITheme.PRIMARY, Color.WHITE);
        btnGoCompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("COMPANY");
            }
        });
        pnlPipeAction.add(btnGoCompany);
        pnlPipeline.add(pnlPipeAction, BorderLayout.SOUTH);

        // Card 2: Technical Readiness Summary
        JPanel pnlReadiness = UITheme.createCard();
        pnlReadiness.setLayout(new BorderLayout(10, 10));
        JLabel lblReadyTitle = new JLabel("Preparation & Interview Readiness");
        lblReadyTitle.setFont(UITheme.FONT_SUBTITLE);
        lblReadyTitle.setForeground(UITheme.TEXT_DARK);
        pnlReadiness.add(lblReadyTitle, BorderLayout.NORTH);

        JPanel readinessContent = new JPanel();
        readinessContent.setLayout(new BoxLayout(readinessContent, BoxLayout.Y_AXIS));
        readinessContent.setBackground(UITheme.CARD_BG);

        lblDsaDetails = new JLabel();
        lblDsaDetails.setFont(UITheme.FONT_BODY);

        lblAptitudeDetails = new JLabel();
        lblAptitudeDetails.setFont(UITheme.FONT_BODY);

        lblInterviewDetails = new JLabel();
        lblInterviewDetails.setFont(UITheme.FONT_BODY);

        readinessContent.add(lblDsaDetails);
        readinessContent.add(Box.createVerticalStrut(8));
        readinessContent.add(lblAptitudeDetails);
        readinessContent.add(Box.createVerticalStrut(8));
        readinessContent.add(lblInterviewDetails);

        pnlReadiness.add(readinessContent, BorderLayout.CENTER);

        JPanel pnlReadyAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlReadyAction.setBackground(UITheme.CARD_BG);
        JButton btnGoReports = UITheme.createStyledButton("View Full Reports →", UITheme.INFO, Color.WHITE);
        btnGoReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("REPORTS");
            }
        });
        pnlReadyAction.add(btnGoReports);
        pnlReadiness.add(pnlReadyAction, BorderLayout.SOUTH);

        breakdownGrid.add(pnlPipeline);
        breakdownGrid.add(pnlReadiness);

        centerPanel.add(breakdownGrid, BorderLayout.CENTER);

        // Bottom Quick Navigation Shortcuts
        JPanel bottomNavCard = UITheme.createCard();
        bottomNavCard.setLayout(new BorderLayout(10, 10));

        JLabel lblQuick = new JLabel("Quick Navigation Modules");
        lblQuick.setFont(UITheme.FONT_HEADER);
        lblQuick.setForeground(UITheme.TEXT_DARK);
        bottomNavCard.add(lblQuick, BorderLayout.NORTH);

        JPanel btnBar = new JPanel(new GridLayout(1, 4, 12, 0));
        btnBar.setBackground(UITheme.CARD_BG);

        JButton btn1 = UITheme.createStyledButton("Company Management", new Color(241, 245, 249), UITheme.TEXT_DARK);
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("COMPANY");
            }
        });

        JButton btn2 = UITheme.createStyledButton("Aptitude Tracker", new Color(241, 245, 249), UITheme.TEXT_DARK);
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("APTITUDE");
            }
        });

        JButton btn3 = UITheme.createStyledButton("DSA Progress Tracker", new Color(241, 245, 249), UITheme.TEXT_DARK);
        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("DSA");
            }
        });

        JButton btn4 = UITheme.createStyledButton("Interview Schedule", new Color(241, 245, 249), UITheme.TEXT_DARK);
        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("INTERVIEW");
            }
        });

        btnBar.add(btn1);
        btnBar.add(btn2);
        btnBar.add(btn3);
        btnBar.add(btn4);

        bottomNavCard.add(btnBar, BorderLayout.CENTER);

        add(welcomeCard, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomNavCard, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(UITheme.CARD_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(10, 4));
        accentBar.setBackground(accentColor);
        card.add(accentBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        valueLbl.setFont(UITheme.FONT_STAT_NUM);
        valueLbl.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(accentColor);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(valueLbl);
        content.add(Box.createVerticalStrut(2));
        content.add(lblSub);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        Report compReport = companyService.getCompanyReport();
        AptitudeReport aptReport = aptitudeService.getReport();
        DSAReport dsaReport = dsaService.getProgressReport();
        InterviewReport intReport = interviewService.getReport();

        lblTotalCompanies.setText(String.valueOf(compReport.getTotalCompanies()));
        lblAptitudeTests.setText(String.valueOf(aptReport.getTotalTests()));
        lblDsaSolved.setText(dsaReport.getSolvedProblems() + " / " + dsaReport.getTotalProblems());
        lblInterviews.setText(String.valueOf(intReport.getTotalInterviews()));

        // Company Breakdown HTML
        String companyHtml = "<html><body style='line-height: 1.5; font-family: Segoe UI;'>"
                + "<b>Applied:</b> " + compReport.getApplied() + " companies<br/>"
                + "<b>Shortlisted:</b> " + compReport.getShortlisted() + "<br/>"
                + "<b>Interview in Progress:</b> " + compReport.getInterview() + "<br/>"
                + "<b>Offers Selected:</b> <font color='#10B981'><b>" + compReport.getSelected() + "</b></font><br/>"
                + "<b>Rejected:</b> " + compReport.getRejected() + "<br/>"
                + "<b>Conversion Success Rate:</b> <font color='#2563EB'><b>"
                + String.format("%.1f%%", compReport.getSuccessRate()) + "</b></font>"
                + "</body></html>";
        lblCompanyDetails.setText(companyHtml);

        // Technical Readiness HTML
        lblDsaDetails.setText("<html><b>[DSA] Coding Practice:</b> " + dsaReport.getSolvedProblems() + " Solved, "
                + dsaReport.getRevisionProblems() + " In Revision, " + dsaReport.getPendingProblems() + " Pending "
                + "(<font color='#10B981'><b>" + String.format("%.1f%%", dsaReport.getCompletionPercentage()) + " complete</b></font>)</html>");

        lblAptitudeDetails.setText("<html><b>[Aptitude] Assessment:</b> " + aptReport.getExcellent() + " Excellent, "
                + aptReport.getGood() + " Good, " + aptReport.getAverage() + " Average "
                + "(<font color='#6366F1'><b>" + String.format("%.1f%%", aptReport.getExcellentPercentage()) + " excellence</b></font>)</html>");

        lblInterviewDetails.setText("<html><b>[Interviews] Schedule:</b> " + intReport.getSelected() + " Cleared, "
                + intReport.getPending() + " Upcoming, " + intReport.getRejected() + " Not Cleared</html>");
    }
}
