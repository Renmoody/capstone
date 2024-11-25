package com.example.studygo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.activities.ui.messages.MessagesFragment;
import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivityAddFriendBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityAddFriend extends AppCompatActivity implements UserListener {

    ActivityAddFriendBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view ->
                getOnBackPressedDispatcher().onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);

        // Step 1: Fetch all friends of the current user in a single query
        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, currentUserID)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null) {
                        Set<String> friendIds = new HashSet<>();

                        // Add friends where currentUserID is the userId
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            friendIds.add(document.getString(Constants.KEY_FRIEND_ID));
                        }

                        // Add friends where currentUserID is the friendId
                        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                                .whereEqualTo(Constants.KEY_FRIEND_ID, currentUserID)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful() && task2.getResult() != null) {
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            friendIds.add(document.getString(Constants.KEY_USER_ID));
                                        }

                                        // Step 2: Fetch users excluding the current user and their friends
                                        fetchUsersExcludingFriends(friendIds, currentUserID);
                                    } else {
                                        loading(false);
                                        showErrorMessage();
                                    }
                                });
                    } else {
                        loading(false);
                        showErrorMessage();
                    }
                });
    }

    private void fetchUsersExcludingFriends(Set<String> friendIds, String currentUserID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_ACCOUNT_TYPE, Constants.KEY_ACCOUNT_STUDNET)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            String userId = queryDocumentSnapshot.getId();

                            // Skip the current user and friends
                            if (currentUserID.equals(userId) || friendIds.contains(userId)) {
                                continue;
                            }

                            // Only retrieve necessary fields to reduce network overhead
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = userId;
                            users.add(user);
                        }

                        // Update the UI with filtered users
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

    @Override
    public void onUserClicked(User user) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Add Friend", (dialog, which) -> sendFriendRequest(user));
        builder.show();

    }

    private void sendFriendRequest(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> request = new HashMap<>();
        request.put(Constants.KEY_RECIEVER_ID, user.id);
        request.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        db.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS).add(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
