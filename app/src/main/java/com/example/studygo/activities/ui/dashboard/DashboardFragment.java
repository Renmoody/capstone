package com.example.studygo.activities.ui.dashboard;

import static android.app.Activity.RESULT_OK;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        setListeners();

        dashboardViewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> updatedEvents) {
                loading(false);
                events.addAll(updatedEvents);
                events.sort(Comparator.comparing(obj -> obj.dateObject));
                binding.chatRecyclerView.setAdapter(eventAdapter);
                binding.chatRecyclerView.setVisibility(View.VISIBLE);
                eventAdapter.notifyDataSetChanged(); // Update adapter with new list
            }
        });


        return binding.getRoot();
    }


    private ActivityResultLauncher<Intent> eventLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("event")) {
                        Event event = (Event) data.getSerializableExtra("event");
                        Toast.makeText(requireContext(), "Event made", Toast.LENGTH_SHORT).show();
                        dashboardViewModel.addEvent(event);
                    }
                }
            });

    private void setListeners() {
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

                dashboardViewModel.updateEvent(event); // Update event in ViewModel
                adapter.notifyDataSetChanged(); // Notify adapter about changes
            }
        });
//        TODO finish deleting from viewmodel
//        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dashboardViewModel.deleteEvent(which); // Update event in ViewModel
//                adapter.notifyDataSetChanged(); // Notify adapter about changes
//            }
//        });
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
}
