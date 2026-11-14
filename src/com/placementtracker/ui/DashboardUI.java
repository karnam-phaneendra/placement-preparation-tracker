package com.placementtracker.ui;

import java.util.Scanner;

import java.util.List;
import com.placementtracker.validation.InputValidator;
import com.placementtracker.ui.AptitudeUI;

public class DashboardUI {

    Scanner sc = new Scanner(System.in);

    CompanyUI companyUI = new CompanyUI();
    private DSAProgressUI dsaUI = new DSAProgressUI();
    private AptitudeUI aptitudeUI = new AptitudeUI();
    private InterviewUI interviewUI = new InterviewUI();
    private ReportUI reportUI = new ReportUI();

    public boolean showDashboard() {

        int choice;

        do {

            System.out.println();
            System.out.println("=========================================");
            System.out.println("   PLACEMENT PREPARATION TRACKER");
            System.out.println("=========================================");
            System.out.println("1. Company Management");
            System.out.println("2. DSA Progress Tracker");
            System.out.println("3. Aptitude Tracker");
            System.out.println("4. Interview Scheduler");
            System.out.println("5. Reports");
            System.out.println("6. Logout");
            System.out.println("7. Exit");
            System.out.print("Enter your choice : ");
            System.out.flush();

            choice = InputValidator.getInt(sc);

            

            switch (choice) {

                case 1:
                    companyMenu();
                    break;

                case 2:
                    dsaUI.dsaMenu();
                    break;

                case 3:
                    aptitudeUI.aptitudeMenu();
                    break;

                case 4:
                    interviewUI.interviewMenu();
                    break;

                case 5:
                    reportUI.showReportsMenu();
                    break;

                case 6:

                    System.out.println();
                    System.out.println("Logged Out Successfully.");

                    return true;

                case 7:

                    System.out.println();
                    System.out.println("Thank You for using Placement Preparation Tracker.");

                    return false;

                default:
                    System.out.println("Invalid Choice.");

            }

        } while (true);

    }

    public void companyMenu() {

        int choice;

        do {

        	System.out.println("========== COMPANY MANAGEMENT ==========");
        	System.out.println("1. Add Company");
        	System.out.println("2. View Companies");
        	System.out.println("3. Update Company Status");
        	System.out.println("4. Delete Company");
        	System.out.println("5. Search Company");
        	System.out.println("6. Filter Companies by Status");
        	System.out.println("7. Sort Companies");
        	System.out.println("8. Back");
        	System.out.print("Enter your choice : ");

        	choice = InputValidator.getInt(sc);

            

            switch (choice) {

                case 1:
                    companyUI.addCompany();
                    break;

                case 2:
                    companyUI.viewCompanies();
                    break;

                case 3:
                    companyUI.updateCompanyStatus();
                    break;

                case 4:
                    companyUI.deleteCompany();
                    break;

                case 5:
                    companyUI.searchCompany();
                    break;

                case 6:
                    companyUI.filterCompaniesByStatus();
                    break;

                case 7:
                    companyUI.sortCompanies();
                    break;

                case 8:
                    System.out.println("\nReturning to Dashboard...");
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }

        } while (true);

     // Never reached, but required by compiler
     // return false;
    }

 }