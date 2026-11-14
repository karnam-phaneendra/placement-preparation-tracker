package com.placementtracker.validation;

import java.util.Scanner;

public class InputValidator {

    public static int getInt(Scanner sc) {

        while (true) {

            try {

                return Integer.parseInt(sc.nextLine());

            } catch (NumberFormatException e) {

                System.out.print("Invalid input. Please enter a number: ");

            }

        }

    }

}