package com.example.studygo.activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.R;
import com.example.studygo.databinding.FragmentHomeBinding;
import com.example.studygo.models.Event;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        TODO Populate homeviewmodel and somehow create "infinite scroll view for feed"
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Find the ListView by its ID
//        ListView feedOfEvents = root.findViewById(R.id.feedOfEvents);
//
//        // Create sample event data
////        TODO pull data from firebase, create algorithm that will populat events list with friends and professors public events
//        ArrayList<Event> events = new ArrayList<>();
//        // Hard coded events from ChatGPT for demonstration of UI
//        events.add(new Event("Study Group", "Group study session on Java programming.", 1729286400000L, "3:00 PM"));
//        events.add(new Event("Exam Prep", "Prepare for the upcoming midterm exams.", 1729286400000L, "11:00 AM"));
//        events.add(new Event("Coding Bootcamp", "Intensive coding bootcamp for beginners.", 1729286400000L, "9:00 AM"));
//        events.add(new Event("AI Workshop", "Learn the basics of Artificial Intelligence.", 1729286400000L, "2:00 PM"));
//        events.add(new Event("Team Project Meetup", "Meet up for the final project discussion.", 1729286400000L, "4:00 PM"));
//        events.add(new Event("Tech Talk", "Guest speaker on advancements in cloud computing.", 1729286400000L, "5:00 PM"));
//        events.add(new Event("Networking Event", "Opportunity to network with industry professionals.", 1729286400000L, "6:00 PM"));
//        events.add(new Event("App Development", "Build your first Android app with Kotlin.", 1729286400000L, "1:00 PM"));
//        events.add(new Event("Hackathon", "24-hour coding challenge with great prizes.", 1729286400000L, "8:00 AM"));
//        events.add(new Event("Machine Learning Seminar", "Introduction to machine learning algorithms.", 1729286400000L, "10:00 AM"));

//        // Create an instance of the custom EventAdapter
//        EventAdapter adapter = new EventAdapter(requireContext(), events);
//
//        // Set the adapter to the ListView
//        feedOfEvents.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
