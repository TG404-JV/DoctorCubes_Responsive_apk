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
import com.tvm.doctorcube.university.model.UniversityDetailsData;
import com.google.android.material.button.MaterialButton;

public class UniversityDetailsFragment extends Fragment {

    private static final String ARG_UNIVERSITY_NAME = "UNIVERSITY_NAME";
    private static final String ARG_COUNTRY = "COUNTRY";

    // Views
    private ImageView universityImageView;
    private TextView locationTextView, descriptionTextView, establishedTextView,
            rankingTextView, addressTextView, phoneTextView, emailTextView, admissionRequirementsTextView;
    private MaterialButton admissionButton;

    public UniversityDetailsFragment() {
        // Required empty public constructor
    }

    public static UniversityDetailsFragment newInstance(String universityName, String country) {
        UniversityDetailsFragment fragment = new UniversityDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UNIVERSITY_NAME, universityName);
        args.putString(ARG_COUNTRY, country);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            String universityName = args.getString(ARG_UNIVERSITY_NAME);
            String country = args.getString(ARG_COUNTRY);
            if (universityName != null) {
                // Fetch university details from UniversityDetailsData
                UniversityDetailsData.UniversityDetail detail = UniversityDetailsData.getUniversityDetails(universityName);
                if (detail != null) {
                    // Set university data to views
                    setUniversityData(universityName, detail);
                } else {
                    // Fallback if university not found
                    setFallbackData(universityName);
                }

                // Setup admission button
                admissionButton.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    Bundle bundle = new Bundle();
                    bundle.putInt("imageResourceId", detail != null ? detail.getImageResourceId() : R.drawable.icon_university);
                    bundle.putString("universityName", universityName);
                    bundle.putString("country", country);
                    navController.navigate(R.id.action_universityDetailsFragment_to_universityDetailsBottomSheet2, bundle);
                });
            }
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

    private void setUniversityData(String universityName, UniversityDetailsData.UniversityDetail detail) {
        universityImageView.setImageResource(detail.getImageResourceId());
        locationTextView.setText(detail.getLocation());
        descriptionTextView.setText(detail.getDescription());
        establishedTextView.setText(detail.getEstablished());
        rankingTextView.setText(detail.getRanking());
        addressTextView.setText(detail.getAddress());
        phoneTextView.setText(detail.getPhone());
        emailTextView.setText(detail.getEmail());
        admissionRequirementsTextView.setText(detail.getAdmissionRequirements());
    }

    private void setFallbackData(String universityName) {
        universityImageView.setImageResource(R.drawable.icon_university);
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