package com.example.studygo.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> eventListLiveData;

    public DashboardViewModel() {
        eventListLiveData = new MutableLiveData<>(new ArrayList<>()); // Initialize with an empty list
    }

    public LiveData<List<Event>> getEventList() {
        return eventListLiveData;
    }

    public void addEvent(Event event) {
        List<Event> currentList = eventListLiveData.getValue();
        if (currentList != null) {
            currentList.add(event);
            eventListLiveData.setValue(currentList); // Notify observers of the updated list
        }
    }

    public void updateEvent(Event event, int position) {
        List<Event> currentList = eventListLiveData.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, event);
            eventListLiveData.setValue(currentList);
        }
    }

    public void deleteEvent(int position) {
        List<Event> currentList = eventListLiveData.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);  // Remove the event at the specified position
            eventListLiveData.setValue(currentList);  // Notify observers of the updated list
        }
    }
}
