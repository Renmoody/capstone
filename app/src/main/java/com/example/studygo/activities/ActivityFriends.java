package com.example.studygo.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.ActivityFriendsBinding;

public class ActivityFriends extends AppCompatActivity {

    private ActivityFriendsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view ->
                getOnBackPressedDispatcher().onBackPressed());
        binding.imageAdd.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), ActivityAddFriend.class)));
    }
}
