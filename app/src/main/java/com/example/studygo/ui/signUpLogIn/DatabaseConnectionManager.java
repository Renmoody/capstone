package com.example.studygo.ui.signUpLogIn;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private Session session;

    // Interface to handle connection success or failure
    public interface ConnectCallback {
        void onSuccess(Connection connection);
        void onFailure(Exception e);
    }

    public void connectViaSSH(String sshUser, String sshHost, int sshPort, String privateKey, String remoteHost, int localPort, int remotePort, String dbUser, String dbPassword, String databaseName, ConnectCallback callback) {
        new Thread(() -> {
            try {
                JSch jsch = new JSch();
                jsch.addIdentity(privateKey);

                session = jsch.getSession(sshUser, sshHost, sshPort);

                // Optional: Set strict host key checking to no
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                // Connect to SSH
                session.connect();

                // Setup port forwarding
                session.setPortForwardingL(localPort, remoteHost, remotePort);
                System.out.println("SSH Tunnel established on localhost:" + localPort);

                // Now connect to the database through the SSH tunnel
                String url = "jdbc:mysql://localhost:" + localPort + "/" + databaseName;
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
                Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);

                System.out.println("Connected to the database!");

                // Notify the callback on success
                callback.onSuccess(connection);
            } catch (Exception e) {
                e.printStackTrace();
                // Notify the callback on failure
                callback.onFailure(e);
            }
        }).start();
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("SSH session disconnected.");
        }
    }
}
