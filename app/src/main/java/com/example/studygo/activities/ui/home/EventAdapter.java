package com.example.studygo.activities.ui.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.studygo.R;
import com.example.studygo.activities.ui.dashboard.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Custom Adapter to handle event data
public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, @NonNull List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_feed_item, parent, false);
        }

        // Lookup view for data population
        TextView eventDate = convertView.findViewById(R.id.feed_event_date);
        TextView eventTime = convertView.findViewById(R.id.feed_event_time);
        TextView eventName = convertView.findViewById(R.id.feed_event_name);
        TextView eventDetails = convertView.findViewById(R.id.feed_event_details);

        // Populate the data into the template view using the event object


        Date date = new Date(event.getEventDate());
        // Create a SimpleDateFormat instance with the desired format (MM/dd/yyyy)
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        eventDate.setText(formattedDate);
        eventTime.setText(event.getEventTime());
        eventName.setText(event.getEventName());
        eventDetails.setText(event.getEventTime());

        // Return the completed view to render on screen
        return convertView;
    }
}
