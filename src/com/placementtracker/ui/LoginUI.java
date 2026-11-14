package com.placementtracker.ui;

import java.util.Scanner;

import com.placementtracker.model.Student;
import com.placementtracker.service.LoginService;

public class LoginUI {

    Scanner sc = new Scanner(System.in);

    LoginService loginService = new LoginService();

    public boolean login() {

        System.out.println("==================================");
        System.out.println(" PLACEMENT PREPARATION TRACKER ");
        System.out.println("==================================");

        System.out.print("Enter Email : ");
        String email = sc.nextLine();

        System.out.print("Enter Password : ");
        String password = sc.nextLine();

        Student student = loginService.login(email, password);

        if (student != null) {

            System.out.println();
            System.out.println();
            System.out.println("======================================");
            System.out.println("LOGIN SUCCESSFUL");
            System.out.println("Welcome " + student.getName());
            System.out.println("======================================");
            //System.out.println("Welcome " + student.getName());

            return true;

        } else {

            
            System.out.println();
            System.out.println("======================================");
            System.out.println("Invalid Email or Password");
            System.out.println("======================================");

            return false;

        }

    }

}