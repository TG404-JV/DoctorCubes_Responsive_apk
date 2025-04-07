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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.tvm.doctorcube.university.ApplyBottomSheetFragment;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    // Invite Friends button
    private AppCompatButton inviteButton;

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        studyButton = view.findViewById(R.id.studyButton);
        examButton = view.findViewById(R.id.examButton);
        universityButton = view.findViewById(R.id.universityButton);
        seeAllEventsButton = view.findViewById(R.id.see_all_events);
        inviteButton = view.findViewById(R.id.invite_button);
        recyclerView = view.findViewById(R.id.recyclerView);
        SocialActions socialActions = new SocialActions();

        view.findViewById(R.id.whatsapp_button).setOnClickListener(v -> socialActions.openWhatsApp(requireActivity()));
        view.findViewById(R.id.call_now_button).setOnClickListener(v -> socialActions.makeDirectCall(requireActivity()));
        setupToolbar();
        setUpComingEvents();
        setupFeaturesRecyclerView(view);
        setupUniversitiesRecyclerView(view);
        setupTestimonialsSlider(view);
        setupCountrySelectionListeners(view);
        setupSearchBar();
        setupCategoryButtons();

        setupEventListeners();

        return view;
    }

    private void setUpComingEvents() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        eventList = new ArrayList<>();
        // For abroad MBBS education consultancy events/offers
        eventList.add(new Event(R.drawable.img_offer_1, "MBBS", "Study in Russia", "Moscow Medical University", "+45 Students Enrolled"));
        eventList.add(new Event(R.drawable.img_offer_2, "MBBS", "Study in Japan", "Kharkiv National Medical University", "+38 Students Enrolled"));
        eventList.add(new Event(R.drawable.img_offer_3, "MBBS", "Study in China", "Jiangsu University", "+52 Students Enrolled"));
        adapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, Event event) {
        FragmentManager fragmentManager = ((AppCompatActivity) requireContext()).getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString("event_title", event.getTitle());
        ApplyBottomSheetFragment bottomSheet = new ApplyBottomSheetFragment();
        bottomSheet.setArguments(args);
        bottomSheet.show(fragmentManager, "ApplyBottomSheet");
    }

    private void setupFeaturesRecyclerView(View view) {
        featuresRecyclerView = view.findViewById(R.id.features_recycler_view);
        if (featuresRecyclerView == null) {
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
            return;
        }
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fullUniversityList = UniversityData.getUniversities();
        if (getContext() != null) {
            universityListAdapter = new UniversityListAdapter(getContext(), new ArrayList<>(fullUniversityList));
            universitiesRecyclerView.setAdapter(universityListAdapter);
        }
    }

    private void setupTestimonialsSlider(View view) {
        testimonialsViewPager = view.findViewById(R.id.testimonials_viewpager);
        if (testimonialsViewPager == null) {
            return;
        }

        List<Testimonial> testimonials = new ArrayList<>();
        testimonials.add(new Testimonial(R.drawable.img_ektaparmar, "Ekta Parmar", "The Doctorcubes team has been very cooperative from the start. They were in constant touch with me, they took care of all the essential procedures. I never had to think twice to contact them to worry about anything. The best part was that they arranged the visa in a matter of days. They are extremely responsive during the first few days too.", String.valueOf(R.drawable.flag_russia), "Kemerovo State Medical University", "4th year", 5.0f));
        testimonials.add(new Testimonial(R.drawable.img_sneha, "Sneha Prakash Navas", "I will be studying one of my favorite subjects abroad. For students considering studying abroad, I highly recommend Doctorcubes. They took care of everything; I didn't have to worry about a thing during this journey. In short, Doctorcubes is 100% trustworthy and reliable.", String.valueOf(R.drawable.flag_russia), "Maykop University", "3rd year", 5.0f));
        testimonials.add(new Testimonial(R.drawable.img_dipanshu, "Dipanshu Tripude", "Initially, when I was planning to go abroad for my studies, I thought it would be very hectic. However, I was later thinking if I could do it. I got in touch with Doctorcubes and they took care of the entire process for me from the beginning till the end.", String.valueOf(R.drawable.flag_russia), "Chechen State Medical University", "2nd year", 5.0f));


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
            russiaLayout.setOnClickListener(v -> openUniversitiesActivity("Russia"));
        if (georgiaLayout != null)
            georgiaLayout.setOnClickListener(v -> openUniversitiesActivity("Georgia"));
        if (kazakhstanLayout != null)
            kazakhstanLayout.setOnClickListener(v -> openUniversitiesActivity("Kazakhstan"));
        if (nepalLayout != null)
            nepalLayout.setOnClickListener(v -> openUniversitiesActivity("Nepal"));
        if (chinaLayout != null)
            chinaLayout.setOnClickListener(v -> openUniversitiesActivity("China"));
        if (uzbekistanLayout != null)
            uzbekistanLayout.setOnClickListener(v -> openUniversitiesActivity("Uzbekistan"));
    }

    private void setupCommunicationButtons(View view) {
        CommunicationUtils commUtils = new CommunicationUtils(getActivity());
        commUtils.setupCommunicationButtons(view);
    }

    private void setupSearchBar() {
        fullSearchList = new ArrayList<>();

        // Add Universities
        for (int i = 0; i < fullUniversityList.size(); i++) {
            University uni = fullUniversityList.get(i);
            fullSearchList.add(new SearchItem(
                    uni.getName() + ", " + uni.getCountry(),
                    "University",
                    uni,
                    i
            ));
        }

        // Add Events
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            fullSearchList.add(new SearchItem(
                    event.getTitle() + ", " + event.getLocation(),
                    "Event",
                    event,
                    i
            ));
        }

        // Add Features
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

        // Add Testimonials
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
                new SearchUtils.SearchCallback<SearchItem>() {
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
                openUniversitiesActivity(university.getCountry());
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
            default:
                break;
        }

        searchEditText.clearFocus();
        searchResultsRecyclerView.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void setupCategoryButtons() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if (studyButton != null) {
            studyButton.setOnClickListener(v -> {
                StudyMaterialFragment studyFragment = new StudyMaterialFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, studyFragment)
                        .setCustomAnimations(
                                R.anim.fragment_fade_enter,
                                R.anim.fragment_fade_exit,
                                R.anim.fragment_fade_enter,
                                R.anim.fragment_fade_exit
                        )
                        .addToBackStack(null)
                        .commit();
                ((MainActivity) requireActivity()).getToolbar().setTitle("Study");
                ((MainActivity) requireActivity()).getBubbleNavigation().setCurrentActiveItem(1);
            });
        }

        if (examButton != null) {
            examButton.setOnClickListener(v -> {
                CustomToast.showToast(requireActivity(), "Coming Soon");
            });
        }

        if (universityButton != null) {
            universityButton.setOnClickListener(v -> openUniversitiesActivity("All"));
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

    private void openUniversitiesActivity(String country) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), UniversitiesActivity.class);
            intent.putExtra("COUNTRY_NAME", country);
            startActivity(intent);
        }
    }

    @Override
    public void onFeatureClick(Feature feature) {
        if (getActivity() == null) return;

        String title = "";
        String description = "";
        switch (feature.getId()) {
            case Feature.TYPE_UNIVERSITY_LISTINGS -> {
                title = "University Listings";
                description = "Explore a curated selection of top universities worldwide, detailed program information, including curriculum, faculty, and research opportunities.  Access admission requirements, application deadlines, and contact information.  Find the perfect university for your academic journey and career aspirations.";
            }
            case Feature.TYPE_SCHOLARSHIP -> {
                title = "Scholarships";
                description = "Discover a wide range of scholarship opportunities, including merit-based, need-based, and program-specific scholarships.  Detailed information on eligibility criteria, application procedures, deadlines, required documents, and funding details.  Get the financial support you need to pursue your education without financial burden.";
            }
            case Feature.TYPE_VISA_ADMISSION -> {
                title = "Visa & Admission";
                description = "Access comprehensive guidance on visa requirements, application procedures, necessary documentation, interview preparation, and admission guidelines for various countries.  Navigate the complexities of international student admissions with ease, ensuring a smooth transition to your chosen university.";
            }
            case Feature.TYPE_TRACKING -> {
                title = "Application Tracking";
                description = "Monitor the progress of your applications in real-time, receive timely updates on application status, manage your submissions efficiently, and stay informed throughout the application process.  Track deadlines, receive notifications, and communicate directly with admissions offices.";
            }
            case Feature.TYPE_SUPPORT -> {
                title = "Support";
                description = "Connect with our dedicated support team for personalized assistance, expert guidance, and prompt answers to your queries.  We provide comprehensive support throughout your journey, from initial inquiries to post-arrival assistance.  Get help with application process, visa assistance, accommodation, and more.";
            }
            default -> {

            }
        }
        ;

        // Show Bottom Sheet
        showFeatureBottomSheet(title, description);
    }

    private void showFeatureBottomSheet(String title, String description) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.feature_details_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Get references to views in the Bottom Sheet layout
        TextView titleTextView = bottomSheetView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = bottomSheetView.findViewById(R.id.descriptionTextView);
        Button closeButton = bottomSheetView.findViewById(R.id.closeButton); // Add a close button in your layout

        // Set the title and description
        titleTextView.setText(title);
        descriptionTextView.setText(description);

        // Set a click listener for the close button
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null) {
                TextView appTitle = toolbar.findViewById(R.id.app_title);
                if (appTitle != null) {
                    appTitle.setText("Home");
                }
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setSubtitle("Premium Study Materials for Abroad Education");
                }
            }
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

    // SearchItem class
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