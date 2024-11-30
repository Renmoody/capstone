package com.example.studygo.activities.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentHomeBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements EventListener {

    FragmentHomeBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> events;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(requireContext());
        events = new ArrayList<>();
        eventAdapter = new EventAdapter(events, this);
        getEvents();
        return binding.getRoot();
    }

    private String getDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void showDialogue(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Join Event?");
        LinearLayout layout = new LinearLayout(requireContext());
        builder.setView(layout);

        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                event.members++;
                db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_EVENTS).document(event.id).update(Constants.KEY_MEMBERS, event.members);
                addUserToEvent(event);
                eventAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", ((dialogInterface, i) ->
                dialogInterface.dismiss()));
        builder.show();
    }


    private void getEvents() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Task<Boolean>> friendCheckTasks = new ArrayList<>();
        List<Event> tempEvents = new ArrayList<>();

        db.collection(Constants.KEY_COLLECTION_EVENTS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    loading(false);

                    // Skip private events
                    if (Constants.KEY_EVENT_ACCESS_PRIVATE.equals(queryDocumentSnapshot.get(Constants.KEY_EVENT_ACCESS))) {
                        continue;
                    }

                    // Handle friend-restricted events
                    if (Constants.KEY_EVENT_ACCESS_FRIENDS.equals(queryDocumentSnapshot.get(Constants.KEY_EVENT_ACCESS))) {
                        String authorId = queryDocumentSnapshot.getString(Constants.KEY_EVENT_AUTHOR_ID);

                        Task<Boolean> friendCheckTask = checkInFriendsTask(authorId);
                        friendCheckTasks.add(friendCheckTask.continueWith(task1 -> {
                            if (task1.isSuccessful() && task1.getResult()) {
                                // The author is a friend
                                Event event = createEventFromSnapshot(queryDocumentSnapshot);
                                tempEvents.add(event);
                            }
                            return task1.getResult();
                        }));
                    }

                    // Add public events or events authored by the current user
                    if (Constants.KEY_EVENT_ACCESS_PUBLIC.equalsIgnoreCase(Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_EVENT_ACCESS)).toString())) {
                        Event event = createEventFromSnapshot(queryDocumentSnapshot);
                        tempEvents.add(event);
                    }
                }

                // Wait for all friend checks to complete
                Tasks.whenAllComplete(friendCheckTasks).addOnCompleteListener(friendCheckTask -> {
                    if (!tempEvents.isEmpty()) {
                        events.clear();
                        events.addAll(tempEvents);
                        events.sort(Comparator.comparing(event -> event.dateObject));
                        binding.eventRecyclerView.setAdapter(eventAdapter);
                        binding.eventRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage();
                    }
                });
            } else {
                loading(false);
                showErrorMessage();
            }
        });
    }

    // Helper method to create an Event object from a Firestore document snapshot
    private Event createEventFromSnapshot(QueryDocumentSnapshot snapshot) {
        Event event = new Event();
        event.name = snapshot.getString(Constants.KEY_EVENT_NAME);
        event.details = snapshot.getString(Constants.KEY_EVENT_DETAILS);
        event.date = getDate(snapshot.getDate(Constants.KEY_EVENT_DATE));
        event.dateObject = snapshot.getDate(Constants.KEY_EVENT_DATE);
        event.members = Integer.parseInt(String.valueOf(snapshot.get(Constants.KEY_MEMBERS)));
        event.id = snapshot.getId();
        return event;
    }

    // Method to check friend relationship as a Task
    private Task<Boolean> checkInFriendsTask(String authorId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        // Query 1: Check if the current user is the sender and authorId is the friend
        Task<QuerySnapshot> query1 = db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, currentUserID)
                .whereEqualTo(Constants.KEY_FRIEND_ID, authorId)
                .get();

        // Query 2: Check if authorId is the sender and the current user is the friend
        Task<QuerySnapshot> query2 = db.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_USER_ID, authorId)
                .whereEqualTo(Constants.KEY_FRIEND_ID, currentUserID)
                .get();

        // Combine both queries
        Tasks.whenAllComplete(query1, query2).addOnCompleteListener(task -> {
            boolean isFriend = query1.isSuccessful() && query1.getResult() != null && !query1.getResult().isEmpty();
            // Check results from Query 2
            if (query2.isSuccessful() && query2.getResult() != null && !query2.getResult().isEmpty()) {
                isFriend = true;
            }

            // Set the result based on the findings
            tcs.setResult(isFriend);
        });

        return tcs.getTask();
    }


    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No Events available"));
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
    public void onEventClicked(Event event) {
        showDialogue(event);
    }

    private void addUserToEvent(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("registeredEvents", FieldValue.arrayUnion(event.id));
        Toast.makeText(requireContext(), "Event Joined!", Toast.LENGTH_SHORT).show();

    }

}
