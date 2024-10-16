package com.example.studygo.ui.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.databinding.FragmentDashboardBinding;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private List<Event> events;
    private ArrayAdapter<Event> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        // Initialize the ListView and CalendarView
        ListView listView = binding.listOfEvents; // Assuming you have a ListView in your binding
        CalendarView calendarView = binding.calendarView; // Assuming you have a CalendarView in your binding

        // Initialize the event list and adapter
        events = new ArrayList<>();
        adapter = new ArrayAdapter<Event>(requireContext(), android.R.layout.simple_list_item_1, events) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(events.get(position).getEventName()); // Customize how you display the event
                return view;
            }
        };

        listView.setAdapter(adapter);

        // Set up CalendarView listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            showEventDialog(year, month, dayOfMonth);
        });

        return binding.getRoot();
    }

    private void showEventDialog(int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Event");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String eventName = input.getText().toString();
            long eventDate = new GregorianCalendar(year, month, day).getTimeInMillis();
            String eventDetails = "Test details";
            Time eventTime = new Time(0,0,0);
            Event event = new Event(eventName, eventDetails, eventDate, eventTime);
            events.add(event);
            adapter.notifyDataSetChanged(); // Update the ListView
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
