package com.placementtracker.gui;

import com.placementtracker.model.Aptitude;
import com.placementtracker.model.AptitudeReport;
import com.placementtracker.service.AptitudeService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AptitudePanel extends JPanel {

    private AptitudeService aptitudeService;
    private JTable tblAptitude;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtAptitudeId;
    private JComboBox<String> cmbTopic;
    private JTextField txtScore;
    private JTextField txtTestDate;
    private JComboBox<String> cmbStatus;

    // Stat Labels
    private JLabel lblTotalTests;
    private JLabel lblExcellent;
    private JLabel lblGood;
    private JLabel lblAverage;

    // Filter
    private JComboBox<String> cmbFilterStatus;
    private JTextField txtSearchTopic;

    public AptitudePanel() {
        this.aptitudeService = new AptitudeService();
        initComponents();
        loadTable(aptitudeService.getAllResults());
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
        JLabel lblTitle = new JLabel("Aptitude Practice & Assessment Tracker");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Log quantitative, logical reasoning, verbal ability, and data interpretation mock test scores.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        headerCard.add(titlePanel, BorderLayout.CENTER);
        topContainer.add(headerCard, BorderLayout.NORTH);

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setBackground(UITheme.APP_BG);
        statsRow.add(UITheme.createStatCard("TOTAL TESTS", "0", "Evaluated mock tests", UITheme.INFO));
        statsRow.add(UITheme.createStatCard("EXCELLENT (>=85%)", "0", "Top tier performance", UITheme.SUCCESS));
        statsRow.add(UITheme.createStatCard("GOOD (70-84%)", "0", "Solid preparation", UITheme.PRIMARY));
        statsRow.add(UITheme.createStatCard("AVERAGE (<70%)", "0", "Needs practice", UITheme.WARNING));

        // Store references to the value labels
        lblTotalTests = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(0)).getComponent(1)).getComponent(2);
        lblExcellent = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(1)).getComponent(1)).getComponent(2);
        lblGood = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(2)).getComponent(1)).getComponent(2);
        lblAverage = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(3)).getComponent(1)).getComponent(2);

        topContainer.add(statsRow, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center split: Form on Left, Table on Right
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(UITheme.APP_BG);

        // Form Card (Left)
        JPanel formCard = UITheme.createCard();
        formCard.setPreferredSize(new Dimension(340, 480));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel lblFormTitle = new JLabel("Test Score Entry Form");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtAptitudeId = new JTextField();
        txtAptitudeId.setEditable(false);
        txtAptitudeId.setBackground(new Color(241, 245, 249));

        cmbTopic = new JComboBox<>(new String[]{
                "Quantitative - Number Series",
                "Quantitative - Time and Work",
                "Quantitative - Speed, Distance and Time",
                "Quantitative - Profit and Loss",
                "Logical Reasoning - Syllogisms",
                "Logical Reasoning - Blood Relations",
                "Logical Reasoning - Seating Arrangement",
                "Verbal Ability - Reading Comprehension",
                "Verbal Ability - Error Spotting",
                "Verbal Ability - Sentence Correction",
                "Data Interpretation - Bar Graphs",
                "Data Interpretation - Pie Charts"
        });
        cmbTopic.setEditable(true);

        txtScore = new JTextField("85");
        txtTestDate = new JTextField(LocalDate.now().toString());

        cmbStatus = new JComboBox<>(new String[]{"Excellent", "Good", "Average", "Needs Improvement"});

        // Auto-update status when score changes
        txtScore.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { autoSetStatus(); }
            @Override
            public void removeUpdate(DocumentEvent e) { autoSetStatus(); }
            @Override
            public void changedUpdate(DocumentEvent e) { autoSetStatus(); }

            private void autoSetStatus() {
                try {
                    int sc = Integer.parseInt(txtScore.getText().trim());
                    if (sc >= 85) cmbStatus.setSelectedItem("Excellent");
                    else if (sc >= 70) cmbStatus.setSelectedItem("Good");
                    else if (sc >= 50) cmbStatus.setSelectedItem("Average");
                    else cmbStatus.setSelectedItem("Needs Improvement");
                } catch (Exception ignored) {}
            }
        });

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(10));
        addFormField(formCard, "Test Record ID (Auto)", txtAptitudeId);
        addFormField(formCard, "Aptitude Topic *", cmbTopic);
        addFormField(formCard, "Score Obtained (0 - 100) *", txtScore);
        addFormField(formCard, "Test Date (YYYY-MM-DD) *", txtTestDate);
        addFormField(formCard, "Performance Status", cmbStatus);

        formCard.add(Box.createVerticalStrut(12));

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(UITheme.CARD_BG);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(320, 80));

        JButton btnAdd = UITheme.createStyledButton("Add Test Record", UITheme.SUCCESS, Color.WHITE);
        JButton btnUpdate = UITheme.createStyledButton("Update Status", UITheme.PRIMARY, Color.WHITE);
        JButton btnDelete = UITheme.createStyledButton("Delete Record", UITheme.DANGER, Color.WHITE);
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

        // Filter bar above table
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Search Topic:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        txtSearchTopic = new JTextField(12);

        JButton btnSearch = UITheme.createStyledButton("Search", UITheme.PRIMARY, Color.WHITE);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);
        cmbFilterStatus = new JComboBox<>(new String[]{"All", "Excellent", "Good", "Average", "Needs Improvement"});

        JButton btnReset = UITheme.createStyledButton("Reset", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        filterBar.add(lblSearch);
        filterBar.add(txtSearchTopic);
        filterBar.add(btnSearch);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblFilter);
        filterBar.add(cmbFilterStatus);
        filterBar.add(btnReset);

        tableCard.add(filterBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Topic Name", "Score", "Test Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAptitude = new JTable(tableModel);
        UITheme.styleTable(tblAptitude);

        JScrollPane scrollPane = new JScrollPane(tblAptitude);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        tblAptitude.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblAptitude.getSelectedRow();
                if (row >= 0) {
                    txtAptitudeId.setText(tableModel.getValueAt(row, 0).toString());
                    cmbTopic.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    txtScore.setText(tableModel.getValueAt(row, 2).toString());
                    txtTestDate.setText(tableModel.getValueAt(row, 3).toString());
                    cmbStatus.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRecord();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
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
                searchTopic();
            }
        });

        cmbFilterStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterStatus();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchTopic.setText("");
                cmbFilterStatus.setSelectedIndex(0);
                loadTable(aptitudeService.getAllResults());
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

    private void loadTable(List<Aptitude> list) {
        tableModel.setRowCount(0);
        for (Aptitude a : list) {
            tableModel.addRow(new Object[]{
                    a.getAptitudeId(),
                    a.getTopic(),
                    a.getScore(),
                    a.getTestDate(),
                    a.getStatus()
            });
        }
    }

    private void refreshStats() {
        AptitudeReport rep = aptitudeService.getReport();
        lblTotalTests.setText(String.valueOf(rep.getTotalTests()));
        lblExcellent.setText(String.valueOf(rep.getExcellent()));
        lblGood.setText(String.valueOf(rep.getGood()));
        lblAverage.setText(String.valueOf(rep.getAverage() + rep.getNeedsImprovement()));
    }

    private void addRecord() {
        String topic = cmbTopic.getSelectedItem() != null ? cmbTopic.getSelectedItem().toString().trim() : "";
        String scoreStr = txtScore.getText().trim();
        String dateStr = txtTestDate.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (topic.isEmpty() || scoreStr.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreStr);
            if (score < 0 || score > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.", "Invalid Score", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Score must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date testDate;
        try {
            testDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Aptitude apt = new Aptitude();
        apt.setTopic(topic);
        apt.setScore(score);
        apt.setTestDate(testDate);
        apt.setStatus(status);

        boolean ok = aptitudeService.addTestResult(apt);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Aptitude record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(aptitudeService.getAllResults());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save aptitude record.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        String idStr = txtAptitudeId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an aptitude row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String status = cmbStatus.getSelectedItem().toString();

        boolean ok = aptitudeService.updateResult(id, status);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Record status updated to '" + status + "'!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(aptitudeService.getAllResults());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update record in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        String idStr = txtAptitudeId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an aptitude row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete test record ID: " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = aptitudeService.deleteResult(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadTable(aptitudeService.getAllResults());
                refreshStats();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchTopic() {
        String term = txtSearchTopic.getText().trim();
        if (term.isEmpty()) {
            loadTable(aptitudeService.getAllResults());
            return;
        }

        Aptitude apt = aptitudeService.searchTopic(term);
        tableModel.setRowCount(0);
        if (apt != null) {
            tableModel.addRow(new Object[]{
                    apt.getAptitudeId(),
                    apt.getTopic(),
                    apt.getScore(),
                    apt.getTestDate(),
                    apt.getStatus()
            });
        } else {
            JOptionPane.showMessageDialog(this, "No record found for topic: " + term, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterStatus() {
        String selected = cmbFilterStatus.getSelectedItem().toString();
        if ("All".equalsIgnoreCase(selected)) {
            loadTable(aptitudeService.getAllResults());
        } else {
            loadTable(aptitudeService.filterStatus(selected));
        }
    }

    private void clearForm() {
        txtAptitudeId.setText("");
        txtScore.setText("85");
        txtTestDate.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        tblAptitude.clearSelection();
    }
}
