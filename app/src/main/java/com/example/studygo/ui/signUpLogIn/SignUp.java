package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.SignUpBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUp extends AppCompatActivity {
    private SignUpBinding binding;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private DatabaseConnectionManager dbManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String SSH_PRIVATE_KEY_PATH = "C:/Users/renpo/.ssh/id_ed25519";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbManager = new DatabaseConnectionManager(); // Initialize the DatabaseConnectionManager
        setListeners(); // Add setListeners() to initialize UI interactions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // Permission already granted, proceed with file operations
                proceedWithSSHConnection();
            } else {
                // Request permission
                requestPermission();
            }
        } else {
            // No need to request permission for devices below Android 6.0
            proceedWithSSHConnection();
        }
    }

    // Check if the permission to read external storage is granted
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission to read external storage
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file operations
                proceedWithSSHConnection();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission Denied. Cannot read the SSH key file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // SSH connection logic
    private void proceedWithSSHConnection() {
        // SSH and database connection details
        String sshUser = "u511097224";
        String sshHost = "92.112.187.2";
        int sshPort = 65002;
        String remoteHost = "127.0.0.1"; // The host for MySQL on the remote server
        int localPort = 3306;
        int remotePort = 3306; // MySQL port
        String dbUser = "u511097224_admin";
        String dbPassword = "Studium3!7";
        String databaseName = "u511097224_StudyGo";

        // Define the SignupListener right here
        SignupListener listener = new SignupListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish(); // Close SignUp activity
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
            }
        };

        // Establish SSH tunnel and connect to the database
        dbManager.connectViaSSH(sshUser, sshHost, sshPort, SSH_PRIVATE_KEY_PATH, remoteHost, localPort, remotePort, dbUser, dbPassword, databaseName, new DatabaseConnectionManager.ConnectCallback() {
            @Override
            public void onSuccess(Connection connection) {
                // Perform the registration in the background
                executor.submit(() -> {
//                    try {
//                        // Insert user details into the database
//                        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
//                        PreparedStatement preparedStatement = connection.prepareStatement(query);
//                        preparedStatement.setString(1, binding.inputName.getText().toString().trim());
//                        preparedStatement.setString(2, binding.inputConfirmPassword.getText().toString().trim());
//                        preparedStatement.setString(3, binding.inputEmail.getText().toString().trim());
//
//                        int result = preparedStatement.executeUpdate(); // Execute the update
//                        if (result > 0) {
//                            // Post success to the UI thread
//                            handler.post(() -> listener.onSuccess("Registration successful!"));
//                        } else {
//                            // Post error to the UI thread
//                            handler.post(() -> listener.onError("Registration failed."));
//                        }
//
//                        // Close the connection after the operation
//                        connection.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                        handler.post(() -> listener.onError("Error: " + e.getMessage()));
//                    }
                    System.out.println("Connected");
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle connection failure
                handler.post(() -> listener.onError("Database connection failed: " + e.getMessage()));
            }
        });
    }

    // Define the SignupListener interface for success and error callbacks
    public interface SignupListener {
        void onSuccess(String message);
        void onError(String message);
    }

    private void setListeners() {
        binding.textSignUp.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(view -> {
            String email = binding.inputEmail.getText().toString().trim();
            String username = binding.inputName.getText().toString().trim();
            String password = binding.inputConfirmPassword.getText().toString().trim();

            // Basic validation (uncomment if needed)
//            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
//                Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Email validation (uncomment if needed)
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(SignUp.this, "Invalid email format", Toast.LENGTH_SHORT).show();
//                return;
//            }

            // Proceed with registration
            proceedWithSSHConnection();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Clean up the executor service
        dbManager.disconnect(); // Shut down the SSH session
    }
}
