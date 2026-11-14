package com.placementtracker.ui;

import java.sql.Date;
import com.placementtracker.model.Report;

import java.util.Scanner;
import java.util.List;
import com.placementtracker.validation.InputValidator;

import com.placementtracker.model.Company;
import com.placementtracker.service.CompanyService;

public class CompanyUI {

    Scanner sc = new Scanner(System.in);

    CompanyService companyService = new CompanyService();

    public void addCompany() {

        Company company = new Company();

        System.out.print("Company Name: ");
        company.setCompanyName(sc.nextLine());

        System.out.print("Role: ");
        company.setRole(sc.nextLine());

        System.out.print("Application Date (YYYY-MM-DD): ");
        company.setApplicationDate(Date.valueOf(sc.nextLine()));

        System.out.println("Select Status:");
        System.out.println("1. Applied");
        System.out.println("2. Shortlisted");
        System.out.println("3. Interview");
        System.out.println("4. Selected");
        System.out.println("5. Rejected");
        System.out.print("Enter your choice: ");

        int statusChoice = InputValidator.getInt(sc);

        String status = "";

        switch (statusChoice) {

            case 1:
                status = "Applied";
                break;

            case 2:
                status = "Shortlisted";
                break;

            case 3:
                status = "Interview";
                break;

            case 4:
                status = "Selected";
                break;

            case 5:
                status = "Rejected";
                break;

            default:
                System.out.println("Invalid Status.");
                return;
        }

        company.setStatus(status);

        boolean success = companyService.addCompany(company);

        if (success) {
            System.out.println("\nCompany Added Successfully.");
        } else {
            System.out.println("\nFailed to Add Company.");
        }
    }
       public void viewCompanies() {
    	

        List<Company> companyList = companyService.getAllCompanies();

        if (companyList.isEmpty()) {

            System.out.println("\nNo Companies Found.");
            return;

        }

        System.out.println();
        System.out.println("====================================================================================================");
        System.out.printf("%-5s %-25s %-25s %-18s %-15s%n",
                "ID", "Company", "Role", "Applied Date", "Status");
        System.out.println("====================================================================================================");

        for (Company company : companyList) {

            System.out.printf("%-5d %-25s %-25s %-18s %-15s%n",
                    company.getCompanyId(),
                    company.getCompanyName(),
                    company.getRole(),
                    company.getApplicationDate(),
                    company.getStatus());

        }

        System.out.println("====================================================================================================");

    }
    public void updateCompanyStatus() {

        System.out.print("Enter Company ID : ");
        int companyId = InputValidator.getInt(sc);

        System.out.println();
        System.out.println("Select New Status:");
        System.out.println("1. Applied");
        System.out.println("2. Shortlisted");
        System.out.println("3. Interview");
        System.out.println("4. Selected");
        System.out.println("5. Rejected");
        System.out.print("Enter your choice: ");

        int statusChoice = InputValidator.getInt(sc);

        String status = "";

        switch (statusChoice) {

            case 1:
                status = "Applied";
                break;

            case 2:
                status = "Shortlisted";
                break;

            case 3:
                status = "Interview";
                break;

            case 4:
                status = "Selected";
                break;

            case 5:
                status = "Rejected";
                break;

            default:
                System.out.println("Invalid Status.");
                return;
        }

        boolean success = companyService.updateCompanyStatus(companyId, status);

        if (success) {
            System.out.println("\nStatus Updated Successfully.");
        } else {
            System.out.println("\nFailed to Update Status.");
        }

    }
   
    public void deleteCompany() {

        System.out.print("Enter Company ID : ");

        int companyId = InputValidator.getInt(sc);

        boolean success = companyService.deleteCompany(companyId);

        if(success) {

            System.out.println("\nCompany Deleted Successfully.");

        }
        else {

            System.out.println("\nCompany Not Found.");

        }

    }
    public void searchCompany() {

        System.out.print("Enter Company Name : ");

        String companyName = sc.nextLine();

        Company company = companyService.searchCompany(companyName);

        if (company == null) {

            System.out.println("\nCompany Not Found.");

            return;

        }

        System.out.println();
        System.out.println("=========== COMPANY DETAILS ===========");

        System.out.println("Company ID      : " + company.getCompanyId());
        System.out.println("Company Name    : " + company.getCompanyName());
        System.out.println("Role            : " + company.getRole());
        System.out.println("Applied Date    : " + company.getApplicationDate());
        System.out.println("Status          : " + company.getStatus());

    }
    public void showCompanyReport() {

        Report report = companyService.getCompanyReport();

        System.out.println();
        System.out.println("========================================");
        System.out.println("           COMPANY REPORT");
        System.out.println("========================================");

        System.out.println("Total Companies : " + report.getTotalCompanies());
        System.out.println("Applied         : " + report.getApplied());
        System.out.println("Shortlisted     : " + report.getShortlisted());
        System.out.println("Interview       : " + report.getInterview());
        System.out.println("Selected        : " + report.getSelected());
        System.out.println("Rejected        : " + report.getRejected());

        System.out.printf("Success Rate    : %.2f%%\n", report.getSuccessRate());

        System.out.println("========================================");
    }
    public void filterCompaniesByStatus() {

        while (true) {

            System.out.println();
            System.out.println("========== FILTER COMPANIES ==========");
            System.out.println("1. Applied");
            System.out.println("2. Shortlisted");
            System.out.println("3. Interview");
            System.out.println("4. Selected");
            System.out.println("5. Rejected");
            System.out.println("6. Back");
            System.out.print("Enter your choice: ");
            System.out.flush();

            int choice = InputValidator.getInt(sc);

           

            String status = "";

            switch (choice) {

                case 1:
                    status = "Applied";
                    break;

                case 2:
                    status = "Shortlisted";
                    break;

                case 3:
                    status = "Interview";
                    break;

                case 4:
                    status = "Selected";
                    break;

                case 5:
                    status = "Rejected";
                    break;

                case 6:
                    System.out.println("\nReturning to Company Management...");
                    return;

                default:
                    System.out.println("Invalid Choice.");
                    continue;
            }

            List<Company> companyList = companyService.getCompaniesByStatus(status);

            if (companyList.isEmpty()) {

                System.out.println("\nNo Companies Found with Status: " + status);
                continue;
            }

            System.out.println();
            System.out.println("====================================================================================================");
            System.out.printf("%-5s %-25s %-25s %-18s %-15s%n",
                    "ID", "Company", "Role", "Applied Date", "Status");
            System.out.println("====================================================================================================");

            for (Company company : companyList) {

                System.out.printf("%-5d %-25s %-25s %-18s %-15s%n",
                        company.getCompanyId(),
                        company.getCompanyName(),
                        company.getRole(),
                        company.getApplicationDate(),
                        company.getStatus());
            }

            System.out.println("====================================================================================================");
        }
    }
    public void sortCompaniesByName() {

        List<Company> companyList = companyService.getCompaniesSortedByName();

        if (companyList.isEmpty()) {

            System.out.println("\nNo Companies Found.");
            return;

        }

        System.out.println();
        System.out.println("============= COMPANIES (SORTED BY NAME) =============");

        System.out.println("====================================================================================================");
        System.out.printf("%-5s %-25s %-25s %-18s %-15s%n",
                "ID", "Company", "Role", "Applied Date", "Status");
        System.out.println("====================================================================================================");

        for (Company company : companyList) {

            System.out.printf("%-5d %-25s %-25s %-18s %-15s%n",
                    company.getCompanyId(),
                    company.getCompanyName(),
                    company.getRole(),
                    company.getApplicationDate(),
                    company.getStatus());

        }

        System.out.println("====================================================================================================");

    }
    public void sortCompaniesByNameDesc() {

        List<Company> companyList = companyService.getCompaniesSortedByNameDesc();

        if (companyList.isEmpty()) {

            System.out.println("\nNo Companies Found.");
            return;
        }

        System.out.println();
        System.out.println("========= COMPANIES (SORTED BY NAME Z-A) =========");

        System.out.println("====================================================================================================");
        System.out.printf("%-5s %-25s %-25s %-18s %-15s%n",
                "ID", "Company", "Role", "Applied Date", "Status");
        System.out.println("====================================================================================================");

        for (Company company : companyList) {

            System.out.printf("%-5d %-25s %-25s %-18s %-15s%n",
                    company.getCompanyId(),
                    company.getCompanyName(),
                    company.getRole(),
                    company.getApplicationDate(),
                    company.getStatus());
        }

        System.out.println("====================================================================================================");
    }
    public void sortCompanies() {

        while (true) {

            System.out.println();
            System.out.println("========== SORT COMPANIES ==========");
            System.out.println("1. Company Name (A-Z)");
            System.out.println("2. Company Name (Z-A)");
            System.out.println("3. Application Date (Newest First)");
            System.out.println("4. Application Date (Oldest First)");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            System.out.flush();

            int choice = InputValidator.getInt(sc);

            

            switch (choice) {

                case 1:
                    sortCompaniesByName();
                    break;

                case 2:
                    sortCompaniesByNameDesc();
                    break;

                case 3:
                    System.out.println("Coming Soon...");
                    break;

                case 4:
                    System.out.println("Coming Soon...");
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }
}