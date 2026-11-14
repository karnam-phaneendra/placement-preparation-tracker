package com.placementtracker.ui;

import java.sql.Date;
import java.util.Scanner;
import java.util.List;

import com.placementtracker.model.Interview;
import com.placementtracker.service.InterviewService;
import com.placementtracker.validation.InputValidator;

public class InterviewUI {

    private Scanner sc = new Scanner(System.in);
    private InterviewService service = new InterviewService();


    public void interviewMenu() {

        int choice;

        do {

            System.out.println("\n========== INTERVIEW SCHEDULER ==========");
            System.out.println("1. Schedule Interview");
            System.out.println("2. View Interviews");
            System.out.println("3. Update Result");
            System.out.println("4. Delete Interview");
            System.out.println("5. Search Interview");
            System.out.println("6. Filter by Result");
            System.out.println("7. Interview Report");
            System.out.println("8. Back");
            System.out.print("Enter your choice : ");

            choice = InputValidator.getInt(sc);

            switch (choice) {

                case 1:
                    scheduleInterview();
                    break;

                case 2:
                    viewInterviews();
                    break;
                case 3:
                    updateInterview();
                    break;

                case 4:
                	InterviewUI interviewUI = new InterviewUI();
                    interviewUI.interviewMenu();
                    break;
                case 5:
                    searchInterview();
                    break;

                case 6:
                    filterResult();
                    break;

                case 7:
                    showReport();
                    break;

                case 8:
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }

        } while (true);

    }

    public void scheduleInterview() {

        Interview interview = new Interview();

        System.out.print("Enter Company Name : ");
        interview.setCompanyName(sc.nextLine());

        System.out.print("Enter Round Name : ");
        interview.setRoundName(sc.nextLine());

        interview.setInterviewDate(new Date(System.currentTimeMillis()));

        System.out.println("Select Result");
        System.out.println("1. Pending");
        System.out.println("2. Selected");
        System.out.println("3. Rejected");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        switch (choice) {

            case 1:
                interview.setResult("Pending");
                break;

            case 2:
                interview.setResult("Selected");
                break;

            case 3:
                interview.setResult("Rejected");
                break;

            default:
                System.out.println("Invalid Result.");
                return;

        }

        if (service.scheduleInterview(interview)) {

            System.out.println("Interview Scheduled Successfully.");

        } else {

            System.out.println("Failed to Schedule Interview.");

        }

    }
    public void viewInterviews() {

        List<Interview> list = service.getAllInterviews();

        if (list.isEmpty()) {

            System.out.println("\nNo Interviews Found.");
            return;

        }

        System.out.println("\n========== INTERVIEW LIST ==========");

        for (Interview interview : list) {

            System.out.println("--------------------------------------");
            System.out.println("Interview ID  : " + interview.getInterviewId());
            System.out.println("Company Name  : " + interview.getCompanyName());
            System.out.println("Round Name    : " + interview.getRoundName());
            System.out.println("Interview Date: " + interview.getInterviewDate());
            System.out.println("Result        : " + interview.getResult());

        }

        System.out.println("--------------------------------------");

    }
    public void updateInterview() {

        System.out.print("Enter Interview ID : ");
        int id = InputValidator.getInt(sc);

        System.out.println("\nSelect Result");
        System.out.println("1. Pending");
        System.out.println("2. Selected");
        System.out.println("3. Rejected");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        String result;

        switch (choice) {

            case 1:
                result = "Pending";
                break;

            case 2:
                result = "Selected";
                break;

            case 3:
                result = "Rejected";
                break;

            default:
                System.out.println("Invalid Choice.");
                return;

        }

        if (service.updateInterview(id, result)) {

            System.out.println("Interview Updated Successfully.");

        } else {

            System.out.println("Update Failed.");

        }

    }
    public void deleteInterview() {

        System.out.print("Enter Interview ID : ");

        int id = InputValidator.getInt(sc);

        if (service.deleteInterview(id)) {

            System.out.println("Interview Deleted Successfully.");

        } else {

            System.out.println("Delete Failed.");

        }

    }
    public void searchInterview() {

        System.out.print("Enter Company Name : ");

        String companyName = sc.nextLine();

        Interview interview = service.searchInterview(companyName);

        if (interview == null) {

            System.out.println("No Interview Found.");
            return;

        }

        System.out.println("\n========== INTERVIEW DETAILS ==========");
        System.out.println("Interview ID   : " + interview.getInterviewId());
        System.out.println("Company Name   : " + interview.getCompanyName());
        System.out.println("Round Name     : " + interview.getRoundName());
        System.out.println("Interview Date : " + interview.getInterviewDate());
        System.out.println("Result         : " + interview.getResult());

    }
    public void filterResult() {

        System.out.println("\nSelect Result");
        System.out.println("1. Pending");
        System.out.println("2. Selected");
        System.out.println("3. Rejected");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        String result;

        switch (choice) {

            case 1:
                result = "Pending";
                break;

            case 2:
                result = "Selected";
                break;

            case 3:
                result = "Rejected";
                break;

            default:
                System.out.println("Invalid Choice.");
                return;

        }

        java.util.List<Interview> list = service.filterResult(result);

        if (list.isEmpty()) {

            System.out.println("No Interviews Found.");
            return;

        }

        System.out.println("\n========== FILTERED INTERVIEWS ==========");

        for (Interview interview : list) {

            System.out.println("----------------------------------------");
            System.out.println("Interview ID   : " + interview.getInterviewId());
            System.out.println("Company Name   : " + interview.getCompanyName());
            System.out.println("Round Name     : " + interview.getRoundName());
            System.out.println("Interview Date : " + interview.getInterviewDate());
            System.out.println("Result         : " + interview.getResult());

        }

    }
    public void showReport() {

        System.out.println("\n========== INTERVIEW REPORT ==========");

        com.placementtracker.model.InterviewReport report = service.getReport();

        System.out.println("Total Interviews : " + report.getTotalInterviews());
        System.out.println("Selected         : " + report.getSelected());
        System.out.println("Rejected         : " + report.getRejected());
        System.out.println("Pending          : " + report.getPending());

        System.out.printf("Selection Percentage : %.2f%%\n",
                report.getSelectionPercentage());

    }

}