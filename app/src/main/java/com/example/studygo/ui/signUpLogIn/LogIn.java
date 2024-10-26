package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.LogInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private LogInBinding binding;
    private FirebaseAuth mAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        setParams();
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUp.class)));
        binding.textForgotPassword.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (!binding.inputEmail.getText().toString().isEmpty()) {
                auth.sendPasswordResetEmail(Objects.requireNonNull(email));
                Toast.makeText(getApplicationContext(), "Password reset link sent!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Please fill out email section", Toast.LENGTH_SHORT).show();
        });
        binding.buttonSignIn.setOnClickListener(view -> {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Auth", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified())
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                else
                                    Toast.makeText(getApplicationContext(), "Please verify email", Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogIn.this, "Incorrect Username or Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
        binding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {email = charSequence.toString();}
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {password = charSequence.toString();}
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setParams() {
        email = binding.inputEmail.toString();
        password = binding.inputPassword.toString();
    }

}
