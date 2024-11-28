package com.example.studygo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygo.databinding.AdFeedItemBinding;
import com.example.studygo.listeners.AdListener;
import com.example.studygo.models.Ad;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private final AdListener adListener;
    private final List<Ad> ads;

    public AdAdapter(List<Ad> ads, AdListener adListener) {
        this.adListener = adListener;
        this.ads = ads;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdFeedItemBinding adFeedItemBinding = AdFeedItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AdViewHolder(adFeedItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        holder.setAdDetails(ads.get(position));
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }


    public class AdViewHolder extends RecyclerView.ViewHolder {
        AdFeedItemBinding binding;

        public AdViewHolder(AdFeedItemBinding adFeedItemBinding) {
            super(adFeedItemBinding.getRoot());
            binding = adFeedItemBinding;
        }

        private void setAdDetails(Ad ad) {
            binding.feedEventName.setText(ad.name);
            binding.feedEventDate.setText(ad.dateStart);
            binding.feedEventDetails.setText(ad.details);
            binding.textMembers.setText(String.valueOf(ad.members));
            binding.getRoot().setOnClickListener(view -> adListener.onAdClicked(ad));
        }

    }
}
