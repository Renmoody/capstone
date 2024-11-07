package com.example.studygo.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.ActivityMessagesBinding;

public class ActivityMessages extends AppCompatActivity {

    ActivityMessagesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setListeners();

    }


    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        binding.fabNewChat.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }
}
