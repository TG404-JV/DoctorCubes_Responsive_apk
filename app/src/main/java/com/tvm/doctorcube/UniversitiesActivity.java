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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

        TextView countryNameTitle = findViewById(R.id.country_name_title);
        countryNameTitle.setText("Universities in " + countryFilter);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(countryFilter.equals("All") ? "All Universities" : "Universities in " + countryFilter);

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

        // Load data
        loadUniversityData();

        // Setup listeners
        setupFilterSpinner();
        setupSearch();
        setupFilterButton();
        setupClearFilters();
        setupBackButton(); // Setup back button listener
    }


    private void setupFilterSpinner() {
        String[] filterOptions = {"None", "Top Ranked", "Sort A-Z", "Sort Z-A", "Sort Grade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

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
        });
    }

    private void setupBackButton() {
        backBtn.setOnClickListener(v -> {
            onBackPressed(); // Call onBackPressed() to navigate back
        });
    }

    private void loadUniversityData() {
        try {
            // Execute the AsyncTask and get the result
            List<University> result = new LoadUniversityDataTask().execute().get(10, TimeUnit.SECONDS); // Set a timeout
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            universities = result != null ? result : new ArrayList<>();
            adapter.updateData(universities);
            filterByCountry(); // Apply initial country filter

        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
            // Handle the error appropriately, e.g., show a message to the user
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            universities =  new ArrayList<>();
            adapter.updateData(universities);
            updateNoUniversitiesView();

        }
    }

    private void filterByCountry() {
        List<University> filtered;
        if (countryFilter.equals("All")) {
            filtered = universities;
        } else {
            filtered = universities.stream()
                    .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filtered);
        updateStats();
        updateNoUniversitiesView();
    }

    private void applyFilters() {
        List<University> filteredUniversities = new ArrayList<>(universities);

        if (!countryFilter.equals("All")) {
            filteredUniversities = filteredUniversities.stream()
                    .filter(u -> u.getCountry() != null && u.getCountry().equalsIgnoreCase(countryFilter))
                    .collect(Collectors.toList());
        }

        if (!selectedFilter.equals("None")) {
            switch (selectedFilter.toLowerCase()) {
                case "top ranked":
                    filteredUniversities = filteredUniversities.stream()
                            .filter(u -> u.getRanking() != null && u.getRanking().contains("Top"))
                            .collect(Collectors.toList());
                    break;
                case "sort a-z":
                    filteredUniversities.sort((u1, u2) -> u1.getName().compareToIgnoreCase(u2.getName()));
                    break;
                case "sort z-a":
                    filteredUniversities.sort((u1, u2) -> u2.getName().compareToIgnoreCase(u1.getName()));
                    break;
                case "sort grade":
                    filteredUniversities.sort((u1, u2) -> {
                        if (u1.getGrade() == null || u2.getGrade() == null) {
                            return 0; // Handle null grades
                        }
                        return u1.getGrade().compareToIgnoreCase(u2.getGrade());
                    });
                    break;
                default:
                    break;

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
    private static class LoadUniversityDataTask extends AsyncTask<Void, Void, List<University>> {
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
            //No need to do anything here.
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher();
        return true;
    }
}
