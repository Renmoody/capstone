package com.example.studygo.ui.signUpLogIn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.SignUpBinding;

public class SignUp  extends AppCompatActivity {
    private SignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners() {
        binding.textSignUp.setOnClickListener(v -> onBackPressed());
    }
}
