package com.placementtracker.web;

import com.placementtracker.dao.*;
import com.placementtracker.database.DBConnection;
import com.placementtracker.model.*;
import com.placementtracker.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class WebServer {

    public static final int DEFAULT_PORT = 8080;
    private static int port = DEFAULT_PORT;
    private static HttpServer server;

    private static final StudentDAO studentDAO = new StudentDAO();
    private static final CompanyDAO companyDAO = new CompanyDAO();
    private static final AptitudeDAO aptitudeDAO = new AptitudeDAO();
    private static final DSAProgressDAO dsaDAO = new DSAProgressDAO();
    private static final InterviewDAO interviewDAO = new InterviewDAO();

    private static final CompanyService companyService = new CompanyService();
    private static final AptitudeService aptitudeService = new AptitudeService();
    private static final DSAProgressService dsaService = new DSAProgressService();
    private static final InterviewService interviewService = new InterviewService();

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        startServer(port);
    }

    public static void startServer(int serverPort) {
        port = serverPort;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Static and API routes
            server.createContext("/", new StaticHandler());
            server.createContext("/api/status", new StatusHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/dashboard", new DashboardHandler());
            server.createContext("/api/companies", new CompanyHandler());
            server.createContext("/api/companies/modify", new CompanyModifyHandler());
            server.createContext("/api/companies/delete", new CompanyDeleteHandler());
            server.createContext("/api/aptitude", new AptitudeHandler());
            server.createContext("/api/aptitude/modify", new AptitudeModifyHandler());
            server.createContext("/api/aptitude/delete", new AptitudeDeleteHandler());
            server.createContext("/api/dsa", new DSAHandler());
            server.createContext("/api/dsa/modify", new DSAModifyHandler());
            server.createContext("/api/dsa/delete", new DSADeleteHandler());
            server.createContext("/api/interviews", new InterviewHandler());
            server.createContext("/api/interviews/modify", new InterviewModifyHandler());
            server.createContext("/api/interviews/delete", new InterviewDeleteHandler());
            server.createContext("/api/reports", new ReportsHandler());

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();

            System.out.println("===============================================================================");
            System.out.println("  PLACEMENT PREPARATION TRACKER - LOCALHOST WEB SERVER STARTED");
            System.out.println("===============================================================================");
            System.out.println("  Local URL   : http://localhost:" + port + "/");
            System.out.println("  Database    : jdbc:mysql://localhost:3306/placement_tracker");
            System.out.println("  Candidate   : Karnam Phaneendra (PIN: 24A85A6102)");
            System.out.println("  Login Email : kphani8937@gmail.com");
            System.out.println("===============================================================================");
            System.out.println("Server is listening on port " + port + ". Press Ctrl+C to terminate.");

        } catch (IOException e) {
            System.err.println("Failed to start HTTP server on port " + port + ": " + e.getMessage());
            if (port == DEFAULT_PORT) {
                System.out.println("Retrying on fallback port 8081...");
                startServer(8081);
            }
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("Localhost Web Server stopped.");
        }
    }

    // =========================================================================
    // HTTP HANDLERS
    // =========================================================================

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.equals("/index.html")) {
                byte[] htmlBytes = null;
                java.io.File[] candidates = new java.io.File[] {
                    new java.io.File("web/index.html"),
                    new java.io.File("PlacementPreparationTracker/web/index.html"),
                    new java.io.File("C:/Users/DELL/workspace/PlacementPreparationTracker/web/index.html")
                };
                for (java.io.File f : candidates) {
                    if (f.exists() && f.isFile()) {
                        try {
                            htmlBytes = java.nio.file.Files.readAllBytes(f.toPath());
                            break;
                        } catch (Exception ignored) {}
                    }
                }
                if (htmlBytes == null) {
                    htmlBytes = getIndexHtml().getBytes(StandardCharsets.UTF_8);
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, htmlBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(htmlBytes);
                os.close();
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}", "application/json");
            }
        }
    }

    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean dbConnected = false;
            int studentCount = 0, companyCount = 0, aptitudeCount = 0, dsaCount = 0, interviewCount = 0;
            String dbVersion = "Unknown";

            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    dbConnected = true;
                    dbVersion = conn.getMetaData().getDatabaseProductVersion();
                    try (Statement stmt = conn.createStatement()) {
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student");
                        if (rs.next()) studentCount = rs.getInt(1);
                        rs = stmt.executeQuery("SELECT COUNT(*) FROM company");
                        if (rs.next()) companyCount = rs.getInt(1);
                        rs = stmt.executeQuery("SELECT COUNT(*) FROM aptitude");
                        if (rs.next()) aptitudeCount = rs.getInt(1);
                        rs = stmt.executeQuery("SELECT COUNT(*) FROM dsa_progress");
                        if (rs.next()) dsaCount = rs.getInt(1);
                        rs = stmt.executeQuery("SELECT COUNT(*) FROM interview");
                        if (rs.next()) interviewCount = rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String json = "{"
                    + "\"status\":\"" + (dbConnected ? "ONLINE" : "OFFLINE") + "\","
                    + "\"database\":\"placement_tracker\","
                    + "\"dbConnected\":" + dbConnected + ","
                    + "\"dbVersion\":\"" + escapeJson(dbVersion) + "\","
                    + "\"port\":" + port + ","
                    + "\"counts\":{"
                    + "\"student\":" + studentCount + ","
                    + "\"company\":" + companyCount + ","
                    + "\"aptitude\":" + aptitudeCount + ","
                    + "\"dsa\":" + dsaCount + ","
                    + "\"interview\":" + interviewCount
                    + "}"
                    + "}";

            sendResponse(exchange, 200, json, "application/json");
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }

            Map<String, String> params = parseFormData(exchange);
            String email = params.get("email");
            String password = params.get("password");

            if (email == null || password == null) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing email or password\"}", "application/json");
                return;
            }

            Student s = studentDAO.login(email.trim(), password.trim());
            if (s != null) {
                String json = "{"
                        + "\"success\":true,"
                        + "\"message\":\"Login successful\","
                        + "\"student\":{"
                        + "\"id\":" + s.getStudentId() + ","
                        + "\"name\":\"" + escapeJson(s.getName()) + "\","
                        + "\"email\":\"" + escapeJson(s.getEmail()) + "\","
                        + "\"pin\":\"24A85A6102\""
                        + "}"
                        + "}";
                sendResponse(exchange, 200, json, "application/json");
            } else {
                sendResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password. Access denied.\"}", "application/json");
            }
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Report companyReport = companyService.getCompanyReport();
            AptitudeReport aptitudeReport = aptitudeService.getReport();
            DSAReport dsaReport = dsaService.getProgressReport();
            InterviewReport interviewReport = interviewService.getReport();

            int totalCompanies = (companyReport != null) ? companyReport.getTotalCompanies() : 0;
            int totalAptitude = (aptitudeReport != null) ? aptitudeReport.getTotalTests() : 0;
            int totalDSA = (dsaReport != null) ? dsaReport.getTotalProblems() : 0;
            int solvedDSA = (dsaReport != null) ? dsaReport.getSolvedProblems() : 0;
            int totalInterviews = (interviewReport != null) ? interviewReport.getTotalInterviews() : 0;

            String json = "{"
                    + "\"totalCompanies\":" + totalCompanies + ","
                    + "\"totalAptitude\":" + totalAptitude + ","
                    + "\"totalDSA\":" + totalDSA + ","
                    + "\"solvedDSA\":" + solvedDSA + ","
                    + "\"totalInterviews\":" + totalInterviews + ","
                    + "\"companyStats\":{"
                    + "\"applied\":" + (companyReport != null ? companyReport.getApplied() : 0) + ","
                    + "\"shortlisted\":" + (companyReport != null ? companyReport.getShortlisted() : 0) + ","
                    + "\"interview\":" + (companyReport != null ? companyReport.getInterview() : 0) + ","
                    + "\"selected\":" + (companyReport != null ? companyReport.getSelected() : 0) + ","
                    + "\"rejected\":" + (companyReport != null ? companyReport.getRejected() : 0)
                    + "},"
                    + "\"aptitudeStats\":{"
                    + "\"excellent\":" + (aptitudeReport != null ? aptitudeReport.getExcellent() : 0) + ","
                    + "\"good\":" + (aptitudeReport != null ? aptitudeReport.getGood() : 0) + ","
                    + "\"average\":" + (aptitudeReport != null ? aptitudeReport.getAverage() : 0) + ","
                    + "\"needsImprovement\":" + (aptitudeReport != null ? aptitudeReport.getNeedsImprovement() : 0)
                    + "},"
                    + "\"dsaStats\":{"
                    + "\"easy\":" + (dsaReport != null ? dsaReport.getEasyProblems() : 0) + ","
                    + "\"medium\":" + (dsaReport != null ? dsaReport.getMediumProblems() : 0) + ","
                    + "\"hard\":" + (dsaReport != null ? dsaReport.getHardProblems() : 0)
                    + "},"
                    + "\"interviewStats\":{"
                    + "\"pending\":" + (interviewReport != null ? interviewReport.getPending() : 0) + ","
                    + "\"selected\":" + (interviewReport != null ? interviewReport.getSelected() : 0) + ","
                    + "\"rejected\":" + (interviewReport != null ? interviewReport.getRejected() : 0)
                    + "}"
                    + "}";

            sendResponse(exchange, 200, json, "application/json");
        }
    }

    static class CompanyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                List<Company> list = companyDAO.getAllCompanies();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Company c = list.get(i);
                    sb.append("{")
                            .append("\"id\":").append(c.getCompanyId()).append(",")
                            .append("\"companyName\":\"").append(escapeJson(c.getCompanyName())).append("\",")
                            .append("\"role\":\"").append(escapeJson(c.getRole())).append("\",")
                            .append("\"applicationDate\":\"").append(c.getApplicationDate() != null ? c.getApplicationDate().toString() : "").append("\",")
                            .append("\"status\":\"").append(escapeJson(c.getStatus())).append("\"")
                            .append("}");
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");

            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> p = parseFormData(exchange);
                String name = p.get("companyName");
                String role = p.get("role");
                String dateStr = p.get("applicationDate");
                String status = p.get("status");

                if (name == null || name.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Company name is required\"}", "application/json");
                    return;
                }

                Company c = new Company();
                c.setCompanyName(name.trim());
                c.setRole(role != null ? role.trim() : "Software Engineer");
                try {
                    c.setApplicationDate(dateStr != null && !dateStr.isEmpty() ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
                } catch (Exception e) {
                    c.setApplicationDate(new Date(System.currentTimeMillis()));
                }
                c.setStatus(status != null ? status.trim() : "Applied");

                boolean ok = companyDAO.addCompany(c);
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Company added successfully" : "Failed to add company") + "\"}", "application/json");
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
            }
        }
    }

    static class CompanyDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    boolean ok = companyDAO.deleteCompany(id);
                    sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Deleted company " + id : "Delete failed") + "\"}", "application/json");
                    return;
                } catch (NumberFormatException ignored) {}
            }
            sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid company ID\"}", "application/json");
        }
    }

    static class AptitudeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                List<Aptitude> list = aptitudeDAO.getAllResults();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Aptitude a = list.get(i);
                    sb.append("{")
                            .append("\"id\":").append(a.getAptitudeId()).append(",")
                            .append("\"topic\":\"").append(escapeJson(a.getTopic())).append("\",")
                            .append("\"score\":").append(a.getScore()).append(",")
                            .append("\"testDate\":\"").append(a.getTestDate() != null ? a.getTestDate().toString() : "").append("\",")
                            .append("\"status\":\"").append(escapeJson(a.getStatus())).append("\"")
                            .append("}");
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");

            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> p = parseFormData(exchange);
                String topic = p.get("topic");
                String scoreStr = p.get("score");
                String dateStr = p.get("testDate");
                String status = p.get("status");

                if (topic == null || topic.trim().isEmpty() || scoreStr == null) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Topic and Score are required\"}", "application/json");
                    return;
                }

                int score = 0;
                try {
                    score = Integer.parseInt(scoreStr.trim());
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Score must be an integer (0-100)\"}", "application/json");
                    return;
                }

                if (status == null || status.trim().isEmpty()) {
                    if (score >= 85) status = "Excellent";
                    else if (score >= 70) status = "Good";
                    else if (score >= 50) status = "Average";
                    else status = "Needs Improvement";
                }

                Aptitude a = new Aptitude();
                a.setTopic(topic.trim());
                a.setScore(score);
                try {
                    a.setTestDate(dateStr != null && !dateStr.isEmpty() ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
                } catch (Exception e) {
                    a.setTestDate(new Date(System.currentTimeMillis()));
                }
                a.setStatus(status.trim());

                boolean ok = aptitudeDAO.addTestResult(a);
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Aptitude test recorded" : "Failed to record test") + "\"}", "application/json");
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
            }
        }
    }

    static class DSAHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                List<DSAProgress> list = dsaDAO.getAllProblems();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    DSAProgress d = list.get(i);
                    sb.append("{")
                            .append("\"id\":").append(d.getProblemId()).append(",")
                            .append("\"topic\":\"").append(escapeJson(d.getTopic())).append("\",")
                            .append("\"problemName\":\"").append(escapeJson(d.getProblemName())).append("\",")
                            .append("\"platform\":\"").append(escapeJson(d.getPlatform())).append("\",")
                            .append("\"difficulty\":\"").append(escapeJson(d.getDifficulty())).append("\",")
                            .append("\"status\":\"").append(escapeJson(d.getStatus())).append("\",")
                            .append("\"dateSolved\":\"").append(d.getDateSolved() != null ? d.getDateSolved().toString() : "").append("\"")
                            .append("}");
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");

            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> p = parseFormData(exchange);
                String topic = p.get("topic");
                String problemName = p.get("problemName");
                String platform = p.get("platform");
                String difficulty = p.get("difficulty");
                String status = p.get("status");
                String dateStr = p.get("dateSolved");

                if (problemName == null || problemName.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Problem Name is required\"}", "application/json");
                    return;
                }

                DSAProgress d = new DSAProgress();
                d.setTopic(topic != null ? topic.trim() : "Arrays");
                d.setProblemName(problemName.trim());
                d.setPlatform(platform != null ? platform.trim() : "LeetCode");
                d.setDifficulty(difficulty != null ? difficulty.trim() : "Medium");
                d.setStatus(status != null ? status.trim() : "Solved");
                try {
                    d.setDateSolved(dateStr != null && !dateStr.isEmpty() ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
                } catch (Exception e) {
                    d.setDateSolved(new Date(System.currentTimeMillis()));
                }

                boolean ok = dsaDAO.addProblem(d);
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "DSA problem logged" : "Failed to log problem") + "\"}", "application/json");
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
            }
        }
    }

    static class InterviewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                List<Interview> list = interviewDAO.getAllInterviews();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Interview inv = list.get(i);
                    sb.append("{")
                            .append("\"id\":").append(inv.getInterviewId()).append(",")
                            .append("\"companyName\":\"").append(escapeJson(inv.getCompanyName())).append("\",")
                            .append("\"roundName\":\"").append(escapeJson(inv.getRoundName())).append("\",")
                            .append("\"interviewDate\":\"").append(inv.getInterviewDate() != null ? inv.getInterviewDate().toString() : "").append("\",")
                            .append("\"result\":\"").append(escapeJson(inv.getResult())).append("\"")
                            .append("}");
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");

            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> p = parseFormData(exchange);
                String companyName = p.get("companyName");
                String roundName = p.get("roundName");
                String dateStr = p.get("interviewDate");
                String result = p.get("result");

                if (companyName == null || companyName.trim().isEmpty() || roundName == null || roundName.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Company Name and Round are required\"}", "application/json");
                    return;
                }

                Interview inv = new Interview();
                inv.setCompanyName(companyName.trim());
                inv.setRoundName(roundName.trim());
                try {
                    inv.setInterviewDate(dateStr != null && !dateStr.isEmpty() ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
                } catch (Exception e) {
                    inv.setInterviewDate(new Date(System.currentTimeMillis()));
                }
                inv.setResult(result != null ? result.trim() : "Pending");

                boolean ok = interviewDAO.scheduleInterview(inv);
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Interview scheduled successfully" : "Failed to schedule interview") + "\"}", "application/json");
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
            }
        }
    }

    static class CompanyModifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            String name = p.get("companyName");
            String role = p.get("role");
            String dateStr = p.get("applicationDate");
            String status = p.get("status");

            if (idStr == null || name == null || role == null) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required fields\"}", "application/json");
                return;
            }
            try {
                int id = Integer.parseInt(idStr.trim());
                Date date;
                try {
                    date = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis());
                } catch (Exception e) {
                    date = new Date(System.currentTimeMillis());
                }
                boolean ok = companyDAO.updateCompanyFull(id, name.trim(), role.trim(), date, status != null ? status.trim() : "Applied");
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Company record updated successfully" : "Update failed") + "\"}", "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    static class InterviewModifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            String companyName = p.get("companyName");
            String roundName = p.get("roundName");
            String dateStr = p.get("interviewDate");
            String result = p.get("result");

            if (idStr == null || companyName == null || roundName == null) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required fields\"}", "application/json");
                return;
            }
            try {
                int id = Integer.parseInt(idStr.trim());
                Date date;
                try {
                    date = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis());
                } catch (Exception e) {
                    date = new Date(System.currentTimeMillis());
                }
                boolean ok = interviewDAO.updateInterviewFull(id, companyName.trim(), roundName.trim(), date, result != null ? result.trim() : "Pending");
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Interview record updated successfully" : "Update failed") + "\"}", "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    static class InterviewDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    boolean ok = interviewDAO.deleteInterview(id);
                    sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Deleted interview record" : "Delete failed") + "\"}", "application/json");
                    return;
                } catch (NumberFormatException ignored) {}
            }
            sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid interview ID\"}", "application/json");
        }
    }

    static class AptitudeModifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            String topic = p.get("topic");
            String scoreStr = p.get("score");
            String dateStr = p.get("testDate");
            String status = p.get("status");

            if (idStr == null || topic == null || scoreStr == null) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required fields\"}", "application/json");
                return;
            }
            try {
                int id = Integer.parseInt(idStr.trim());
                int score = Integer.parseInt(scoreStr.trim());
                Date date;
                try {
                    date = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis());
                } catch (Exception e) {
                    date = new Date(System.currentTimeMillis());
                }
                if (status == null || status.trim().isEmpty()) {
                    if (score >= 85) status = "Excellent";
                    else if (score >= 70) status = "Good";
                    else if (score >= 50) status = "Average";
                    else status = "Needs Improvement";
                }
                boolean ok = aptitudeDAO.updateResultFull(id, topic.trim(), score, date, status.trim());
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Aptitude record updated successfully" : "Update failed") + "\"}", "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    static class AptitudeDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    boolean ok = aptitudeDAO.deleteResult(id);
                    sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Deleted aptitude record" : "Delete failed") + "\"}", "application/json");
                    return;
                } catch (NumberFormatException ignored) {}
            }
            sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid aptitude ID\"}", "application/json");
        }
    }

    static class DSAModifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            String topic = p.get("topic");
            String problemName = p.get("problemName");
            String platform = p.get("platform");
            String difficulty = p.get("difficulty");
            String status = p.get("status");
            String dateStr = p.get("dateSolved");

            if (idStr == null || topic == null || problemName == null) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required fields\"}", "application/json");
                return;
            }
            try {
                int id = Integer.parseInt(idStr.trim());
                Date date;
                try {
                    date = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis());
                } catch (Exception e) {
                    date = new Date(System.currentTimeMillis());
                }
                boolean ok = dsaDAO.updateProblemFull(id, topic.trim(), problemName.trim(),
                        platform != null ? platform.trim() : "LeetCode",
                        difficulty != null ? difficulty.trim() : "Medium",
                        status != null ? status.trim() : "Solved", date);
                sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "DSA problem updated successfully" : "Update failed") + "\"}", "application/json");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}", "application/json");
            }
        }
    }

    static class DSADeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }
            Map<String, String> p = parseFormData(exchange);
            String idStr = p.get("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    boolean ok = dsaDAO.deleteProblem(id);
                    sendResponse(exchange, 200, "{\"success\":" + ok + ",\"message\":\"" + (ok ? "Deleted DSA problem" : "Delete failed") + "\"}", "application/json");
                    return;
                } catch (NumberFormatException ignored) {}
            }
            sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid problem ID\"}", "application/json");
        }
    }

    static class ReportsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Report companyReport = companyService.getCompanyReport();
            AptitudeReport aptitudeReport = aptitudeService.getReport();
            DSAReport dsaReport = dsaService.getProgressReport();
            InterviewReport interviewReport = interviewService.getReport();

            int totalCompanies = companyReport != null ? companyReport.getTotalCompanies() : 0;
            int selectedCompanies = companyReport != null ? companyReport.getSelected() : 0;
            double companyConversion = companyReport != null ? companyReport.getSuccessRate() : 0.0;

            int totalDsa = dsaReport != null ? dsaReport.getTotalProblems() : 0;
            int solvedDsa = dsaReport != null ? dsaReport.getSolvedProblems() : 0;
            double dsaRate = dsaReport != null ? dsaReport.getCompletionPercentage() : 0.0;

            int totalAptitude = aptitudeReport != null ? aptitudeReport.getTotalTests() : 0;
            int excellentAptitude = aptitudeReport != null ? aptitudeReport.getExcellent() : 0;
            double aptitudeRate = aptitudeReport != null ? aptitudeReport.getExcellentPercentage() : 0.0;

            int totalInterviews = interviewReport != null ? interviewReport.getTotalInterviews() : 0;
            int selectedInterviews = interviewReport != null ? interviewReport.getSelected() : 0;
            double interviewRate = interviewReport != null ? interviewReport.getSelectionPercentage() : 0.0;

            // Placement Readiness Index Formula
            double readinessIndex = (0.25 * (totalCompanies > 0 ? 100 : 0))
                    + (0.35 * dsaRate)
                    + (0.25 * aptitudeRate)
                    + (0.15 * interviewRate);

            String json = "{"
                    + "\"companyConversion\":" + String.format(Locale.US, "%.1f", companyConversion) + ","
                    + "\"dsaCompletion\":" + String.format(Locale.US, "%.1f", dsaRate) + ","
                    + "\"aptitudeExcellence\":" + String.format(Locale.US, "%.1f", aptitudeRate) + ","
                    + "\"interviewClearance\":" + String.format(Locale.US, "%.1f", interviewRate) + ","
                    + "\"readinessIndex\":" + String.format(Locale.US, "%.1f", readinessIndex) + ","
                    + "\"summary\":{"
                    + "\"companies\":{"
                        + "\"total\":" + totalCompanies + ","
                        + "\"applied\":" + (companyReport != null ? companyReport.getApplied() : 0) + ","
                        + "\"shortlisted\":" + (companyReport != null ? companyReport.getShortlisted() : 0) + ","
                        + "\"interview\":" + (companyReport != null ? companyReport.getInterview() : 0) + ","
                        + "\"selected\":" + selectedCompanies + ","
                        + "\"rejected\":" + (companyReport != null ? companyReport.getRejected() : 0)
                    + "},"
                    + "\"dsa\":{"
                        + "\"total\":" + totalDsa + ","
                        + "\"solved\":" + solvedDsa + ","
                        + "\"easy\":" + (dsaReport != null ? dsaReport.getEasyProblems() : 0) + ","
                        + "\"medium\":" + (dsaReport != null ? dsaReport.getMediumProblems() : 0) + ","
                        + "\"hard\":" + (dsaReport != null ? dsaReport.getHardProblems() : 0) + ","
                        + "\"revision\":" + (dsaReport != null ? dsaReport.getRevisionProblems() : 0) + ","
                        + "\"pending\":" + (dsaReport != null ? dsaReport.getPendingProblems() : 0)
                    + "},"
                    + "\"aptitude\":{"
                        + "\"total\":" + totalAptitude + ","
                        + "\"excellent\":" + excellentAptitude + ","
                        + "\"good\":" + (aptitudeReport != null ? aptitudeReport.getGood() : 0) + ","
                        + "\"average\":" + (aptitudeReport != null ? aptitudeReport.getAverage() : 0) + ","
                        + "\"needsImprovement\":" + (aptitudeReport != null ? aptitudeReport.getNeedsImprovement() : 0)
                    + "},"
                    + "\"interview\":{"
                        + "\"total\":" + totalInterviews + ","
                        + "\"selected\":" + selectedInterviews + ","
                        + "\"pending\":" + (interviewReport != null ? interviewReport.getPending() : 0) + ","
                        + "\"rejected\":" + (interviewReport != null ? interviewReport.getRejected() : 0)
                    + "}"
                    + "}"
                    + "}";

            sendResponse(exchange, 200, json, "application/json");
        }
    }

    // =========================================================================
    // UTILITIES
    // =========================================================================

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> result = new HashMap<>();
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        String body = new String(baos.toByteArray(), StandardCharsets.UTF_8).trim();

        if (body.startsWith("{") && body.endsWith("}")) {
            // Very simple JSON parser for key-value string pairs
            body = body.substring(1, body.length() - 1);
            String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replace("\"", "");
                    String v = kv[1].trim().replace("\"", "");
                    result.put(k, v);
                }
            }
        } else if (!body.isEmpty()) {
            // URL-encoded form data
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    String k = URLDecoder.decode(kv[0], "UTF-8");
                    String v = URLDecoder.decode(kv[1], "UTF-8");
                    result.put(k, v);
                }
            }
        }
        return result;
    }

    private static void sendResponse(HttpExchange exchange, int status, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // =========================================================================
    // RESPONSIVE HTML5 / CSS3 / JAVASCRIPT SINGLE PAGE APPLICATION
    // =========================================================================

    private static String getIndexHtml() {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\" />\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "  <title>Placement Preparation Tracker — Localhost Portal</title>\n"
                + "  <style>\n"
                + "    :root {\n"
                + "      --primary: #2563EB;\n"
                + "      --primary-dark: #1D4ED8;\n"
                + "      --bg-dark: #1E293B;\n"
                + "      --sidebar-bg: #0F172A;\n"
                + "      --app-bg: #F8FAFC;\n"
                + "      --card-bg: #FFFFFF;\n"
                + "      --border-color: #E2E8F0;\n"
                + "      --text-main: #1E293B;\n"
                + "      --text-muted: #64748B;\n"
                + "      --success: #10B981;\n"
                + "      --warning: #F59E0B;\n"
                + "      --danger: #EF4444;\n"
                + "    }\n"
                + "    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }\n"
                + "    body { background-color: var(--app-bg); color: var(--text-main); height: 100vh; display: flex; overflow: hidden; }\n"
                + "    \n"
                + "    /* Sidebar */\n"
                + "    .sidebar { width: 260px; background: var(--sidebar-bg); color: #fff; display: flex; flex-direction: column; flex-shrink: 0; border-right: 1px solid #1e293b; }\n"
                + "    .sidebar-header { padding: 24px 20px; border-bottom: 1px solid #1e293b; display: flex; align-items: center; gap: 12px; }\n"
                + "    .logo-badge { width: 38px; height: 38px; border-radius: 8px; background: linear-gradient(135deg, #2563EB, #3B82F6); display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 18px; color: #fff; }\n"
                + "    .brand-title { font-size: 16px; font-weight: 700; color: #F8FAFC; line-height: 1.2; }\n"
                + "    .brand-subtitle { font-size: 11px; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }\n"
                + "    .nav-links { list-style: none; padding: 16px 12px; flex: 1; overflow-y: auto; }\n"
                + "    .nav-item { margin-bottom: 4px; }\n"
                + "    .nav-btn { width: 100%; display: flex; align-items: center; gap: 12px; padding: 11px 16px; background: transparent; border: none; border-radius: 8px; color: #94A3B8; font-size: 14px; font-weight: 500; cursor: pointer; text-align: left; transition: all 0.15s; }\n"
                + "    .nav-btn:hover { background: rgba(255,255,255,0.06); color: #F8FAFC; }\n"
                + "    .nav-btn.active { background: var(--primary); color: #fff; font-weight: 600; }\n"
                + "    .user-footer { padding: 16px 20px; border-top: 1px solid #1e293b; background: #0b1120; font-size: 12px; }\n"
                + "    .user-name { font-weight: 600; color: #F1F5F9; }\n"
                + "    .user-pin { color: #64748B; font-size: 11px; }\n"
                + "    \n"
                + "    /* Main Content */\n"
                + "    .main-wrapper { flex: 1; display: flex; flex-direction: column; overflow: hidden; }\n"
                + "    .topbar { height: 64px; background: #fff; border-bottom: 1px solid var(--border-color); display: flex; align-items: center; justify-content: space-between; padding: 0 28px; flex-shrink: 0; }\n"
                + "    .topbar-title { font-size: 18px; font-weight: 700; color: var(--text-main); }\n"
                + "    .topbar-right { display: flex; align-items: center; gap: 16px; }\n"
                + "    .db-status-pill { display: flex; align-items: center; gap: 6px; font-size: 12px; padding: 4px 10px; border-radius: 20px; background: #ECFDF5; color: #065F46; font-weight: 600; }\n"
                + "    .status-dot { width: 8px; height: 8px; border-radius: 50%; background: #10B981; animation: pulse 2s infinite; }\n"
                + "    @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }\n"
                + "    \n"
                + "    .content-area { flex: 1; padding: 24px 28px; overflow-y: auto; }\n"
                + "    \n"
                + "    /* Components */\n"
                + "    .view-section { display: none; }\n"
                + "    .view-section.active { display: block; }\n"
                + "    .card { background: #fff; border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; margin-bottom: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }\n"
                + "    .card-title { font-size: 15px; font-weight: 700; margin-bottom: 14px; color: var(--text-main); display: flex; align-items: center; justify-content: space-between; }\n"
                + "    \n"
                + "    /* KPI Grid */\n"
                + "    .kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 16px; margin-bottom: 24px; }\n"
                + "    .kpi-card { background: #fff; border: 1px solid var(--border-color); border-radius: 12px; padding: 18px 20px; display: flex; align-items: center; gap: 16px; }\n"
                + "    .kpi-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 16px; font-weight: bold; }\n"
                + "    .kpi-num { font-size: 24px; font-weight: 800; color: var(--text-main); }\n"
                + "    .kpi-label { font-size: 12px; color: var(--text-muted); font-weight: 500; text-transform: uppercase; letter-spacing: 0.5px; }\n"
                + "    \n"
                + "    /* Tables */\n"
                + "    .table-container { overflow-x: auto; }\n"
                + "    table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13.5px; }\n"
                + "    th { background: #F1F5F9; color: #475569; font-weight: 600; padding: 10px 14px; border-bottom: 1px solid var(--border-color); }\n"
                + "    td { padding: 11px 14px; border-bottom: 1px solid var(--border-color); color: #334155; }\n"
                + "    tr:hover { background-color: #F8FAFC; }\n"
                + "    \n"
                + "    /* Badges */\n"
                + "    .badge { display: inline-block; padding: 3px 9px; border-radius: 12px; font-size: 11.5px; font-weight: 600; text-transform: capitalize; }\n"
                + "    .badge-applied, .badge-pending { background: #FEF3C7; color: #92400E; }\n"
                + "    .badge-selected, .badge-solved, .badge-excellent { background: #D1FAE5; color: #065F46; }\n"
                + "    .badge-rejected, .badge-hard, .badge-needs-improvement { background: #FEE2E2; color: #991B1B; }\n"
                + "    .badge-interview, .badge-good, .badge-medium { background: #DBEAFE; color: #1E40AF; }\n"
                + "    .badge-shortlisted, .badge-average, .badge-easy { background: #E0E7FF; color: #3730A3; }\n"
                + "    \n"
                + "    /* Forms */\n"
                + "    .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 14px; margin-bottom: 14px; }\n"
                + "    .form-group { display: flex; flex-direction: column; gap: 5px; }\n"
                + "    .form-group label { font-size: 12.5px; font-weight: 600; color: #475569; }\n"
                + "    .form-control { padding: 8px 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 13.5px; outline: none; transition: border 0.15s; }\n"
                + "    .form-control:focus { border-color: var(--primary); }\n"
                + "    .btn { padding: 9px 18px; border-radius: 6px; border: none; font-size: 13.5px; font-weight: 600; cursor: pointer; transition: background 0.15s; }\n"
                + "    .btn-primary { background: var(--primary); color: #fff; }\n"
                + "    .btn-primary:hover { background: var(--primary-dark); }\n"
                + "    .btn-danger { background: var(--danger); color: #fff; padding: 4px 10px; font-size: 12px; border-radius: 4px; }\n"
                + "    .btn-danger:hover { background: #DC2626; }\n"
                + "    \n"
                + "    /* Modal Login */\n"
                + "    #loginModal { position: fixed; inset: 0; background: rgba(15,23,42,0.8); display: none; align-items: center; justify-content: center; z-index: 1000; }\n"
                + "    .login-box { width: 380px; background: #fff; border-radius: 12px; padding: 32px 28px; box-shadow: 0 10px 25px rgba(0,0,0,0.3); text-align: center; }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <!-- Sidebar -->\n"
                + "  <aside class=\"sidebar\">\n"
                + "    <div class=\"sidebar-header\">\n"
                + "      <div class=\"logo-badge\">P</div>\n"
                + "      <div>\n"
                + "        <div class=\"brand-title\">Placement Tracker</div>\n"
                + "        <div class=\"brand-subtitle\">APSCHE - ExcelR</div>\n"
                + "      </div>\n"
                + "    </div>\n"
                + "    <ul class=\"nav-links\">\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn active\" onclick=\"showView('dashboard')\">[=] Dashboard</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('companies')\">[C] Companies</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('aptitude')\">[A] Aptitude Tracker</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('dsa')\">[D] DSA Progress</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('interviews')\">[I] Interviews</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('reports')\">[R] Progress Reports</button></li>\n"
                + "      <li class=\"nav-item\"><button class=\"nav-btn\" onclick=\"showView('diagnostics')\">[*] MySQL Diagnostic</button></li>\n"
                + "    </ul>\n"
                + "    <div class=\"user-footer\">\n"
                + "      <div class=\"user-name\" id=\"userDisplay\">Karnam Phaneendra</div>\n"
                + "      <div class=\"user-pin\">PIN: 24A85A6102 • MySQL 9.7</div>\n"
                + "    </div>\n"
                + "  </aside>\n"
                + "\n"
                + "  <!-- Main Area -->\n"
                + "  <div class=\"main-wrapper\">\n"
                + "    <header class=\"topbar\">\n"
                + "      <div class=\"topbar-title\" id=\"pageTitle\">Executive Dashboard</div>\n"
                + "      <div class=\"topbar-right\">\n"
                + "        <div class=\"db-status-pill\">\n"
                + "          <span class=\"status-dot\"></span>\n"
                + "          <span id=\"dbStatusText\">MySQL: localhost:3306</span>\n"
                + "        </div>\n"
                + "        <button class=\"btn btn-primary\" style=\"padding: 6px 14px; font-size: 12px;\" onclick=\"refreshAll()\">Refresh</button>\n"
                + "      </div>\n"
                + "    </header>\n"
                + "\n"
                + "    <main class=\"content-area\">\n"
                + "      <!-- DASHBOARD VIEW -->\n"
                + "      <section id=\"view-dashboard\" class=\"view-section active\">\n"
                + "        <div class=\"card\" style=\"background: linear-gradient(135deg, #1E293B, #0F172A); color: #fff;\">\n"
                + "          <h2 style=\"font-size: 20px; font-weight: 700;\">Welcome back, <span id=\"welcomeName\">Karnam Phaneendra</span>!</h2>\n"
                + "          <p style=\"font-size: 13.5px; color: #94A3B8; margin-top: 6px;\">Campus Placement Roadmap • Full Stack Java Development • Database: placement_tracker</p>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"kpi-grid\">\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div class=\"kpi-icon\" style=\"background: #EFF6FF; color: #2563EB;\">CMP</div>\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"kpiCompanies\">0</div>\n"
                + "              <div class=\"kpi-label\">Total Companies</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div class=\"kpi-icon\" style=\"background: #ECFDF5; color: #10B981;\">APT</div>\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"kpiAptitude\">0</div>\n"
                + "              <div class=\"kpi-label\">Aptitude Tests</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div class=\"kpi-icon\" style=\"background: #F5F3FF; color: #7C3AED;\">DSA</div>\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"kpiDsa\">0/0</div>\n"
                + "              <div class=\"kpi-label\">DSA Solved</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div class=\"kpi-icon\" style=\"background: #FFFBEB; color: #D97706;\">INT</div>\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"kpiInterviews\">0</div>\n"
                + "              <div class=\"kpi-label\">Interviews</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Target Companies Overview</div>\n"
                + "          <div class=\"table-container\">\n"
                + "            <table id=\"dashCompanyTable\">\n"
                + "              <thead><tr><th>ID</th><th>Company</th><th>Role</th><th>Date</th><th>Status</th></tr></thead>\n"
                + "              <tbody><tr><td colspan=\"5\" style=\"text-align:center;\">Loading MySQL data...</td></tr></tbody>\n"
                + "            </table>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- COMPANIES VIEW -->\n"
                + "      <section id=\"view-companies\" class=\"view-section\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Add New Corporate Opportunity</div>\n"
                + "          <form id=\"companyForm\" onsubmit=\"addCompany(event)\">\n"
                + "            <div class=\"form-grid\">\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Company Name</label>\n"
                + "                <input type=\"text\" id=\"cName\" class=\"form-control\" placeholder=\"e.g. Infosys, TCS\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Job Role</label>\n"
                + "                <input type=\"text\" id=\"cRole\" class=\"form-control\" placeholder=\"e.g. Systems Engineer\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Application Date</label>\n"
                + "                <input type=\"date\" id=\"cDate\" class=\"form-control\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Status</label>\n"
                + "                <select id=\"cStatus\" class=\"form-control\">\n"
                + "                  <option value=\"Applied\">Applied</option>\n"
                + "                  <option value=\"Shortlisted\">Shortlisted</option>\n"
                + "                  <option value=\"Interview\">Interview</option>\n"
                + "                  <option value=\"Selected\">Selected</option>\n"
                + "                  <option value=\"Rejected\">Rejected</option>\n"
                + "                </select>\n"
                + "              </div>\n"
                + "            </div>\n"
                + "            <button type=\"submit\" class=\"btn btn-primary\">+ Save Company</button>\n"
                + "          </form>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Stored Corporate Opportunities</div>\n"
                + "          <div class=\"table-container\">\n"
                + "            <table id=\"companyTable\">\n"
                + "              <thead><tr><th>ID</th><th>Company</th><th>Role</th><th>Date</th><th>Status</th><th>Action</th></tr></thead>\n"
                + "              <tbody></tbody>\n"
                + "            </table>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- APTITUDE VIEW -->\n"
                + "      <section id=\"view-aptitude\" class=\"view-section\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Record Mock Assessment Test</div>\n"
                + "          <form id=\"aptForm\" onsubmit=\"addAptitude(event)\">\n"
                + "            <div class=\"form-grid\">\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Assessment Topic</label>\n"
                + "                <input type=\"text\" id=\"aTopic\" class=\"form-control\" placeholder=\"e.g. Quantitative - Time & Work\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Score (0-100)</label>\n"
                + "                <input type=\"number\" id=\"aScore\" class=\"form-control\" min=\"0\" max=\"100\" placeholder=\"e.g. 85\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Assessment Date</label>\n"
                + "                <input type=\"date\" id=\"aDate\" class=\"form-control\" required />\n"
                + "              </div>\n"
                + "            </div>\n"
                + "            <button type=\"submit\" class=\"btn btn-primary\">+ Log Assessment</button>\n"
                + "          </form>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Aptitude Assessment History</div>\n"
                + "          <div class=\"table-container\">\n"
                + "            <table id=\"aptTable\">\n"
                + "              <thead><tr><th>ID</th><th>Topic</th><th>Score</th><th>Test Date</th><th>Performance Grade</th></tr></thead>\n"
                + "              <tbody></tbody>\n"
                + "            </table>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- DSA VIEW -->\n"
                + "      <section id=\"view-dsa\" class=\"view-section\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Log Coding Problem</div>\n"
                + "          <form id=\"dsaForm\" onsubmit=\"addDsa(event)\">\n"
                + "            <div class=\"form-grid\">\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Topic</label>\n"
                + "                <input type=\"text\" id=\"dTopic\" class=\"form-control\" placeholder=\"e.g. Arrays, Trees, Dynamic Programming\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Problem Title</label>\n"
                + "                <input type=\"text\" id=\"dName\" class=\"form-control\" placeholder=\"e.g. Two Sum\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Platform</label>\n"
                + "                <select id=\"dPlatform\" class=\"form-control\">\n"
                + "                  <option value=\"LeetCode\">LeetCode</option>\n"
                + "                  <option value=\"HackerRank\">HackerRank</option>\n"
                + "                  <option value=\"GeeksforGeeks\">GeeksforGeeks</option>\n"
                + "                </select>\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Difficulty</label>\n"
                + "                <select id=\"dDiff\" class=\"form-control\">\n"
                + "                  <option value=\"Easy\">Easy</option>\n"
                + "                  <option value=\"Medium\" selected>Medium</option>\n"
                + "                  <option value=\"Hard\">Hard</option>\n"
                + "                </select>\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Status</label>\n"
                + "                <select id=\"dStatus\" class=\"form-control\">\n"
                + "                  <option value=\"Solved\" selected>Solved</option>\n"
                + "                  <option value=\"Revision\">Revision</option>\n"
                + "                  <option value=\"Pending\">Pending</option>\n"
                + "                </select>\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Date</label>\n"
                + "                <input type=\"date\" id=\"dDate\" class=\"form-control\" required />\n"
                + "              </div>\n"
                + "            </div>\n"
                + "            <button type=\"submit\" class=\"btn btn-primary\">+ Log Problem</button>\n"
                + "          </form>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">DSA Solved Problem Records</div>\n"
                + "          <div class=\"table-container\">\n"
                + "            <table id=\"dsaTable\">\n"
                + "              <thead><tr><th>ID</th><th>Topic</th><th>Problem Title</th><th>Platform</th><th>Difficulty</th><th>Status</th><th>Date</th></tr></thead>\n"
                + "              <tbody></tbody>\n"
                + "            </table>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- INTERVIEWS VIEW -->\n"
                + "      <section id=\"view-interviews\" class=\"view-section\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Schedule Campus Interview Round</div>\n"
                + "          <form id=\"invForm\" onsubmit=\"addInterview(event)\">\n"
                + "            <div class=\"form-grid\">\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Company Name</label>\n"
                + "                <input type=\"text\" id=\"iCompany\" class=\"form-control\" placeholder=\"e.g. Infosys, TCS\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Round Name</label>\n"
                + "                <input type=\"text\" id=\"iRound\" class=\"form-control\" placeholder=\"e.g. Technical Round 1, HR Discussion\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Interview Date</label>\n"
                + "                <input type=\"date\" id=\"iDate\" class=\"form-control\" required />\n"
                + "              </div>\n"
                + "              <div class=\"form-group\">\n"
                + "                <label>Result Status</label>\n"
                + "                <select id=\"iResult\" class=\"form-control\">\n"
                + "                  <option value=\"Pending\">Pending</option>\n"
                + "                  <option value=\"Selected\">Selected</option>\n"
                + "                  <option value=\"Rejected\">Rejected</option>\n"
                + "                </select>\n"
                + "              </div>\n"
                + "            </div>\n"
                + "            <button type=\"submit\" class=\"btn btn-primary\">+ Schedule Round</button>\n"
                + "          </form>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">Scheduled Recruitment Rounds</div>\n"
                + "          <div class=\"table-container\">\n"
                + "            <table id=\"invTable\">\n"
                + "              <thead><tr><th>ID</th><th>Company</th><th>Round</th><th>Date</th><th>Result</th></tr></thead>\n"
                + "              <tbody></tbody>\n"
                + "            </table>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- REPORTS VIEW -->\n"
                + "      <section id=\"view-reports\" class=\"view-section\">\n"
                + "        <div class=\"card\" style=\"text-align: center; padding: 28px;\">\n"
                + "          <div style=\"font-size: 13px; text-transform: uppercase; color: #64748B; font-weight: 700;\">Composite Placement Readiness Index</div>\n"
                + "          <div id=\"repIndex\" style=\"font-size: 54px; font-weight: 800; color: #2563EB; margin: 10px 0;\">--%</div>\n"
                + "          <p style=\"font-size: 13.5px; color: #64748B;\">Computed dynamically using live weights: 25% Applications + 35% DSA Mastery + 25% Aptitude + 15% Interview Clearance</p>\n"
                + "        </div>\n"
                + "\n"
                + "        <div class=\"kpi-grid\">\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"repConversion\">--%</div>\n"
                + "              <div class=\"kpi-label\">Company Conversion</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"repDsa\">--%</div>\n"
                + "              <div class=\"kpi-label\">DSA Completion</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"repAptitude\">--%</div>\n"
                + "              <div class=\"kpi-label\">Aptitude Excellence</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          <div class=\"kpi-card\">\n"
                + "            <div>\n"
                + "              <div class=\"kpi-num\" id=\"repInterview\">--%</div>\n"
                + "              <div class=\"kpi-label\">Interview Clearance</div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "\n"
                + "      <!-- DIAGNOSTICS VIEW -->\n"
                + "      <section id=\"view-diagnostics\" class=\"view-section\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-title\">MySQL JDBC Server Diagnostics</div>\n"
                + "          <div id=\"diagDetails\" style=\"font-family: monospace; font-size: 13px; line-height: 1.8; color: #334155; background: #F8FAFC; padding: 16px; border-radius: 8px; border: 1px solid var(--border-color);\">\n"
                + "            Scanning connection...\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </section>\n"
                + "    </main>\n"
                + "  </div>\n"
                + "\n"
                + "  <script>\n"
                + "    const todayStr = new Date().toISOString().split('T')[0];\n"
                + "    ['cDate', 'aDate', 'dDate', 'iDate'].forEach(id => { const el = document.getElementById(id); if (el) el.value = todayStr; });\n"
                + "\n"
                + "    function showView(name) {\n"
                + "      document.querySelectorAll('.view-section').forEach(s => s.classList.remove('active'));\n"
                + "      document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));\n"
                + "      const s = document.getElementById('view-' + name);\n"
                + "      if (s) s.classList.add('active');\n"
                + "      const titles = {\n"
                + "        dashboard: 'Executive Dashboard',\n"
                + "        companies: 'Corporate Opportunities Management',\n"
                + "        aptitude: 'Aptitude Practice & Performance Tracker',\n"
                + "        dsa: 'Data Structures & Algorithms Tracker',\n"
                + "        interviews: 'Campus Recruitment Interview Scheduler',\n"
                + "        reports: 'Consolidated Progress & Analytics Reports',\n"
                + "        diagnostics: 'MySQL JDBC Diagnostics Console'\n"
                + "      };\n"
                + "      document.getElementById('pageTitle').textContent = titles[name] || 'Placement Tracker';\n"
                + "      const navs = document.querySelectorAll('.nav-btn');\n"
                + "      const map = {dashboard:0, companies:1, aptitude:2, dsa:3, interviews:4, reports:5, diagnostics:6};\n"
                + "      if (map[name] !== undefined) navs[map[name]].classList.add('active');\n"
                + "      if (name === 'companies') loadCompanies();\n"
                + "      if (name === 'aptitude') loadAptitude();\n"
                + "      if (name === 'dsa') loadDsa();\n"
                + "      if (name === 'interviews') loadInterviews();\n"
                + "      if (name === 'reports') loadReports();\n"
                + "      if (name === 'diagnostics') loadDiagnostics();\n"
                + "    }\n"
                + "\n"
                + "    async function refreshAll() {\n"
                + "      await loadDashboard();\n"
                + "      loadCompanies();\n"
                + "      loadAptitude();\n"
                + "      loadDsa();\n"
                + "      loadInterviews();\n"
                + "      loadReports();\n"
                + "      loadDiagnostics();\n"
                + "    }\n"
                + "\n"
                + "    async function loadDashboard() {\n"
                + "      try {\n"
                + "        const res = await fetch('/api/dashboard');\n"
                + "        const d = await res.json();\n"
                + "        document.getElementById('kpiCompanies').textContent = d.totalCompanies;\n"
                + "        document.getElementById('kpiAptitude').textContent = d.totalAptitude;\n"
                + "        document.getElementById('kpiDsa').textContent = d.solvedDSA + '/' + d.totalDSA;\n"
                + "        document.getElementById('kpiInterviews').textContent = d.totalInterviews;\n"
                + "      } catch(e) { console.error(e); }\n"
                + "\n"
                + "      try {\n"
                + "        const res2 = await fetch('/api/companies');\n"
                + "        const list = await res2.json();\n"
                + "        const tb = document.querySelector('#dashCompanyTable tbody');\n"
                + "        tb.innerHTML = '';\n"
                + "        list.slice(0, 5).forEach(c => {\n"
                + "          const badgeClass = 'badge-' + (c.status || '').toLowerCase();\n"
                + "          tb.innerHTML += `<tr><td>${c.id}</td><td><strong>${c.companyName}</strong></td><td>${c.role}</td><td>${c.applicationDate}</td><td><span class=\"badge ${badgeClass}\">${c.status}</span></td></tr>`;\n"
                + "        });\n"
                + "      } catch(e) { console.error(e); }\n"
                + "    }\n"
                + "\n"
                + "    async function loadCompanies() {\n"
                + "      const res = await fetch('/api/companies');\n"
                + "      const list = await res.json();\n"
                + "      const tb = document.querySelector('#companyTable tbody');\n"
                + "      tb.innerHTML = '';\n"
                + "      list.forEach(c => {\n"
                + "        const badgeClass = 'badge-' + (c.status || '').toLowerCase();\n"
                + "        tb.innerHTML += `<tr><td>${c.id}</td><td><strong>${c.companyName}</strong></td><td>${c.role}</td><td>${c.applicationDate}</td><td><span class=\"badge ${badgeClass}\">${c.status}</span></td><td><button class=\"btn btn-danger\" onclick=\"deleteCompany(${c.id})\">Delete</button></td></tr>`;\n"
                + "      });\n"
                + "    }\n"
                + "\n"
                + "    async function addCompany(e) {\n"
                + "      e.preventDefault();\n"
                + "      const data = {\n"
                + "        companyName: document.getElementById('cName').value,\n"
                + "        role: document.getElementById('cRole').value,\n"
                + "        applicationDate: document.getElementById('cDate').value,\n"
                + "        status: document.getElementById('cStatus').value\n"
                + "      };\n"
                + "      await fetch('/api/companies', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(data) });\n"
                + "      document.getElementById('companyForm').reset();\n"
                + "      document.getElementById('cDate').value = todayStr;\n"
                + "      loadCompanies();\n"
                + "      loadDashboard();\n"
                + "    }\n"
                + "\n"
                + "    async function deleteCompany(id) {\n"
                + "      if (!confirm('Are you sure you want to delete company #' + id + '?')) return;\n"
                + "      await fetch('/api/companies/delete', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({id: String(id)}) });\n"
                + "      loadCompanies();\n"
                + "      loadDashboard();\n"
                + "    }\n"
                + "\n"
                + "    async function loadAptitude() {\n"
                + "      const res = await fetch('/api/aptitude');\n"
                + "      const list = await res.json();\n"
                + "      const tb = document.querySelector('#aptTable tbody');\n"
                + "      tb.innerHTML = '';\n"
                + "      list.forEach(a => {\n"
                + "        const badgeClass = 'badge-' + (a.status || '').toLowerCase().replace(/\\s+/g, '-');\n"
                + "        tb.innerHTML += `<tr><td>${a.id}</td><td><strong>${a.topic}</strong></td><td>${a.score}/100</td><td>${a.testDate}</td><td><span class=\"badge ${badgeClass}\">${a.status}</span></td></tr>`;\n"
                + "      });\n"
                + "    }\n"
                + "\n"
                + "    async function addAptitude(e) {\n"
                + "      e.preventDefault();\n"
                + "      const data = {\n"
                + "        topic: document.getElementById('aTopic').value,\n"
                + "        score: document.getElementById('aScore').value,\n"
                + "        testDate: document.getElementById('aDate').value\n"
                + "      };\n"
                + "      await fetch('/api/aptitude', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(data) });\n"
                + "      document.getElementById('aptForm').reset();\n"
                + "      document.getElementById('aDate').value = todayStr;\n"
                + "      loadAptitude();\n"
                + "      loadDashboard();\n"
                + "    }\n"
                + "\n"
                + "    async function loadDsa() {\n"
                + "      const res = await fetch('/api/dsa');\n"
                + "      const list = await res.json();\n"
                + "      const tb = document.querySelector('#dsaTable tbody');\n"
                + "      tb.innerHTML = '';\n"
                + "      list.forEach(d => {\n"
                + "        const diffBadge = 'badge-' + (d.difficulty || '').toLowerCase();\n"
                + "        const statusBadge = 'badge-' + (d.status || '').toLowerCase();\n"
                + "        tb.innerHTML += `<tr><td>${d.id}</td><td>${d.topic}</td><td><strong>${d.problemName}</strong></td><td>${d.platform}</td><td><span class=\"badge ${diffBadge}\">${d.difficulty}</span></td><td><span class=\"badge ${statusBadge}\">${d.status}</span></td><td>${d.dateSolved}</td></tr>`;\n"
                + "      });\n"
                + "    }\n"
                + "\n"
                + "    async function addDsa(e) {\n"
                + "      e.preventDefault();\n"
                + "      const data = {\n"
                + "        topic: document.getElementById('dTopic').value,\n"
                + "        problemName: document.getElementById('dName').value,\n"
                + "        platform: document.getElementById('dPlatform').value,\n"
                + "        difficulty: document.getElementById('dDiff').value,\n"
                + "        status: document.getElementById('dStatus').value,\n"
                + "        dateSolved: document.getElementById('dDate').value\n"
                + "      };\n"
                + "      await fetch('/api/dsa', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(data) });\n"
                + "      document.getElementById('dsaForm').reset();\n"
                + "      document.getElementById('dDate').value = todayStr;\n"
                + "      loadDsa();\n"
                + "      loadDashboard();\n"
                + "    }\n"
                + "\n"
                + "    async function loadInterviews() {\n"
                + "      const res = await fetch('/api/interviews');\n"
                + "      const list = await res.json();\n"
                + "      const tb = document.querySelector('#invTable tbody');\n"
                + "      tb.innerHTML = '';\n"
                + "      list.forEach(inv => {\n"
                + "        const badgeClass = 'badge-' + (inv.result || '').toLowerCase();\n"
                + "        tb.innerHTML += `<tr><td>${inv.id}</td><td><strong>${inv.companyName}</strong></td><td>${inv.roundName}</td><td>${inv.interviewDate}</td><td><span class=\"badge ${badgeClass}\">${inv.result}</span></td></tr>`;\n"
                + "      });\n"
                + "    }\n"
                + "\n"
                + "    async function addInterview(e) {\n"
                + "      e.preventDefault();\n"
                + "      const data = {\n"
                + "        companyName: document.getElementById('iCompany').value,\n"
                + "        roundName: document.getElementById('iRound').value,\n"
                + "        interviewDate: document.getElementById('iDate').value,\n"
                + "        result: document.getElementById('iResult').value\n"
                + "      };\n"
                + "      await fetch('/api/interviews', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(data) });\n"
                + "      document.getElementById('invForm').reset();\n"
                + "      document.getElementById('iDate').value = todayStr;\n"
                + "      loadInterviews();\n"
                + "      loadDashboard();\n"
                + "    }\n"
                + "\n"
                + "    async function loadReports() {\n"
                + "      const res = await fetch('/api/reports');\n"
                + "      const r = await res.json();\n"
                + "      document.getElementById('repIndex').textContent = r.readinessIndex + '%';\n"
                + "      document.getElementById('repConversion').textContent = r.companyConversion + '%';\n"
                + "      document.getElementById('repDsa').textContent = r.dsaCompletion + '%';\n"
                + "      document.getElementById('repAptitude').textContent = r.aptitudeExcellence + '%';\n"
                + "      document.getElementById('repInterview').textContent = r.interviewClearance + '%';\n"
                + "    }\n"
                + "\n"
                + "    async function loadDiagnostics() {\n"
                + "      const res = await fetch('/api/status');\n"
                + "      const s = await res.json();\n"
                + "      document.getElementById('diagDetails').innerHTML = `\n"
                + "        <strong>JDBC Driver:</strong> com.mysql.cj.jdbc.Driver<br/>\n"
                + "        <strong>Database URL:</strong> jdbc:mysql://localhost:3306/placement_tracker<br/>\n"
                + "        <strong>Connection Status:</strong> <span style=\"color:#10B981; font-weight:bold;\">${s.status} (Connected)</span><br/>\n"
                + "        <strong>MySQL Version:</strong> ${s.dbVersion}<br/>\n"
                + "        <strong>Localhost Web Port:</strong> ${s.port}<br/><br/>\n"
                + "        <strong>Table Record Counts in MySQL:</strong><br/>\n"
                + "        • student: ${s.counts.student} record (Karnam Phaneendra)<br/>\n"
                + "        • company: ${s.counts.company} records<br/>\n"
                + "        • aptitude: ${s.counts.aptitude} records<br/>\n"
                + "        • dsa_progress: ${s.counts.dsa} records<br/>\n"
                + "        • interview: ${s.counts.interview} records<br/>\n"
                + "      `;\n"
                + "    }\n"
                + "\n"
                + "    // Initial load\n"
                + "    refreshAll();\n"
                + "  </script>\n"
                + "</body>\n"
                + "</html>";
    }
}
