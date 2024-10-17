package com.example.studygo.ui.signUpLogIn;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnectionManager {

    private ExecutorService executorService;

    public DatabaseConnectionManager() {
        executorService = Executors.newSingleThreadExecutor(); // Creates a single thread executor
    }

    public void connectToDatabase() {
        executorService.submit(() -> {
            String url = "jdbc:mysql://srv1619.hstgr.io:3306/u511097224_StudyGo?connectTimeout=10000&socketTimeout=10000";
            String user = "u511097224_admin";
            String pass = "Studium3!7";

            try {
                System.out.println("About to connect...");

                // Print before attempting connection
                System.out.println("Before DriverManager.getConnection()");

                // Establish connection using DriverManager
                DriverManager.setLoginTimeout(10);
                Connection connect = DriverManager.getConnection(url, user, pass);

                // Print after connection is successful
                System.out.println("Connected to the database successfully!");

                // Use the connection as needed
                connect.close();
                System.out.println("Connection closed successfully!");

            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
                e.printStackTrace(); // Log the error
            } catch (Exception e) {
                System.out.println("General exception: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        executorService.shutdown(); // Shut down the executor when done
    }
}

