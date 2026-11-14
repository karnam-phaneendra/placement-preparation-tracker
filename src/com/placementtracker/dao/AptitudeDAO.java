package com.placementtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.placementtracker.database.DBConnection;
import com.placementtracker.model.Aptitude;
import com.placementtracker.model.AptitudeReport;

public class AptitudeDAO {

    private Connection con;

    public AptitudeDAO() {

        con = DBConnection.getConnection();

    }

    // ===========================
    // ADD TEST RESULT
    // ===========================

    public boolean addTestResult(Aptitude aptitude) {

        String query = "INSERT INTO aptitude(topic,score,test_date,status) VALUES(?,?,?,?)";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, aptitude.getTopic());
            ps.setInt(2, aptitude.getScore());
            ps.setDate(3, aptitude.getTestDate());
            ps.setString(4, aptitude.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    // ===========================
    // VIEW ALL
    // ===========================

    public List<Aptitude> getAllResults() {

        List<Aptitude> list = new ArrayList<>();

        String query = "SELECT * FROM aptitude";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Aptitude aptitude = new Aptitude();

                aptitude.setAptitudeId(rs.getInt("aptitude_id"));
                aptitude.setTopic(rs.getString("topic"));
                aptitude.setScore(rs.getInt("score"));
                aptitude.setTestDate(rs.getDate("test_date"));
                aptitude.setStatus(rs.getString("status"));

                list.add(aptitude);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return list;

    }

    // ===========================
    // UPDATE
    // ===========================

    public boolean updateResult(int id, String status) {

        String query = "UPDATE aptitude SET status=? WHERE aptitude_id=?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    // ===========================
    // DELETE
    // ===========================

    public boolean deleteResult(int id) {

        String query = "DELETE FROM aptitude WHERE aptitude_id=?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    // ===========================
    // SEARCH
    // ===========================

    public Aptitude searchTopic(String topic) {

        String query = "SELECT * FROM aptitude WHERE topic=?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, topic);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Aptitude aptitude = new Aptitude();

                aptitude.setAptitudeId(rs.getInt("aptitude_id"));
                aptitude.setTopic(rs.getString("topic"));
                aptitude.setScore(rs.getInt("score"));
                aptitude.setTestDate(rs.getDate("test_date"));
                aptitude.setStatus(rs.getString("status"));

                return aptitude;

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return null;

    }

    // ===========================
    // FILTER
    // ===========================

    public List<Aptitude> filterStatus(String status) {

        List<Aptitude> list = new ArrayList<>();

        String query = "SELECT * FROM aptitude WHERE status=?";

        try {

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Aptitude aptitude = new Aptitude();

                aptitude.setAptitudeId(rs.getInt("aptitude_id"));
                aptitude.setTopic(rs.getString("topic"));
                aptitude.setScore(rs.getInt("score"));
                aptitude.setTestDate(rs.getDate("test_date"));
                aptitude.setStatus(rs.getString("status"));

                list.add(aptitude);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return list;

    }

    // ===========================
    // REPORT
    // ===========================

    public AptitudeReport getReport() {

        AptitudeReport report = new AptitudeReport();

        try {

            PreparedStatement ps;
            ResultSet rs;

            ps = con.prepareStatement("SELECT COUNT(*) FROM aptitude");
            rs = ps.executeQuery();

            if (rs.next())
                report.setTotalTests(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM aptitude WHERE status='Excellent'");
            rs = ps.executeQuery();

            if (rs.next())
                report.setExcellent(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM aptitude WHERE status='Good'");
            rs = ps.executeQuery();

            if (rs.next())
                report.setGood(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM aptitude WHERE status='Average'");
            rs = ps.executeQuery();

            if (rs.next())
                report.setAverage(rs.getInt(1));

            ps = con.prepareStatement("SELECT COUNT(*) FROM aptitude WHERE status='Needs Improvement'");
            rs = ps.executeQuery();

            if (rs.next())
                report.setNeedsImprovement(rs.getInt(1));

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return report;

    }

}