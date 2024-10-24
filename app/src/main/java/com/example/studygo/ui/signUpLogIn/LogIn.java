package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.MainActivity;
import com.example.studygo.R;
import com.example.studygo.databinding.LogInBinding;
import com.example.studygo.ui.dashboard.DashboardFragment;

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
       binding.buttonSignIn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUp.class)));
    }





}
