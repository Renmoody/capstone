package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.Constants;
import com.example.studygo.MainActivity;
import com.example.studygo.databinding.SignUpBinding;

public class SignUp extends AppCompatActivity {
    private SignUpBinding binding;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textSignUp.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(view -> {
            String email = binding.inputEmail.getText().toString().trim();
            String username = binding.inputName.getText().toString().trim();
            String password = binding.inputConfirmPassword.getText().toString().trim();

            // Basic validation
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            // Email validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUp.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            register(username, password, email, new SignupListener() {
                @Override
                public void onSuccess(String message) {
                    // Handle successful signup, e.g., show a success message
                    // or navigate to another activity
                }

                @Override
                public void onError(String message) {
                    // Handle signup error, e.g., show an error message
                }
            });
            if (registered) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });
    }

    public interface SignupListener {
        void onSuccess(String message);
        void onError(String message);
    }

    private void register(final String username, final String password, final String email, final SignupListener listener) {
        executor.execute(() -> {
            String result = null;
            try {
                URL url = new URL("https://srv1619-files.hstgr.io/087fd1470ba22df8/files/public_html/SignUpRemote.php"); // Adjust your URL here
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Checking to see if the url exists or is restricted
                int respCode = conn.getResponseCode();
                if (respCode >= 400) {
                    if (respCode == 404 || respCode == 410) {
                        throw new FileNotFoundException(url.toString());
                    } else {
                        throw new java.io.IOException(
                                "Server returned HTTP"
                                        + " response code: " + respCode
                                        + " for URL: " + url.toString());
                    }
                }
                // Prepare the POST data
                String postData = "username=" + username + "&password=" + password + "&email=" + email;
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Handle the response on the main thread
            String finalResult = result;
            handler.post(() -> {
                if (finalResult != null) {
                    // Handle the JSON response
                    try {
                        JSONObject jsonResponse = new JSONObject(finalResult);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if (status.equals("success")) {
                            listener.onSuccess(message);
                            registered = true;
                        } else {
                            listener.onError(message);
                        }
                    } catch (JSONException e) {
                        listener.onError("Parsing error");
                    }
                } else {
                    listener.onError("Connection error");
                }
            });
        });
    }
}
