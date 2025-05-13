package com.tvm.doctorcube.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.adapter.UpcomingEventAdapter;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.util.ArrayList;
import java.util.List;

public class FragmentUpcomingEvents extends Fragment {

    private TextView tvCurrentMonth, featuredUpcomingEventTitle, featuredUpcomingEventTime, featuredUpcomingEventLocation;
    private RecyclerView rvThisMonthUpcomingEvents, rvUpcomingUpcomingEvents;
    private MaterialButton btnFilter, btnRegister, btnRemind, btnViewAll;
    private ChipGroup categoryChipGroup;
    private UpcomingEventAdapter thisMonthAdapter, upcomingAdapter;
    private List<UpcomingEvent> thisMonthUpcomingEvents = new ArrayList<>();
    private List<UpcomingEvent> upcomingUpcomingEvents = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        // Initialize views
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        featuredUpcomingEventTitle = view.findViewById(R.id.featuredEventTitle);
        featuredUpcomingEventTime = view.findViewById(R.id.featuredEventTime);
        featuredUpcomingEventLocation = view.findViewById(R.id.featuredEventLocation);
        rvThisMonthUpcomingEvents = view.findViewById(R.id.rvThisMonthEvents);
        rvUpcomingUpcomingEvents = view.findViewById(R.id.rvUpcomingEvents);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnRemind = view.findViewById(R.id.btnRemind);
        btnViewAll = view.findViewById(R.id.btnViewAll);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents");

        // Setup RecyclerViews
        thisMonthAdapter = new UpcomingEventAdapter(thisMonthUpcomingEvents);
        rvThisMonthUpcomingEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvThisMonthUpcomingEvents.setAdapter(thisMonthAdapter);

        upcomingAdapter = new UpcomingEventAdapter(upcomingUpcomingEvents);
        rvUpcomingUpcomingEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcomingUpcomingEvents.setAdapter(upcomingAdapter);

        // Load data
        loadFeaturedUpcomingEvent();
        loadThisMonthUpcomingEvents();
        loadUpcomingUpcomingEvents();

        // Setup chip group filter
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String category = "All";
            if (checkedId != -1) {
                Chip chip = group.findViewById(checkedId);
                category = chip.getText().toString();
            }
            filterUpcomingEvents(category);
        });

        return view;
    }

    private void loadFeaturedUpcomingEvent() {
        databaseReference.child("featured").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent UpcomingEvent = data.getValue(UpcomingEvent.class);
                    if (UpcomingEvent != null) {
                        featuredUpcomingEventTitle.setText(UpcomingEvent.getTitle());
                        featuredUpcomingEventTime.setText(UpcomingEvent.getDate() + " • " + UpcomingEvent.getTime());
                        featuredUpcomingEventLocation.setText(UpcomingEvent.getLocation());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadThisMonthUpcomingEvents() {
        databaseReference.child("this_month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thisMonthUpcomingEvents.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent UpcomingEvent = data.getValue(UpcomingEvent.class);
                    if (UpcomingEvent != null) {
                        thisMonthUpcomingEvents.add(UpcomingEvent);
                    }
                }
                thisMonthAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadUpcomingUpcomingEvents() {
        databaseReference.child("upcoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upcomingUpcomingEvents.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent UpcomingEvent = data.getValue(UpcomingEvent.class);
                    if (UpcomingEvent != null) {
                        upcomingUpcomingEvents.add(UpcomingEvent);
                    }
                }
                upcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void filterUpcomingEvents(String category) {
        List<UpcomingEvent> filteredThisMonth = new ArrayList<>();
        List<UpcomingEvent> filteredUpcoming = new ArrayList<>();

        if (category.equals("All")) {
            filteredThisMonth.addAll(thisMonthUpcomingEvents);
            filteredUpcoming.addAll(upcomingUpcomingEvents);
        } else {
            for (UpcomingEvent UpcomingEvent : thisMonthUpcomingEvents) {
                if (UpcomingEvent.getCategory().equals(category)) {
                    filteredThisMonth.add(UpcomingEvent);
                }
            }
            for (UpcomingEvent UpcomingEvent : upcomingUpcomingEvents) {
                if (UpcomingEvent.getCategory().equals(category)) {
                    filteredUpcoming.add(UpcomingEvent);
                }
            }
        }

        thisMonthAdapter.updateUpcomingEvents(filteredThisMonth);
        upcomingAdapter.updateUpcomingEvents(filteredUpcoming);
    }
}