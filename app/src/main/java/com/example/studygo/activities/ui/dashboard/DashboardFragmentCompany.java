package com.example.studygo.activities.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
