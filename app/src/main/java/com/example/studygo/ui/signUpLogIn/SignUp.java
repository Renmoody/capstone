package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.SignUpBinding;

public class SignUp extends AppCompatActivity {
    private SignUpBinding binding;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private boolean registered;
    private DatabaseConnectionManager dbManager; // Declare instance variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbManager = new DatabaseConnectionManager(); // Initialize the instance variable
        setListeners();
    }

    private void setListeners() {
        binding.textSignUp.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(view -> {
            String email = binding.inputEmail.getText().toString().trim();
            String username = binding.inputName.getText().toString().trim();
            String password = binding.inputConfirmPassword.getText().toString().trim();

            // Basic validation
//            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
//                Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            // Email validation
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(SignUp.this, "Invalid email format", Toast.LENGTH_SHORT).show();
//                return;
//            }

            register(username, password, email, new SignupListener() {
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
            });
        });
    }

    public interface SignupListener {
        void onSuccess(String message);
        void onError(String message);
    }

    private void register(final String username, final String password, final String email, final SignupListener listener) {
        // Ensure that dbManager is initialized before calling this method
        if (dbManager != null) {
            dbManager.connectToDatabase(); // Connect to the database
        } else {
            listener.onError("Database connection manager is not initialized.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Clean up the executor service
    }
}
