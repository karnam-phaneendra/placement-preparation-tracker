package com.placementtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.placementtracker.database.DBConnection;
import com.placementtracker.model.Student;

public class StudentDAO {

    public Student login(String email, String password) {

        Student student = null;

        try {

            Connection con = DBConnection.getConnection();

            String query =
                    "SELECT * FROM student WHERE email=? AND password=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                student = new Student();

                student.setStudentId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setPassword(rs.getString("password"));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return student;

    }

}