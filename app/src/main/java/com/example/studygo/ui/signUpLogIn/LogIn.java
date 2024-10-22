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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }
    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUp.class)));
        binding.buttonSignIn.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

}
