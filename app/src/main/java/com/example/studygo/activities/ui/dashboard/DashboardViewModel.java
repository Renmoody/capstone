package com.example.studygo.activities.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studygo.models.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private static final MutableLiveData<List<Event>> eventListLiveData = new MutableLiveData<>(new ArrayList<>());
    ;

    public DashboardViewModel() {

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

    public void updateEvent(Event event) {
        List<Event> currentList = eventListLiveData.getValue();
        if (currentList != null) {
            if (currentList.contains(event.id)) {
                currentList.add(event);
                currentList.sort(Comparator.comparing(obj -> obj.dateObject));
            }
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
