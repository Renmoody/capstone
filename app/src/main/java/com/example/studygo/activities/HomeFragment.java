package com.example.studygo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentHomeBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements EventListener {

    private EventAdapter eventAdapter;
    private List<Event> events;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    FragmentHomeBinding binding;

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
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                events.add(event);
                eventAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", ((dialogInterface, i) ->
                dialogInterface.dismiss()));
        builder.show();
    }

    private void getEvents() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EVENTS).get().addOnCompleteListener(task -> {
                loading(false);
                String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (currentUserID.equals(queryDocumentSnapshot.getId())) continue;
                        Event event = new Event();
                        event.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                        event.details = queryDocumentSnapshot.getString(Constants.KEY_MESSAGE);
                        event.date = getDate(queryDocumentSnapshot.getDate(Constants.KEY_EVENT_DATE));
                        event.dateObject =queryDocumentSnapshot.getDate(Constants.KEY_EVENT_DATE);
                        events.add(event);
                    }
                    if (!events.isEmpty()) {
                        events.sort(Comparator.comparing(obj -> obj.dateObject));
                        binding.eventRecyclerView.setAdapter(eventAdapter);
                        binding.eventRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage();
                    }
                } else {
                    showErrorMessage();
                }
            });
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

}
