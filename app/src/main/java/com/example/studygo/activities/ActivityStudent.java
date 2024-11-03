package com.example.studygo.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.studygo.R;
import com.example.studygo.databinding.ActivityStudentBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.firebase.auth.FirebaseUser;

public class ActivityStudent extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final String TAG = "Preference Change";
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the binding
        // Declare binding as a field
        com.example.studygo.databinding.ActivityStudentBinding binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Setting up shared preferences
        SharedPreferences settings = getSharedPreferences(this.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        settings.registerOnSharedPreferenceChangeListener(this);

        // Set up the bottom navigation view
        BottomNavigationView navView = binding.navViewStudent; // Use the binding

        // Specify top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_messages, R.xml.root_preferences)
                .build();

        // Set up the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_student);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Allow the action bar to handle up navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_student);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    //Checking for shared pref changes
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        Log.d(TAG, "onSharedPreferenceChanged: Preference "+s+" changed!");
    }
}