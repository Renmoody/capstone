package com.example.studygo.activities.ui.dashboard;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment implements EventListener {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private EventAdapter eventAdapter;
    private List<Event> events;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        events = new ArrayList<>();
        eventAdapter = new EventAdapter(events, this);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

// Initialize ViewModel
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);


    }
//TODO finsih setting up eventselector class and set up logic for adding new events to db
    private void setListeners() {
        binding.fabNewEvent.setOnClickListener(view -> {
//            Intent intent = new Intent(requireContext(), EventSelector.class);
//            startActivity(intent);

        });
    }


    private int mYear, mMonth, mDay, mHour, mMinute;

    private void showEventDialog(int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Event");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText eventTimeInput = new EditText(requireContext());
        eventTimeInput.setHint("Event Time");
        layout.addView(eventTimeInput);

        final EditText eventNameInput = new EditText(requireContext());
        eventNameInput.setHint("Event Name");
        layout.addView(eventNameInput);

        final EditText eventDetailsInput = new EditText(requireContext());
        eventDetailsInput.setHint("Event Details (Optional)");
        layout.addView(eventDetailsInput);

        Spinner eventAccessInput = new Spinner(requireContext());

        // Set up the options for the Spinner
        String[] accessOptions = new String[]{"Public", "Private", "Friends Only"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, accessOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventAccessInput.setAdapter(adapter);
        layout.addView(eventAccessInput);
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        eventTimeInput.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();


        builder.setView(layout);

        builder.setPositiveButton("Register Event", (dialog, which) -> {
            String eventName = eventNameInput.getText().toString();
            String eventDetails = eventDetailsInput.getText().toString();
            Date eventDate = getDate(year, month, day);
            String eventTime = eventTimeInput.getText().toString();
            String access = eventAccessInput.getSelectedItem().toString();
            if (eventName.isEmpty() || eventTime.isEmpty()) {
                Toast.makeText(getContext(), "Fill out required fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            Event event = new Event(access, eventName, eventDetails, eventDate, eventTime);
            dashboardViewModel.addEvent(event);  // Add event to ViewModel
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    //    When clicking on an event in event list, this method is called and will
//    prompt an event dialog where the user can edit the event contents
    private void showEditEventDialog(Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Event");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText eventTimeInput = new EditText(requireContext());
        eventTimeInput.setHint("Event Time");
        eventTimeInput.setText(event.time);
        layout.addView(eventTimeInput);

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

                dashboardViewModel.updateEvent(event, position); // Update event in ViewModel
                adapter.notifyDataSetChanged(); // Notify adapter about changes
            }
        });
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dashboardViewModel.deleteEvent(position); // Update event in ViewModel
                adapter.notifyDataSetChanged(); // Notify adapter about changes
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public Date getDate(int year, int month, int dayOfMonth) {
        Date selectedDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0); // Reset milliseconds for consistency
        selectedDate = calendar.getTime();
        return selectedDate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventClicked(Event event) {

    }
}
