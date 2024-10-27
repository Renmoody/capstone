package com.example.studygo.activities.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.studygo.databinding.FragmentMessagesBinding;


public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessagesViewModel messagesViewModel =
                new ViewModelProvider(this).get(MessagesViewModel.class);

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}