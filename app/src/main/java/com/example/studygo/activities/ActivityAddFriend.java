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
import java.util.List;

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
        db.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_ACCOUNT_TYPE, Constants.KEY_ACCOUNT_STUDNET).get().addOnCompleteListener(task -> {
            loading(false);
            String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
            if (task.isSuccessful() && task.getResult() != null) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    if (currentUserID.equals(queryDocumentSnapshot.getId())) {
                        continue;
                    }
                    User user = new User();
                    user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                    user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                    user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                    user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                    user.id = queryDocumentSnapshot.getId();
                    users.add(user);
                }
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
