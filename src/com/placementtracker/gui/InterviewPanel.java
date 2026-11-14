package com.placementtracker.gui;

import com.placementtracker.model.Interview;
import com.placementtracker.model.InterviewReport;
import com.placementtracker.service.InterviewService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class InterviewPanel extends JPanel {

    private InterviewService interviewService;
    private JTable tblInterviews;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtInterviewId;
    private JComboBox<String> cmbCompany;
    private JComboBox<String> cmbRoundName;
    private JTextField txtInterviewDate;
    private JComboBox<String> cmbResult;

    // Stat Labels
    private JLabel lblTotal;
    private JLabel lblSelected;
    private JLabel lblPending;
    private JLabel lblRejected;

    // Filter
    private JTextField txtSearchCompany;
    private JComboBox<String> cmbFilterResult;

    public InterviewPanel() {
        this.interviewService = new InterviewService();
        initComponents();
        loadTable(interviewService.getAllInterviews());
        refreshStats();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.APP_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Top Section: Header & Stat Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setBackground(UITheme.APP_BG);

        JPanel headerCard = UITheme.createCard();
        headerCard.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setBackground(UITheme.CARD_BG);
        JLabel lblTitle = new JLabel("Campus Placement Interview Scheduler");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Schedule technical interviews, assessment tests, HR discussions, and track outcome results.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        headerCard.add(titlePanel, BorderLayout.CENTER);
        topContainer.add(headerCard, BorderLayout.NORTH);

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setBackground(UITheme.APP_BG);
        statsRow.add(UITheme.createStatCard("SCHEDULED INTERVIEWS", "0", "All scheduled rounds", UITheme.PRIMARY));
        statsRow.add(UITheme.createStatCard("OFFERS / CLEARED", "0", "Selected rounds", UITheme.SUCCESS));
        statsRow.add(UITheme.createStatCard("UPCOMING / PENDING", "0", "Awaiting execution", UITheme.WARNING));
        statsRow.add(UITheme.createStatCard("REJECTED / CLOSED", "0", "Disqualified rounds", UITheme.DANGER));

        lblTotal = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(0)).getComponent(1)).getComponent(2);
        lblSelected = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(1)).getComponent(1)).getComponent(2);
        lblPending = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(2)).getComponent(1)).getComponent(2);
        lblRejected = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(3)).getComponent(1)).getComponent(2);

        topContainer.add(statsRow, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center Split: Form on Left, Table on Right
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(UITheme.APP_BG);

        // Form Card (Left)
        JPanel formCard = UITheme.createCard();
        formCard.setPreferredSize(new Dimension(340, 500));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel lblFormTitle = new JLabel("Interview Schedule Form");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtInterviewId = new JTextField();
        txtInterviewId.setEditable(false);
        txtInterviewId.setBackground(new Color(241, 245, 249));

        cmbCompany = new JComboBox<>(new String[]{
                "Infosys",
                "Tata Consultancy Services",
                "Wipro Technologies",
                "Accenture",
                "Cognizant",
                "Capgemini",
                "Tech Mahindra",
                "Amazon",
                "Microsoft"
        });
        cmbCompany.setEditable(true);

        cmbRoundName = new JComboBox<>(new String[]{
                "Online Assessment Round",
                "Technical Interview Round 1",
                "Technical Interview Round 2",
                "System Design & Coding Round",
                "Technical & HR Discussion",
                "Final HR & Leadership Round"
        });
        cmbRoundName.setEditable(true);

        txtInterviewDate = new JTextField(LocalDate.now().plusDays(3).toString());
        cmbResult = new JComboBox<>(new String[]{"Pending", "Selected", "Rejected"});

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(10));
        addFormField(formCard, "Interview ID (Auto)", txtInterviewId);
        addFormField(formCard, "Target Company *", cmbCompany);
        addFormField(formCard, "Interview Round *", cmbRoundName);
        addFormField(formCard, "Interview Date (YYYY-MM-DD) *", txtInterviewDate);
        addFormField(formCard, "Interview Result Status", cmbResult);

        formCard.add(Box.createVerticalStrut(12));

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(UITheme.CARD_BG);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(320, 80));

        JButton btnAdd = UITheme.createStyledButton("Schedule Round", UITheme.SUCCESS, Color.WHITE);
        JButton btnUpdate = UITheme.createStyledButton("Update Result", UITheme.PRIMARY, Color.WHITE);
        JButton btnDelete = UITheme.createStyledButton("Delete Schedule", UITheme.DANGER, Color.WHITE);
        JButton btnClear = UITheme.createStyledButton("Clear Form", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel);
        centerPanel.add(formCard, BorderLayout.WEST);

        // Table Card (Right)
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Search Company:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        txtSearchCompany = new JTextField(12);

        JButton btnSearch = UITheme.createStyledButton("Search", UITheme.PRIMARY, Color.WHITE);

        JLabel lblFilter = new JLabel("Result:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);
        cmbFilterResult = new JComboBox<>(new String[]{"All", "Pending", "Selected", "Rejected"});

        JButton btnReset = UITheme.createStyledButton("Reset", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        filterBar.add(lblSearch);
        filterBar.add(txtSearchCompany);
        filterBar.add(btnSearch);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblFilter);
        filterBar.add(cmbFilterResult);
        filterBar.add(btnReset);

        tableCard.add(filterBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Company Name", "Interview Round", "Scheduled Date", "Result Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblInterviews = new JTable(tableModel);
        UITheme.styleTable(tblInterviews);

        JScrollPane scrollPane = new JScrollPane(tblInterviews);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        tblInterviews.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblInterviews.getSelectedRow();
                if (row >= 0) {
                    txtInterviewId.setText(tableModel.getValueAt(row, 0).toString());
                    cmbCompany.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    cmbRoundName.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtInterviewDate.setText(tableModel.getValueAt(row, 3).toString());
                    cmbResult.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleInterview();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateResult();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteInterview();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInterview();
            }
        });

        cmbFilterResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterResult();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchCompany.setText("");
                cmbFilterResult.setSelectedIndex(0);
                loadTable(interviewService.getAllInterviews());
                refreshStats();
                clearForm();
            }
        });
    }

    private void addFormField(JPanel parent, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(UITheme.FONT_BODY);
        field.setMaximumSize(new Dimension(320, 34));
        field.setPreferredSize(new Dimension(320, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(new CompoundBorder(
                    new LineBorder(UITheme.CARD_BORDER, 1, true),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(3));
        parent.add(field);
        parent.add(Box.createVerticalStrut(8));
    }

    private void loadTable(List<Interview> list) {
        tableModel.setRowCount(0);
        for (Interview inv : list) {
            tableModel.addRow(new Object[]{
                    inv.getInterviewId(),
                    inv.getCompanyName(),
                    inv.getRoundName(),
                    inv.getInterviewDate(),
                    inv.getResult()
            });
        }
    }

    private void refreshStats() {
        InterviewReport rep = interviewService.getReport();
        lblTotal.setText(String.valueOf(rep.getTotalInterviews()));
        lblSelected.setText(String.valueOf(rep.getSelected()));
        lblPending.setText(String.valueOf(rep.getPending()));
        lblRejected.setText(String.valueOf(rep.getRejected()));
    }

    private void scheduleInterview() {
        String comp = cmbCompany.getSelectedItem() != null ? cmbCompany.getSelectedItem().toString().trim() : "";
        String round = cmbRoundName.getSelectedItem() != null ? cmbRoundName.getSelectedItem().toString().trim() : "";
        String dateStr = txtInterviewDate.getText().trim();
        String result = cmbResult.getSelectedItem().toString();

        if (comp.isEmpty() || round.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter company, round, and interview date.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date invDate;
        try {
            invDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Interview inv = new Interview();
        inv.setCompanyName(comp);
        inv.setRoundName(round);
        inv.setInterviewDate(invDate);
        inv.setResult(result);

        boolean ok = interviewService.scheduleInterview(inv);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Interview scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(interviewService.getAllInterviews());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save interview schedule.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResult() {
        String idStr = txtInterviewId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an interview row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String result = cmbResult.getSelectedItem().toString();

        boolean ok = interviewService.updateInterview(id, result);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Interview result updated to '" + result + "'!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(interviewService.getAllInterviews());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update interview in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInterview() {
        String idStr = txtInterviewId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an interview row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete interview ID: " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = interviewService.deleteInterview(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Interview schedule deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadTable(interviewService.getAllInterviews());
                refreshStats();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete interview schedule.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchInterview() {
        String term = txtSearchCompany.getText().trim();
        if (term.isEmpty()) {
            loadTable(interviewService.getAllInterviews());
            return;
        }

        Interview inv = interviewService.searchInterview(term);
        tableModel.setRowCount(0);
        if (inv != null) {
            tableModel.addRow(new Object[]{
                    inv.getInterviewId(),
                    inv.getCompanyName(),
                    inv.getRoundName(),
                    inv.getInterviewDate(),
                    inv.getResult()
            });
        } else {
            JOptionPane.showMessageDialog(this, "No interview found for company: " + term, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterResult() {
        String selected = cmbFilterResult.getSelectedItem().toString();
        if ("All".equalsIgnoreCase(selected)) {
            loadTable(interviewService.getAllInterviews());
        } else {
            loadTable(interviewService.filterResult(selected));
        }
    }

    private void clearForm() {
        txtInterviewId.setText("");
        txtInterviewDate.setText(LocalDate.now().plusDays(3).toString());
        cmbResult.setSelectedIndex(0);
        tblInterviews.clearSelection();
    }
}
