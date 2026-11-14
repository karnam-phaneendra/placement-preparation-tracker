package com.placementtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.placementtracker.database.DBConnection;
import com.placementtracker.model.Interview;
import com.placementtracker.model.InterviewReport;

public class InterviewDAO {

    // ADD INTERVIEW

    public boolean scheduleInterview(Interview interview) {

        String sql = "INSERT INTO interview(company_name, round_name, interview_date, result) VALUES(?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, interview.getCompanyName());
            ps.setString(2, interview.getRoundName());
            ps.setDate(3, interview.getInterviewDate());
            ps.setString(4, interview.getResult());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    // VIEW ALL

    public List<Interview> getAllInterviews() {

        List<Interview> list = new ArrayList<>();

        String sql = "SELECT * FROM interview";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Interview interview = new Interview();

                interview.setInterviewId(rs.getInt("interview_id"));
                interview.setCompanyName(rs.getString("company_name"));
                interview.setRoundName(rs.getString("round_name"));
                interview.setInterviewDate(rs.getDate("interview_date"));
                interview.setResult(rs.getString("result"));

                list.add(interview);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    // UPDATE RESULT

    public boolean updateInterview(int id, String result) {

        String sql = "UPDATE interview SET result=? WHERE interview_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, result);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    // DELETE

    public boolean deleteInterview(int id) {

        String sql = "DELETE FROM interview WHERE interview_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    // SEARCH

    public Interview searchInterview(String companyName) {

        String sql = "SELECT * FROM interview WHERE company_name=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, companyName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Interview interview = new Interview();

                interview.setInterviewId(rs.getInt("interview_id"));
                interview.setCompanyName(rs.getString("company_name"));
                interview.setRoundName(rs.getString("round_name"));
                interview.setInterviewDate(rs.getDate("interview_date"));
                interview.setResult(rs.getString("result"));

                return interview;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    // FILTER

    public List<Interview> filterResult(String result) {

        List<Interview> list = new ArrayList<>();

        String sql = "SELECT * FROM interview WHERE result=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, result);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Interview interview = new Interview();

                interview.setInterviewId(rs.getInt("interview_id"));
                interview.setCompanyName(rs.getString("company_name"));
                interview.setRoundName(rs.getString("round_name"));
                interview.setInterviewDate(rs.getDate("interview_date"));
                interview.setResult(rs.getString("result"));

                list.add(interview);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    // REPORT

    public InterviewReport getReport() {

        InterviewReport report = new InterviewReport();

        String sql = "SELECT COUNT(*) total,"
                + "SUM(CASE WHEN result='Selected' THEN 1 ELSE 0 END) selected,"
                + "SUM(CASE WHEN result='Rejected' THEN 1 ELSE 0 END) rejected,"
                + "SUM(CASE WHEN result='Pending' THEN 1 ELSE 0 END) pending "
                + "FROM interview";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                report.setTotalInterviews(rs.getInt("total"));
                report.setSelected(rs.getInt("selected"));
                report.setRejected(rs.getInt("rejected"));
                report.setPending(rs.getInt("pending"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return report;

    }

}