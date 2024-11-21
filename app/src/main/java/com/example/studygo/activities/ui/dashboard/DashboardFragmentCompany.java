package com.example.studygo.activities.ui.dashboard;

import androidx.fragment.app.Fragment;

import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Ad;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.PreferenceManager;

import java.util.List;

public class DashboardFragmentCompany extends Fragment implements EventListener {
    private EventAdapter eventAdapter;
    private List<Ad> ads;
    private PreferenceManager preferenceManager;
    @Override
    public void onEventClicked(Event event) {

    }
}
