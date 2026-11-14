package com.placementtracker.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/placement_tracker";

    private static final String USER = "root";

    private static final String PASSWORD = "manager";

    public static Connection getConnection() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            return con;

        }

        catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}