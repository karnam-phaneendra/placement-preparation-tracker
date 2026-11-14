package com.placementtracker.service;
import com.placementtracker.model.Report;
import java.util.List;
import com.placementtracker.model.Company;

import java.util.List;

import com.placementtracker.dao.CompanyDAO;
import com.placementtracker.model.Company;

public class CompanyService {

    CompanyDAO companyDAO = new CompanyDAO();

    public boolean addCompany(Company company) {

        return companyDAO.addCompany(company);

    }

    public List<Company> getAllCompanies() {

        return companyDAO.getAllCompanies();

    }

    public boolean updateCompanyStatus(int companyId, String status) {

        return companyDAO.updateCompanyStatus(companyId, status);

    }
    public boolean deleteCompany(int companyId) {

        return companyDAO.deleteCompany(companyId);

    }
    public Company searchCompany(String companyName) {

        return companyDAO.searchCompany(companyName);

    }
    public Report getCompanyReport() {

        return companyDAO.getCompanyReport();

    }
    public List<Company> getCompaniesByStatus(String status) {

        return companyDAO.getCompaniesByStatus(status);

    }
    public List<Company> getCompaniesSortedByName() {

        return companyDAO.getCompaniesSortedByName();

    }
    public List<Company> getCompaniesSortedByNameDesc() {

        return companyDAO.getCompaniesSortedByNameDesc();

    }

}