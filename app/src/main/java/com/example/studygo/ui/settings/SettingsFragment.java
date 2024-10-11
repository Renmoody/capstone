package com.example.studygo.ui.settings;

import android.os.Bundle;
import android.renderscript.ScriptGroup;

import androidx.preference.PreferenceFragmentCompat;

import com.example.studygo.R;
import com.example.studygo.databinding.ActivityMainBinding;
import com.example.studygo.databinding.FragmentDashboardBinding;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }


}