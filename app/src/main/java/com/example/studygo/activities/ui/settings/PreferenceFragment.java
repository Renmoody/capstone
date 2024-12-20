package com.example.studygo.activities.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.studygo.R;
import com.example.studygo.activities.LogIn;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class PreferenceFragment extends PreferenceFragmentCompat {
    EditTextPreference email;
    EditTextPreference username;
    EditTextPreference password;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        preferenceManager = new PreferenceManager(requireContext());
        email = findPreference(Constants.KEY_EMAIL);
        username = findPreference(Constants.KEY_NAME);
        password = findPreference(Constants.KEY_PASSWORD);

        if (email != null) {
            email.setOnPreferenceChangeListener((preference, newValue) -> {
                String updatedEmail = newValue.toString();
                // Handle email update logic, e.g., update in Firestore
                updateUserField(Constants.KEY_EMAIL, updatedEmail);
                return true;  // Returning true saves the preference
            });
        }

        if (username != null) {
            username.setOnPreferenceChangeListener((preference, newValue) -> {
                String updatedUsername = newValue.toString();
                // Handle username update logic, e.g., update in Firestore
                updateUserField(Constants.KEY_NAME, updatedUsername);
                return true;  // Returning true saves the preference
            });
        }

        if (password != null) {
            password.setOnPreferenceChangeListener((preference, newValue) -> {
                String updatedPassword = newValue.toString();
                // Handle password update logic securely
                updateUserField(Constants.KEY_PASSWORD, updatedPassword);
                return true;  // Returning true saves the preference
            });
        }
        getUser();
    }

    private void updateUserField(String key, String update) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(key, update);
        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).update(updates)
                .addOnSuccessListener(v -> Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(v -> Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("sign_out".equals(key)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection("users").document(
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updates).addOnSuccessListener(unused -> {
                        preferenceManager.clear();
                        startActivity(new Intent(requireContext(), LogIn.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Unable to sign out", Toast.LENGTH_SHORT).show());

        }
        if ("delete_account".equals(key)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).delete().addOnSuccessListener(task -> {
                Toast.makeText(requireContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                preferenceManager.clear();
            });


        }
        return super.onPreferenceTreeClick(preference);
    }

    private void getUser() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    email.setText(Objects.requireNonNull(document.get(Constants.KEY_EMAIL)).toString());
                    username.setText(Objects.requireNonNull(document.get(Constants.KEY_NAME)).toString());
                    password.setText(Objects.requireNonNull(document.get(Constants.KEY_PASSWORD)).toString());
                } else {
                    Log.d("DOCUMENT", "Doesnt exist");
                }
            } else {
                Log.d("TASK", "get failed with ", task.getException());
            }
        });
        if (password != null) {
            password.setOnBindEditTextListener(editText ->
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
            );
        }
    }
}