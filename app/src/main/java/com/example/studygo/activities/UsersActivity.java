package com.example.studygo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.activities.ui.messages.MessagesFragment;
import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivityUsersBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getFriends();
    }


    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void getFriends() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);

        // Step 1: Fetch all friend relationships where the current user is involved
        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, currentUserID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> friendIds = new ArrayList<>();

                        // Add the friend IDs from each document
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String friendId = document.getString(Constants.KEY_FRIEND_ID);
                            friendIds.add(friendId);
                        }

                        // Fetch friends where the current user is the friend (reverse relationship)
                        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                                .whereEqualTo(Constants.KEY_FRIEND_ID, currentUserID)
                                .get()
                                .addOnCompleteListener(reverseTask -> {
                                    if (reverseTask.isSuccessful() && reverseTask.getResult() != null) {
                                        for (QueryDocumentSnapshot document : reverseTask.getResult()) {
                                            String friendId = document.getString(Constants.KEY_USER_ID);
                                            if (!friendIds.contains(friendId)) { // Avoid duplicates
                                                friendIds.add(friendId);
                                            }
                                        }
                                        // Step 2: Retrieve user details for each friend
                                        fetchFriendDetails(friendIds);
                                    } else {
                                        showErrorMessage(); // Handle error in reverse fetch
                                    }
                                });
                    } else {
                        loading(false);
                        showErrorMessage(); // Handle error in the initial fetch
                    }
                });
    }

    // Helper method to fetch user details
    private void fetchFriendDetails(List<String> friendIds) {
        if (friendIds.isEmpty()) {
            loading(false);
            showErrorMessage(); // No friends found
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<User> friends = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0); // To track the number of completed fetches

        // Loop through each friend ID to fetch user details
        for (String friendId : friendIds) {
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(friendId)
                    .get()
                    .addOnSuccessListener(userDocument -> {
                        if (userDocument.exists()) {
                            User user = new User();
                            user.name = userDocument.getString(Constants.KEY_NAME);
                            user.email = userDocument.getString(Constants.KEY_EMAIL);
                            user.image = userDocument.getString(Constants.KEY_IMAGE);
                            user.token = userDocument.getString(Constants.KEY_FCM_TOKEN);
                            user.id = userDocument.getId();
                            friends.add(user);
                        }
                        if (counter.incrementAndGet() == friendIds.size()) {
                            loading(false);
                            updateRecyclerView(friends);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (counter.incrementAndGet() == friendIds.size()) {
                            loading(false);
                            updateRecyclerView(friends); // Update with whatever data was fetched
                        }
                    });
        }
    }

    // Helper method to update RecyclerView
    private void updateRecyclerView(List<User> friends) {
        if (!friends.isEmpty()) {
            UsersAdapter usersAdapter = new UsersAdapter(friends, this, null);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage(); // No friends found to display
        }
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
        Intent intent = new Intent(getApplicationContext(), MessagesFragment.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();

    }
}
