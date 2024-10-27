package com.example.studygo.activities.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.studygo.R;
import com.example.studygo.activities.LogIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingsFragment extends PreferenceFragmentCompat {
    private Button button;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }


    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if ("sign_out".equals(preference.getKey())) {
            // Sign out the user
            // Redirect to login activity
            startActivity(new Intent(getContext(), LogIn.class));
            return true;
        }
        if ("delete_account".equals(preference.getKey())) {
            // Sign out the user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Redirect to login activity
            user.delete();
            startActivity(new Intent(getContext(), LogIn.class));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

}
