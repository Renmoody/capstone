package com.example.studygo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.ActivitySelectClassBinding;
import com.example.studygo.models.StudentClass;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ActivitySelectClass extends AppCompatActivity {
    ActivitySelectClassBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        binding.imageInfo.setOnClickListener(view -> addClass());
    }

    private void addClassToDatabase(StudentClass studentClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> update = new HashMap<>();
        update.put(Constants.CRN, studentClass.CRN);
        update.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS).add(update)
                .addOnCompleteListener(task -> showToast("Class Added!"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addClass() {
        StudentClass studentClass = new StudentClass();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Class");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText className = new EditText(this);
        className.setHint("Class Name");
        layout.addView(className);

        final EditText classCRN = new EditText(this);
        classCRN.setHint("Class CRN");
        layout.addView(classCRN);

        builder.setView(layout);

        builder.setPositiveButton("Add", ((dialogInterface, i) -> {
            studentClass.name = className.getText().toString().trim();
            studentClass.CRN = classCRN.getText().toString().trim();
            checkCRN(studentClass.CRN);
            addClassToDatabase(studentClass);
        }));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    private void checkCRN(String crn) {
    }

    private void removeClass() {

    }

    private void updateClass() {

    }
}
