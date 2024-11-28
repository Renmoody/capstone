package com.example.studygo.activities.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.studygo.adapters.AdAdapter;
import com.example.studygo.adapters.EventAdapter;
import com.example.studygo.databinding.FragmentDashboardBinding;
import com.example.studygo.listeners.AdListener;
import com.example.studygo.listeners.EventListener;
import com.example.studygo.models.Ad;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DashboardFragmentCompany extends Fragment implements AdListener {
    private FragmentDashboardBinding binding;
    private AdAdapter adAdapter;
    private List<Ad> ads;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        ads = new ArrayList<>();
        adAdapter = new AdAdapter(ads, this);
        preferenceManager = new PreferenceManager(requireContext());
        setListeners();
        getAds();
        return binding.getRoot();
    }

    private void getAds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_ADS).whereEqualTo(Constants.KEY_AD_AUTHOR_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        loading(false);
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Ad ad = add(queryDocumentSnapshot);
                            ads.add(ad);
                            if (!ads.isEmpty()) {
                                ads.sort(Comparator.comparing(obj -> obj.dateStart));
                                binding.eventRecyclerView.setAdapter(adAdapter);
                                binding.eventRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        }
                    } else {
                        loading(false);
                        showErrorMessage();
                    }

                });
    }
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No Ads available, have you made one yet?"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private Ad add(QueryDocumentSnapshot queryDocumentSnapshot) {
        Ad ad = new Ad();
        ad.authorId = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_AUTHOR_ID)).toString();
        ad.dateStart = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_DATE_START)).toString();
        ad.dateEnd = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_DATE_END)).toString();
        ad.details = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_DETAILS)).toString();
        ad.name = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_NAME)).toString();
        ad.Monday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_MONDAY)).toString();
        ad.Tuesday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_TUESDAY)).toString();
        ad.Wednesday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_WEDNESDAY)).toString();
        ad.Thursday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_THURSDAY)).toString();
        ad.Friday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_FRIDAY)).toString();
        ad.Saturday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_SATURDAY)).toString();
        ad.Sunday = Objects.requireNonNull(queryDocumentSnapshot.get(Constants.KEY_AD_SUNDAY)).toString();
        return ad;
    }


    private final ActivityResultLauncher<Intent> adLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.hasExtra("ad")) {
                Ad ad = (Ad) data.getSerializableExtra("ad");
                if (ad != null) {
                    publishAd(ad);
                }
            }
        }
    });

    private void publishAd(Ad a) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> ad = new HashMap<>();
        ad.put(Constants.KEY_AD_NAME, a.name);
        ad.put(Constants.KEY_AD_DETAILS, a.details);
        ad.put(Constants.KEY_AD_AUTHOR_ID, a.authorId);
        ad.put(Constants.KEY_AD_DATE_START, a.dateStart);
        ad.put(Constants.KEY_AD_DATE_END, a.dateEnd);
        ad.put(Constants.KEY_AD_MONDAY, a.Monday);
        ad.put(Constants.KEY_AD_TUESDAY, a.Tuesday);
        ad.put(Constants.KEY_AD_WEDNESDAY, a.Wednesday);
        ad.put(Constants.KEY_AD_THURSDAY, a.Thursday);
        ad.put(Constants.KEY_AD_FRIDAY, a.Friday);
        ad.put(Constants.KEY_AD_SATURDAY, a.Saturday);
        ad.put(Constants.KEY_AD_SUNDAY, a.Sunday);
        ad.put(Constants.KEY_MEMBERS, a.members);
        db.collection(Constants.KEY_COLLECTION_ADS).add(ad).addOnSuccessListener(documentReference -> {
            Toast.makeText(requireContext(), "Ad Created", Toast.LENGTH_SHORT).show();
        });
    }

    private void setListeners() {
        loading(true);
        binding.fabNewEvent.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), AdSelector.class);
            adLauncher.launch(intent);
        });
    }

    //    Toggles loading animation
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {

    }
}
