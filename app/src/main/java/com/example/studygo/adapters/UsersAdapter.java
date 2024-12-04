package com.example.studygo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygo.databinding.ItemContainerUserBinding;
import com.example.studygo.listeners.UserListener;
import com.example.studygo.models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final UserListener userListener;
    private final List<User> users;
    private Map<String, Map<String, String>> userCommonCrnsMap;

    public UsersAdapter(List<User> users, UserListener userListener, Map<String, Map<String, String>> userCommonCrnsMap) {
        this.users = users;
        this.userListener = userListener;
        this.userCommonCrnsMap = userCommonCrnsMap;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserDetails(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private Bitmap getUserImage(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        private void setUserDetails(User user) {
            if (userCommonCrnsMap != null && !userCommonCrnsMap.isEmpty()) {
                // Retrieve the CRN-to-name map for the current user
                Map<String, String> crnNameMap = userCommonCrnsMap.get(user.id);  // Assuming 'user.id' is the key in the map

                if (crnNameMap != null) {
                    List<String> classList = crnNameMap.values().stream().limit(2).collect(Collectors.toList());
                    String crnDisplay = String.join(", ", classList);

                    if (crnNameMap.size() > 2) {
                        crnDisplay += " + " + (crnNameMap.size() - 2) + " more";
                    }
                    binding.textEmail.setVisibility(View.GONE);
                    binding.textClasses.setText(crnDisplay);
                    binding.textClasses.setVisibility(View.VISIBLE);
                    if (user.major != null) {
                        binding.textMajor.setText(user.major);
                        binding.textMajor.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.textEmail.setVisibility(View.GONE);
                    if (user.major != null) {
                        binding.textMajor.setText(user.major);
                        binding.textMajor.setVisibility(View.VISIBLE);
                    }
                    binding.textClasses.setVisibility(View.GONE);  // Hide the classes if no CRNs exist for the user
                }
            }
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(view -> userListener.onUserClicked(user));
        }



    }

}
