package com.tvm.doctorcube.university;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.university.model.University;
import com.google.android.material.button.MaterialButton;

public class UniversityDetailsFragment extends Fragment {

    private static final String ARG_UNIVERSITY = "UNIVERSITY";

    // Views
    private ImageView universityImageView;
    private TextView locationTextView, descriptionTextView, establishedTextView,
            rankingTextView, addressTextView, phoneTextView, emailTextView, admissionRequirementsTextView;
    private MaterialButton admissionButton;

    public UniversityDetailsFragment() {
    }

    public static UniversityDetailsFragment newInstance(University university) {
        UniversityDetailsFragment fragment = new UniversityDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_UNIVERSITY, university);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_university_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            University university = (University) args.getSerializable(ARG_UNIVERSITY);
            if (university != null) {
                setUniversityData(university);
                // Setup admission button
                admissionButton.setOnClickListener(v -> {
                    try {
                        NavController navController = Navigation.findNavController(v);
                        Bundle bundle = new Bundle();
                        bundle.putInt("imageResourceId", university.getBannerResourceId());
                        bundle.putString("universityName", university.getName());
                        bundle.putString("country", university.getCountry());
                        bundle.putSerializable("UNIVERSITY", university);
                        bundle.putString("universityId", university.getId());
                        navController.navigate(R.id.action_universityDetailsFragment_to_universityDetailsBottomSheet, bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle navigation error
                    }
                });
            } else {
                setFallbackData("Unknown");
            }
        } else {
            setFallbackData("Unknown");
        }
    }

    private void initializeViews(View view) {
        universityImageView = view.findViewById(R.id.university_image);
        locationTextView = view.findViewById(R.id.university_location);
        descriptionTextView = view.findViewById(R.id.university_description);
        establishedTextView = view.findViewById(R.id.university_established);
        rankingTextView = view.findViewById(R.id.university_ranking);
        addressTextView = view.findViewById(R.id.university_address);
        phoneTextView = view.findViewById(R.id.university_phone);
        emailTextView = view.findViewById(R.id.university_email);
        admissionRequirementsTextView = view.findViewById(R.id.admission_requirements);
        admissionButton = view.findViewById(R.id.apply_button);
    }

    private void setUniversityData(University university) {
        universityImageView.setImageResource(university.getBannerResourceId() != 0 ? university.getBannerResourceId() : R.drawable.university_campus);
        locationTextView.setText(university.getCity() != null && university.getCountry() != null
                ? university.getCity() + ", " + university.getCountry() : "N/A");
        descriptionTextView.setText(university.getDescription() != null ? university.getDescription() : "N/A");
        establishedTextView.setText(university.getEstablished() != null ? university.getEstablished() : "N/A");
        rankingTextView.setText(university.getDetailedRanking() != null ? university.getDetailedRanking() : "N/A");
        addressTextView.setText(university.getAddress() != null ? university.getAddress() : "N/A");
        phoneTextView.setText(university.getPhone() != null ? university.getPhone() : "N/A");
        emailTextView.setText(university.getEmail() != null ? university.getEmail() : "N/A");
        admissionRequirementsTextView.setText(university.getAdmissionRequirements() != null ? university.getAdmissionRequirements() : "N/A");
    }

    private void setFallbackData(String universityName) {
        universityImageView.setImageResource(R.drawable.university_campus);
        locationTextView.setText("Location Not Available");
        descriptionTextView.setText("Details for " + universityName + " are currently unavailable.");
        establishedTextView.setText("N/A");
        rankingTextView.setText("N/A");
        addressTextView.setText("N/A");
        phoneTextView.setText("N/A");
        emailTextView.setText("N/A");
        admissionRequirementsTextView.setText("N/A");
    }
}