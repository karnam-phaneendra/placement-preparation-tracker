package com.placementtracker.ui;
import com.placementtracker.model.Report;
import java.util.Scanner;
import com.placementtracker.validation.InputValidator;
import com.placementtracker.service.CompanyService;
import com.placementtracker.service.DSAProgressService;
import com.placementtracker.service.AptitudeService;
import com.placementtracker.service.InterviewService;
import com.placementtracker.model.DSAReport;
import com.placementtracker.model.DSAReport;
import com.placementtracker.model.AptitudeReport;
import com.placementtracker.model.InterviewReport;


public class ReportUI {

    private Scanner sc = new Scanner(System.in);
    
    private CompanyService companyService = new CompanyService();
    private DSAProgressService dsaService = new DSAProgressService();
    private AptitudeService aptitudeService = new AptitudeService();
    private InterviewService interviewService = new InterviewService();
    

    public void reportMenu() {

        while (true) {

            System.out.println("\n========================================");
            System.out.println("              REPORTS");
            System.out.println("========================================");
            System.out.println("1. Company Report");
            System.out.println("2. DSA Report");
            System.out.println("3. Aptitude Report");
            System.out.println("4. Interview Report");
            System.out.println("5. Overall Dashboard Report");
            System.out.println("6. Back");
            System.out.print("Enter your choice : ");

            int choice = InputValidator.getInt(sc);

            switch (choice) {

                case 1:
                    showCompanyReport();
                    break;

                case 2:
                    showDSAReport();
                    break;

                case 3:
                    showAptitudeReport();
                    break;

                case 4:
                    showInterviewReport();
                    break;

                case 5:
                    showOverallReport();
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }
        }
    }
    public void showReportsMenu() {

        while (true) {

            System.out.println();
            System.out.println("=======================================");
            System.out.println("               REPORTS");
            System.out.println("=======================================");
            System.out.println("1. Company Report");
            System.out.println("2. DSA Report");
            System.out.println("3. Aptitude Report");
            System.out.println("4. Interview Report");
            System.out.println("5. Overall Report");
            System.out.println("6. Back");
            System.out.print("Enter your choice : ");

            int choice = InputValidator.getInt(sc);

            switch (choice) {

                case 1:
                    showCompanyReport();
                    break;

                case 2:
                    showDSAReport();
                    break;

                case 3:
                    showAptitudeReport();
                    break;

                case 4:
                    showInterviewReport();
                    break;

                case 5:
                    showOverallReport();
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }
    private void showCompanyReport() {

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

        System.out.printf("Success Rate    : %.2f%%\n",
                report.getSuccessRate());

        System.out.println("========================================");
    }
    private void showDSAReport() {

        DSAReport report = dsaService.getProgressReport();

        System.out.println();
        System.out.println("========================================");
        System.out.println("             DSA REPORT");
        System.out.println("========================================");

        System.out.println("Total Problems   : " + report.getTotalProblems());
        System.out.println("Solved           : " + report.getSolvedProblems());
        System.out.println("Pending          : " + report.getPendingProblems());

        System.out.printf("Completion Rate  : %.2f%%\n",
                report.getCompletionPercentage());

        System.out.println("========================================");

    }

    private void showAptitudeReport() {

        AptitudeReport report = aptitudeService.getReport();

        System.out.println();
        System.out.println("========================================");
        System.out.println("          APTITUDE REPORT");
        System.out.println("========================================");

        System.out.println("Total Tests        : " + report.getTotalTests());
        System.out.println("Excellent          : " + report.getExcellent());
        System.out.println("Good               : " + report.getGood());
        System.out.println("Average            : " + report.getAverage());
        System.out.println("Needs Improvement  : " + report.getNeedsImprovement());

        System.out.printf("Excellent Rate     : %.2f%%\n",
                report.getExcellentPercentage());

        System.out.println("========================================");

    }

    private void showInterviewReport() {

        InterviewReport report = interviewService.getReport();

        System.out.println();
        System.out.println("========================================");
        System.out.println("         INTERVIEW REPORT");
        System.out.println("========================================");

        System.out.println("Total Interviews : " + report.getTotalInterviews());
        System.out.println("Selected         : " + report.getSelected());
        System.out.println("Rejected         : " + report.getRejected());
        System.out.println("Pending          : " + report.getPending());

        System.out.printf("Selection Rate   : %.2f%%\n",
                report.getSelectionPercentage());

        System.out.println("========================================");

    }

    private void showOverallReport() {

        Report company = companyService.getCompanyReport();
        DSAReport dsa = dsaService.getProgressReport();
        AptitudeReport aptitude = aptitudeService.getReport();
        InterviewReport interview = interviewService.getReport();

        System.out.println();
        System.out.println("========================================");
        System.out.println("      PLACEMENT DASHBOARD REPORT");
        System.out.println("========================================");

        System.out.println("Companies Applied     : " + company.getTotalCompanies());
        System.out.println("Companies Selected    : " + company.getSelected());

        System.out.println();

        System.out.println("DSA Problems Solved   : " + dsa.getSolvedProblems());
        System.out.println("DSA Completion        : "
                + String.format("%.2f%%", dsa.getCompletionPercentage()));

        System.out.println();

        System.out.println("Aptitude Tests        : " + aptitude.getTotalTests());
        System.out.println("Excellent Tests       : " + aptitude.getExcellent());

        System.out.println();

        System.out.println("Interviews            : " + interview.getTotalInterviews());
        System.out.println("Selected              : " + interview.getSelected());

        System.out.println("========================================");

    }
}