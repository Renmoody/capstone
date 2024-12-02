package com.example.studygo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.R;
import com.example.studygo.adapters.StudentClassAdapter;
import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivitySelectClassBinding;
import com.example.studygo.listeners.StudentClassListener;
import com.example.studygo.models.StudentClass;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ActivitySelectClass extends AppCompatActivity implements StudentClassListener {
    ActivitySelectClassBinding binding;
    PreferenceManager preferenceManager;
    private String currentClassId;
    private String currentClassName;
    private List<StudentClass> studentClassList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        studentClassList = new ArrayList<>();
        getClasses();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        binding.imageInfo.setOnClickListener(view -> addClass());
    }

    private void getClasses() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS).whereEqualTo(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        loading(false);
                        if (studentClassList.isEmpty()) {
                            StudentClassAdapter classAdapter = new StudentClassAdapter(studentClassList, this);
                            binding.studentClassRecyclerView.setAdapter(classAdapter);
                        }
                        for (QueryDocumentSnapshot qs : task.getResult()) {
                            Log.d("CLASS", Objects.requireNonNull(qs.getString(Constants.KEY_CLASS_NAME)));
                            StudentClass studentClass = new StudentClass();
                            studentClass.name = qs.getString(Constants.KEY_CLASS_NAME);
                            studentClass.CRN = qs.getString(Constants.KEY_CRN);
                            studentClassList.add(studentClass);
                        }
                    } else {
                        showErrorMessage(); // No friends found to display
                    }
                });
    }

    private void undoErrorMessage() {
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void loading(boolean b) {
        if (b) binding.progressBar.setVisibility(View.VISIBLE);
        else binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(R.string.no_classes_added);
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void addClassToDatabase(StudentClass studentClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> update = new HashMap<>();
        update.put(Constants.KEY_CRN, studentClass.CRN);
        update.put(Constants.KEY_CLASS_NAME, studentClass.name);
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
        builder.setNegativeButton("Delete", (dialog, which) -> removeUserFromClass(currentClassId));
        builder.show();

    }

    private void checkCRN(String crn) {
    }

    private void removeUserFromClass(String currentClassId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS)
                .document(currentClassId)
                .delete()
                .addOnSuccessListener(task -> showToast(String.format("%s %s", "Removed", currentClassName)));

    }

    private void updateClass() {

    }

    @Override
    public void onClassClicked(StudentClass studentClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS)
                .whereEqualTo(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_CRN, studentClass.CRN)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                        currentClassId = ds.getId();
                        currentClassName = ds.getString(Constants.KEY_CLASS_NAME);
                        Log.d("USER CLICKED", currentClassId);
                        showOptions(studentClass);
                    }
                });
    }

    private void showOptions(StudentClass studentClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(studentClass.name);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setHint("Class name");
        name.setText(currentClassName);
        layout.addView(name);

        EditText crn = new EditText(this);
        name.setHint("CRN");
        layout.addView(crn);

        builder.setNeutralButton("Delete", ((dialogInterface, i) -> deleteClass()));
        builder.setPositiveButton("Save Changes", ((dialogInterface, i) -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_CLASS_NAME, name.getText().toString().trim());
            updates.put(Constants.KEY_CRN, crn.getText().toString().trim());
            db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS).document(currentClassId)
                    .update(updates).addOnCompleteListener(task -> showToast("Update Complete"));
        }));
        builder.setView(layout);
        builder.show();
    }

    private void deleteClass() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS).document(currentClassId).delete()
                .addOnCompleteListener(task -> showToast("Class Deleted!"));
    }
}
