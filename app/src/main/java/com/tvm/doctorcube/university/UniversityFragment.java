package com.tvm.doctorcube.university;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.university.adapter.UniversityAdapter;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UniversityFragment extends Fragment {

    private static final String ARG_COUNTRY_NAME = "COUNTRY_NAME";
    private RecyclerView recyclerView;
    private UniversityAdapter adapter;
    private List<University> universities;
    private EditText searchEditText;
    private TextView countryNameTitle, noUniversitiesText, universityCount, topRankedCount;
    private MaterialButton clearFiltersBtn, filterBtn;
    private Spinner filterSpinner;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private NavController navController;
    private String countryFilter = "All";
    private String selectedFilter = "None";
    private ImageButton backBtn;

    public UniversityFragment() {
    }

    public static UniversityFragment newInstance(String countryName) {
        UniversityFragment fragment = new UniversityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY_NAME, countryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String country = getArguments().getString(ARG_COUNTRY_NAME, "All");
            countryFilter = country != null && !country.isEmpty() ? country : "All";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_university, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.universities_recycler_view);
        searchEditText = view.findViewById(R.id.search_university);
        countryNameTitle = view.findViewById(R.id.country_name_title);
        noUniversitiesText = view.findViewById(R.id.no_universities_text);
        filterBtn = view.findViewById(R.id.filterBtn);
        universityCount = view.findViewById(R.id.university_count);
        topRankedCount = view.findViewById(R.id.top_ranked_count);
        clearFiltersBtn = view.findViewById(R.id.clear_filters_btn);
        filterSpinner = view.findViewById(R.id.filter_spinner);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        progressBar = view.findViewById(R.id.progressBar);
        backBtn = view.findViewById(R.id.backBtn);

        navController = NavHostFragment.findNavController(this);

        // Set country title
        if (countryNameTitle != null) {
            countryNameTitle.setText(countryFilter.equals("All") ? "All Universities" : "Universities in " + countryFilter);
        }

        // Verify critical components
      /*  if (filterBtn == null || filterSpinner == unimportant) {
            Log.e("UniversityFragment", "Critical UI components missing");
            return;
        }*/

        // Show loading state
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Load data
        loadUniversityData();

        // Setup listeners
        setupFilterSpinner();
        setupSearch();
        setupFilterButton();
        setupClearFilters();
        setupBackButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        universities = new ArrayList<>();
        adapter = new UniversityAdapter(requireContext(), universities, this::navigateToUniversityDetails, navController);
        recyclerView.setAdapter(adapter);
    }

    private void navigateToUniversityDetails(University university) {
        if (university == null) {
            Log.e("UniversityFragment", "Attempted to navigate with null university");
            return;
        }
        try {
            Bundle args = new Bundle();
            args.putSerializable("UNIVERSITY", university);
            args.putString("universityId", university.getId());
            navController.navigate(R.id.action_universityFragment_to_universityDetailsFragment, args);
        } catch (Exception e) {
            Log.e("UniversityFragment", "Navigation failed for " + university.getName(), e);
        }
    }

    private void setupFilterSpinner() {
        String[] filterOptions = {"None", "Top Ranked", "Sort A-Z", "Sort Z-A", "Sort Grade"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = filterOptions[position];
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = "None";
                applyFilters();
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.filterByName(s.toString());
                    updateNoUniversitiesView();
                    updateStats();
                }
            }
        });
    }

    private void setupFilterButton() {
        filterBtn.setOnClickListener(v -> {
            int visibility = filterSpinner.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            filterSpinner.setVisibility(visibility);
        });
    }

    private void setupClearFilters() {
        clearFiltersBtn.setOnClickListener(v -> {
            filterSpinner.setSelection(0);
            selectedFilter = "None";
            searchEditText.setText("");
            filterByCountry();
            filterSpinner.setVisibility(View.GONE);
            updateStats();
        });
    }

    private void setupBackButton() {
        backBtn.setOnClickListener(v -> {
            try {
                navController.navigateUp();
            } catch (Exception e) {
                Log.e("UniversityFragment", "Back navigation failed", e);
            }
        });
    }

    private void loadUniversityData() {
        try {
            universities = UniversityData.getUniversities(requireContext());
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            filterByCountry();
            Log.d("UniversityFragment", "Loaded " + universities.size() + " universities for " + countryFilter);
        } catch (Exception e) {
            Log.e("UniversityFragment", "Error loading university data", e);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            universities = new ArrayList<>();
            adapter.updateData(universities);
            updateNoUniversitiesView();
            updateStats();
        }
    }

    private void filterByCountry() {
        List<University> filtered = new ArrayList<>();
        if (countryFilter.equals("All") || countryFilter.isEmpty()) {
            filtered.addAll(universities);
        } else {
            filtered.addAll(universities.stream()
                    .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                    .collect(Collectors.toList()));
        }
        adapter.updateData(filtered);
        updateStats();
        updateNoUniversitiesView();
    }

    private void applyFilters() {
        List<University> filteredUniversities = new ArrayList<>(universities);

        if (!countryFilter.equals("All") && !countryFilter.isEmpty()) {
            filteredUniversities = filteredUniversities.stream()
                    .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                    .collect(Collectors.toList());
        }

        if (!selectedFilter.equals("None")) {
            switch (selectedFilter.toLowerCase()) {
                case "top ranked":
                    filteredUniversities = filteredUniversities.stream()
                            .filter(u -> u.getRanking() != null && u.getRanking().toLowerCase().contains("top"))
                            .collect(Collectors.toList());
                    break;
                case "sort a-z":
                    adapter.sortByName(true);
                    updateStats();
                    updateNoUniversitiesView();
                    return;
                case "sort z-a":
                    adapter.sortByName(false);
                    updateStats();
                    updateNoUniversitiesView();
                    return;
                case "sort grade":
                    adapter.sortByGrade(true);
                    updateStats();
                    updateNoUniversitiesView();
                    return;
            }
        }
        adapter.updateData(filteredUniversities);
        updateStats();
        updateNoUniversitiesView();
    }

    private void updateStats() {
        if (adapter == null) {
            universityCount.setText("0");
            topRankedCount.setText("0");
            return;
        }
        int totalCount = adapter.getItemCount();
        long topRanked = universities.stream()
                .filter(u -> u.getRanking() != null && u.getRanking().toLowerCase().contains("top"))
                .count();
        universityCount.setText(String.valueOf(totalCount));
        topRankedCount.setText(String.valueOf(topRanked));
    }

    private void updateNoUniversitiesView() {
        View emptyStateContainer = requireView().findViewById(R.id.empty_state_container);
        if (adapter == null || adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }
}