package com.example.studygo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.HashMap;
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

        // Fetch friend requests where the current user is the receiver
        db.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, currentUserID) // Adjust this based on your field name
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();

                        // Loop through each friend request document
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String senderUserId = document.getString(Constants.KEY_USER_ID); // Sender's ID

                            // Fetch user details for each sender
                            if (senderUserId != null) {
                                db.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(senderUserId)
                                        .get()
                                        .addOnSuccessListener(userDocument -> {
                                            if (userDocument.exists()) {
                                                User user = new User();
                                                user.name = userDocument.getString(Constants.KEY_NAME);
                                                user.email = userDocument.getString(Constants.KEY_EMAIL);
                                                user.image = userDocument.getString(Constants.KEY_IMAGE);
                                                user.token = userDocument.getString(Constants.KEY_FCM_TOKEN);
                                                user.id = userDocument.getId();
                                                users.add(user);

                                                // Update RecyclerView when all users are loaded
                                                if (users.size() == task.getResult().size()) {
                                                    updateRecyclerView(users);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            showErrorMessage(); // Handle errors when fetching user data
                                        });
                            }
                        }

                        // If there are no friend requests
                        if (task.getResult().isEmpty()) {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage(); // Handle errors when fetching friend requests
                    }
                });
    }

    // Helper method to update RecyclerView
    private void updateRecyclerView(List<User> users) {
        if (!users.isEmpty()) {
            UsersAdapter usersAdapter = new UsersAdapter(users, this, null);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage();
        }
    }


    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No friend requests"));
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
        builder.setNegativeButton("Deny", ((dialogInterface, i) -> deleteRequest(user)));
        builder.setPositiveButton("Accept Request", (dialog, which) -> addFriend(user));
        builder.show();
    }

    //    TODO Delete friend request
    private void deleteRequest(User user) {
    }


    private void addFriend(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Add the friend to the 'Friends' collection
        HashMap<String, String> friend = new HashMap<>();
        friend.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        friend.put(Constants.KEY_FRIEND_ID, user.id);

        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .add(friend)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Friend Added", Toast.LENGTH_SHORT).show();

                        // Step 2: Delete the friend request
                        db.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                                .whereEqualTo(Constants.KEY_RECIEVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                                .whereEqualTo(Constants.KEY_USER_ID, user.id)
                                .get()
                                .addOnCompleteListener(requestTask -> {
                                    if (requestTask.isSuccessful() && !requestTask.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : requestTask.getResult()) {
                                            // Delete each matching friend request
                                            db.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                                                    .document(document.getId())
                                                    .delete();
                                        }
                                    } else {
                                        Toast.makeText(this, "No matching friend request found", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to add friend", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
