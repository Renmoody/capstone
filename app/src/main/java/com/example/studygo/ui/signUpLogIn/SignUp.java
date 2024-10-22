package com.example.studygo.ui.signUpLogIn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studygo.MainActivity;
import com.example.studygo.databinding.SignUpBinding;
import com.google.android.material.snackbar.Snackbar;

public class SignUp extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS};
    private static final int PERMS_REQ_CODE = 200;
    DatabaseConnectionManager dbmanager;
    private String username = "renmoody67@gmail.com";
    private String password = "testPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.studygo.databinding.SignUpBinding binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbmanager = new DatabaseConnectionManager();
        verifyPermissions();
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the new method to connect to the database asynchronously
                dbmanager.connectToDatabaseAndAddUser(username, password, new ConnectionCallback() {
                    @Override
                    public void onSuccess() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUp.this, "Connected to database", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUp.this, "Connection failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean verifyPermissions() {
        boolean allGranted = true;
        for (String permission : PERMISSIONS) {
            allGranted = allGranted && (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        }

        if (!allGranted) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Snackbar.make(findViewById(android.R.id.content), permission + " WE GOTTA HAVE IT!", Snackbar.LENGTH_LONG).show();
                }
            }
            requestPermissions(PERMISSIONS, PERMS_REQ_CODE);
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        boolean allGranted = true;
        if (permsRequestCode == PERMS_REQ_CODE) {
            for (int result : grantResults) {
                allGranted = allGranted && (result == PackageManager.PERMISSION_GRANTED);
            }
        }
        if (allGranted) {
            // Permissions granted, proceed with the connection logic
            dbmanager.connectToDatabaseAndAddUser(username, password, new ConnectionCallback() {
                @Override
                public void onSuccess() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUp.this, "Connected to database", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, MainActivity.class));

                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUp.this, "Connection failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            // Handle the case where permissions are not granted
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SignUp.this, "Permissions required to connect to the database.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbmanager.shutDown();  // Shutdown the executor service in the manager
    }
}
