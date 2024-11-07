package com.example.studygo.activities.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.studygo.R;
import com.example.studygo.activities.LogIn;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private PreferenceManager preferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

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
            db.collection("users").document(Constants.KEY_USER_ID).delete().addOnSuccessListener(task -> {
                Toast.makeText(requireContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                preferenceManager.clear();
            });


        }
        return super.onPreferenceTreeClick(preference);
    }
}