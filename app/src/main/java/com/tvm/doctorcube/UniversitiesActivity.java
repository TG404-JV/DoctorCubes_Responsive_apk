package com.tvm.doctorcube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.university.adapter.UniversityAdapter;
import com.tvm.doctorcube.university.model.University;
import com.tvm.doctorcube.university.model.UniversityData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UniversitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UniversityAdapter adapter;
    private List<University> universities;
    private EditText searchEditText;
    private TextView countryNameTitle, noUniversitiesText, universityCount, topRankedCount;
    private MaterialButton clearFiltersBtn, filterBtn;
    private Spinner filterSpinner;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private String countryFilter;
    private String selectedFilter = "None";
    private ImageButton backBtn; // Declare backBtn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universities);

        // Get country filter from intent
        countryFilter = getIntent().getStringExtra("COUNTRY_NAME") != null ?
                getIntent().getStringExtra("COUNTRY_NAME") : "All";

        // Initialize views
        recyclerView = findViewById(R.id.universities_recycler_view);
        searchEditText = findViewById(R.id.search_university);
        countryNameTitle = findViewById(R.id.country_name_title);
        noUniversitiesText = findViewById(R.id.no_universities_text);
        filterBtn = findViewById(R.id.filterBtn);
        universityCount = findViewById(R.id.university_count);
        topRankedCount = findViewById(R.id.top_ranked_count);
        clearFiltersBtn = findViewById(R.id.clear_filters_btn);
        filterSpinner = findViewById(R.id.filter_spinner);
        appBarLayout = findViewById(R.id.appBarLayout);
        progressBar = findViewById(R.id.progressBar);
        backBtn = findViewById(R.id.backBtn); // Initialize backBtn

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(countryFilter.equals("All") ? "All Universities" : "Universities in " + countryFilter);

        // Verify critical components
        if (filterBtn == null || filterSpinner == null) {
            return;
        }

        // Show loading state
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        // Setup RecyclerView with empty data initially
        setupRecyclerView();

        // Load data asynchronously
        new LoadUniversityDataTask().execute();

        // Setup listeners
        setupFilterSpinner();
        setupSearch();
        setupFilterButton();
        setupClearFilters();
        setupBackButton(); // Setup back button listener
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        universities = new ArrayList<>(); // Initialize empty list
        adapter = new UniversityAdapter(this, universities, countryFilter);
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterSpinner() {
        String[] filterOptions = {"None", "Top Ranked", "Scholarships",  "Sort A-Z", "Sort Z-A", "Sort Grade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = filterOptions[position];
                applyFiltersAsync();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = "None";
                applyFiltersAsync();
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.filterByName(s.toString());
                    updateNoUniversitiesView();
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
            if (adapter != null) {
                filterByCountryAsync();
            }
            filterSpinner.setVisibility(View.GONE);
        });
    }

    private void setupBackButton() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call onBackPressed() to navigate back
            }
        });
    }


    private void filterByCountryAsync() {
        new FilterByCountryTask().execute();
    }

    private void applyFiltersAsync() {
        new ApplyFiltersTask().execute();
    }

    private void updateStats() {
        if (adapter == null) {
            universityCount.setText("0");
            topRankedCount.setText("0");
            return;
        }
        int totalCount = adapter.getItemCount();
        int topRanked = (int) universities.stream()
                .filter(u -> u.getRanking() != null && u.getRanking().contains("Top"))
                .count();
        universityCount.setText(String.valueOf(totalCount));
        topRankedCount.setText(String.valueOf(topRanked));
    }

    private void updateNoUniversitiesView() {
        View emptyStateContainer = findViewById(R.id.empty_state_container);
        if (adapter == null || adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }

    // AsyncTask to load university data
    private class LoadUniversityDataTask extends AsyncTask<Void, Void, List<University>> {
        @Override
        protected List<University> doInBackground(Void... voids) {
            try {
                return UniversityData.getUniversities();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<University> result) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            universities = result != null ? result : new ArrayList<>();
            adapter.updateData(universities);
            filterByCountryAsync(); // Apply initial country filter
        }
    }

    // AsyncTask for country filtering
    private class FilterByCountryTask extends AsyncTask<Void, Void, List<University>> {
        @Override
        protected List<University> doInBackground(Void... voids) {
            if (countryFilter.equals("All")) {
                return universities;
            }
            return universities.stream()
                    .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                    .collect(Collectors.toList());
        }

        @Override
        protected void onPostExecute(List<University> filtered) {
            if (adapter != null) {
                adapter.updateData(filtered);
                updateStats();
                updateNoUniversitiesView();
            }
        }
    }

    // AsyncTask for applying filters
    private class ApplyFiltersTask extends AsyncTask<Void, Void, List<University>> {
        @Override
        protected List<University> doInBackground(Void... voids) {
            List<University> filteredUniversities = new ArrayList<>(universities);

            if (!countryFilter.equals("All")) {
                filteredUniversities = filteredUniversities.stream()
                        .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                        .collect(Collectors.toList());
            }

            if (!selectedFilter.equals("None")) {
                switch (selectedFilter.toLowerCase()) {
                    case "top ranked":
                        return filteredUniversities.stream()
                                .filter(u -> u.getRanking() != null && u.getRanking().contains("Top"))
                                .collect(Collectors.toList());
                    case "scholarships":
                        return filteredUniversities.stream()
                                .filter(u -> u.getScholarshipInfo() != null && !u.getScholarshipInfo().isEmpty())
                                .collect(Collectors.toList());
                    case "engineering":
                        return filteredUniversities.stream()
                                .filter(u -> u.getField() != null && u.getField().equalsIgnoreCase("engineering"))
                                .collect(Collectors.toList());
                }
            }
            return filteredUniversities;
        }

        @Override
        protected void onPostExecute(List<University> filtered) {
            if (adapter != null) {
                adapter.updateData(filtered);
                if (selectedFilter.equalsIgnoreCase("Sort A-Z")) {
                    adapter.sortByName(true);
                } else if (selectedFilter.equalsIgnoreCase("Sort Z-A")) {
                    adapter.sortByName(false);
                } else if (selectedFilter.equalsIgnoreCase("Sort Grade")) {
                    adapter.sortByGrade(true);
                }
                updateStats();
                updateNoUniversitiesView();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
