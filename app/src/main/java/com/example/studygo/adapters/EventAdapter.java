package com.example.studygo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygo.databinding.EventFeedItemBinding;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final EventListener eventListener;
    private final List<Event> events;

    public EventAdapter(List<Event> events, EventListener eventListener) {
        this.eventListener = eventListener;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventFeedItemBinding eventFeedItemBinding = EventFeedItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EventViewHolder(eventFeedItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.setEventDetails(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public class EventViewHolder extends RecyclerView.ViewHolder {
        EventFeedItemBinding binding;

        public EventViewHolder(EventFeedItemBinding eventFeedItemBinding) {
            super(eventFeedItemBinding.getRoot());
            binding = eventFeedItemBinding;
        }

        private void setEventDetails(Event event) {
            binding.feedEventDate.setText(event.date);
            binding.feedEventName.setText(event.name);
            binding.feedEventDetails.setText(event.details);
            binding.textMembers.setText(String.valueOf(event.members));
            binding.getRoot().setOnClickListener(view -> eventListener.onEventClicked(event));
        }

    }
}
