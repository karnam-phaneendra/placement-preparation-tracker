package com.placementtracker.gui;

import com.placementtracker.model.DSAProgress;
import com.placementtracker.model.DSAReport;
import com.placementtracker.service.DSAProgressService;

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

public class DSAPanel extends JPanel {

    private DSAProgressService dsaService;
    private JTable tblDSA;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtProblemId;
    private JComboBox<String> cmbTopic;
    private JTextField txtProblemName;
    private JComboBox<String> cmbPlatform;
    private JComboBox<String> cmbDifficulty;
    private JComboBox<String> cmbStatus;
    private JTextField txtDateSolved;

    // Stat Labels
    private JLabel lblTotal;
    private JLabel lblSolved;
    private JLabel lblEasy;
    private JLabel lblMedium;

    // Filter controls
    private JTextField txtSearchName;
    private JComboBox<String> cmbFilterDiff;

    public DSAPanel() {
        this.dsaService = new DSAProgressService();
        initComponents();
        loadTable(dsaService.getAllProblems());
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
        JLabel lblTitle = new JLabel("Data Structures & Algorithms (DSA) Progress Tracker");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Log daily coding problem-solving across LeetCode, HackerRank, GeeksforGeeks, and track completion.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        headerCard.add(titlePanel, BorderLayout.CENTER);
        topContainer.add(headerCard, BorderLayout.NORTH);

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setBackground(UITheme.APP_BG);
        statsRow.add(UITheme.createStatCard("TOTAL PROBLEMS", "0", "Coding problems logged", UITheme.PRIMARY));
        statsRow.add(UITheme.createStatCard("SOLVED PROBLEMS", "0", "Successfully completed", UITheme.SUCCESS));
        statsRow.add(UITheme.createStatCard("EASY PROBLEMS", "0", "Foundational patterns", UITheme.INFO));
        statsRow.add(UITheme.createStatCard("MEDIUM / HARD", "0", "Interview tier problems", UITheme.WARNING));

        lblTotal = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(0)).getComponent(1)).getComponent(2);
        lblSolved = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(1)).getComponent(1)).getComponent(2);
        lblEasy = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(2)).getComponent(1)).getComponent(2);
        lblMedium = (JLabel) ((JPanel) ((JPanel) statsRow.getComponent(3)).getComponent(1)).getComponent(2);

        topContainer.add(statsRow, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center Split: Form on Left, Table on Right
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(UITheme.APP_BG);

        // Form Card (Left)
        JPanel formCard = UITheme.createCard();
        formCard.setPreferredSize(new Dimension(340, 520));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel lblFormTitle = new JLabel("Problem Progress Form");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtProblemId = new JTextField();
        txtProblemId.setEditable(false);
        txtProblemId.setBackground(new Color(241, 245, 249));

        cmbTopic = new JComboBox<>(new String[]{
                "Arrays",
                "Strings",
                "Linked List",
                "Stack & Queue",
                "Trees & BST",
                "Dynamic Programming",
                "Graphs",
                "Recursion & Backtracking",
                "Binary Search",
                "Two Pointers & Sliding Window"
        });
        cmbTopic.setEditable(true);

        txtProblemName = new JTextField();
        cmbPlatform = new JComboBox<>(new String[]{"LeetCode", "HackerRank", "GeeksforGeeks", "CodeChef", "Codeforces"});
        cmbDifficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        cmbStatus = new JComboBox<>(new String[]{"Solved", "Revision", "Pending"});
        txtDateSolved = new JTextField(LocalDate.now().toString());

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(10));
        addFormField(formCard, "Problem ID (Auto)", txtProblemId);
        addFormField(formCard, "DSA Topic *", cmbTopic);
        addFormField(formCard, "Problem Name *", txtProblemName);
        addFormField(formCard, "Coding Platform", cmbPlatform);
        addFormField(formCard, "Difficulty Level", cmbDifficulty);
        addFormField(formCard, "Current Status", cmbStatus);
        addFormField(formCard, "Date Solved (YYYY-MM-DD)", txtDateSolved);

        formCard.add(Box.createVerticalStrut(10));

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(UITheme.CARD_BG);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(320, 80));

        JButton btnAdd = UITheme.createStyledButton("Add Problem", UITheme.SUCCESS, Color.WHITE);
        JButton btnUpdate = UITheme.createStyledButton("Update Status", UITheme.PRIMARY, Color.WHITE);
        JButton btnDelete = UITheme.createStyledButton("Delete Problem", UITheme.DANGER, Color.WHITE);
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

        JLabel lblSearch = new JLabel("Search Problem:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        txtSearchName = new JTextField(12);

        JButton btnSearch = UITheme.createStyledButton("Search", UITheme.PRIMARY, Color.WHITE);

        JLabel lblFilter = new JLabel("Difficulty:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);
        cmbFilterDiff = new JComboBox<>(new String[]{"All", "Easy", "Medium", "Hard"});

        JButton btnReset = UITheme.createStyledButton("Reset", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        filterBar.add(lblSearch);
        filterBar.add(txtSearchName);
        filterBar.add(btnSearch);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblFilter);
        filterBar.add(cmbFilterDiff);
        filterBar.add(btnReset);

        tableCard.add(filterBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Topic", "Problem Name", "Platform", "Difficulty", "Status", "Date Solved"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDSA = new JTable(tableModel);
        UITheme.styleTable(tblDSA);

        JScrollPane scrollPane = new JScrollPane(tblDSA);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        tblDSA.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblDSA.getSelectedRow();
                if (row >= 0) {
                    txtProblemId.setText(tableModel.getValueAt(row, 0).toString());
                    cmbTopic.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    txtProblemName.setText(tableModel.getValueAt(row, 2).toString());
                    cmbPlatform.setSelectedItem(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "LeetCode");
                    cmbDifficulty.setSelectedItem(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "Easy");
                    cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                    txtDateSolved.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProblem();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProblem();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProblem();
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
                searchProblem();
            }
        });

        cmbFilterDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterByDifficulty();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchName.setText("");
                cmbFilterDiff.setSelectedIndex(0);
                loadTable(dsaService.getAllProblems());
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
        field.setMaximumSize(new Dimension(320, 32));
        field.setPreferredSize(new Dimension(320, 32));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(new CompoundBorder(
                    new LineBorder(UITheme.CARD_BORDER, 1, true),
                    new EmptyBorder(3, 8, 3, 8)
            ));
        }

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(2));
        parent.add(field);
        parent.add(Box.createVerticalStrut(6));
    }

    private void loadTable(List<DSAProgress> list) {
        tableModel.setRowCount(0);
        for (DSAProgress p : list) {
            tableModel.addRow(new Object[]{
                    p.getProblemId(),
                    p.getTopic(),
                    p.getProblemName(),
                    p.getPlatform(),
                    p.getDifficulty(),
                    p.getStatus(),
                    p.getDateSolved()
            });
        }
    }

    private void refreshStats() {
        DSAReport rep = dsaService.getProgressReport();
        lblTotal.setText(String.valueOf(rep.getTotalProblems()));
        lblSolved.setText(rep.getSolvedProblems() + " (" + String.format("%.0f%%", rep.getCompletionPercentage()) + ")");
        lblEasy.setText(String.valueOf(rep.getEasyProblems()));
        lblMedium.setText(String.valueOf(rep.getMediumProblems() + rep.getHardProblems()));
    }

    private void addProblem() {
        String topic = cmbTopic.getSelectedItem() != null ? cmbTopic.getSelectedItem().toString().trim() : "";
        String name = txtProblemName.getText().trim();
        String platform = cmbPlatform.getSelectedItem().toString();
        String diff = cmbDifficulty.getSelectedItem().toString();
        String status = cmbStatus.getSelectedItem().toString();
        String dateStr = txtDateSolved.getText().trim();

        if (topic.isEmpty() || name.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter topic, problem name, and date.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date solvedDate;
        try {
            solvedDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DSAProgress p = new DSAProgress();
        p.setTopic(topic);
        p.setProblemName(name);
        p.setPlatform(platform);
        p.setDifficulty(diff);
        p.setStatus(status);
        p.setDateSolved(solvedDate);

        boolean ok = dsaService.addProblem(p);
        if (ok) {
            JOptionPane.showMessageDialog(this, "DSA problem saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dsaService.getAllProblems());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save DSA problem.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProblem() {
        String idStr = txtProblemId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a problem row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String status = cmbStatus.getSelectedItem().toString();

        boolean ok = dsaService.updateProblemStatus(id, status);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Problem status updated to '" + status + "'!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dsaService.getAllProblems());
            refreshStats();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update problem in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProblem() {
        String idStr = txtProblemId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a problem row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete problem ID: " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = dsaService.deleteProblem(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Problem deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadTable(dsaService.getAllProblems());
                refreshStats();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete problem.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchProblem() {
        String term = txtSearchName.getText().trim();
        if (term.isEmpty()) {
            loadTable(dsaService.getAllProblems());
            return;
        }

        DSAProgress p = dsaService.searchProblem(term);
        tableModel.setRowCount(0);
        if (p != null) {
            tableModel.addRow(new Object[]{
                    p.getProblemId(),
                    p.getTopic(),
                    p.getProblemName(),
                    p.getPlatform(),
                    p.getDifficulty(),
                    p.getStatus(),
                    p.getDateSolved()
            });
        } else {
            JOptionPane.showMessageDialog(this, "No problem found with name: " + term, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterByDifficulty() {
        String selected = cmbFilterDiff.getSelectedItem().toString();
        if ("All".equalsIgnoreCase(selected)) {
            loadTable(dsaService.getAllProblems());
        } else {
            loadTable(dsaService.getProblemsByDifficulty(selected));
        }
    }

    private void clearForm() {
        txtProblemId.setText("");
        txtProblemName.setText("");
        txtDateSolved.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        tblDSA.clearSelection();
    }
}
