package com.example.studygo.activities.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.R;
import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ArrayAdapter<Event> adapter;
    private DashboardViewModel dashboardViewModel;
    private EventAdapter eventAdapter;
    private List<Event> events;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
        // Initialize ViewModel
//        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Initialize the adapter
//        adapter = new ArrayAdapter<Event>(requireContext(), 0, new ArrayList<>()) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_item, parent, false);
//                }
//
//                Event event = getItem(position);
//                if (event != null) {
//                    TextView eventDateView = convertView.findViewById(R.id.child_event_date);
//                    TextView eventTimeView = convertView.findViewById(R.id.child_event_time);
//                    TextView eventNameTextView = convertView.findViewById(R.id.child_event_name);
//                    TextView eventDetailsTextView = convertView.findViewById(R.id.child_event_details);
//
//                    Date date = new Date(event.getEventDate());
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//                    String formattedDate = dateFormat.format(date);
//
//                    // Update UI
//                    eventDateView.setText(formattedDate);
//                    eventTimeView.setText(event.getEventTime());
//                    eventNameTextView.setText(event.getEventName());
//                    eventDetailsTextView.setText(event.getEventDetails());
//                }
//
//                return convertView;
//            }
//        };

        // Set up ListView and adapter
//        ListView listView = binding.listOfEvents;
//        listView.setAdapter(adapter);
//
//        // Set up CalendarView listener
//        CalendarView calendarView = binding.calendarView;
//        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
////            showEventDialog(year, month, dayOfMonth);
//        });
//        // Set up OnItemClickListener for editing events
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event selectedEvent = adapter.getItem(position);
//                if (selectedEvent != null) {
////                    showEditEventDialog(selectedEvent, position);
//                }
//            }
//        });

        // Observe the event list from the ViewModel
//        dashboardViewModel.getEventList().observe(getViewLifecycleOwner(), events -> {
//            adapter.clear();
//            adapter.addAll(events);  // Update the adapter with the new data
//            adapter.notifyDataSetChanged();
//        });
//
//    }

//    private void showEventDialog(int year, int month, int day) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("Add New Event");
//
//        LinearLayout layout = new LinearLayout(requireContext());
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        final EditText eventTimeInput = new EditText(requireContext());
//        eventTimeInput.setHint("Event Time");
//        layout.addView(eventTimeInput);
//
//        final EditText eventNameInput = new EditText(requireContext());
//        eventNameInput.setHint("Event Name");
//        layout.addView(eventNameInput);
//
//        final EditText eventDetailsInput = new EditText(requireContext());
//        eventDetailsInput.setHint("Event Details (Optional)");
//        layout.addView(eventDetailsInput);
//
//        builder.setView(layout);
//
//        builder.setPositiveButton("Register Event", (dialog, which) -> {
//            String eventName = eventNameInput.getText().toString();
//            String eventDetails = eventDetailsInput.getText().toString();
//            Long eventDate = new GregorianCalendar(year, month, day).getTimeInMillis();
//            String eventTime = eventTimeInput.getText().toString();
//            if (eventName.isEmpty() || eventTime.isEmpty()) {
//                Toast.makeText(getContext(), "Fill out required fields!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Event event = new Event(eventName, eventDetails, eventDate, eventTime);
//            dashboardViewModel.addEvent(event);  // Add event to ViewModel
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.show();
//    }

//    When clicking on an event in event list, this method is called and will
//    prompt an event dialog where the user can edit the event contents
//    private void showEditEventDialog(Event event, int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("Edit Event");
//
//        LinearLayout layout = new LinearLayout(requireContext());
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        final EditText eventTimeInput = new EditText(requireContext());
//        eventTimeInput.setHint("Event Time");
//        eventTimeInput.setText(event.time());
//        layout.addView(eventTimeInput);
//
//        final EditText eventNameInput = new EditText(requireContext());
//        eventNameInput.setHint("Event Name");
//        eventNameInput.setText(event.getEventName());
//        layout.addView(eventNameInput);
//
//        final EditText eventDetailsInput = new EditText(requireContext());
//        eventDetailsInput.setHint("Event Details (Optional)");
//        eventDetailsInput.setText(event.getEventDetails());
//        layout.addView(eventDetailsInput);
//
//        builder.setView(layout);
//
//        // Save changes
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String updatedEventName = eventNameInput.getText().toString();
//                String updatedEventDetails = eventDetailsInput.getText().toString();
//                String updatedEventTime = eventTimeInput.getText().toString();
//
//                if (updatedEventName.isEmpty() || updatedEventTime.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill out required fields!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Update the event object with new values
//                event.setEventName(updatedEventName);
//                event.setEventDetails(updatedEventDetails);
//                event.setEventTime(updatedEventTime);
//
//                dashboardViewModel.updateEvent(event, position); // Update event in ViewModel
//                adapter.notifyDataSetChanged(); // Notify adapter about changes
//            }
//        });
//        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dashboardViewModel.deleteEvent(position); // Update event in ViewModel
//                adapter.notifyDataSetChanged(); // Notify adapter about changes
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.show();
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}
