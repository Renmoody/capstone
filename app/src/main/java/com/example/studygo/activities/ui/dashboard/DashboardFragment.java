package com.example.studygo.activities.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.R;
import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        getEvents();
        addEvents(eventIds);
        return binding.getRoot();
    }


    private ActivityResultLauncher<Intent> eventLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
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
        HashMap<String, Object> register = new HashMap<>();
        event.put(Constants.KEY_EVENT_NAME, e.name);
        event.put(Constants.KEY_EVENT_DETAILS, e.details);
        event.put(Constants.KEY_EVENT_ACCESS, e.access);
        event.put(Constants.KEY_EVENT_DATE, e.dateObject);
        event.put(Constants.KEY_MEMBERS, e.members);
        db.collection(Constants.KEY_COLLECTION_EVENTS).add(event).addOnSuccessListener(documentReference -> {
            register.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            register.put(Constants.KEY_EVENT_ID, documentReference.getId());
            db.collection(Constants.KEY_COLLECTION_EVENT_USERS).add(register);
        });
    }

    private final List<String> eventIds = new ArrayList<>();

    private void getEvents() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EVENT_USERS).get().addOnCompleteListener(task -> {
            loading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    if (!Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_USER_ID)).toString().equals(preferenceManager.getString(Constants.KEY_USER_ID)))
                        continue;
                    eventIds.add(Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_EVENT_ID)).toString());
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

    private void addEvents(List<String> eventIds) {
        if (eventIds.isEmpty()) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection(Constants.KEY_COLLECTION_EVENTS);
        for (String id : eventIds) {
            DocumentReference documentReference = ref.document(id);
            documentReference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            loading(false);
                            Event event = new Event();
                            event.name = documentSnapshot.getString(Constants.KEY_EVENT_NAME);
                            event.details = documentSnapshot.getString(Constants.KEY_EVENT_DETAILS);
                            event.date = getDate(documentSnapshot.getDate(Constants.KEY_EVENT_DATE));
                            event.dateObject = documentSnapshot.getDate(Constants.KEY_EVENT_DATE);
                            event.members = Integer.parseInt(String.valueOf(documentSnapshot.get(Constants.KEY_MEMBERS)));
                            event.id = documentSnapshot.getId();
                            events.add(event);

                        }
                    }
            );
        }
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, accessOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventAccessInput.setAdapter(adapter);
        layout.addView(eventAccessInput);

        builder.setView(layout);

        // Save changes
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedEventName = eventNameInput.getText().toString();
                String updatedEventDetails = eventDetailsInput.getText().toString();
                String updatedEventTime = eventTimeInput.getText().toString();
                String updatedAccess = eventAccessInput.getSelectedItem().toString();


                if (updatedEventName.isEmpty() || updatedEventTime.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill out required fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the event object with new values
                event.setName(updatedEventName);
                event.setDetails(updatedEventDetails);
                event.setTime(updatedEventTime);
                event.setAccess(updatedAccess);


//               TODO update database with updated values
                adapter.notifyDataSetChanged(); // Notify adapter about changes
            }
        });
//        TODO finish deleting from viewmodel
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        showEditEventDialog(event);
    }

    private void showTime(EditText editTextSelectTime) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        editTextSelectTime.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String getDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No Events available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
}
