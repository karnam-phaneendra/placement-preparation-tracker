package com.placementtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.placementtracker.database.DBConnection;
import com.placementtracker.model.Company;
import com.placementtracker.model.Report;
public class CompanyDAO {

    public boolean addCompany(Company company) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO company(company_name, role, application_date, status) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, company.getCompanyName());
            ps.setString(2, company.getRole());
            ps.setDate(3, company.getApplicationDate());
            ps.setString(4, company.getStatus());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }   // <-- You were missing this closing brace

    public List<Company> getAllCompanies() {

    	List<Company> companyList = new ArrayList<Company>();

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM company";

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Company company = new Company();

                company.setCompanyId(rs.getInt("company_id"));
                company.setCompanyName(rs.getString("company_name"));
                company.setRole(rs.getString("role"));
                company.setApplicationDate(rs.getDate("application_date"));
                company.setStatus(rs.getString("status"));

                companyList.add(company);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return companyList;

    }
    public boolean updateCompanyStatus(int companyId, String status) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "UPDATE company SET status=? WHERE company_id=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, companyId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }
    public boolean deleteCompany(int companyId) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "DELETE FROM company WHERE company_id=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, companyId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }
    public Company searchCompany(String companyName) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM company WHERE company_name = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, companyName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Company company = new Company();

                company.setCompanyId(rs.getInt("company_id"));
                company.setCompanyName(rs.getString("company_name"));
                company.setRole(rs.getString("role"));
                company.setApplicationDate(rs.getDate("application_date"));
                company.setStatus(rs.getString("status"));

                return company;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }
    public Report getCompanyReport() {

        Report report = new Report();

        try {

            Connection con = DBConnection.getConnection();

            // Total Companies
            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                report.setTotalCompanies(rs.getInt(1));
            }

            // Applied
            ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company WHERE status='Applied'");
            rs = ps.executeQuery();

            if (rs.next()) {
                report.setApplied(rs.getInt(1));
            }

            // Shortlisted
            ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company WHERE status='Shortlisted'");
            rs = ps.executeQuery();

            if (rs.next()) {
                report.setShortlisted(rs.getInt(1));
            }

            // Interview
            ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company WHERE status='Interview'");
            rs = ps.executeQuery();

            if (rs.next()) {
                report.setInterview(rs.getInt(1));
            }

            // Selected
            ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company WHERE status='Selected'");
            rs = ps.executeQuery();

            if (rs.next()) {
                report.setSelected(rs.getInt(1));
            }

            // Rejected
            ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM company WHERE status='Rejected'");
            rs = ps.executeQuery();

            if (rs.next()) {
                report.setRejected(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return report;
    }
    public List<Company> getCompaniesByStatus(String status) {

        List<Company> companyList = new ArrayList<>();

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM company WHERE status = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Company company = new Company();

                company.setCompanyId(rs.getInt("company_id"));
                company.setCompanyName(rs.getString("company_name"));
                company.setRole(rs.getString("role"));
                company.setApplicationDate(rs.getDate("application_date"));
                company.setStatus(rs.getString("status"));

                companyList.add(company);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyList;
    }
    public List<Company> getCompaniesSortedByName() {

        List<Company> companyList = new ArrayList<Company>();

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM company ORDER BY company_name ASC";

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Company company = new Company();

                company.setCompanyId(rs.getInt("company_id"));
                company.setCompanyName(rs.getString("company_name"));
                company.setRole(rs.getString("role"));
                company.setApplicationDate(rs.getDate("application_date"));
                company.setStatus(rs.getString("status"));

                companyList.add(company);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyList;
    }
    public List<Company> getCompaniesSortedByNameDesc() {

        List<Company> companyList = new ArrayList<Company>();

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM company ORDER BY company_name DESC";

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Company company = new Company();

                company.setCompanyId(rs.getInt("company_id"));
                company.setCompanyName(rs.getString("company_name"));
                company.setRole(rs.getString("role"));
                company.setApplicationDate(rs.getDate("application_date"));
                company.setStatus(rs.getString("status"));

                companyList.add(company);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return companyList;
    }
}