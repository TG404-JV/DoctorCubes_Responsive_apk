package com.tvm.doctorcube.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.util.HashMap;
import java.util.Map;

public class EventDetailsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "EventDetailsBottomSheet";

    public static EventDetailsBottomSheet newInstance(UpcomingEvent event) {
        EventDetailsBottomSheet fragment = new EventDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putString("eventId", event.getEventId());
        args.putString("imageUrl", event.getImageUrl());
        args.putString("eventTitle", event.getTitle());
        args.putString("eventCategory", event.getCategory());
        args.putString("eventDate", event.getDate());
        args.putString("eventTime", event.getTime());
        args.putString("eventLocation", event.getLocation());
        args.putString("eventAttendees", event.getAttendees());
        args.putBoolean("premium", event.isPremium());
        args.putBoolean("featured", event.isFeatured());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details_bottom_sheet, container, false);

        // Initialize views
        ImageView eventImage = view.findViewById(R.id.eventImage);
        TextView eventCategory = view.findViewById(R.id.eventCategory);
        TextView eventTitle = view.findViewById(R.id.eventTitle);
        TextView eventDate = view.findViewById(R.id.eventDate);
        TextView eventLocation = view.findViewById(R.id.eventLocation);
        ImageButton favoriteButton = view.findViewById(R.id.favoriteButton);
        MaterialButton detailsButton = view.findViewById(R.id.detailsButton);
        MaterialButton registerButton = view.findViewById(R.id.registerButton);

        // Get event details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String eventId = args.getString("eventId", "");
            String imageUrl = args.getString("imageUrl", "");
            String title = args.getString("eventTitle", "Unknown Event");
            String category = args.getString("eventCategory", "Unknown Category");
            String date = args.getString("eventDate", "Unknown Date");
            String time = args.getString("eventTime", "");
            String location = args.getString("eventLocation", "Unknown Location");
            String attendees = args.getString("eventAttendees", "0");
            boolean premium = args.getBoolean("premium", false);
            boolean featured = args.getBoolean("featured", false);

            // Load image with Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.date_badge_premium_bg)
                    .centerCrop()
                    .into(eventImage);

            // Set text fields
            eventCategory.setText(category.toUpperCase());
            eventTitle.setText(title);
            eventDate.setText(time.isEmpty() ? date : date + " • " + time);
            eventLocation.setText(location);

            // Check favorite status
            checkFavoriteStatus(eventId, favoriteButton);

            // Handle favorite button click
            favoriteButton.setOnClickListener(v -> toggleFavorite(eventId, title, favoriteButton));

            // Handle details button click
            detailsButton.setOnClickListener(v -> {
                Log.d(TAG, "Details clicked for event: " + title);
                CustomToast.showToast(requireActivity(), "Details for " + title + " Not Added");
                // Add navigation to detailed event page if needed
            });

            // Handle register button click
            registerButton.setOnClickListener(v -> registerForEvent(eventId, title, category, date, time, location, attendees, imageUrl, premium, featured));
        } else {
            // Fallback if arguments are missing
            eventCategory.setText("UNKNOWN CATEGORY");
            eventTitle.setText("Unknown Event");
            eventDate.setText("Unknown Date");
            eventLocation.setText("Unknown Location");
            eventImage.setImageResource(R.drawable.date_badge_premium_bg);
        }

        return view;
    }

    private void checkFavoriteStatus(String eventId, ImageButton favoriteButton) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || eventId.isEmpty()) {
            favoriteButton.setImageResource(R.drawable.ic_bookmark_border);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("favorites")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    favoriteButton.setImageResource(documentSnapshot.exists() ?
                            R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking favorite status: ", e);
                    favoriteButton.setImageResource(R.drawable.ic_bookmark_border);
                });
    }

    private void toggleFavorite(String eventId, String eventName, ImageButton favoriteButton) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || eventId.isEmpty()) {
            CustomToast.showToast(requireActivity(), "Marked");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String favoriteDocPath = "users/" + user.getUid() + "/favorites/" + eventId;

        db.collection("users")
                .document(user.getUid())
                .collection("favorites")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Remove from favorites
                        db.document(favoriteDocPath)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    favoriteButton.setImageResource(R.drawable.ic_bookmark_border);
                                    CustomToast.showToast(requireActivity(), "Removed " + eventName + " from favorites");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to remove favorite: ", e);
                                    CustomToast.showToast(requireActivity(), "Failed to update favorites");
                                });
                    } else {
                        // Add to favorites
                        Map<String, Object> favoriteData = new HashMap<>();
                        favoriteData.put("eventId", eventId);
                        favoriteData.put("eventName", eventName);
                        favoriteData.put("timestamp", FieldValue.serverTimestamp());

                        db.document(favoriteDocPath)
                                .set(favoriteData)
                                .addOnSuccessListener(aVoid -> {
                                    favoriteButton.setImageResource(R.drawable.ic_bookmark_filled);
                                    CustomToast.showToast(requireActivity(), "Added " + eventName + " to favorites");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to add favorite: ", e);
                                    CustomToast.showToast(requireActivity(), "Failed to update favorites");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking favorite status: ", e);
                    CustomToast.showToast(requireActivity(), "Failed to update favorites");
                });
    }

    private void registerForEvent(String eventId, String title, String category, String date, String time,
                                  String location, String attendees, String imageUrl, boolean premium, boolean featured) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            CustomToast.showToast(requireActivity(), "Please log in to register for the event");
            dismiss();
            return;
        }

        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
        String userName = prefs.getString("name", "");
        String userEmail = prefs.getString("email", "");

        if (userName.isEmpty() || userEmail.isEmpty()) {
            CustomToast.showToast(requireActivity(), "Please complete your profile to register");
            return;
        }

        // Check if already registered
        FirebaseFirestore.getInstance()
                .collection("event_registrations")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        CustomToast.showToast(requireActivity(), "You are already registered for " + title);
                        dismiss();
                        return;
                    }

                    // Register the user
                    Map<String, Object> registration = new HashMap<>();
                    registration.put("userName", userName);
                    registration.put("userEmail", userEmail);
                    registration.put("userId", user.getUid());
                    registration.put("eventId", eventId);
                    registration.put("eventName", title);
                    registration.put("eventCategory", category);
                    registration.put("eventImageUrl", imageUrl);
                    registration.put("eventDate", date);
                    registration.put("eventTime", time);
                    registration.put("eventLocation", location);
                    registration.put("eventAttendees", attendees);
                    registration.put("premium", premium);
                    registration.put("featured", featured);
                    registration.put("timestamp", FieldValue.serverTimestamp());

                    FirebaseFirestore.getInstance()
                            .collection("event_registrations")
                            .add(registration)
                            .addOnSuccessListener(documentReference -> {
                                CustomToast.showToast(requireActivity(), "Successfully registered for " + title);
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to register for event: ", e);
                                CustomToast.showToast(requireActivity(), "Registration failed: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking registration status: ", e);
                    CustomToast.showToast(requireActivity(), "Failed to check registration status");
                });
    }
}