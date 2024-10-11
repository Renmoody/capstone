package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studygo.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.LogInBinding;

public class LogIn extends AppCompatActivity {
    private LogInBinding binding;
    private Authentication auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = new Authentication();
        EditText emailEditText = binding.inputEmail;
        EditText passwordEditText = binding.inputPassword;
        Button loginButton = binding.buttonSignIn;

        loginButton.setOnClickListener(view -> {
            String username = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            auth.login(username, password, new Authentication.AuthListener() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogIn.this, MainActivity.class));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        setListeners();
    }
    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUp.class)));
    }

}
