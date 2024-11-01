package com.example.studygo.activities.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.studygo.R;
import com.example.studygo.activities.LogIn;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        // Handle the click event for any preference
        String key = preference.getKey();
        if (key.equals("sign_out")) {
            startActivity(new Intent(getContext(), LogIn.class));
        }

        return super.onPreferenceTreeClick(preference);
    }

}
