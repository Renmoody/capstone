package com.example.studygo.ui.signUpLogIn;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;
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
            String url = "jdbc:mysql://srv1619.hstgr.io:3306/u511097224_StudyGo";
            String user = "u511097224_admin";
            String pass = "Studium3!7";

            try {
                System.out.println("About to connect");
                Connection connection = DriverManager.getConnection(url, user, pass);
                System.out.println("Connected to the database successfully!");


                // Use the connection as needed
                // Don't forget to close the connection when done!
                connection.close();
                System.out.println("Closing connection");
            } catch (SQLException e) {
                e.printStackTrace(); // Log the error
            }
        });
    }

    public void shutdown() {
        executorService.shutdown(); // Shut down the executor when done
    }
}

