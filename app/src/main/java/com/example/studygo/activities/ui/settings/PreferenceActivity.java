package com.example.studygo.activities.ui.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.studygo.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Add the fragment to the activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new PreferenceFragment())
                .commit();

    }
}
