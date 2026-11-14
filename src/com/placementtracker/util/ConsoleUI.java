package com.placementtracker.util;

public class ConsoleUI {

    public static void printHeader(String title) {

        System.out.println();
        System.out.println("============================================================");
        System.out.printf("%30s%n", title);
        System.out.println("============================================================");
    }

    public static void printFooter() {

        System.out.println("============================================================");
    }

    public static void printSubHeader(String title) {

        System.out.println();
        System.out.println("-------------------- " + title + " --------------------");
    }

}