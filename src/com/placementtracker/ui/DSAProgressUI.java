package com.placementtracker.ui;

import java.sql.Date;
import java.util.Scanner;

import com.placementtracker.model.DSAProgress;
import com.placementtracker.service.DSAProgressService;
import com.placementtracker.validation.InputValidator;
import java.util.List;
import com.placementtracker.model.DSAReport;

public class DSAProgressUI {

    private Scanner sc = new Scanner(System.in);
    private DSAProgressService service = new DSAProgressService();

    public void dsaMenu() {

        int choice;

        do {

            System.out.println("\n========== DSA PROGRESS TRACKER ==========");
            System.out.println("1. Add Solved Problem");
            System.out.println("2. View All Problems");
            System.out.println("3. Update Problem Status");
            System.out.println("4. Delete Problem");
            System.out.println("5. Search Problem");
            System.out.println("6. Filter by Difficulty");
            System.out.println("7. Progress Report");
            System.out.println("8. Back");
            System.out.print("Enter your choice : ");

            choice = InputValidator.getInt(sc);

            switch (choice) {

                case 1:
                    addProblem();
                    break;

                case 2:
                    viewProblems();
                    break;
                case 3:
                    updateProblemStatus();
                    break;
                case 4:
                    deleteProblem();
                    break;
                case 5:
                    searchProblem();
                    break;
                case 6:
                    filterProblemsByDifficulty();
                    break;
                case 7:
                    showProgressReport();
                    break;

                case 8:
                    System.out.println("Returning to Dashboard...");
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }

        } while (true);
    }

    public void addProblem() {

        DSAProgress problem = new DSAProgress();

        System.out.print("Enter Topic : ");
        problem.setTopic(sc.nextLine());

        System.out.print("Enter Problem Name : ");
        problem.setProblemName(sc.nextLine());

        System.out.print("Enter Platform : ");
        problem.setPlatform(sc.nextLine());

        System.out.println("Select Difficulty");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.print("Choice : ");

        int difficultyChoice = InputValidator.getInt(sc);

        switch (difficultyChoice) {

            case 1:
                problem.setDifficulty("Easy");
                break;

            case 2:
                problem.setDifficulty("Medium");
                break;

            case 3:
                problem.setDifficulty("Hard");
                break;

            default:
                System.out.println("Invalid Difficulty.");
                return;

        }

        problem.setStatus("Solved");

        problem.setDateSolved(new Date(System.currentTimeMillis()));

        if (service.addProblem(problem)) {

            System.out.println("Problem Added Successfully.");

        } else {

            System.out.println("Failed to Add Problem.");

        }

    }
    public void viewProblems() {

        List<DSAProgress> list = service.getAllProblems();

        if (list.isEmpty()) {

            System.out.println("\nNo Problems Found.");
            return;

        }

        System.out.println("\n==================== DSA PROBLEMS ====================");

        for (DSAProgress problem : list) {

            System.out.println("----------------------------------------------");
            System.out.println("Problem ID   : " + problem.getProblemId());
            System.out.println("Topic        : " + problem.getTopic());
            System.out.println("Problem Name : " + problem.getProblemName());
            System.out.println("Platform     : " + problem.getPlatform());
            System.out.println("Difficulty   : " + problem.getDifficulty());
            System.out.println("Status       : " + problem.getStatus());
            System.out.println("Solved Date  : " + problem.getDateSolved());

        }

        System.out.println("----------------------------------------------");

    }
    public void updateProblemStatus() {

        System.out.print("Enter Problem ID : ");
        int problemId = InputValidator.getInt(sc);

        System.out.println("\nSelect Status");
        System.out.println("1. Solved");
        System.out.println("2. Revision");
        System.out.println("3. Pending");
        System.out.print("Choice : ");

        int choice = InputValidator.getInt(sc);

        String status;

        switch (choice) {

            case 1:
                status = "Solved";
                break;

            case 2:
                status = "Revision";
                break;

            case 3:
                status = "Pending";
                break;

            default:
                System.out.println("Invalid Status.");
                return;

        }

        if (service.updateProblemStatus(problemId, status)) {

            System.out.println("Problem Status Updated Successfully.");

        } else {

            System.out.println("Problem ID Not Found.");

        }

    }
    public void deleteProblem() {

        System.out.print("Enter Problem ID to Delete : ");

        int problemId = InputValidator.getInt(sc);

        System.out.print("Are you sure? (Y/N) : ");

        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("Y")) {

            System.out.println("Deletion Cancelled.");
            return;

        }

        if (service.deleteProblem(problemId)) {

            System.out.println("Problem Deleted Successfully.");

        } else {

            System.out.println("Problem ID Not Found.");

        }

    }
    public void searchProblem() {

        System.out.print("Enter Problem Name : ");

        String problemName = sc.nextLine();

        DSAProgress problem = service.searchProblem(problemName);

        if (problem == null) {

            System.out.println("Problem Not Found.");
            return;

        }

        System.out.println("\n========== PROBLEM DETAILS ==========");
        System.out.println("Problem ID   : " + problem.getProblemId());
        System.out.println("Topic        : " + problem.getTopic());
        System.out.println("Problem Name : " + problem.getProblemName());
        System.out.println("Platform     : " + problem.getPlatform());
        System.out.println("Difficulty   : " + problem.getDifficulty());
        System.out.println("Status       : " + problem.getStatus());
        System.out.println("Solved Date  : " + problem.getDateSolved());

    }
    public void filterProblemsByDifficulty() {

        while (true) {

            System.out.println("\n========== FILTER PROBLEMS ==========");
            System.out.println("1. Easy");
            System.out.println("2. Medium");
            System.out.println("3. Hard");
            System.out.println("4. Back");
            System.out.print("Enter your choice : ");

            int choice = InputValidator.getInt(sc);

            String difficulty;

            switch (choice) {

                case 1:
                    difficulty = "Easy";
                    break;

                case 2:
                    difficulty = "Medium";
                    break;

                case 3:
                    difficulty = "Hard";
                    break;

                case 4:
                    System.out.println("Returning to DSA Menu...");
                    return;

                default:
                    System.out.println("Invalid Choice.");
                    continue;

            }

            List<DSAProgress> list = service.getProblemsByDifficulty(difficulty);

            if (list.isEmpty()) {

                System.out.println("No Problems Found.");

            } else {

                System.out.println("\n========== " + difficulty.toUpperCase() + " PROBLEMS ==========");

                for (DSAProgress problem : list) {

                    System.out.println("------------------------------------");
                    System.out.println("Problem ID   : " + problem.getProblemId());
                    System.out.println("Topic        : " + problem.getTopic());
                    System.out.println("Problem Name : " + problem.getProblemName());
                    System.out.println("Platform     : " + problem.getPlatform());
                    System.out.println("Difficulty   : " + problem.getDifficulty());
                    System.out.println("Status       : " + problem.getStatus());
                    System.out.println("Solved Date  : " + problem.getDateSolved());

                }

            }

        }

    }
    public void showProgressReport() {

        DSAReport report = service.getProgressReport();

        System.out.println("\n========== DSA PROGRESS REPORT ==========");

        System.out.println("Total Problems      : " + report.getTotalProblems());
        System.out.println("Easy Problems       : " + report.getEasyProblems());
        System.out.println("Medium Problems     : " + report.getMediumProblems());
        System.out.println("Hard Problems       : " + report.getHardProblems());

        System.out.println();

        System.out.println("Solved Problems     : " + report.getSolvedProblems());
        System.out.println("Revision Problems   : " + report.getRevisionProblems());
        System.out.println("Pending Problems    : " + report.getPendingProblems());

        System.out.println();

        System.out.printf("Completion %%        : %.2f%%\n",
                report.getCompletionPercentage());

    }

}