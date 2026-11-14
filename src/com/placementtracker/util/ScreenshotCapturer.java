package com.placementtracker.util;

import com.placementtracker.gui.LoginFrame;
import com.placementtracker.gui.MainFrame;
import com.placementtracker.model.Student;
import com.placementtracker.service.LoginService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScreenshotCapturer {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        System.out.println("Starting automated genuine screenshot capture of running application...");

        // 1. Capture Login Frame
        try {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.pack();
            loginFrame.setSize(480, 580);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);

            // Allow layout and rendering
            Thread.sleep(600);
            saveComponentScreenshot(loginFrame, new File(screenshotDir, "01_login.png"));
            System.out.println("Captured: 01_login.png");
            loginFrame.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fetch genuine student from DB for MainFrame session
        LoginService loginService = new LoginService();
        Student student = loginService.login("kphani8937@gmail.com", "Phani@123");
        if (student == null) {
            student = new Student();
            student.setStudentId(1);
            student.setName("Karnam Phaneendra");
            student.setEmail("kphani8937@gmail.com");
        }

        try {
            MainFrame mainFrame = new MainFrame(student);
            mainFrame.pack();
            mainFrame.setSize(1180, 780);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            Thread.sleep(800);

            // 02: Dashboard
            mainFrame.showCard("DASHBOARD");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "02_dashboard.png"));
            System.out.println("Captured: 02_dashboard.png");

            // 03: Company Management
            mainFrame.showCard("COMPANY");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "03_company_management.png"));
            System.out.println("Captured: 03_company_management.png");

            // 04: Aptitude Tracker
            mainFrame.showCard("APTITUDE");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "04_aptitude_tracker.png"));
            System.out.println("Captured: 04_aptitude_tracker.png");

            // 05: DSA Progress Tracker
            mainFrame.showCard("DSA");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "05_dsa_progress.png"));
            System.out.println("Captured: 05_dsa_progress.png");

            // 06: Interview Schedule
            mainFrame.showCard("INTERVIEW");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "06_interview_schedule.png"));
            System.out.println("Captured: 06_interview_schedule.png");

            // 07: Progress Reports
            mainFrame.showCard("REPORTS");
            Thread.sleep(400);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "07_progress_reports.png"));
            System.out.println("Captured: 07_progress_reports.png");

            // 08: Database Operations
            mainFrame.showCard("DATABASE");
            mainFrame.getDbPanel().runDiagnosticCheck();
            mainFrame.getDbPanel().runTableCountsQuery();
            mainFrame.getDbPanel().fetchStudentInfo();
            mainFrame.getDbPanel().fetchRecentActivities();
            Thread.sleep(500);
            saveComponentScreenshot(mainFrame, new File(screenshotDir, "08_database_operations.png"));
            System.out.println("Captured: 08_database_operations.png");

            mainFrame.dispose();
            System.out.println("\nAll 8 application screenshots successfully captured and saved in screenshots/ !");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void saveComponentScreenshot(Component comp, File file) {
        try {
            int width = comp.getWidth();
            int height = comp.getHeight();
            if (width <= 0) width = 1180;
            if (height <= 0) height = 780;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            comp.printAll(g2d);
            g2d.dispose();

            ImageIO.write(image, "png", file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
