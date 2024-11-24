package com.example.studygo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivityRequestsBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityRequests extends AppCompatActivity implements UserListener {

    private ActivityRequestsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        getRequests();

    }

    private void getRequests() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
        db.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS).whereEqualTo(Constants.KEY_USER_ID, currentUserID).get().addOnCompleteListener(task -> {
            loading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<User> users = new ArrayList<>();
                
                if (!users.isEmpty()) {
                    UsersAdapter usersAdapter = new UsersAdapter(users, this);
                    binding.usersRecyclerView.setAdapter(usersAdapter);
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No users available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view ->
                getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public void onUserClicked(User user) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Accept Request", (dialog, which) -> addFriend(user));
        builder.show();
    }

    private void addFriend(User user) {
    }
}
