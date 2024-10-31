package com.example.studygo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.SignUpBinding;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    private SignUpBinding binding;
    private RadioGroup radioGroup;
    private String accountType;
    private String encodedImage;
    private PreferenceManager preferenceManager;
    private boolean accountSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        setParams();
        setListeners();

    }

    private Boolean validSignUp() {
        if (encodedImage == null) {
            Toast.makeText(this, "Select a profile Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!accountSelected) {
            Toast.makeText(this, "Select account type", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill out email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (binding.inputName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill out name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            Toast.makeText(this, "Please input valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill out password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill out confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords must match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
    private void setListeners() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                String accountSelect = checkedRadioButton.getText().toString();
                if (accountSelect.trim().isEmpty()) {
                    accountSelected = false;
                }
                if (accountSelect.equals("Professor")) {
                    accountSelected = true;
                    accountType = "professor";
                }
                if (accountSelect.equals("Student")) {
                    accountSelected = true;
                    accountType = "student";
                }
            }
        });
        binding.textAddImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.buttonSignUp.setOnClickListener(view -> {
            if (validSignUp()) {
                signUp();
            }
        });
        binding.textLogIn.setOnClickListener(view ->
            startActivity(new Intent(getApplicationContext(), LogIn.class)));
       
    }


    private String encodeImage(Bitmap bitmap) {
        int previewWidth =150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
    });

    private void setParams() {
        radioGroup = binding.accountTypeGroup;
    }

    private void load(Boolean loading) {
        if (loading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void signUp() {
        load(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    load(false);
                    preferenceManager.putBool(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_TYPE, accountType);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    load(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
