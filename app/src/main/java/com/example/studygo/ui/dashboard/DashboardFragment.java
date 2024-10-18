package com.example.studygo.ui.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.R;
import com.example.studygo.databinding.FragmentDashboardBinding;

import org.w3c.dom.Text;

import java.sql.Time;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Initialize the adapter
        adapter = new ArrayAdapter<Event>(requireContext(), 0, new ArrayList<>()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_item, parent, false);
                }

                Event event = getItem(position);
                if (event != null) {
                    TextView eventDateView = convertView.findViewById(R.id.child_event_date);
                    TextView eventTimeView = convertView.findViewById(R.id.child_event_time);
                    TextView eventNameTextView = convertView.findViewById(R.id.child_event_name);
                    TextView eventDetailsTextView = convertView.findViewById(R.id.child_event_details);


                    Date date = new Date(event.getEventDate());
                    // Create a SimpleDateFormat instance with the desired format (MM/dd/yyyy)
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);

                    // Update UI
                    eventDateView.setText(formattedDate);
                    eventTimeView.setText(event.getEventTime());
                    eventNameTextView.setText(event.getEventName());
                    eventDetailsTextView.setText(event.getEventDetails());

                }

                return convertView;
            }
        };

        // Set up ListView and adapter
        ListView listView = binding.listOfEvents;
        listView.setAdapter(adapter);

        // Observe the event list from the ViewModel
        dashboardViewModel.getEventList().observe(getViewLifecycleOwner(), events -> {
            adapter.clear();
            adapter.addAll(events);  // Update the adapter with the new data
            adapter.notifyDataSetChanged();
        });

        // Set up CalendarView listener
        CalendarView calendarView = binding.calendarView;
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            showEventDialog(year, month, dayOfMonth);
        });
    }

    private void showEventDialog(int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Event");

        // Create layout to hold EditText inputs
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText eventTimeInput = new EditText(requireContext());
        eventTimeInput.setHint("Event Time");
        layout.addView(eventTimeInput);

        final EditText eventNameInput = new EditText(requireContext());
        eventNameInput.setHint("Event Name");
        layout.addView(eventNameInput);

        final EditText eventDetailsInput = new EditText(requireContext());
        eventDetailsInput.setHint("Event Details");
        layout.addView(eventDetailsInput);


        builder.setView(layout);

        // Add action buttons
        builder.setPositiveButton("Register Event", (dialog, which) -> {
            String eventName = eventNameInput.getText().toString();
            String eventDetails = eventDetailsInput.getText().toString();
            Long eventDate = new GregorianCalendar(year, month, day).getTimeInMillis();
            String eventTime = eventTimeInput.getText().toString();
            Event event = new Event(eventName, eventDetails, eventDate, eventTime);

            dashboardViewModel.addEvent(event);  // Add event to ViewModel
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
