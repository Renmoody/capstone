package com.example.studygo.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.studygo.R;

import java.util.List;
import java.util.Map;

public class EventExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> eventTitles; // Group titles (dates)
    private Map<String, List<Event>> eventDetailsMap; // Event details (child)

    public EventExpandableListAdapter(Context context, List<String> eventTitles, Map<String, List<Event>> eventDetailsMap) {
        this.context = context;
        this.eventTitles = eventTitles;
        this.eventDetailsMap = eventDetailsMap;
    }

    @Override
    public int getGroupCount() {
        return eventTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String title = eventTitles.get(groupPosition);
        List<Event> events = eventDetailsMap.get(title);
        return events != null ? events.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return eventTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String title = eventTitles.get(groupPosition);
        List<Event> events = eventDetailsMap.get(title);
        return events != null ? events.get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String eventTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.group_title);
        textView.setText(eventTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Event event = (Event) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false);
        }
        TextView eventName = convertView.findViewById(R.id.child_event_name);
        TextView eventDetails = convertView.findViewById(R.id.child_event_details);

        if (event != null) {
            eventName.setText(event.getEventName());
            eventDetails.setText(event.getEventDetails());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
