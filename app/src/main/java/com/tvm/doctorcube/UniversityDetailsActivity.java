package com.tvm.doctorcube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.tvm.doctorcube.university.ApplyBottomSheetFragment;
import com.tvm.doctorcube.university.UniversityDetailsBottomSheet;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityDetailsData;
import com.google.android.material.button.MaterialButton;

public class UniversityDetailsActivity extends AppCompatActivity {

    // Views
    private ImageView universityImageView;
    private TextView locationTextView, descriptionTextView, establishedTextView,
            rankingTextView, addressTextView, phoneTextView, emailTextView, admissionRequirementsTextView; // Added TextView for admission requirements
private MaterialButton admissionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_university_details);

        // Initialize views
        initializeViews();

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String universityName = intent.getStringExtra("UNIVERSITY_NAME");
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
            }
        }

        admissionButton.setOnClickListener(v -> {
            // Create the university data
            // Get the user name (you'll need to retrieve this from your app)


            assert intent != null;
            String universityName = intent.getStringExtra("UNIVERSITY_NAME");
            String country= intent.getStringExtra("country");
            UniversityDetailsData.UniversityDetail detail = UniversityDetailsData.getUniversityDetails(universityName);



            // Use the newInstance method to create the bottom sheet with proper parameters
            UniversityDetailsBottomSheet bottomSheet =
                    UniversityDetailsBottomSheet.newInstance(detail.getImageResourceId(), universityName,country);

            // Show the bottom sheet
            bottomSheet.show(getSupportFragmentManager(), "ApplyBottomSheet");
        });    }

    private void initializeViews() {
        universityImageView = findViewById(R.id.university_image);
        locationTextView = findViewById(R.id.university_location);
        descriptionTextView = findViewById(R.id.university_description);
        establishedTextView = findViewById(R.id.university_established);
        rankingTextView = findViewById(R.id.university_ranking);
        addressTextView = findViewById(R.id.university_address);
        phoneTextView = findViewById(R.id.university_phone);
        emailTextView = findViewById(R.id.university_email);
        admissionRequirementsTextView = findViewById(R.id.admission_requirements); // Initialize the new TextView
        admissionButton = findViewById(R.id.apply_button);
    }

    private void setUniversityData(String universityName, UniversityDetailsData.UniversityDetail detail) {
        // Set the collapsing toolbar title
        setTitle(universityName);

        // Set data to views
        universityImageView.setImageResource(detail.getImageResourceId());
        locationTextView.setText(detail.getLocation());
        descriptionTextView.setText(detail.getDescription());
        establishedTextView.setText(detail.getEstablished());
        rankingTextView.setText(detail.getRanking());
        addressTextView.setText(detail.getAddress());
        phoneTextView.setText(detail.getPhone());
        emailTextView.setText(detail.getEmail());
        admissionRequirementsTextView.setText(detail.getAdmissionRequirements()); // Set the admission requirements
    }

    private void setFallbackData(String universityName) {
        setTitle(universityName);
        universityImageView.setImageResource(R.drawable.icon_university); // Default fallback image
        locationTextView.setText("Location Not Available");
        descriptionTextView.setText("Details for " + universityName + " are currently unavailable.");
        establishedTextView.setText("N/A");
        rankingTextView.setText("N/A");
        addressTextView.setText("N/A");
        phoneTextView.setText("N/A");
        emailTextView.setText("N/A");
        admissionRequirementsTextView.setText("N/A"); // Set N/A for fallback
    }
}

