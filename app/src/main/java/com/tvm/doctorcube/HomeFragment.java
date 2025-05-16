package com.tvm.doctorcube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.tvm.doctorcube.communication.CommunicationUtils;
import com.tvm.doctorcube.communication.SearchUtils;
import com.tvm.doctorcube.home.adapter.EventAdapter;
import com.tvm.doctorcube.home.adapter.FeaturesAdapter;
import com.tvm.doctorcube.home.adapter.TestimonialsSliderAdapter;
import com.tvm.doctorcube.home.adapter.UniversityListAdapter;
import com.tvm.doctorcube.home.data.FeatureData;
import com.tvm.doctorcube.home.data.Testimonial;
import com.tvm.doctorcube.home.model.Event;
import com.tvm.doctorcube.home.model.Feature;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FeaturesAdapter.OnFeatureClickListener, EventAdapter.OnItemClickListener {

    private RecyclerView featuresRecyclerView;
    private FeaturesAdapter featuresAdapter;
    private RecyclerView universitiesRecyclerView;
    private UniversityListAdapter universityListAdapter;
    private ViewPager2 testimonialsViewPager;
    private TestimonialsSliderAdapter testimonialsAdapter;
    private Handler sliderHandler = new Handler();
    private Runnable testimonialsSliderRunnable;
    private final int AUTO_SLIDE_INTERVAL = 3000;

    // Search bar components
    private EditText searchEditText;
    private List<University> fullUniversityList;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private List<SearchItem> fullSearchList;

    // Category buttons
    private MaterialCardView studyButton, examButton, universityButton;

    // Upcoming Events "SEE ALL" button
    private TextView seeAllEventsButton;

    // University count

    // Invite Friends button
    private AppCompatButton inviteButton;

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Initialize UI components
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.search_results);
        studyButton = view.findViewById(R.id.studyButton);
        examButton = view.findViewById(R.id.examButton);
        universityButton = view.findViewById(R.id.universityButton);
        seeAllEventsButton = view.findViewById(R.id.see_all_events);
        inviteButton = view.findViewById(R.id.invite_button);
        recyclerView = view.findViewById(R.id.recyclerView);
        SocialActions socialActions = new SocialActions();

        // Setup click listeners
        View whatsappButton = view.findViewById(R.id.whatsapp_button);
        if (whatsappButton != null) {
            whatsappButton.setOnClickListener(v -> socialActions.openWhatsApp(requireActivity()));
        }
        View callNowButton = view.findViewById(R.id.call_now_button);
        if (callNowButton != null) {
            callNowButton.setOnClickListener(v -> socialActions.makeDirectCall(requireActivity()));
        }

        // Setup UI components
        setUpComingEvents();
        setupFeaturesRecyclerView(view);
        setupUniversitiesRecyclerView(view);
        setupTestimonialsSlider(view);
        setupCountrySelectionListeners(view);
        setupSearchBar();
        setupCategoryButtons();
        setupEventListeners();
    }

    private void setUpComingEvents() {
        if (recyclerView == null) return;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        eventList = new ArrayList<>();
        eventList.add(new Event(R.drawable.img_offer_1, "MBBS", "Study in Russia", "Moscow Medical University", "+45 Students Enrolled"));
        eventList.add(new Event(R.drawable.img_offer_2, "MBBS", "Study in Japan", "Kharkiv National Medical University", "+38 Students Enrolled"));
        eventList.add(new Event(R.drawable.img_offer_3, "MBBS", "Study in China", "Jiangsu University", "+52 Students Enrolled"));
        adapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, Event event) {
        Bundle args = new Bundle();
        args.putInt("imageResourceId", event.getImageResId());
        args.putString("universityName", event.getLocation());
        args.putString("country", extractCountry(event.getLocation()));
        navController.navigate(R.id.action_homeFragment_to_universityDetailsBottomSheet2, args);
    }

    private String extractCountry(String location) {
        try {
            return location.split(", ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unknown";
        }
    }

    private void setupFeaturesRecyclerView(View view) {
        featuresRecyclerView = view.findViewById(R.id.features_recycler_view);
        if (featuresRecyclerView == null) return;
        featuresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        featuresAdapter = new FeaturesAdapter(features, this);
        featuresRecyclerView.setAdapter(featuresAdapter);
    }

    private void setupUniversitiesRecyclerView(View view) {
        universitiesRecyclerView = view.findViewById(R.id.universities_recycler_view);
        if (universitiesRecyclerView == null) return;
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fullUniversityList = UniversityData.getUniversities(requireContext());
        if (getContext() != null) {
            universityListAdapter = new UniversityListAdapter(getContext(), new ArrayList<>(fullUniversityList), this::navigateToUniversityDetails);
            universitiesRecyclerView.setAdapter(universityListAdapter);
        }
    }



    private void navigateToUniversityDetails(University university) {
        Bundle args = new Bundle();
        args.putSerializable("UNIVERSITY", university);
        navController.navigate(R.id.action_homeFragment_to_universityDetailsFragment, args);
    }

    private void setupTestimonialsSlider(View view) {
        testimonialsViewPager = view.findViewById(R.id.testimonials_viewpager);
        if (testimonialsViewPager == null) return;

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

    private void setupCountrySelectionListeners(View view) {
        CardView russiaLayout = view.findViewById(R.id.country_russia_card);
        CardView georgiaLayout = view.findViewById(R.id.country_georgia_card);
        CardView kazakhstanLayout = view.findViewById(R.id.country_kazakhstan_card);
        CardView nepalLayout = view.findViewById(R.id.country_nepal_card);
        CardView chinaLayout = view.findViewById(R.id.country_china_card);
        CardView uzbekistanLayout = view.findViewById(R.id.country_uzbekistan_card);

        if (russiaLayout != null)
            russiaLayout.setOnClickListener(v -> navigateToUniversities("Russia"));
        if (georgiaLayout != null)
            georgiaLayout.setOnClickListener(v -> navigateToUniversities("Georgia"));
        if (kazakhstanLayout != null)
            kazakhstanLayout.setOnClickListener(v -> navigateToUniversities("Kazakhstan"));
        if (nepalLayout != null)
            nepalLayout.setOnClickListener(v -> navigateToUniversities("Nepal"));
        if (chinaLayout != null)
            chinaLayout.setOnClickListener(v -> navigateToUniversities("China"));
        if (uzbekistanLayout != null)
            uzbekistanLayout.setOnClickListener(v -> navigateToUniversities("Uzbekistan"));
    }

    private void navigateToUniversities(String country) {
        Bundle args = new Bundle();
        args.putString("COUNTRY_NAME", country);
        navController.navigate(R.id.action_homeFragment_to_universityFragment, args);
    }

    private void setupSearchBar() {
        if (searchEditText == null || searchResultsRecyclerView == null) return;

        fullSearchList = new ArrayList<>();

        for (int i = 0; i < fullUniversityList.size(); i++) {
            University uni = fullUniversityList.get(i);
            fullSearchList.add(new SearchItem(
                    uni.getName() + ", " + uni.getCountry(),
                    "University",
                    uni,
                    i
            ));
        }

        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            fullSearchList.add(new SearchItem(
                    event.getTitle() + ", " + event.getLocation(),
                    "Event",
                    event,
                    i
            ));
        }

        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            fullSearchList.add(new SearchItem(
                    feature.getTitle(),
                    "Feature",
                    feature,
                    i
            ));
        }

        List<Testimonial> testimonials = testimonialsAdapter.getTestimonials();
        for (int i = 0; i < testimonials.size(); i++) {
            Testimonial testimonial = testimonials.get(i);
            fullSearchList.add(new SearchItem(
                    testimonial.getName() + " - " + testimonial.getUniversity(),
                    "Testimonial",
                    testimonial,
                    i
            ));
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
                        searchResultsRecyclerView.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public String getSearchText(SearchItem item) {
                        return item.getTitle();
                    }
                }
        );
        searchUtils.setupSearchBar();

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && searchEditText.getText().toString().isEmpty()) {
                searchResultsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void navigateToSection(SearchItem item) {
        switch (item.getType()) {
            case "University":
                University university = (University) item.getData();
                navigateToUniversities(university.getCountry());
                break;
            case "Event":
                recyclerView.smoothScrollToPosition(item.getSectionPosition());
                break;
            case "Feature":
                featuresRecyclerView.smoothScrollToPosition(item.getSectionPosition());
                break;
            case "Testimonial":
                testimonialsViewPager.setCurrentItem(item.getSectionPosition(), true);
                break;
        }

        searchEditText.clearFocus();
        searchResultsRecyclerView.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void setupCategoryButtons() {
        if (studyButton != null) {
            studyButton.setOnClickListener(v -> {
                navController.navigate(R.id.action_homeFragment_to_studyMaterialFragment);
            });
        }

        if (examButton != null) {
            examButton.setOnClickListener(v -> {
                CustomToast.showToast(requireActivity(), "Coming Soon");
            });
        }

        if (universityButton != null) {
            universityButton.setOnClickListener(v -> navigateToUniversities("All"));
        }
    }

    private void setupEventListeners() {
        if (seeAllEventsButton != null) {
            seeAllEventsButton.setOnClickListener(v -> {
                CustomToast.showToast(getActivity(), "Coming Soon");
            });
        }

        if (inviteButton != null) {
            inviteButton.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Join me on DoctorCube to explore study abroad opportunities! Get $20 for a ticket: [Your Referral Link]");
                startActivity(Intent.createChooser(shareIntent, "Invite Friends"));
            });
        }
    }

    @Override
    public void onFeatureClick(Feature feature) {
        if (getActivity() == null) return;

        String title = "";
        String description = "";
        switch (feature.getId()) {
            case Feature.TYPE_UNIVERSITY_LISTINGS:
                title = "University Listings";
                description = "Explore a curated selection of top universities worldwide...";
                break;
            case Feature.TYPE_SCHOLARSHIP:
                title = "Scholarships";
                description = "Discover a wide range of scholarship opportunities...";
                break;
            case Feature.TYPE_VISA_ADMISSION:
                title = "Visa & Admission";
                description = "Access comprehensive guidance on visa requirements...";
                break;
            case Feature.TYPE_TRACKING:
                title = "Application Tracking";
                description = "Monitor the progress of your applications in real-time...";
                break;
            case Feature.TYPE_SUPPORT:
                title = "Support";
                description = "Connect with our dedicated support team...";
                break;
        }

        showFeatureBottomSheet(title, description);
    }

    private void showFeatureBottomSheet(String title, String description) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.feature_details_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView titleTextView = bottomSheetView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = bottomSheetView.findViewById(R.id.descriptionTextView);
        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
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
        Object data;
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
            this.searchItems = searchItems;
            this.listener = listener;
        }

        public void updateData(List<SearchItem> newItems) {
            this.searchItems = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchItem item = searchItems.get(position);
            holder.textView.setText(item.getTitle());
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return searchItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }

        interface OnItemClickListener {
            void onItemClick(SearchItem item);
        }
    }
}