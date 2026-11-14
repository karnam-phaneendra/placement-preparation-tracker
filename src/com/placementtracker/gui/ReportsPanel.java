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

public class ReportsPanel extends JPanel {

    private CompanyService companyService;
    private AptitudeService aptitudeService;
    private DSAProgressService dsaService;
    private InterviewService interviewService;

    // Report detail labels
    private JLabel lblCompStats;
    private JLabel lblDsaStats;
    private JLabel lblAptStats;
    private JLabel lblIntStats;
    private JLabel lblReadinessScore;

    public ReportsPanel() {
        this.companyService = new CompanyService();
        this.aptitudeService = new AptitudeService();
        this.dsaService = new DSAProgressService();
        this.interviewService = new InterviewService();

        initComponents();
        generateReports();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.APP_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Top Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setBackground(UITheme.CARD_BG);
        JLabel lblTitle = new JLabel("Consolidated Placement Preparation Reports");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Comprehensive metrics computed from live student records in the MySQL placement_tracker database.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topCard.add(titlePanel, BorderLayout.CENTER);

        JButton btnRefresh = UITheme.createStyledButton("↻ Recompute All Reports", UITheme.PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReports();
            }
        });
        topCard.add(btnRefresh, BorderLayout.EAST);
        add(topCard, BorderLayout.NORTH);

        // Center: 2x2 Grid of In-depth Report Cards
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 14, 14));
        gridPanel.setBackground(UITheme.APP_BG);

        // 1. Company Report Card
        JPanel cardCompany = createReportCard("1. Company Application & Conversion Report", UITheme.PRIMARY);
        lblCompStats = new JLabel();
        lblCompStats.setFont(UITheme.FONT_BODY);
        cardCompany.add(lblCompStats, BorderLayout.CENTER);
        gridPanel.add(cardCompany);

        // 2. DSA Report Card
        JPanel cardDSA = createReportCard("2. Data Structures & Algorithms Progress Report", UITheme.SUCCESS);
        lblDsaStats = new JLabel();
        lblDsaStats.setFont(UITheme.FONT_BODY);
        cardDSA.add(lblDsaStats, BorderLayout.CENTER);
        gridPanel.add(cardDSA);

        // 3. Aptitude Report Card
        JPanel cardAptitude = createReportCard("3. Aptitude & Reasoning Performance Report", UITheme.INFO);
        lblAptStats = new JLabel();
        lblAptStats.setFont(UITheme.FONT_BODY);
        cardAptitude.add(lblAptStats, BorderLayout.CENTER);
        gridPanel.add(cardAptitude);

        // 4. Interview Report Card
        JPanel cardInterview = createReportCard("4. Campus Recruitment Interview Report", UITheme.WARNING);
        lblIntStats = new JLabel();
        lblIntStats.setFont(UITheme.FONT_BODY);
        cardInterview.add(lblIntStats, BorderLayout.CENTER);
        gridPanel.add(cardInterview);

        add(gridPanel, BorderLayout.CENTER);

        // Bottom Banner: Overall Readiness Score
        JPanel bottomCard = UITheme.createCard();
        bottomCard.setLayout(new BorderLayout());
        bottomCard.setBackground(new Color(239, 246, 255));
        bottomCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));

        lblReadinessScore = new JLabel();
        lblReadinessScore.setFont(UITheme.FONT_BODY);
        lblReadinessScore.setForeground(UITheme.TEXT_DARK);
        bottomCard.add(lblReadinessScore, BorderLayout.CENTER);

        add(bottomCard, BorderLayout.SOUTH);
    }

    private JPanel createReportCard(String title, Color accent) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(UITheme.CARD_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(accent);
        header.add(lblTitle, BorderLayout.CENTER);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(10, 2));
        divider.setBackground(accent);
        header.add(divider, BorderLayout.SOUTH);

        card.add(header, BorderLayout.NORTH);
        return card;
    }

    public void generateReports() {
        Report comp = companyService.getCompanyReport();
        DSAReport dsa = dsaService.getProgressReport();
        AptitudeReport apt = aptitudeService.getReport();
        InterviewReport inv = interviewService.getReport();

        // 1. Company HTML
        String compHtml = "<html><body style='font-family: Segoe UI; font-size: 11pt; line-height: 1.4;'>"
                + "<table cellpadding='3' cellspacing='0' width='100%'>"
                + "<tr><td><b>Total Companies Tracked:</b></td><td align='right'><b>" + comp.getTotalCompanies() + "</b></td></tr>"
                + "<tr><td>Applications Submitted:</td><td align='right'>" + comp.getApplied() + "</td></tr>"
                + "<tr><td>Shortlisted Candidates:</td><td align='right'>" + comp.getShortlisted() + "</td></tr>"
                + "<tr><td>Interviews Scheduled:</td><td align='right'>" + comp.getInterview() + "</td></tr>"
                + "<tr><td>Final Offers Selected:</td><td align='right'><font color='#10B981'><b>" + comp.getSelected() + "</b></font></td></tr>"
                + "<tr><td>Applications Rejected:</td><td align='right'><font color='#EF4444'>" + comp.getRejected() + "</font></td></tr>"
                + "<tr><td colspan='2'><hr/></td></tr>"
                + "<tr><td><b>Placement Success Rate:</b></td><td align='right'><font color='#2563EB'><b>"
                + String.format("%.2f%%", comp.getSuccessRate()) + "</b></font></td></tr>"
                + "</table></body></html>";
        lblCompStats.setText(compHtml);

        // 2. DSA HTML
        String dsaHtml = "<html><body style='font-family: Segoe UI; font-size: 11pt; line-height: 1.4;'>"
                + "<table cellpadding='3' cellspacing='0' width='100%'>"
                + "<tr><td><b>Total Problems Logged:</b></td><td align='right'><b>" + dsa.getTotalProblems() + "</b></td></tr>"
                + "<tr><td>Easy Problems:</td><td align='right'>" + dsa.getEasyProblems() + "</td></tr>"
                + "<tr><td>Medium Problems:</td><td align='right'>" + dsa.getMediumProblems() + "</td></tr>"
                + "<tr><td>Hard Problems:</td><td align='right'>" + dsa.getHardProblems() + "</td></tr>"
                + "<tr><td>Solved & Verified:</td><td align='right'><font color='#10B981'><b>" + dsa.getSolvedProblems() + "</b></font></td></tr>"
                + "<tr><td>Revision Needed:</td><td align='right'>" + dsa.getRevisionProblems() + "</td></tr>"
                + "<tr><td>Pending Solutions:</td><td align='right'>" + dsa.getPendingProblems() + "</td></tr>"
                + "<tr><td colspan='2'><hr/></td></tr>"
                + "<tr><td><b>DSA Completion Rate:</b></td><td align='right'><font color='#10B981'><b>"
                + String.format("%.2f%%", dsa.getCompletionPercentage()) + "</b></font></td></tr>"
                + "</table></body></html>";
        lblDsaStats.setText(dsaHtml);

        // 3. Aptitude HTML
        String aptHtml = "<html><body style='font-family: Segoe UI; font-size: 11pt; line-height: 1.4;'>"
                + "<table cellpadding='3' cellspacing='0' width='100%'>"
                + "<tr><td><b>Total Tests Attempted:</b></td><td align='right'><b>" + apt.getTotalTests() + "</b></td></tr>"
                + "<tr><td>Excellent (&ge;85%):</td><td align='right'><font color='#10B981'><b>" + apt.getExcellent() + "</b></font></td></tr>"
                + "<tr><td>Good (70% - 84%):</td><td align='right'>" + apt.getGood() + "</td></tr>"
                + "<tr><td>Average (50% - 69%):</td><td align='right'>" + apt.getAverage() + "</td></tr>"
                + "<tr><td>Needs Improvement (&lt;50%):</td><td align='right'>" + apt.getNeedsImprovement() + "</td></tr>"
                + "<tr><td colspan='2'><hr/></td></tr>"
                + "<tr><td><b>Excellence Ratio:</b></td><td align='right'><font color='#6366F1'><b>"
                + String.format("%.2f%%", apt.getExcellentPercentage()) + "</b></font></td></tr>"
                + "</table></body></html>";
        lblAptStats.setText(aptHtml);

        // 4. Interview HTML
        String invHtml = "<html><body style='font-family: Segoe UI; font-size: 11pt; line-height: 1.4;'>"
                + "<table cellpadding='3' cellspacing='0' width='100%'>"
                + "<tr><td><b>Total Interview Rounds:</b></td><td align='right'><b>" + inv.getTotalInterviews() + "</b></td></tr>"
                + "<tr><td>Rounds Cleared (Selected):</td><td align='right'><font color='#10B981'><b>" + inv.getSelected() + "</b></font></td></tr>"
                + "<tr><td>Upcoming / In Progress:</td><td align='right'><font color='#F59E0B'><b>" + inv.getPending() + "</b></font></td></tr>"
                + "<tr><td>Not Cleared (Rejected):</td><td align='right'><font color='#EF4444'>" + inv.getRejected() + "</font></td></tr>"
                + "<tr><td colspan='2'><hr/></td></tr>"
                + "<tr><td><b>Interview Clearance Rate:</b></td><td align='right'><font color='#F59E0B'><b>"
                + String.format("%.2f%%", inv.getSelectionPercentage()) + "</b></font></td></tr>"
                + "</table></body></html>";
        lblIntStats.setText(invHtml);

        // Overall Readiness Score
        double overallScore = 0;
        int components = 0;
        if (dsa.getTotalProblems() > 0) {
            overallScore += dsa.getCompletionPercentage();
            components++;
        }
        if (apt.getTotalTests() > 0) {
            overallScore += apt.getExcellentPercentage();
            components++;
        }
        if (inv.getTotalInterviews() > 0) {
            overallScore += inv.getSelectionPercentage();
            components++;
        }
        double avgReadiness = components > 0 ? (overallScore / components) : 0;

        lblReadinessScore.setText("<html><b>📊 Placement Readiness Index:</b> Composite Preparation Score is "
                + "<b><font color='#2563EB'>" + String.format("%.1f%%", avgReadiness) + "</font></b>. "
                + "Student <b>Karnam Phaneendra</b> has cleared <b>" + inv.getSelected() + " interview rounds</b> and holds active offers across tracked premier campus recruitment drives. "
                + "All data synchronized with MySQL database <code>placement_tracker</code>.</html>");
    }
}
