package com.example.studygo.activities.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.studygo.R;
import com.example.studygo.activities.ui.messages.GroupMessagesFragment;
import com.example.studygo.activities.ui.messages.MessagesFragment;
import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DashboardFragment extends Fragment implements EventListener {

    private FragmentDashboardBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> events;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        events = new ArrayList<>();
        eventAdapter = new EventAdapter(events, this);
        preferenceManager = new PreferenceManager(requireContext());
        setListeners();
        addEvents();
        return binding.getRoot();
    }


    private final ActivityResultLauncher<Intent> eventLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.hasExtra("event")) {
                Event event = (Event) data.getSerializableExtra("event");
                if (event != null) {
                    publishEvent(event);
                }
            }
        }
    });


    private void publishEvent(Event e) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> event = new HashMap<>();
        event.put(Constants.KEY_EVENT_NAME, e.name);
        event.put(Constants.KEY_EVENT_DETAILS, e.details);
        event.put(Constants.KEY_EVENT_ACCESS, e.access);
        event.put(Constants.KEY_EVENT_DATE, e.dateObject);
        event.put(Constants.KEY_MEMBERS, e.members);
        event.put(Constants.KEY_EVENT_AUTHOR_ID, e.authorId);
        event.put(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        db.collection(Constants.KEY_COLLECTION_EVENTS).add(event).addOnSuccessListener(documentReference -> {
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.KEY_ARRAY_REGISTERED_EVENTS, FieldValue.arrayUnion(documentReference.getId()));
            Toast.makeText(requireContext(), "Event published!", Toast.LENGTH_SHORT).show();

        });
    }

    private void addEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection(Constants.KEY_COLLECTION_USERS);
        DocumentReference df = ref.document(preferenceManager.getString(Constants.KEY_USER_ID));
        df.get().addOnSuccessListener(documentSnapshot -> {
            //noinspection unchecked
            ArrayList<String> events = (ArrayList<String>) documentSnapshot.get(Constants.KEY_ARRAY_REGISTERED_EVENTS);
            if (events != null) {
                for (String event : events) {
                    getEvent(event);
                    Log.d("EVENTS", event.toString());
                }
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                if (events.isEmpty()) {
                    showErrorMessage();
                }
            }
        });

    }

    private void getEvent(String e) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EVENTS).document(e).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                loading(false);
                Event event = new Event();
                event.name = documentSnapshot.getString(Constants.KEY_EVENT_NAME);
                event.details = documentSnapshot.getString(Constants.KEY_EVENT_DETAILS);
                event.date = getDate(documentSnapshot.getDate(Constants.KEY_EVENT_DATE));
                event.dateObject = documentSnapshot.getDate(Constants.KEY_EVENT_DATE);
                event.members = Integer.parseInt(String.valueOf(documentSnapshot.get(Constants.KEY_MEMBERS)));
                event.id = documentSnapshot.getId();
                event.image = documentSnapshot.getString(Constants.KEY_IMAGE);
                event.authorId = preferenceManager.getString(Constants.KEY_USER_ID);
                events.add(event);
            }
            if (!events.isEmpty()) {
                undoErrorMessage();
                events.sort(Comparator.comparing(obj -> obj.dateObject));
                binding.eventRecyclerView.setAdapter(eventAdapter);
                binding.eventRecyclerView.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }

        });
    }

    private void setListeners() {
        loading(true);
        binding.fabNewEvent.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), EventSelector.class);
            eventLauncher.launch(intent);
        });
    }

    //    When clicking on an event in event list, this method is called and will
//    prompt an event dialog where the user can edit the event contents
    private void showEditEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Event");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layout1 = new LinearLayout(requireContext());
        layout1.setOrientation(LinearLayout.HORIZONTAL);

        final EditText eventTimeInput = new EditText(requireContext());
        eventTimeInput.setHint("Event Time");
        eventTimeInput.setText(event.time);
        layout1.addView(eventTimeInput);

        final Button timeButton = new Button(requireContext());
        timeButton.setText(R.string.select);
        timeButton.setOnClickListener(view -> showTime(eventTimeInput));
        layout1.addView(timeButton);
        layout.addView(layout1);

        final EditText eventNameInput = new EditText(requireContext());
        eventNameInput.setHint("Event Name");
        eventNameInput.setText(event.name);
        layout.addView(eventNameInput);

        final EditText eventDetailsInput = new EditText(requireContext());
        eventDetailsInput.setHint("Event Details (Optional)");
        eventDetailsInput.setText(event.details);
        layout.addView(eventDetailsInput);

        Spinner eventAccessInput = new Spinner(requireContext());
        // Set up the options for the Spinner
        String[] accessOptions = new String[]{"Public", "Private", "Friends Only"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, accessOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventAccessInput.setAdapter(adapter);
        layout.addView(eventAccessInput);

        builder.setView(layout);

        // Save changes
        builder.setPositiveButton("Save", (dialog, which) -> {
            if (eventNameInput.getText().toString().isEmpty() ||
                    eventTimeInput.getText().toString().isEmpty() ||
                    eventDetailsInput.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please fill out required fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            event.name = eventNameInput.getText().toString();
            event.details = eventDetailsInput.getText().toString();
            event.time = eventTimeInput.getText().toString();
            event.access = eventAccessInput.getSelectedItem().toString();
            adapter.notifyDataSetChanged();
            updateEvent(event);
        });
        builder.setPositiveButton("Chat", ((dialogInterface, i) -> startGroupChat(event)));
        builder.setNeutralButton("Delete", (dialog, which) -> deleteEvent(event));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startGroupChat(Event event) {
        Intent intent = new Intent(requireContext(), GroupMessagesFragment.class);
        intent.putExtra(Constants.KEY_GROUP, event);
        startActivity(intent);

    }

    private void updateEvent(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_EVENT_ACCESS, event.access);
        updates.put(Constants.KEY_EVENT_DETAILS, event.details);
        updates.put(Constants.KEY_EVENT_NAME, event.name);
        db.collection(Constants.KEY_COLLECTION_EVENTS).document(event.id).update(updates);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //    Toggles loading animation
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //    Checks if the event the user clicked on is the author of it or not,
//    if they are they have the ability to edit, if not they can simply unregister
    @Override
    public void onEventClicked(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EVENTS).document(event.id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot ds = task.getResult();
                if (Objects.equals(ds.get(Constants.KEY_EVENT_AUTHOR_ID),
                        preferenceManager.getString(Constants.KEY_USER_ID))) {
                    showEditEventDialog(event);
                } else {
                    showEventDialog(event);
                }
            }
        });
    }

    private void showEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Hey there!");
        builder.setNeutralButton("Chat", ((dialogInterface, i) -> startGroupChat(event)));
        builder.setPositiveButton("Unregister", (dialogInterface, i) -> unregister(event));
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();

    }

    private void unregister(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection(Constants.KEY_COLLECTION_USERS);
        DocumentReference df = ref.document(preferenceManager.getString(Constants.KEY_USER_ID));
        df.get().addOnSuccessListener(documentSnapshot -> {

            Map<String, Object> update = new HashMap<>();
            update.put(Constants.KEY_ARRAY_REGISTERED_EVENTS, FieldValue.arrayRemove(event.id));
            df.update(update);
            Toast.makeText(requireContext(), "Unregistered", Toast.LENGTH_SHORT).show();
            update.clear();
            update.put(Constants.KEY_MEMBERS, event.members - 1);
            db.collection(Constants.KEY_COLLECTION_EVENTS).document(event.id).update(update);

        });
        db.collection(Constants.KEY_COLLECTION_EVENTS)
                .document(event.id)
                .update(Constants.KEY_ARRAY_GROUP_MEMBERS,
                FieldValue.arrayRemove(preferenceManager.getString(Constants.KEY_USER_ID)));

    }

    private void showTime(EditText editTextSelectTime) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> editTextSelectTime.setText(String.format("%d:%d", hourOfDay, minute)), mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String getDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void undoErrorMessage() {
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        loading(false);
        binding.textErrorMessage.setText(String.format("%s", "No Events available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void deleteEvent(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EVENTS)
                .document(event.id).delete()
                .addOnSuccessListener(task -> {
                    db.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID))
                            .update(Constants.KEY_ARRAY_REGISTERED_EVENTS, FieldValue.arrayRemove(event.id));
                    Toast.makeText(requireContext(),"Event Deleted", Toast.LENGTH_SHORT).show();
                });
    }

}
