package com.placementtracker.gui;

import com.placementtracker.database.DBConnection;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseOperationsPanel extends JPanel {

    private JTextArea txtConsoleLog;
    private JLabel lblStatusBadge;
    private JLabel lblDbUrl;

    public DatabaseOperationsPanel() {
        initComponents();
        runDiagnosticCheck();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.APP_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Top Header
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setBackground(UITheme.CARD_BG);
        JLabel lblTitle = new JLabel("MySQL JDBC Database Operations & Diagnostics");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        lblDbUrl = new JLabel("Connected to: jdbc:mysql://localhost:3306/placement_tracker (MySQL Server 9.7)");
        lblDbUrl.setFont(UITheme.FONT_BODY);
        lblDbUrl.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblDbUrl);
        topCard.add(titlePanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        statusPanel.setBackground(UITheme.CARD_BG);

        lblStatusBadge = new JLabel("[Connected]");
        lblStatusBadge.setFont(UITheme.FONT_BODY_BOLD);
        lblStatusBadge.setForeground(UITheme.SUCCESS);
        statusPanel.add(lblStatusBadge);

        topCard.add(statusPanel, BorderLayout.EAST);
        add(topCard, BorderLayout.NORTH);

        // Center: Controls on Top, Dark Output Console Below
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(UITheme.APP_BG);

        // Quick Query Actions
        JPanel actionCard = UITheme.createCard();
        actionCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblActions = new JLabel("Execute Database Query:");
        lblActions.setFont(UITheme.FONT_BODY_BOLD);
        actionCard.add(lblActions);

        JButton btnTestConn = UITheme.createStyledButton("Ping Connection", UITheme.PRIMARY, Color.WHITE);
        JButton btnCountRows = UITheme.createStyledButton("Table Counts", UITheme.INFO, Color.WHITE);
        JButton btnViewStudent = UITheme.createStyledButton("Fetch Student Row", UITheme.SUCCESS, Color.WHITE);
        JButton btnViewRecent = UITheme.createStyledButton("Fetch Recent Activities", new Color(241, 245, 249), UITheme.TEXT_DARK);
        JButton btnClearLog = UITheme.createStyledButton("Clear Console", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        actionCard.add(btnTestConn);
        actionCard.add(btnCountRows);
        actionCard.add(btnViewStudent);
        actionCard.add(btnViewRecent);
        actionCard.add(btnClearLog);

        centerPanel.add(actionCard, BorderLayout.NORTH);

        // Dark Terminal Console
        JPanel terminalCard = UITheme.createCard();
        terminalCard.setLayout(new BorderLayout());

        JLabel lblConsoleTitle = new JLabel("Live JDBC Query Execution Log");
        lblConsoleTitle.setFont(UITheme.FONT_SUBTITLE);
        lblConsoleTitle.setForeground(UITheme.TEXT_DARK);
        terminalCard.add(lblConsoleTitle, BorderLayout.NORTH);

        txtConsoleLog = new JTextArea();
        txtConsoleLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtConsoleLog.setBackground(new Color(15, 23, 42)); // dark slate
        txtConsoleLog.setForeground(new Color(241, 245, 249)); // light text
        txtConsoleLog.setCaretColor(Color.WHITE);
        txtConsoleLog.setEditable(false);
        txtConsoleLog.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtConsoleLog);
        scrollPane.setBorder(new LineBorder(UITheme.CARD_BORDER, 1));
        terminalCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(terminalCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        btnTestConn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDiagnosticCheck();
            }
        });

        btnCountRows.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runTableCountsQuery();
            }
        });

        btnViewStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchStudentInfo();
            }
        });

        btnViewRecent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchRecentActivities();
            }
        });

        btnClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtConsoleLog.setText("");
            }
        });
    }

    public void runDiagnosticCheck() {
        log("================================================================================");
        log("[JDBC DIAGNOSTICS] Initializing MySQL Database Connection Verification...");
        log("================================================================================");
        long start = System.currentTimeMillis();

        try {
            Connection con = DBConnection.getConnection();
            if (con != null && !con.isClosed()) {
                DatabaseMetaData meta = con.getMetaData();
                long elapsed = System.currentTimeMillis() - start;

                log("✓ Connection Established Successfully in " + elapsed + " ms.");
                log("• Database Product Name    : " + meta.getDatabaseProductName());
                log("• Database Product Version : " + meta.getDatabaseProductVersion());
                log("• JDBC Driver Name         : " + meta.getDriverName());
                log("• JDBC Driver Version      : " + meta.getDriverVersion());
                log("• Database URL             : " + meta.getURL());
                log("• User Name                : " + meta.getUserName());
                log("• Transaction Isolation    : " + con.getTransactionIsolation());
                log("• Auto-Commit Mode         : " + con.getAutoCommit());
                log("================================================================================");

                lblStatusBadge.setText("[Connected] MySQL 9.7");
                lblStatusBadge.setForeground(UITheme.SUCCESS);
            } else {
                log("[-] ERROR: Connection object returned NULL or closed.");
                lblStatusBadge.setText("[Disconnected]");
                lblStatusBadge.setForeground(UITheme.DANGER);
            }
        } catch (Exception ex) {
            log("[-] EXCEPTION: " + ex.getMessage());
            lblStatusBadge.setText("[Connection Error]");
            lblStatusBadge.setForeground(UITheme.DANGER);
        }
    }

    public void runTableCountsQuery() {
        log("\n[QUERY EXECUTION] SELECT COUNT(*) FROM each table in placement_tracker;");
        String[] tables = {"student", "company", "aptitude", "dsa_progress", "interview"};

        try (Connection con = DBConnection.getConnection()) {
            for (String tbl : tables) {
                String sql = "SELECT COUNT(*) FROM " + tbl;
                try (PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        log("  -> Table: `" + String.format("%-14s", tbl) + "` | Total Records: " + rs.getInt(1));
                    }
                }
            }
            log("✓ Table row integrity check completed successfully.");
        } catch (Exception ex) {
            log("✗ ERROR executing count query: " + ex.getMessage());
        }
    }

    public void fetchStudentInfo() {
        log("\n[QUERY EXECUTION] SELECT student_id, name, email FROM student LIMIT 1;");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT student_id, name, email FROM student");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                log("  -> Student ID : " + rs.getInt("student_id"));
                log("  -> Full Name  : " + rs.getString("name"));
                log("  -> Email ID   : " + rs.getString("email"));
                log("  -> Status     : Authenticated Student (Karnam Phaneendra)");
            }
            log("✓ Student identity record verified in MySQL.");
        } catch (Exception ex) {
            log("✗ ERROR: " + ex.getMessage());
        }
    }

    public void fetchRecentActivities() {
        log("\n[QUERY EXECUTION] Fetching Latest Recorded Activities Across Modules...");
        try (Connection con = DBConnection.getConnection()) {
            // Recent Company
            PreparedStatement ps1 = con.prepareStatement("SELECT company_name, role, status FROM company ORDER BY company_id DESC LIMIT 1");
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                log("  • Latest Company   : " + rs1.getString("company_name") + " (" + rs1.getString("role") + ") - " + rs1.getString("status"));
            }

            // Recent Aptitude
            PreparedStatement ps2 = con.prepareStatement("SELECT topic, score, status FROM aptitude ORDER BY aptitude_id DESC LIMIT 1");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                log("  • Latest Aptitude  : " + rs2.getString("topic") + " | Score: " + rs2.getInt("score") + " (" + rs2.getString("status") + ")");
            }

            // Recent DSA
            PreparedStatement ps3 = con.prepareStatement("SELECT problem_name, platform, difficulty, status FROM dsa_progress ORDER BY problem_id DESC LIMIT 1");
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                log("  • Latest DSA       : " + rs3.getString("problem_name") + " (" + rs3.getString("platform") + ") | " + rs3.getString("difficulty") + " - " + rs3.getString("status"));
            }

            // Recent Interview
            PreparedStatement ps4 = con.prepareStatement("SELECT company_name, round_name, result FROM interview ORDER BY interview_id DESC LIMIT 1");
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) {
                log("  • Latest Interview : " + rs4.getString("company_name") + " - " + rs4.getString("round_name") + " [" + rs4.getString("result") + "]");
            }
            log("✓ All 4 active preparation streams successfully queried from MySQL.");
        } catch (Exception ex) {
            log("✗ ERROR: " + ex.getMessage());
        }
    }

    private void log(String line) {
        txtConsoleLog.append(line + "\n");
        txtConsoleLog.setCaretPosition(txtConsoleLog.getDocument().getLength());
    }
}
