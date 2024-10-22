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
    private static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int PERMS_REQ_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.studygo.databinding.SignUpBinding binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        verifyPermissions();
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.textSignUp.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), LogIn.class)));
    }

    private boolean verifyPermissions() {
        boolean allGranted = true;
        for (String permission : PERMISSIONS) {
            allGranted = allGranted && (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        }

        if (!allGranted) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Toast.makeText(SignUp.this, "All permissions not granted yet", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SignUp.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else
            Toast.makeText(SignUp.this, "All permissions not granted yet", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
