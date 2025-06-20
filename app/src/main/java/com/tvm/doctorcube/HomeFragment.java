package com.tvm.doctorcube;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup ;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
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

    private static final String TAG = "HomeFragment";
    private static final int AUTO_SLIDE_INTERVAL = 3000;

    private RecyclerView featuresRecyclerView, universitiesRecyclerView, eventsRecyclerView, countryRecyclerView;
    private FeaturesAdapter featuresAdapter;
    private UniversityListAdapter universityListAdapter;
    private UpcomingEventAdapter eventAdapter;
    private CountryAdapter countryAdapter;
    private ViewPager2 testimonialsViewPager;
    private TestimonialsSliderAdapter testimonialsAdapter;
    private Handler sliderHandler;
    private Runnable testimonialsSliderRunnable;

    private EditText searchEditText;
    private ImageView clearSearchButton;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private List<SearchItem> fullSearchList;
    private SearchUtils<SearchItem> searchUtils;

    private MaterialCardView studyButton, examButton, universityButton;
    private TextView seeAllEventsButton;
    private AppCompatButton inviteButton;

    private NavController navController;
    private DatabaseReference databaseReference;
    private List<UpcomingEvent> eventList;
    private List<University> fullUniversityList;

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
        sliderHandler = new Handler(Looper.getMainLooper());
        fullSearchList = new ArrayList<>();

        // Initialize views
        initializeViews(view);

        // Setup back press handling
        setupBackPressHandling();

        // Setup social actions
        setupSocialActions(view);

        // Setup UI components
        setupEventsRecyclerView();
        setupFeaturesRecyclerView();
        setupUniversitiesRecyclerView();
        setupTestimonialsSlider();
        setupCountryRecyclerView();
        setupSearchBar();
        setupCategoryButtons();
        setupEventListeners();

        setupToolbar();
        // Apply animations
        animateViews();
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        clearSearchButton = view.findViewById(R.id.clearSearchButton);
        searchResultsRecyclerView = view.findViewById(R.id.search_results);
        studyButton = view.findViewById(R.id.studyButton);
        examButton = view.findViewById(R.id.examButton);
        universityButton = view.findViewById(R.id.universityButton);
        seeAllEventsButton = view.findViewById(R.id.see_all_events);
        inviteButton = view.findViewById(R.id.invite_button);
        eventsRecyclerView = view.findViewById(R.id.recyclerView);

        // Validate views
        if (searchEditText == null || clearSearchButton == null || searchResultsRecyclerView == null) {
            Log.e(TAG, "Search UI components missing");
        } else {
            searchResultsRecyclerView.setVisibility(View.GONE);
            searchResultsRecyclerView.setAlpha(0f);
        }
    }

    private void setupBackPressHandling() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchEditText != null && searchEditText.hasFocus()) {
                    clearSearchState();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void setupSocialActions(View view) {
        SocialActions socialActions = new SocialActions();
        View whatsappButton = view.findViewById(R.id.whatsapp_button);
        View callNowButton = view.findViewById(R.id.call_now_button);

        if (whatsappButton != null) {
            whatsappButton.setOnClickListener(v -> socialActions.openWhatsApp(requireActivity()));
        }
        if (callNowButton != null) {
            callNowButton.setOnClickListener(v -> socialActions.makeDirectCall(requireActivity()));
        }
    }

    private void setupEventsRecyclerView() {
        if (eventsRecyclerView == null) {
            Log.e(TAG, "eventsRecyclerView is null");
            return;
        }
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        eventList = new ArrayList<>();
        eventAdapter = new UpcomingEventAdapter(eventList, this);
        eventsRecyclerView.setAdapter(eventAdapter);

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
                animateRecyclerView(eventsRecyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load events: " + error.getMessage());
                CustomToast.showToast(requireActivity(), "Failed to load events");
            }
        });
    }

    @Override
    public void onItemClick(int position, UpcomingEvent event) {
        if (event == null) {
            Log.e(TAG, "Event is null at position: " + position);
            return;
        }
        Bundle args = new Bundle();
        args.putString("imageUrl", event.getImageUrl());
        args.putString("eventTitle", event.getTitle());
        args.putString("eventDate", event.getDate() + " • " + event.getTime());
        args.putString("eventLocation", event.getLocation());
        try {
            navController.navigate(R.id.action_homeFragment_to_eventDetailsBottomSheet, args);
        } catch (Exception e) {
            Log.e(TAG, "Navigation to event details failed: " + e.getMessage());
        }
    }

    private void setupFeaturesRecyclerView() {
        featuresRecyclerView = requireView().findViewById(R.id.features_recycler_view);
        if (featuresRecyclerView == null) {
            Log.e(TAG, "featuresRecyclerView is null");
            return;
        }
        featuresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        featuresAdapter = new FeaturesAdapter(features, this);
        featuresRecyclerView.setAdapter(featuresAdapter);
        animateRecyclerView(featuresRecyclerView);
    }

    private void setupUniversitiesRecyclerView() {
        universitiesRecyclerView = requireView().findViewById(R.id.universities_recycler_view);
        if (universitiesRecyclerView == null) {
            Log.e(TAG, "universitiesRecyclerView is null");
            return;
        }
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fullUniversityList = UniversityData.getUniversities(requireContext());
        if (!fullUniversityList.isEmpty()) {
            universityListAdapter = new UniversityListAdapter(requireContext(), new ArrayList<>(fullUniversityList), this::navigateToUniversityDetails);
            universitiesRecyclerView.setAdapter(universityListAdapter);
            updateSearchList();
            animateRecyclerView(universitiesRecyclerView);
        } else {
            Log.w(TAG, "University list is null or empty");
        }
    }

    private void navigateToUniversityDetails(University university) {
        if (university == null) {
            Log.e(TAG, "University is null");
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable("UNIVERSITY", university);
        args.putString("universityId", university.getId());
        try {
            navController.navigate(R.id.action_homeFragment_to_universityDetailsFragment, args);
        } catch (Exception e) {
            Log.e(TAG, "Navigation to university details failed: " + e.getMessage());
        }
    }

    private void setupTestimonialsSlider() {
        testimonialsViewPager = requireView().findViewById(R.id.testimonials_viewpager);
        if (testimonialsViewPager == null) {
            Log.e(TAG, "testimonialsViewPager is null");
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
            testimonialsViewPager.setCurrentItem(currentPosition == testimonialsAdapter.getItemCount() - 1 ? 0 : currentPosition + 1);
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

        animateViewPager();
    }

    private void setupCountryRecyclerView() {
        countryRecyclerView = requireView().findViewById(R.id.country_recycler_view);
        if (countryRecyclerView == null) {
            Log.e(TAG, "countryRecyclerView is null");
            return;
        }
        countryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Country> countries = UniversityData.getCountries(requireContext());
        countryAdapter = new CountryAdapter(countries, this);
        countryRecyclerView.setAdapter(countryAdapter);
        animateRecyclerView(countryRecyclerView);
    }

    @Override
    public void onCountryClick(Country country) {
        if (country == null) {
            Log.e(TAG, "Country is null");
            return;
        }
        Bundle args = new Bundle();
        args.putString("COUNTRY_NAME", country.getName());
        try {
            navController.navigate(R.id.action_homeFragment_to_universityFragment, args);
        } catch (Exception e) {
            Log.e(TAG, "Navigation to university fragment failed: " + e.getMessage());
        }
    }

    private void setupSearchBar() {
        if (searchEditText == null || searchResultsRecyclerView == null || clearSearchButton == null) {
            Log.e(TAG, "Search UI components missing");
            return;
        }

        // Initialize adapter
        searchResultsAdapter = new SearchResultsAdapter(new ArrayList<>(), this::navigateToSection);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Setup clear search button
        clearSearchButton.setOnClickListener(v -> clearSearchState());

        // TextWatcher for clear button visibility
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    clearSearchButton.setVisibility(!s.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initialize SearchUtils
        searchUtils = new SearchUtils<>(
                getActivity(),
                searchEditText,
                fullSearchList,
                new SearchUtils.SearchCallback<>() {
                    @Override
                    public void onSearchResults(List<SearchItem> filteredList) {
                        searchResultsAdapter.updateData(filteredList);
                        boolean shouldShowResults = searchEditText.isFocused() || !searchEditText.getText().toString().isEmpty();
                        if (shouldShowResults && !filteredList.isEmpty()) {
                            animateSearchResultsIn();
                        } else {
                            animateSearchResultsOut();
                        }
                        Log.d(TAG, "Search results updated: " + filteredList.size() + " items");
                    }

                    @Override
                    public String getSearchText(SearchItem item) {
                        return item.title() != null ? item.title() : "";
                    }
                }
        );
        searchUtils.setupSearchBar();

        // Focus listener
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            String query = searchEditText.getText().toString();
            searchUtils.filter(query);
            if (hasFocus && !query.isEmpty()) {
                animateSearchResultsIn();
            } else if (!hasFocus) {
                animateSearchResultsOut();
            }
        });
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity activity) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            LottieAnimationView home =activity.findViewById(R.id.nav_home_icon);
            if (toolbar != null) {
                TextView appTitle = toolbar.findViewById(R.id.app_title);
                if (appTitle != null) {
                    appTitle.setText(getString(com.shockwave.pdfium.R.string.app_name)); // Directly set the TextView text
                }
                // Hide the back button
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    activity.getSupportActionBar().setHomeButtonEnabled(false);
                }
                toolbar.setNavigationIcon(null); // Explicitly remove navigation icon
            }
        }
    }


    private void clearSearchState() {
        if (searchEditText != null) {
            searchEditText.setText("");
            searchEditText.clearFocus();
        }
        hideKeyboard();
        animateSearchResultsOut();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && searchEditText != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    private void updateSearchList() {
        fullSearchList.clear();

        // Universities
        if (fullUniversityList != null) {
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
        }

        // Events
        if (eventList != null) {
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
        }

        // Features
        List<Feature> features = FeatureData.getInstance().getFeatures(requireContext());
        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            if (feature != null) {
                fullSearchList.add(new SearchItem(
                        feature.getTitle(),
                        "Feature",
                        feature,
                        i
                ));
            }
        }

        // Testimonials
        List<Testimonial> testimonials = testimonialsAdapter != null ? testimonialsAdapter.getTestimonials() : new ArrayList<>();
        if (testimonials != null) {
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
        }

        if (searchResultsAdapter != null) {
            searchResultsAdapter.updateData(fullSearchList);
            Log.d(TAG, "Search list updated with " + fullSearchList.size() + " items");
        }
    }

    private void navigateToSection(SearchItem item) {
        if (item == null) {
            Log.e(TAG, "Search item is null");
            return;
        }

        try {
            switch (item.type()) {
                case "University":
                    navigateToUniversityDetails((University) item.data());
                    break;
                case "Event":
                    if (eventsRecyclerView != null) {
                        eventsRecyclerView.smoothScrollToPosition(item.sectionPosition());
                    }
                    break;
                case "Feature":
                    onFeatureClick((Feature) item.data());
                    break;
                case "Testimonial":
                    if (testimonialsViewPager != null) {
                        testimonialsViewPager.setCurrentItem(item.sectionPosition(), true);
                    }
                    break;
            }
            clearSearchState();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to section: " + item.type(), e);
        }
    }

    private void setupCategoryButtons() {
        if (studyButton != null) {
            studyButton.setOnClickListener(v -> {
                animateButtonClick(studyButton);
                try {
                    navController.navigate(R.id.action_homeFragment_to_studyMaterialFragment);
                } catch (Exception e) {
                    Log.e(TAG, "Navigation to study material failed: " + e.getMessage());
                }
            });
        }

        if (examButton != null) {
            examButton.setOnClickListener(v -> {
                animateButtonClick(examButton);
                CustomToast.showToast(requireActivity(), "Coming Soon");
            });
        }

        if (universityButton != null) {
            universityButton.setOnClickListener(v -> {
                animateButtonClick(universityButton);
                try {
                    navController.navigate(R.id.action_homeFragment_to_universityFragment);
                } catch (Exception e) {
                    Log.e(TAG, "Navigation to university fragment failed: " + e.getMessage());
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
                    Log.e(TAG, "Navigation to events fragment failed: " + e.getMessage());
                }
            });
        }

        if (inviteButton != null) {
            inviteButton.setOnClickListener(v -> {
                try {
                    String playStoreLink = "https://play.google.com/store/apps/details?id=com.tvm.doctorcube";

                    String message = "Hey! Join me on DoctorCube to explore study abroad opportunities.\n"
                            + "Download the app now: " + playStoreLink;

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);

                    startActivity(Intent.createChooser(shareIntent, "Invite Friends"));
                } catch (Exception e) {
                    Log.e("Invite", "Error sharing invite: " + e.getMessage());
                }

            });
        }
    }

    @Override
    public void onFeatureClick(Feature feature) {
        if (feature == null) {
            Log.e(TAG, "Feature is null");
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable("FEATURE", feature);
        try {
            navController.navigate(R.id.action_homeFragment_to_featuresFragment, args);
        } catch (Exception e) {
            Log.e(TAG, "Navigation to features fragment failed: " + e.getMessage());
        }
    }

    private void startTestimonialsAutoSlide() {
        if (testimonialsSliderRunnable != null && sliderHandler != null) {
            sliderHandler.postDelayed(testimonialsSliderRunnable, AUTO_SLIDE_INTERVAL);
        }
    }

    private void animateViews() {
        if (studyButton != null) {
            animateViewFadeIn(studyButton, 0);
        }
        if (examButton != null) {
            animateViewFadeIn(examButton, 100);
        }
        if (universityButton != null) {
            animateViewFadeIn(universityButton, 200);
        }
    }

    private void animateViewFadeIn(View view, long delay) {
        view.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, slideUp);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }

    private void animateRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(recyclerView, "alpha", 0f, 1f);
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(recyclerView, "translationX", 100f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, slideIn);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void animateViewPager() {
        if (testimonialsViewPager != null) {
            testimonialsViewPager.setAlpha(0f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(testimonialsViewPager, "alpha", 0f, 1f);
            fadeIn.setDuration(500);
            fadeIn.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            fadeIn.start();
        }
    }

    private void animateSearchResultsIn() {
        if (searchResultsRecyclerView != null && searchResultsRecyclerView.getVisibility() != View.VISIBLE) {
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(searchResultsRecyclerView, "alpha", 0f, 1f);
            ObjectAnimator slideDown = ObjectAnimator.ofFloat(searchResultsRecyclerView, "translationY", -50f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(fadeIn, slideDown);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            animatorSet.start();
        }
    }

    private void animateSearchResultsOut() {
        if (searchResultsRecyclerView != null && searchResultsRecyclerView.getVisibility() == View.VISIBLE) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(searchResultsRecyclerView, "alpha", 1f, 0f);
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(searchResultsRecyclerView, "translationY", 0f, -50f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(fadeOut, slideUp);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationStart(@NonNull Animator animation) {}
                @Override
                public void onAnimationCancel(@NonNull Animator animation) {}
                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {}
            });
            animatorSet.start();
        }
    }

    private void animateButtonClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTestimonialsAutoSlide();
        clearSearchState();
        updateSearchList();
        setupSearchBar();
        Log.d(TAG, "Search state reset in onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(testimonialsSliderRunnable);
        }
        clearSearchState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchUtils != null) {
            searchUtils.clear();
            searchUtils = null;
        }
        searchResultsAdapter = null;
        searchEditText = null;
        clearSearchButton = null;
        searchResultsRecyclerView = null;
        Log.d(TAG, "Cleared search components in onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(testimonialsSliderRunnable);
            sliderHandler = null;
        }
    }

    public record SearchItem(String title, String type, Object data, int sectionPosition) {
    }

    static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
        private List<SearchItem> searchItems;
        private final OnItemClickListener listener;

        public SearchResultsAdapter(List<SearchItem> searchItems, OnItemClickListener listener) {
            this.searchItems = searchItems != null ? searchItems : new ArrayList<>();
            this.listener = listener;
        }

        public void updateData(List<SearchItem> newItems) {
            this.searchItems = newItems != null ? newItems : new ArrayList<>();
            notifyDataSetChanged();
            Log.d(TAG, "SearchResultsAdapter updated with " + searchItems.size() + " items");
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
            if (item == null) {
                Log.w(TAG, "SearchItem at position " + position + " is null");
                return;
            }

            holder.textView.setText(item.title());
            switch (item.type()) {
                case "University":
                    holder.iconView.setImageResource(R.drawable.icon_university);
                    break;
                case "Event":
                    holder.iconView.setImageResource(R.drawable.ic_event);
                    break;
                case "Feature":
                    holder.iconView.setImageResource(R.drawable.ic_feature);
                    break;
                case "Testimonial":
                    holder.iconView.setImageResource(R.drawable.ic_testimonal);
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

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView textView;
            final ImageView iconView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.result_text);
                iconView = itemView.findViewById(R.id.result_icon);
                if (textView == null || iconView == null) {
                    Log.e(TAG, "Search result item views missing");
                }
            }
        }

        interface OnItemClickListener {
            void onItemClick(SearchItem item);
        }
    }
}