package com.example.studygo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygo.databinding.ItemContainerClassBinding;
import com.example.studygo.listeners.StudentClassListener;
import com.example.studygo.models.StudentClass;
import com.example.studygo.models.User;

import java.util.List;

public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.StudentClassViewHolder> {
    private final StudentClassListener studentClassListener;
    private final List<StudentClass> classes;

    public StudentClassAdapter(List<StudentClass> classes, StudentClassListener studentClassListener) {
        this.studentClassListener = studentClassListener;
        this.classes = classes;
    }


    @NonNull
    @Override
    public StudentClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerClassBinding itemContainerClassBinding = ItemContainerClassBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new StudentClassViewHolder(itemContainerClassBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassViewHolder holder, int position) {
        holder.setClassDetails(classes.get(position));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    class StudentClassViewHolder extends RecyclerView.ViewHolder {
        ItemContainerClassBinding binding;
        StudentClassViewHolder(@NonNull ItemContainerClassBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        private void setClassDetails(StudentClass studentClass) {
            binding.studentClassName.setText(studentClass.name.trim());
            binding.textCRN.setText(studentClass.CRN.trim());
            binding.getRoot().setOnClickListener(view -> studentClassListener.onClassClicked(studentClass));
        }
    }
}
