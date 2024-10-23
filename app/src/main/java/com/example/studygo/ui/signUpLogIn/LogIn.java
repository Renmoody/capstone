package com.example.studygo.ui.signUpLogIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.LogInBinding;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
        binding.buttonSignIn.setOnClickListener(view -> addDataToFireBase());
    }

    private void addDataToFireBase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Ren");
        data.put("last_name", "Moody");
        db.collection("users").add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
