package com.placementtracker.gui;

import com.placementtracker.model.Company;
import com.placementtracker.service.CompanyService;

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

public class CompanyPanel extends JPanel {

    private CompanyService companyService;
    private JTable tblCompanies;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtCompanyId;
    private JTextField txtCompanyName;
    private JTextField txtRole;
    private JTextField txtApplicationDate;
    private JComboBox<String> cmbStatus;

    // Filter and search
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterStatus;

    public CompanyPanel() {
        this.companyService = new CompanyService();
        initComponents();
        loadCompanyTable(companyService.getAllCompanies());
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.APP_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Header and Search/Filter Bar
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setBackground(UITheme.CARD_BG);
        JLabel lblTitle = new JLabel("Company Placement Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Track targeted companies, recruitment job roles, application timelines, and selection status.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topCard.add(titlePanel, BorderLayout.NORTH);

        // Search and Filter controls
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Search Company:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        txtSearch = new JTextField(12);
        txtSearch.setFont(UITheme.FONT_BODY);

        JButton btnSearch = UITheme.createStyledButton("Search", UITheme.PRIMARY, Color.WHITE);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);
        cmbFilterStatus = new JComboBox<>(new String[]{"All", "Applied", "Shortlisted", "Interview", "Selected", "Rejected"});
        cmbFilterStatus.setFont(UITheme.FONT_BODY);

        JButton btnSortAZ = UITheme.createStyledButton("Sort A-Z", new Color(241, 245, 249), UITheme.TEXT_DARK);
        JButton btnSortZA = UITheme.createStyledButton("Sort Z-A", new Color(241, 245, 249), UITheme.TEXT_DARK);
        JButton btnReset = UITheme.createStyledButton("Reset All", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        filterBar.add(lblSearch);
        filterBar.add(txtSearch);
        filterBar.add(btnSearch);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblFilter);
        filterBar.add(cmbFilterStatus);
        filterBar.add(btnSortAZ);
        filterBar.add(btnSortZA);
        filterBar.add(btnReset);

        topCard.add(filterBar, BorderLayout.CENTER);
        add(topCard, BorderLayout.NORTH);

        // Center split: Form on Left (330px), Table on Right
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(UITheme.APP_BG);

        // LEFT: Form Card
        JPanel formCard = UITheme.createCard();
        formCard.setPreferredSize(new Dimension(340, 500));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel lblFormHeader = new JLabel("Company Details Form");
        lblFormHeader.setFont(UITheme.FONT_SUBTITLE);
        lblFormHeader.setForeground(UITheme.TEXT_DARK);
        lblFormHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHint = new JLabel("Click table row to edit or enter new info:");
        lblHint.setFont(UITheme.FONT_SMALL);
        lblHint.setForeground(UITheme.TEXT_MUTED);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCompanyId = new JTextField();
        txtCompanyId.setEditable(false);
        txtCompanyId.setBackground(new Color(241, 245, 249));

        txtCompanyName = new JTextField();
        txtRole = new JTextField();
        txtApplicationDate = new JTextField(LocalDate.now().toString());

        cmbStatus = new JComboBox<>(new String[]{"Applied", "Shortlisted", "Interview", "Selected", "Rejected"});
        cmbStatus.setFont(UITheme.FONT_BODY);

        formCard.add(lblFormHeader);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(lblHint);
        formCard.add(Box.createVerticalStrut(12));

        addFormField(formCard, "Company ID (Auto)", txtCompanyId);
        addFormField(formCard, "Company Name *", txtCompanyName);
        addFormField(formCard, "Job Role / Profile *", txtRole);
        addFormField(formCard, "Application Date (YYYY-MM-DD) *", txtApplicationDate);
        addFormField(formCard, "Application Status", cmbStatus);

        formCard.add(Box.createVerticalStrut(15));

        // Form action buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(UITheme.CARD_BG);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(320, 80));

        JButton btnAdd = UITheme.createStyledButton("Add Company", UITheme.SUCCESS, Color.WHITE);
        JButton btnUpdate = UITheme.createStyledButton("Update Status", UITheme.PRIMARY, Color.WHITE);
        JButton btnDelete = UITheme.createStyledButton("Delete Record", UITheme.DANGER, Color.WHITE);
        JButton btnClear = UITheme.createStyledButton("Clear Fields", new Color(241, 245, 249), UITheme.TEXT_MUTED);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel);
        centerPanel.add(formCard, BorderLayout.WEST);

        // RIGHT: Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Company Name", "Job Role", "Application Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCompanies = new JTable(tableModel);
        UITheme.styleTable(tblCompanies);

        JScrollPane scrollPane = new JScrollPane(tblCompanies);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Event Listeners
        tblCompanies.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCompanies.getSelectedRow();
                if (row >= 0) {
                    txtCompanyId.setText(tableModel.getValueAt(row, 0).toString());
                    txtCompanyName.setText(tableModel.getValueAt(row, 1).toString());
                    txtRole.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                    txtApplicationDate.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                    cmbStatus.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCompany();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCompanyStatus();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCompany();
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
                searchCompany();
            }
        });

        cmbFilterStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterByStatus();
            }
        });

        btnSortAZ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCompanyTable(companyService.getCompaniesSortedByName());
            }
        });

        btnSortZA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCompanyTable(companyService.getCompaniesSortedByNameDesc());
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                cmbFilterStatus.setSelectedIndex(0);
                loadCompanyTable(companyService.getAllCompanies());
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

    private void loadCompanyTable(List<Company> companies) {
        tableModel.setRowCount(0);
        for (Company c : companies) {
            tableModel.addRow(new Object[]{
                    c.getCompanyId(),
                    c.getCompanyName(),
                    c.getRole(),
                    c.getApplicationDate(),
                    c.getStatus()
            });
        }
    }

    private void addCompany() {
        String name = txtCompanyName.getText().trim();
        String role = txtRole.getText().trim();
        String dateStr = txtApplicationDate.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (name.isEmpty() || role.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date appDate;
        try {
            appDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD (e.g. 2026-07-25).", "Date Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Company company = new Company();
        company.setCompanyName(name);
        company.setRole(role);
        company.setApplicationDate(appDate);
        company.setStatus(status);

        boolean ok = companyService.addCompany(company);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Company added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCompanyTable(companyService.getAllCompanies());
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add company record to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCompanyStatus() {
        String idStr = txtCompanyId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a company row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String status = cmbStatus.getSelectedItem().toString();

        boolean ok = companyService.updateCompanyStatus(id, status);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Status updated to '" + status + "' successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            loadCompanyTable(companyService.getAllCompanies());
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCompany() {
        String idStr = txtCompanyId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a company row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String name = txtCompanyName.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete company record: " + name + " (ID: " + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = companyService.deleteCompany(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Company deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadCompanyTable(companyService.getAllCompanies());
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete company.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCompany() {
        String term = txtSearch.getText().trim();
        if (term.isEmpty()) {
            loadCompanyTable(companyService.getAllCompanies());
            return;
        }

        Company company = companyService.searchCompany(term);
        tableModel.setRowCount(0);
        if (company != null) {
            tableModel.addRow(new Object[]{
                    company.getCompanyId(),
                    company.getCompanyName(),
                    company.getRole(),
                    company.getApplicationDate(),
                    company.getStatus()
            });
        } else {
            JOptionPane.showMessageDialog(this, "No company found with name: " + term, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterByStatus() {
        String selected = cmbFilterStatus.getSelectedItem().toString();
        if ("All".equalsIgnoreCase(selected)) {
            loadCompanyTable(companyService.getAllCompanies());
        } else {
            loadCompanyTable(companyService.getCompaniesByStatus(selected));
        }
    }

    private void clearForm() {
        txtCompanyId.setText("");
        txtCompanyName.setText("");
        txtRole.setText("");
        txtApplicationDate.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        tblCompanies.clearSelection();
    }
}
