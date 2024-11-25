package com.example.studygo.activities.ui.friends;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.studygo.activities.ActivityAddFriend;
import com.example.studygo.activities.ActivityRequests;
import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivityFriendsBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsFragment extends Fragment implements UserListener {

    private ActivityFriendsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityFriendsBinding.inflate(inflater, container, false);
        preferenceManager = new PreferenceManager(requireContext());
        setListeners();
        getFriends();
        return binding.getRoot();
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

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No friends yet, click on the plus to make friend requests!"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    // Helper method to update RecyclerView
    private void updateRecyclerView(List<User> friends) {
        if (!friends.isEmpty()) {
            UsersAdapter usersAdapter = new UsersAdapter(friends, this);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage(); // No friends found to display
        }
    }


    private void setListeners() {
        binding.imageAdd.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), ActivityAddFriend.class)));
        binding.imageRequests.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), ActivityRequests.class)));
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
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        builder.setView(layout);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Remove Friend", (dialog, which) -> removeFriend(user));
        builder.show();
    }

    private void removeFriend(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);

        // First query: currentUserID is the sender, user.id is the friend
        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, currentUserID)
                .whereEqualTo(Constants.KEY_FRIEND_ID, user.id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                            ds.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Friend removed successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error removing friend", Toast.LENGTH_SHORT).show());
                        }
                    }
                });

        // Second query: user.id is the sender, currentUserID is the friend
        db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, user.id)
                .whereEqualTo(Constants.KEY_FRIEND_ID, currentUserID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                            ds.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Friend removed successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error removing friend", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }


}
