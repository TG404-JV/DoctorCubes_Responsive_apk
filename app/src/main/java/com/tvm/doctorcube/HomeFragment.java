package com.tvm.doctorcube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tvm.doctorcube.communication.SearchUtils;
import com.tvm.doctorcube.home.adapter.CountryAdapter;
import com.tvm.doctorcube.home.adapter.FeaturesAdapter;
import com.tvm.doctorcube.home.adapter.TestimonialsSliderAdapter;
import com.tvm.doctorcube.home.adapter.UniversityListAdapter;
import com.tvm.doctorcube.home.adapter.UpcomingEventAdapter;
import com.tvm.doctorcube.home.data.FeatureData;
import com.tvm.doctorcube.home.data.Testimonial;
import com.tvm.doctorcube.home.model.Country;
import com.tvm.doctorcube.home.model.Feature;
import com.tvm.doctorcube.home.model.UpcomingEvent;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityData;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FeaturesAdapter.OnFeatureClickListener, UpcomingEventAdapter.OnItemClickListener, CountryAdapter.OnCountryClickListener {

    private RecyclerView featuresRecyclerView, universitiesRecyclerView, recyclerView, countryRecyclerView;
    private FeaturesAdapter featuresAdapter;
    private UniversityListAdapter universityListAdapter;
    private UpcomingEventAdapter eventAdapter;
    private CountryAdapter countryAdapter;
    private ViewPager2 testimonialsViewPager;
    private TestimonialsSliderAdapter testimonialsAdapter;
    private Handler sliderHandler = new Handler();
    private Runnable testimonialsSliderRunnable;
    private final int AUTO_SLIDE_INTERVAL = 3000;

    private EditText searchEditText;
    private List<University> fullUniversityList;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private List<SearchItem> fullSearchList = new ArrayList<>();

    private MaterialCardView studyButton, examButton, universityButton;
    private TextView seeAllEventsButton;
    private AppCompatButton inviteButton;

    private NavController navController;
    private DatabaseReference databaseReference;
    private List<UpcomingEvent> eventList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents");

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.search_results);
        studyButton = view.findViewById(R.id.studyButton);
        examButton = view.findViewById(R.id.examButton);
        universityButton = view.findViewById(R.id.universityButton);
        seeAllEventsButton = view.findViewById(R.id.see_all_events);
        inviteButton = view.findViewById(R.id.invite_button);
        recyclerView = view.findViewById(R.id.recyclerView);
        SocialActions socialActions = new SocialActions();

        // Debug view initialization
        if (searchEditText == null) {
            Log.e("HomeFragment", "searchEditText is null. Check R.id.searchEditText in fragment_home.xml");
        }
        if (searchResultsRecyclerView == null) {
            Log.e("HomeFragment", "searchResultsRecyclerView is null. Check R.id.search_results in fragment_home.xml");
        }

        View whatsappButton = view.findViewById(R.id.whatsapp_button);
        if (whatsappButton != null) {
            whatsappButton.setOnClickListener(v -> socialActions.openWhatsApp(requireActivity()));
        }
        View callNowButton = view.findViewById(R.id.call_now_button);
        if (callNowButton != null) {
            callNowButton.setOnClickListener(v -> socialActions.makeDirectCall(requireActivity()));
        }

        setUpComingEvents();
        setupFeaturesRecyclerView(view);
        setupUniversitiesRecyclerView(view);
        setupTestimonialsSlider(view);
        setupCountryRecyclerView(view);
        setupSearchBar();
        setupCategoryButtons();
        setupEventListeners();
    }

    private void setUpComingEvents() {
        if (recyclerView == null) {
            Log.e("HomeFragment", "recyclerView is null");
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        eventList = new ArrayList<>();
        eventAdapter = new UpcomingEventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        databaseReference.child("this_month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UpcomingEvent event = data.getValue(UpcomingEvent.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
                updateSearchList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to load events: " + error.getMessage());
                CustomToast.showToast(requireActivity(), "Failed to load events");
            }
        });
    }

    @Override
    public void onItemClick(int position, UpcomingEvent event) {
        Bundle args = new Bundle();
        args.putString("imageUrl", event.getImageUrl());
        args.putString("eventTitle", event.getTitle());
        args.putString("eventDate", event.getDate() + " • " + event.getTime());
        args.putString("eventLocation", event.getLocation());
        try {
            navController.navigate(R.id.action_homeFragment_to_eventDetailsBottomSheet, args);
        } catch (Exception e) {
            Log.e("HomeFragment", "Navigation to event details failed: " + e.getMessage());
        }
    }

    private void setupFeaturesRecyclerView(View view) {
        featuresRecyclerView = view.findViewById(R.id.features_recycler_view);
        if (featuresRecyclerView == null) {
            Log.e("HomeFragment", "featuresRecyclerView is null");
            return;
        }
        featuresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        featuresAdapter = new FeaturesAdapter(features, this);
        featuresRecyclerView.setAdapter(featuresAdapter);
    }

    private void setupUniversitiesRecyclerView(View view) {
        universitiesRecyclerView = view.findViewById(R.id.universities_recycler_view);
        if (universitiesRecyclerView == null) {
            Log.e("HomeFragment", "universitiesRecyclerView is null");
            return;
        }
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fullUniversityList = UniversityData.getUniversities(requireContext());
        if (getContext() != null && fullUniversityList != null && !fullUniversityList.isEmpty()) {
            universityListAdapter = new UniversityListAdapter(getContext(), new ArrayList<>(fullUniversityList), this::navigateToUniversityDetails);
            universitiesRecyclerView.setAdapter(universityListAdapter);
            updateSearchList();
        } else {
            Log.w("HomeFragment", "University list is null or empty");
        }
    }

    private void navigateToUniversityDetails(University university) {
        if (university == null) {
            Log.e("HomeFragment", "Attempted to navigate with null university");
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable("UNIVERSITY", university);
        args.putString("universityId", university.getId());
        try {
            navController.navigate(R.id.action_homeFragment_to_universityDetailsFragment, args);
        } catch (Exception e) {
            Log.e("HomeFragment", "Navigation to university details failed: " + e.getMessage());
        }
    }

    private void setupTestimonialsSlider(View view) {
        testimonialsViewPager = view.findViewById(R.id.testimonials_viewpager);
        if (testimonialsViewPager == null) {
            Log.e("HomeFragment", "testimonialsViewPager is null");
            return;
        }

        List<Testimonial> testimonials = new ArrayList<>();
        testimonials.add(new Testimonial(R.drawable.img_ektaparmar, "Ekta Parmar", "The Doctorcubes team has been very cooperative from the start...", String.valueOf(R.drawable.flag_russia), "Kemerovo State Medical University", "4th year", 5.0f));
        testimonials.add(new Testimonial(R.drawable.img_sneha, "Sneha Prakash Navas", "I will be studying one of my favorite subjects abroad...", String.valueOf(R.drawable.flag_russia), "Maykop University", "3rd year", 5.0f));
        testimonials.add(new Testimonial(R.drawable.img_dipanshu, "Dipanshu Tripude", "Initially, when I was planning to go abroad for my studies...", String.valueOf(R.drawable.flag_russia), "Chechen State Medical University", "2nd year", 5.0f));

        testimonialsAdapter = new TestimonialsSliderAdapter(testimonials);
        testimonialsViewPager.setAdapter(testimonialsAdapter);

        testimonialsSliderRunnable = () -> {
            int currentPosition = testimonialsViewPager.getCurrentItem();
            if (currentPosition == testimonialsAdapter.getItemCount() - 1) {
                testimonialsViewPager.setCurrentItem(0);
            } else {
                testimonialsViewPager.setCurrentItem(currentPosition + 1);
            }
            sliderHandler.postDelayed(testimonialsSliderRunnable, AUTO_SLIDE_INTERVAL);
        };
        startTestimonialsAutoSlide();

        testimonialsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(testimonialsSliderRunnable);
                sliderHandler.postDelayed(testimonialsSliderRunnable, AUTO_SLIDE_INTERVAL);
            }
        });
    }

    private void setupCountryRecyclerView(View view) {
        countryRecyclerView = view.findViewById(R.id.country_recycler_view);
        if (countryRecyclerView == null) {
            Log.e("HomeFragment", "countryRecyclerView is null");
            return;
        }
        countryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Country> countries = UniversityData.getCountries(requireContext());
        countryAdapter = new CountryAdapter(countries, this);
        countryRecyclerView.setAdapter(countryAdapter);
    }

    @Override
    public void onCountryClick(Country country) {
        if (country == null) {
            Log.e("HomeFragment", "Country is null");
            return;
        }
        Bundle args = new Bundle();
        args.putString("COUNTRY_NAME", country.getName());
        try {
            navController.navigate(R.id.action_homeFragment_to_universityFragment, args);
        } catch (Exception e) {
            Log.e("HomeFragment", "Navigation to university fragment failed: " + e.getMessage());
        }
    }

    private void setupSearchBar() {
        if (searchEditText == null || searchResultsRecyclerView == null) {
            Log.e("HomeFragment", "Search UI components missing: searchEditText=" + (searchEditText == null) + ", searchResultsRecyclerView=" + (searchResultsRecyclerView == null));
            return;
        }

        SearchUtils<SearchItem> searchUtils = new SearchUtils<>(
                getActivity(),
                searchEditText,
                fullSearchList,
                new SearchUtils.SearchCallback<>() {
                    @Override
                    public void onSearchResults(List<SearchItem> filteredList) {
                        if (searchResultsAdapter == null) {
                            searchResultsAdapter = new SearchResultsAdapter(filteredList, item -> navigateToSection(item));
                            searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            searchResultsRecyclerView.setAdapter(searchResultsAdapter);
                        } else {
                            searchResultsAdapter.updateData(filteredList);
                        }
                        searchResultsRecyclerView.setVisibility(filteredList.isEmpty() && searchEditText.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
                        Log.d("HomeFragment", "Search results updated: " + filteredList.size() + " items");
                    }

                    @Override
                    public String getSearchText(SearchItem item) {
                        return item.getTitle() != null ? item.getTitle() : "";
                    }
                }
        );
        searchUtils.setupSearchBar();

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && searchEditText.getText().toString().isEmpty()) {
                searchResultsRecyclerView.setVisibility(View.GONE);
            } else if (hasFocus && searchResultsAdapter != null) {
                searchResultsRecyclerView.setVisibility(View.VISIBLE);
                searchResultsAdapter.updateData(fullSearchList);
            }
        });
    }

    private void updateSearchList() {
        fullSearchList.clear();

        if (fullUniversityList != null && !fullUniversityList.isEmpty()) {
            for (int i = 0; i < fullUniversityList.size(); i++) {
                University uni = fullUniversityList.get(i);
                if (uni != null && uni.getName() != null && uni.getCountry() != null) {
                    fullSearchList.add(new SearchItem(
                            uni.getName() + ", " + uni.getCountry(),
                            "University",
                            uni,
                            i
                    ));
                }
            }
            Log.d("HomeFragment", "Added " + fullUniversityList.size() + " universities to search list");
        } else {
            Log.w("HomeFragment", "University list is null or empty");
        }

        if (eventList != null && !eventList.isEmpty()) {
            for (int i = 0; i < eventList.size(); i++) {
                UpcomingEvent event = eventList.get(i);
                if (event != null && event.getTitle() != null && event.getLocation() != null) {
                    fullSearchList.add(new SearchItem(
                            event.getTitle() + ", " + event.getLocation(),
                            "Event",
                            event,
                            i
                    ));
                }
            }
            Log.d("HomeFragment", "Added " + eventList.size() + " events to search list");
        } else {
            Log.w("HomeFragment", "Event list is null or empty");
        }

        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        if (features != null && !features.isEmpty()) {
            for (int i = 0; i < features.size(); i++) {
                Feature feature = features.get(i);
                if (feature != null && feature.getTitle() != null) {
                    fullSearchList.add(new SearchItem(
                            feature.getTitle(),
                            "Feature",
                            feature,
                            i
                    ));
                }
            }
            Log.d("HomeFragment", "Added " + features.size() + " features to search list");
        }

        List<Testimonial> testimonials = testimonialsAdapter != null ? testimonialsAdapter.getTestimonials() : new ArrayList<>();
        if (testimonials != null && !testimonials.isEmpty()) {
            for (int i = 0; i < testimonials.size(); i++) {
                Testimonial testimonial = testimonials.get(i);
                if (testimonial != null && testimonial.getName() != null && testimonial.getUniversity() != null) {
                    fullSearchList.add(new SearchItem(
                            testimonial.getName() + " - " + testimonial.getUniversity(),
                            "Testimonial",
                            testimonial,
                            i
                    ));
                }
            }
            Log.d("HomeFragment", "Added " + testimonials.size() + " testimonials to search list");
        }

        if (searchResultsAdapter != null) {
            searchResultsAdapter.updateData(fullSearchList);
        }
        Log.d("HomeFragment", "Search list updated with " + fullSearchList.size() + " items");
    }

    private void navigateToSection(SearchItem item) {
        if (item == null) {
            Log.e("HomeFragment", "Search item is null");
            return;
        }

        try {
            switch (item.getType()) {
                case "University":
                    University university = (University) item.getData();
                    navigateToUniversityDetails(university);
                    break;
                case "Event":
                    if (recyclerView != null) {
                        recyclerView.smoothScrollToPosition(item.getSectionPosition());
                    } else {
                        Log.w("HomeFragment", "Event recyclerView is null");
                    }
                    break;
                case "Feature":
                    onFeatureClick((Feature) item.getData());
                    break;
                case "Testimonial":
                    if (testimonialsViewPager != null) {
                        testimonialsViewPager.setCurrentItem(item.getSectionPosition(), true);
                    } else {
                        Log.w("HomeFragment", "Testimonials ViewPager is null");
                    }
                    break;
            }

            searchEditText.clearFocus();
            searchResultsRecyclerView.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("HomeFragment", "Error navigating to section: " + item.getType(), e);
        }
    }

    private void setupCategoryButtons() {
        if (studyButton != null) {
            studyButton.setOnClickListener(v -> {
                try {
                    navController.navigate(R.id.action_homeFragment_to_studyMaterialFragment);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Navigation to study material failed: " + e.getMessage());
                }
            });
        }

        if (examButton != null) {
            examButton.setOnClickListener(v -> {
                CustomToast.showToast(requireActivity(), "Coming Soon");
            });
        }

        if (universityButton != null) {
            universityButton.setOnClickListener(v -> {
                try {
                    navController.navigate(R.id.action_homeFragment_to_universityFragment);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Navigation to university fragment failed: " + e.getMessage());
                }
            });
        }
    }

    private void setupEventListeners() {
        if (seeAllEventsButton != null) {
            seeAllEventsButton.setOnClickListener(v -> {
                try {
                    navController.navigate(R.id.action_homeFragment_to_fragmentUpcomingEvents);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Navigation to events fragment failed: " + e.getMessage());
                }
            });
        }

        if (inviteButton != null) {
            inviteButton.setOnClickListener(v -> {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Join me on DoctorCube to explore study abroad opportunities! Get $20 for a ticket: [Your Referral Link]");
                    startActivity(Intent.createChooser(shareIntent, "Invite Friends"));
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error sharing invite: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onFeatureClick(Feature feature) {
        if (getActivity() == null || feature == null) {
            Log.e("HomeFragment", "Activity or feature is null");
            return;
        }

        Bundle args = new Bundle();
        args.putSerializable("FEATURE", feature);
        try {
            navController.navigate(R.id.action_homeFragment_to_featuresFragment, args);
        } catch (Exception e) {
            Log.e("HomeFragment", "Navigation to features fragment failed: " + e.getMessage());
        }
    }

    private void startTestimonialsAutoSlide() {
        if (testimonialsSliderRunnable != null && sliderHandler != null) {
            sliderHandler.postDelayed(testimonialsSliderRunnable, AUTO_SLIDE_INTERVAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTestimonialsAutoSlide();
        updateSearchList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(testimonialsSliderRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(testimonialsSliderRunnable);
            sliderHandler = null;
        }
    }

    public static class SearchItem {
        private String title;
        private String type;
        private Object data;
        private int sectionPosition;

        public SearchItem(String title, String type, Object data, int sectionPosition) {
            this.title = title;
            this.type = type;
            this.data = data;
            this.sectionPosition = sectionPosition;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public Object getData() {
            return data;
        }

        public int getSectionPosition() {
            return sectionPosition;
        }
    }

    class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
        private List<SearchItem> searchItems;
        private final OnItemClickListener listener;

        public SearchResultsAdapter(List<SearchItem> searchItems, OnItemClickListener listener) {
            this.searchItems = searchItems != null ? searchItems : new ArrayList<>();
            this.listener = listener;
        }

        public void updateData(List<SearchItem> newItems) {
            this.searchItems = newItems != null ? newItems : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_search_result_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchItem item = searchItems.get(position);
            if (item == null) return;

            holder.textView.setText(item.getTitle());
            switch (item.getType()) {
                case "University":
                    holder.iconView.setImageResource(R.drawable.icon_university);
                    break;
                case "Event":
                    holder.iconView.setImageResource(R.drawable.ic_notes);
                    break;
                case "Feature":
                    holder.iconView.setImageResource(R.drawable.ic_search);
                    break;
                case "Testimonial":
                    holder.iconView.setImageResource(R.drawable.icon_university);
                    break;
                default:
                    holder.iconView.setImageResource(R.drawable.ic_search);
            }
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return searchItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView iconView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.result_text);
                iconView = itemView.findViewById(R.id.result_icon);
                if (textView == null || iconView == null) {
                    Log.e("HomeFragment", "Search result item views missing: textView=" + (textView == null) + ", iconView=" + (iconView == null));
                }
            }
        }

        interface OnItemClickListener {
            void onItemClick(SearchItem item);
        }
    }
}