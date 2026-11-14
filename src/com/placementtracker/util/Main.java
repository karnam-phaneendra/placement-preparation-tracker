package com.placementtracker.util;

import com.placementtracker.gui.LoginFrame;
import com.placementtracker.ui.DashboardUI;
import com.placementtracker.ui.LoginUI;
import com.placementtracker.web.WebServer;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Check if user specifically requested console mode
        if (args != null && args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            runConsoleMode();
            return;
        }

        // Check if user requested web-only mode
        if (args != null && args.length > 0 && ("--web".equalsIgnoreCase(args[0]) || "--localhost".equalsIgnoreCase(args[0]))) {
            System.out.println("Starting Placement Preparation Tracker on Localhost Web Server...");
            WebServer.startServer(WebServer.DEFAULT_PORT);
            return;
        }

        // By default, start the Localhost Web Server in background on port 8080
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebServer.startServer(WebServer.DEFAULT_PORT);
                } catch (Exception e) {
                    System.err.println("Notice: Web server could not bind to port 8080: " + e.getMessage());
                }
            }
        }).start();

        // Launch the modern Java Swing GUI application
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }

    private static void runConsoleMode() {
        LoginUI loginUI = new LoginUI();

        while (true) {
            int attempts = 3;
            boolean loginSuccess = false;

            while (attempts > 0) {
                System.out.println();
                System.out.println("Remaining Login Attempts : " + attempts);
                loginSuccess = loginUI.login();

                if (loginSuccess) {
                    break;
                }
                attempts--;
            }

            if (!loginSuccess) {
                System.out.println();
                System.out.println("========================================");
                System.out.println(" Too many failed login attempts.");
                System.out.println(" Application Closed.");
                System.out.println("========================================");
                break;
            }

            DashboardUI dashboard = new DashboardUI();
            boolean logout = dashboard.showDashboard();

            if (!logout) {
                break;
            }
        }
    }
}
