package com.placementtracker.ui;
import java.util.List;

import java.sql.Date;
import java.util.Scanner;

import com.placementtracker.model.Aptitude;
import com.placementtracker.service.AptitudeService;
import com.placementtracker.validation.InputValidator;

public class AptitudeUI {

    private Scanner sc = new Scanner(System.in);
    private AptitudeService service = new AptitudeService();

    public void aptitudeMenu() {

        int choice;

        do {

            System.out.println("\n========== APTITUDE TRACKER ==========");
            System.out.println("1. Add Test Result");
            System.out.println("2. View Test Results");
            System.out.println("3. Update Test Result");
            System.out.println("4. Delete Test Result");
            System.out.println("5. Search by Topic");
            System.out.println("6. Filter by Status");
            System.out.println("7. Aptitude Report");
            System.out.println("8. Back");
            System.out.print("Enter your choice : ");

            choice = InputValidator.getInt(sc);

            switch (choice) {

                case 1:
                    addTestResult();
                    break;

                case 2:
                    viewResults();
                    break;
                case 3:
                    updateResult();
                    break;

                case 4:
                    deleteResult();
                    break;
                case 5:
                    searchTopic();
                    break;

                case 6:
                    filterStatus();
                    break;

                case 7:
                    showReport();
                    break;

                case 8:
                    System.out.println("Returning to Dashboard...");
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }

        } while (true);

    }

    public void addTestResult() {

        Aptitude aptitude = new Aptitude();

        System.out.print("Enter Topic : ");
        aptitude.setTopic(sc.nextLine());

        System.out.print("Enter Score : ");
        aptitude.setScore(InputValidator.getInt(sc));

        System.out.println("Select Status");
        System.out.println("1. Excellent");
        System.out.println("2. Good");
        System.out.println("3. Average");
        System.out.println("4. Needs Improvement");
        System.out.print("Choice : ");

        int statusChoice = InputValidator.getInt(sc);

        switch (statusChoice) {

            case 1:
                aptitude.setStatus("Excellent");
                break;

            case 2:
                aptitude.setStatus("Good");
                break;

            case 3:
                aptitude.setStatus("Average");
                break;

            case 4:
                aptitude.setStatus("Needs Improvement");
                break;

            default:
                System.out.println("Invalid Status.");
                return;

        }

        aptitude.setTestDate(new Date(System.currentTimeMillis()));

        if (service.addTestResult(aptitude)) {

            System.out.println("Test Result Added Successfully.");

        } else {

            System.out.println("Failed to Add Test Result.");

        }

    }
    public void viewResults() {

        List<Aptitude> list = service.getAllResults();

        if (list.isEmpty()) {

            System.out.println("\nNo Test Results Found.");
            return;

        }

        System.out.println("\n========== TEST RESULTS ==========");

        for (Aptitude aptitude : list) {

            System.out.println("-----------------------------------------");
            System.out.println("Test ID    : " + aptitude.getAptitudeId());
            System.out.println("Topic      : " + aptitude.getTopic());
            System.out.println("Score      : " + aptitude.getScore());
            System.out.println("Test Date  : " + aptitude.getTestDate());
            System.out.println("Status     : " + aptitude.getStatus());

        }

        System.out.println("-----------------------------------------");

    }
    public void updateResult() {

        System.out.print("Enter Test ID to Update : ");
        int id = InputValidator.getInt(sc);

        System.out.println("\nSelect New Status");
        System.out.println("1. Excellent");
        System.out.println("2. Good");
        System.out.println("3. Average");
        System.out.println("4. Needs Improvement");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        String status;

        switch (choice) {

            case 1:
                status = "Excellent";
                break;

            case 2:
                status = "Good";
                break;

            case 3:
                status = "Average";
                break;

            case 4:
                status = "Needs Improvement";
                break;

            default:
                System.out.println("Invalid Status.");
                return;

        }

        if (service.updateResult(id, status)) {

            System.out.println("Test Result Updated Successfully.");

        } else {

            System.out.println("Update Failed.");

        }

    }
    public void deleteResult() {

        System.out.print("Enter Test ID to Delete : ");

        int id = InputValidator.getInt(sc);

        if (service.deleteResult(id)) {

            System.out.println("Test Result Deleted Successfully.");

        } else {

            System.out.println("Delete Failed.");

        }

    }
    public void searchTopic() {

        System.out.print("Enter Topic : ");

        String topic = sc.nextLine();

        Aptitude aptitude = service.searchTopic(topic);

        if (aptitude == null) {

            System.out.println("No Record Found.");
            return;

        }

        System.out.println("\n========== TEST DETAILS ==========");
        System.out.println("Test ID    : " + aptitude.getAptitudeId());
        System.out.println("Topic      : " + aptitude.getTopic());
        System.out.println("Score      : " + aptitude.getScore());
        System.out.println("Test Date  : " + aptitude.getTestDate());
        System.out.println("Status     : " + aptitude.getStatus());

    }
    public void filterStatus() {

        System.out.println("\nSelect Status");
        System.out.println("1. Excellent");
        System.out.println("2. Good");
        System.out.println("3. Average");
        System.out.println("4. Needs Improvement");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        String status;

        switch (choice) {

            case 1:
                status = "Excellent";
                break;

            case 2:
                status = "Good";
                break;

            case 3:
                status = "Average";
                break;

            case 4:
                status = "Needs Improvement";
                break;

            default:
                System.out.println("Invalid Choice.");
                return;

        }

        java.util.List<Aptitude> list = service.filterStatus(status);

        if (list.isEmpty()) {

            System.out.println("No Records Found.");
            return;

        }

        System.out.println("\n========== FILTER RESULTS ==========");

        for (Aptitude aptitude : list) {

            System.out.println("--------------------------------");
            System.out.println("Test ID    : " + aptitude.getAptitudeId());
            System.out.println("Topic      : " + aptitude.getTopic());
            System.out.println("Score      : " + aptitude.getScore());
            System.out.println("Test Date  : " + aptitude.getTestDate());
            System.out.println("Status     : " + aptitude.getStatus());

        }

    }
    public void showReport() {

        System.out.println("\n========== APTITUDE REPORT ==========");

        com.placementtracker.model.AptitudeReport report = service.getReport();

        System.out.println("Total Tests         : " + report.getTotalTests());
        System.out.println("Excellent           : " + report.getExcellent());
        System.out.println("Good                : " + report.getGood());
        System.out.println("Average             : " + report.getAverage());
        System.out.println("Needs Improvement   : " + report.getNeedsImprovement());

        System.out.printf("Excellent Percentage : %.2f%%\n",
                report.getExcellentPercentage());

    }

}