package com.tvm.doctorcube.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.adapter.UpcomingEventAdapter;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.util.ArrayList;
import java.util.List;

public class FragmentUpcomingEvents extends Fragment {

    private TextView tvCurrentMonth, featuredEventTitle, featuredEventTime, featuredEventLocation;
    private ImageView featuredEventImage;
    private RecyclerView rvThisMonthEvents, rvUpcomingEvents;
    private MaterialButton btnFilter, btnRegister, btnRemind, btnViewAll;
    private UpcomingEventAdapter thisMonthAdapter, upcomingAdapter;
    private List<UpcomingEvent> thisMonthEvents = new ArrayList<>();
    private List<UpcomingEvent> upcomingEvents = new ArrayList<>();
    private DatabaseReference databaseReference;
    private UpcomingEvent featuredEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        // Initialize views
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        featuredEventTitle = view.findViewById(R.id.featuredEventTitle);
        featuredEventTime = view.findViewById(R.id.featuredEventTime);
        featuredEventLocation = view.findViewById(R.id.featuredEventLocation);
        featuredEventImage = view.findViewById(R.id.featuredEventImage);
        rvThisMonthEvents = view.findViewById(R.id.rvThisMonthEvents);
        rvUpcomingEvents = view.findViewById(R.id.rvUpcomingEvents);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnRemind = view.findViewById(R.id.btnRemind);
        btnViewAll = view.findViewById(R.id.btnViewAll);

        // Set current month
        tvCurrentMonth.setText("May 2025");

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents");

        // Setup RecyclerViews
        thisMonthAdapter = new UpcomingEventAdapter(thisMonthEvents);
        rvThisMonthEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvThisMonthEvents.setAdapter(thisMonthAdapter);

        upcomingAdapter = new UpcomingEventAdapter(upcomingEvents);
        rvUpcomingEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcomingEvents.setAdapter(upcomingAdapter);

        // Load data
        loadFeaturedEvent();
        loadThisMonthEvents();
        loadUpcomingEvents();



        // Handle featured event registration
        btnRegister.setOnClickListener(v -> {
            if (featuredEvent != null) {
                EventDetailsBottomSheet bottomSheet = EventDetailsBottomSheet.newInstance(featuredEvent);
                bottomSheet.show(getParentFragmentManager(), EventDetailsBottomSheet.TAG);
            } else {
                CustomToast.showToast(requireActivity(), "No featured event available");
            }
        });

        // Handle remind button (optional: implement reminder logic)
        btnRemind.setOnClickListener(v -> CustomToast.showToast(requireActivity(), "Reminder set for featured event"));

        // Handle view all button
        btnViewAll.setOnClickListener(v -> {
            // Navigate to a new fragment/activity showing all events
            CustomToast.showToast(requireActivity(), "No Upcoming Events Available");
        });

        return view;
    }

    private void loadFeaturedEvent() {
        databaseReference.child("featured").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    featuredEvent = data.getValue(UpcomingEvent.class);
                    if (featuredEvent != null) {
                        featuredEventTitle.setText(featuredEvent.getTitle());
                        featuredEventTime.setText(featuredEvent.getDate() + " • " + featuredEvent.getTime());
                        featuredEventLocation.setText(featuredEvent.getLocation());
                        Glide.with(featuredEventImage.getContext())
                                .load(featuredEvent.getImageUrl())
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.date_badge_premium_bg)
                                .into(featuredEventImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(requireActivity(), "Failed to load featured event");
            }
        });
    }

    private void loadThisMonthEvents() {
        databaseReference.child("this_month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thisMonthEvents.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent event = data.getValue(UpcomingEvent.class);
                    if (event != null) {
                        thisMonthEvents.add(event);
                    }
                }
                thisMonthAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(requireActivity(), "Failed to load this month's events");
            }
        });
    }

    private void loadUpcomingEvents() {
        databaseReference.child("upcoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upcomingEvents.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent event = data.getValue(UpcomingEvent.class);
                    if (event != null) {
                        upcomingEvents.add(event);
                    }
                }
                upcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(requireActivity(), "Failed to load upcoming events");
            }
        });
    }

    private void filterEvents(String category) {
        List<UpcomingEvent> filteredThisMonth = new ArrayList<>();
        List<UpcomingEvent> filteredUpcoming = new ArrayList<>();

        if (category.equals("All")) {
            filteredThisMonth.addAll(thisMonthEvents);
            filteredUpcoming.addAll(upcomingEvents);
        } else {
            for (UpcomingEvent event : thisMonthEvents) {
                if (event.getCategory().equalsIgnoreCase(category)) {
                    filteredThisMonth.add(event);
                }
            }
            for (UpcomingEvent event : upcomingEvents) {
                if (event.getCategory().equalsIgnoreCase(category)) {
                    filteredUpcoming.add(event);
                }
            }
        }

        thisMonthAdapter.updateEvents(filteredThisMonth);
        upcomingAdapter.updateEvents(filteredUpcoming);
    }
}