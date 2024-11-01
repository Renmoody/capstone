package com.example.studygo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.LogInBinding;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private LogInBinding binding;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private PreferenceManager preferenceManager;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = LogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        setParams();
        setListeners();
    }

    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    public Boolean isValidSignIn() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Invalid Email");
            return false;
        }
        else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }
        else {
            return true;
        }

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
            if (isValidSignIn())
                signIn();
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

    private void signIn() {
        load(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                        preferenceManager.putBool(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, ds.getId());
                        preferenceManager.putString(Constants.KEY_NAME, ds.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, ds.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_ACCOUNT_TYPE, ds.getString(Constants.KEY_ACCOUNT_TYPE));
                        accountType = preferenceManager.getString(Constants.KEY_ACCOUNT_TYPE);
                        launch();
                    }
                    else {
                        load(false);
                        showToast("Invalid Username or Password");
                    }
                });
    }

    private void launch() {
        switch (accountType) {
            case "student" :
                Intent intent = new Intent(getApplicationContext(), ActivityStudent.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case "professor" :
                Intent intentProfessor = new Intent(getApplicationContext(), ActivityTeacher.class);
                intentProfessor.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentProfessor);
        }
    }

    private void load(Boolean loading) {
        if (loading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setParams() {
        email = binding.inputEmail.toString();
        password = binding.inputPassword.toString();
    }

}
