package com.tvm.doctorcube.adminpannel.adminhome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FragmentUploadEvent extends Fragment {

    private TextInputEditText etEventTitle, etEventDate, etStartTime, etEndTime, etEventLocation, etAttendees;
    private Spinner spinnerCategory, spinnerEventType;
    private CheckBox cbPremium;
    private MaterialButton btnSubmit, btnSelectImage;
    private ImageView ivImagePreview;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Calendar calendar;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isUploading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_event, container, false);

        // Initialize views
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etEventDate = view.findViewById(R.id.etEventDate);
        etStartTime = view.findViewById(R.id.etStartTime);
        etEndTime = view.findViewById(R.id.etEndTime);
        etEventLocation = view.findViewById(R.id.etEventLocation);
        etAttendees = view.findViewById(R.id.etAttendees);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerEventType = view.findViewById(R.id.spinnerEventType);
        cbPremium = view.findViewById(R.id.cbPremium);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        ivImagePreview = view.findViewById(R.id.ivImagePreview);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents");
        storageReference = FirebaseStorage.getInstance().getReference("events");

        // Initialize calendar
        calendar = Calendar.getInstance();

        // Setup category spinner
        String[] categories = {"Webinars", "Counseling", "Admissions", "Workshops"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Setup event type spinner
        String[] eventTypes = {"Featured", "This Month", "Upcoming"};
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(eventTypeAdapter);

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                ivImagePreview.setImageURI(selectedImageUri);
                ivImagePreview.setVisibility(View.VISIBLE);
            }
        });

        // Setup date picker
        etEventDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        etEventDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Setup time pickers
        etStartTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view1, hourOfDay, minute1) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        etStartTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        etEndTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view1, hourOfDay, minute1) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        etEndTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Image selection
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> checkAdminAndUpload());

        return view;
    }

    private void checkAdminAndUpload() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Please log in to upload events", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            uploadEvent();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error checking role: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadEvent() {
        if (isUploading) return;
        isUploading = true;
        btnSubmit.setEnabled(false);

        // Get input values
        String title = etEventTitle.getText() != null ? etEventTitle.getText().toString().trim() : "";
        String date = etEventDate.getText() != null ? etEventDate.getText().toString().trim() : "";
        String startTime = etStartTime.getText() != null ? etStartTime.getText().toString().trim() : "";
        String endTime = etEndTime.getText() != null ? etEndTime.getText().toString().trim() : "";
        String location = etEventLocation.getText() != null ? etEventLocation.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem().toString();
        String eventType = spinnerEventType.getSelectedItem().toString();
        String attendees = etAttendees.getText() != null ? etAttendees.getText().toString().trim() : "";
        boolean isPremium = cbPremium.isChecked();
        boolean isFeatured = eventType.equals("Featured");

        // Validate inputs
        if (title.isEmpty()) {
            etEventTitle.setError("Title is required");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (date.isEmpty() || !isValidDate(date)) {
            etEventDate.setError("Valid date is required (YYYY-MM-DD)");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (startTime.isEmpty() || !isValidTime(startTime)) {
            etStartTime.setError("Valid start time is required (HH:MM)");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (endTime.isEmpty() || !isValidTime(endTime)) {
            etEndTime.setError("Valid end time is required (HH:MM)");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (!isEndTimeAfterStartTime(startTime, endTime)) {
            etEndTime.setError("End time must be after start time");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (location.isEmpty()) {
            etEventLocation.setError("Location is required");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (attendees.isEmpty()) {
            etAttendees.setError("Attendees information is required");
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an event image", Toast.LENGTH_SHORT).show();
            isUploading = false;
            btnSubmit.setEnabled(true);
            return;
        }

        // Format time range
        String time = startTime + " - " + endTime;

        // Upload image to Firebase Storage
        String imagePath = "event_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child(imagePath);
        imageRef.putFile(selectedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // Create event object
                    UpcomingEvent event = new UpcomingEvent(title, date, time, location, category, downloadUri.toString(), attendees, isPremium, isFeatured);

                    // Determine Firebase path
                    String path = eventType.equals("Featured") ? "featured" :
                            (eventType.equals("This Month") || isThisMonthEvent(date)) ? "this_month" : "upcoming";

                    // Upload to Firebase Database
                    String eventId = databaseReference.child(path).push().getKey();
                    if (eventId != null) {
                        databaseReference.child(path).child(eventId).setValue(event)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Event uploaded successfully", Toast.LENGTH_SHORT).show();
                                    clearForm();
                                    isUploading = false;
                                    btnSubmit.setEnabled(true);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to upload event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    isUploading = false;
                                    btnSubmit.setEnabled(true);
                                });
                    } else {
                        Toast.makeText(requireContext(), "Failed to generate event ID", Toast.LENGTH_SHORT).show();
                        isUploading = false;
                        btnSubmit.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    isUploading = false;
                    btnSubmit.setEnabled(true);
                });
    }

    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            return end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isThisMonthEvent(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = sdf.parse(date);
            Calendar eventCal = Calendar.getInstance();
            assert eventDate != null;
            eventCal.setTime(eventDate);

            Calendar currentCal = Calendar.getInstance();
            return eventCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                    eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH);
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearForm() {
        etEventTitle.setText("");
        etEventDate.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        etEventLocation.setText("");
        etAttendees.setText("");
        cbPremium.setChecked(false);
        spinnerCategory.setSelection(0);
        spinnerEventType.setSelection(0);
        selectedImageUri = null;
        ivImagePreview.setImageDrawable(null);
        ivImagePreview.setVisibility(View.GONE);
    }
}