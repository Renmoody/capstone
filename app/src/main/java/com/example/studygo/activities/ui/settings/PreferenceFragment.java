package com.example.studygo.activities.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.studygo.R;
import com.example.studygo.activities.LogIn;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;

import java.util.Objects;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private PreferenceManager preferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        preferenceManager = new PreferenceManager(requireContext());

        EditTextPreference namePreference = null;
        if (namePreference != null) {
            String name = preferenceManager.getString("name");
            if (name != null) {
                namePreference.setText(name);
            }
        }

        EditTextPreference emailPreference = findPreference("email");
        if (emailPreference != null) {
            String email = preferenceManager.getString("email");
            if (email != null) {
                emailPreference.setText(email);
            }
        }

        // Add listeners to save changes in PreferenceManager
        if (namePreference != null) {
            namePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String newName = (String) newValue;
                preferenceManager.putString("name", newName);
                return true;
            });
        }

        if (emailPreference != null) {
            emailPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String newEmail = (String) newValue;
                preferenceManager.putString("email", newEmail);
                return true;
            });
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("sign_out".equals(key)) {
            preferenceManager.putBool(Constants.KEY_IS_SIGNED_IN, false);
            startActivity(new Intent(getContext(), LogIn.class));
        }
        return super.onPreferenceTreeClick(preference);
    }
}