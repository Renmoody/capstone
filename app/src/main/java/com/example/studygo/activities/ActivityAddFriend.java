package com.example.studygo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.adapters.UsersAdapter;
import com.example.studygo.databinding.ActivityAddFriendBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityAddFriend extends AppCompatActivity implements UserListener {

    ActivityAddFriendBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading(true);
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getCRNS();
        //        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view ->
                getOnBackPressedDispatcher().onBackPressed());
    }

    private final ArrayList<String> crns = new ArrayList<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private void getCRNS() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS)
                .whereEqualTo(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot qs : task.getResult()) {
                            crns.add(qs.getString(Constants.KEY_CRN));
                        }
                        // Run getSimilarStudents() on a separate thread
                        executorService.execute(this::getSimilarStudents);
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void getSimilarStudents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(Constants.KEY_COLLECTION_REGISTERED_CLASS);
        CollectionReference userRef = db.collection(Constants.KEY_COLLECTION_USERS);
        CollectionReference friendsRef = db.collection(Constants.KEY_COLLECTION_FRIENDS);

        Set<String> friendIds = new HashSet<>();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        // Fetch friends asynchronously
        friendsRef.whereEqualTo(Constants.KEY_USER_ID, currentUserId)
                .get()
                .addOnSuccessListener(friendTask1 -> {
                    if (friendTask1 != null && !friendTask1.isEmpty()) {
                        for (DocumentSnapshot doc : friendTask1.getDocuments()) {
                            String friendId = doc.getString(Constants.KEY_FRIEND_ID);
                            if (friendId != null) friendIds.add(friendId);
                        }
                    }

                    friendsRef.whereEqualTo(Constants.KEY_FRIEND_ID, currentUserId)
                            .get()
                            .addOnSuccessListener(friendTask2 -> {
                                if (friendTask2 != null && !friendTask2.isEmpty()) {
                                    for (DocumentSnapshot doc : friendTask2.getDocuments()) {
                                        String userId = doc.getString(Constants.KEY_USER_ID);
                                        if (userId != null) friendIds.add(userId);
                                    }
                                }

                                // Proceed to fetch students
                                executorService.execute(() -> fetchStudentsExcludingFriends(collection, userRef, friendIds));
                            });
                });
    }

    private final List<User> students = new ArrayList<>();
    private Map<String, Map<String, String>> userCommonCrnsMap = new HashMap<>();
    private void fetchStudentsExcludingFriends(CollectionReference collection, CollectionReference userRef, Set<String> friendIds) {


        for (String crn : crns) {
            collection.whereEqualTo(Constants.KEY_CRN, crn)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot qs : task.getResult()) {
                                String userId = qs.getString(Constants.KEY_USER_ID);
                                if (userId != null && !userId.equals(preferenceManager.getString(Constants.KEY_USER_ID)) && !friendIds.contains(userId)) {
                                    userRef.document(userId)
                                            .get()
                                            .addOnSuccessListener(userDocument -> {
                                                if (userDocument.exists()) {
                                                    synchronized (students) {
                                                        String userIdFromDoc = userDocument.getId();
                                                        String crnName = qs.getString(Constants.KEY_CLASS_NAME);

                                                        userCommonCrnsMap.putIfAbsent(userIdFromDoc, new HashMap<>());
                                                        Objects.requireNonNull(userCommonCrnsMap.get(userIdFromDoc)).put(crn, crnName);

                                                        if (findUserById(userIdFromDoc) == null) {
                                                            User user = new User();
                                                            user.name = userDocument.getString(Constants.KEY_NAME);
                                                            user.email = userDocument.getString(Constants.KEY_EMAIL);
                                                            user.image = userDocument.getString(Constants.KEY_IMAGE);
                                                            user.token = userDocument.getString(Constants.KEY_FCM_TOKEN);
                                                            user.major = userDocument.getString(Constants.KEY_MAJOR);
                                                            user.id = userIdFromDoc;
                                                            students.add(user);
                                                        }
                                                    }

                                                    // Update RecyclerView on the main thread
                                                    mainHandler.post(() -> updateRecyclerViewWithCommonCrns(students, userCommonCrnsMap));
                                                }
                                            });
                                }
                            }
                            // After fetching similar students, fetch additional users
                            executorService.execute(() -> fetchAdditionalUsers(userRef, friendIds));
                        } else {
                            mainHandler.post(this::showErrorMessage);
                        }
                    });
        }
    }

    private void fetchAdditionalUsers(CollectionReference userRef, Set<String> friendIds) {
        userRef.whereEqualTo(Constants.KEY_ACCOUNT_TYPE, Constants.KEY_ACCOUNT_STUDNET)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot userDoc : task.getResult()) {
                            String userId = userDoc.getId();
                            if (!friendIds.contains(userId) && findUserById(userId) == null && !userId.equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
                                synchronized (students) {
                                    User user = new User();
                                    user.name = userDoc.getString(Constants.KEY_NAME);
                                    user.email = userDoc.getString(Constants.KEY_EMAIL);
                                    user.image = userDoc.getString(Constants.KEY_IMAGE);
                                    user.major = userDoc.getString(Constants.KEY_MAJOR);
                                    user.token = userDoc.getString(Constants.KEY_FCM_TOKEN);
                                    user.id = userId;
                                    students.add(user);
                                }
                            }
                        }
                        // Update RecyclerView on the main thread
                        mainHandler.post(() -> updateRecyclerViewWithAdditionalUsers(students, userCommonCrnsMap));
                    } else {
                        mainHandler.post(this::showErrorMessage);
                    }
                });
    }

    private void updateRecyclerViewWithAdditionalUsers(List<User> students, Map<String, Map<String, String>> userCommonCrnsMap) {
        if (!students.isEmpty()) {
            loading(false);
            UsersAdapter usersAdapter = new UsersAdapter(students, this, userCommonCrnsMap);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage();
        }

    }

    // Helper method to find a user in the list by ID
    private User findUserById(String userId) {
        for (User user : students) {
            if (user.id.equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // Update the RecyclerView with the list of students and their common CRNs
    private void updateRecyclerViewWithCommonCrns(List<User> students, Map<String, Map<String, String>> userCommonCrnsMap) {
        if (!students.isEmpty()) {
            loading(false);
            UsersAdapter usersAdapter = new UsersAdapter(students, this, userCommonCrnsMap);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage();
        }

    }


    private void showErrorMessage() {
        loading(false);
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
