package com.example.studygo.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.studygo.R;
import com.example.studygo.databinding.ActivityStudentBinding;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class ActivityStudent extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the binding
        // Declare binding as a field
        com.example.studygo.databinding.ActivityStudentBinding binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferenceManager = new PreferenceManager(getApplicationContext());

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
        getToken();


    }

    @Override
    public boolean onSupportNavigateUp() {
        // Allow the action bar to handle up navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_student);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager
                        .getString(Constants.KEY_USER_ID));

        documentReference
                .update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused ->
                showToast("Token updated Successfully")).addOnFailureListener(e ->
                showToast("Token failed to update"));
    }

}
