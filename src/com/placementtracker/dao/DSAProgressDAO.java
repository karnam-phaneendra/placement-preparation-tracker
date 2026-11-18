package com.placementtracker.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.placementtracker.model.DSAReport;

import com.placementtracker.database.DBConnection;
import com.placementtracker.model.DSAProgress;

public class DSAProgressDAO {

    private Connection con;

    public DSAProgressDAO() {

        con = DBConnection.getConnection();

    }

    public boolean addProblem(DSAProgress problem) {

        String query = "INSERT INTO dsa_progress(topic, problem_name, platform, difficulty, status, date_solved) VALUES (?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, problem.getTopic());
            ps.setString(2, problem.getProblemName());
            ps.setString(3, problem.getPlatform());
            ps.setString(4, problem.getDifficulty());
            ps.setString(5, problem.getStatus());
            ps.setDate(6, problem.getDateSolved());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }
    public List<DSAProgress> getAllProblems() {

        List<DSAProgress> list = new ArrayList<>();

        String query = "SELECT * FROM dsa_progress";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DSAProgress problem = new DSAProgress();

                problem.setProblemId(rs.getInt("problem_id"));
                problem.setTopic(rs.getString("topic"));
                problem.setProblemName(rs.getString("problem_name"));
                problem.setPlatform(rs.getString("platform"));
                problem.setDifficulty(rs.getString("difficulty"));
                problem.setStatus(rs.getString("status"));
                problem.setDateSolved(rs.getDate("date_solved"));

                list.add(problem);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return list;

    }
    public boolean updateProblemStatus(int problemId, String status) {

        String query = "UPDATE dsa_progress SET status = ? WHERE problem_id = ?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, problemId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;
    }

    public boolean updateProblemFull(int id, String topic, String name, String platform, String diff, String status, java.sql.Date date) {

        String query = "UPDATE dsa_progress SET topic=?, problem_name=?, platform=?, difficulty=?, status=?, date_solved=? WHERE problem_id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, topic);
            ps.setString(2, name);
            ps.setString(3, platform);
            ps.setString(4, diff);
            ps.setString(5, status);
            ps.setDate(6, date);
            ps.setInt(7, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public boolean deleteProblem(int problemId) {

        String query = "DELETE FROM dsa_progress WHERE problem_id = ?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, problemId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }
    
    public DSAProgress searchProblem(String problemName) {

        String query = "SELECT * FROM dsa_progress WHERE problem_name = ?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, problemName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                DSAProgress problem = new DSAProgress();

                problem.setProblemId(rs.getInt("problem_id"));
                problem.setTopic(rs.getString("topic"));
                problem.setProblemName(rs.getString("problem_name"));
                problem.setPlatform(rs.getString("platform"));
                problem.setDifficulty(rs.getString("difficulty"));
                problem.setStatus(rs.getString("status"));
                problem.setDateSolved(rs.getDate("date_solved"));

                return problem;

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return null;

    }
    public List<DSAProgress> getProblemsByDifficulty(String difficulty) {

        List<DSAProgress> list = new ArrayList<>();

        String query = "SELECT * FROM dsa_progress WHERE difficulty = ?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, difficulty);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DSAProgress problem = new DSAProgress();

                problem.setProblemId(rs.getInt("problem_id"));
                problem.setTopic(rs.getString("topic"));
                problem.setProblemName(rs.getString("problem_name"));
                problem.setPlatform(rs.getString("platform"));
                problem.setDifficulty(rs.getString("difficulty"));
                problem.setStatus(rs.getString("status"));
                problem.setDateSolved(rs.getDate("date_solved"));

                list.add(problem);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return list;

    }
    public DSAReport getProgressReport() {

        DSAReport report = new DSAReport();

        try {

            PreparedStatement ps;

            ResultSet rs;

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress");
            rs = ps.executeQuery();
            if (rs.next())
                report.setTotalProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE difficulty='Easy'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setEasyProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE difficulty='Medium'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setMediumProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE difficulty='Hard'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setHardProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE status='Solved'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setSolvedProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE status='Revision'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setRevisionProblems(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM dsa_progress WHERE status='Pending'");
            rs = ps.executeQuery();
            if (rs.next())
                report.setPendingProblems(rs.getInt(1));

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return report;

    }
}