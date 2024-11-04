package com.example.studygo.activities.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


import com.example.studygo.R;
import com.example.studygo.activities.LogIn;
import com.example.studygo.utilities.PreferenceManager;

public class PreferenceActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Add the fragment to the activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new PreferenceFragment())
                .commit();

    }
}
