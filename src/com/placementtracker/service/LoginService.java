package com.placementtracker.service;

import com.placementtracker.dao.StudentDAO;
import com.placementtracker.model.Student;

public class LoginService {

    StudentDAO studentDAO = new StudentDAO();

    public Student login(String email, String password) {

        return studentDAO.login(email, password);

    }

}