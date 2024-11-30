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
            binding.feedEventDateEnd.setText(ad.dateEnd);
            checkDays(ad);
            binding.getRoot().setOnClickListener(view -> adListener.onAdClicked(ad));
        }

        private void checkDays(Ad ad) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean r = true;
            stringBuilder.append("Repeats: ");
            if (ad.Monday.equals("true")) {
                stringBuilder.append("Monday ");
                r = false;
            }
            if (ad.Tuesday.equals("true")) {
                stringBuilder.append("Tuesday ");
                r = false;
            }
            if (ad.Wednesday.equals("true")) {
                stringBuilder.append("Wednesday ");
                r = false;
            }
            if (ad.Thursday.equals("true")) {
                stringBuilder.append("Thursday ");
                r = false;
            }
            if (ad.Friday.equals("true")) {
                stringBuilder.append("Friday ");
                r = false;
            }
            if (ad.Saturday.equals("true")) {
                stringBuilder.append("Saturday ");
                r = false;
            }
            if (ad.Sunday.equals("true")) {
                stringBuilder.append("Sunday ");
                r = false;
            }
            if (r) {
                stringBuilder.append("Never");
            }
            binding.feedEventRepeat.setText(stringBuilder);
        }

    }
}
