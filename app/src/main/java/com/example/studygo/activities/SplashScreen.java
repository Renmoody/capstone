package com.example.studygo.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.R;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;

public class SplashScreen extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBool(Constants.KEY_IS_SIGNED_IN)) {
            launch();
        } else {
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }

    }

    private void launch() {
        String accountType = preferenceManager.getString(Constants.KEY_ACCOUNT_TYPE);
        switch (accountType) {
            case "student":
                Intent intent = new Intent(getApplicationContext(), ActivityStudent.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case "professor":
                Intent intentProfessor = new Intent(getApplicationContext(), ActivityTeacher.class);
                intentProfessor.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentProfessor);
                finish();
                break;
            case "company":
                Intent intentCompany = new Intent(getApplicationContext(), ActivityCompany.class);
                intentCompany.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentCompany);
                finish();
                break;
        }
    }
}