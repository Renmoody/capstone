package com.example.studygo.activities.ui.settings;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;


import com.example.studygo.R;
import com.example.studygo.utilities.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Add the fragment to the activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

}
