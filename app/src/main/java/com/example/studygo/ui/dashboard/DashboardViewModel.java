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
}
