package com.tvm.doctorcube;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.tvm.doctorcube.study.adapter.StudyMaterialPagerAdapter;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class StudyMaterialFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;
    private ViewPager2 viewPager;
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private StudyMaterialPagerAdapter pagerAdapter;
    private String currentSearchQuery = "";
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study_material, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        toggleGroup = view.findViewById(R.id.segmentedButtonGroup);
        viewPager = view.findViewById(R.id.viewPager);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearSearchButton = view.findViewById(R.id.clearSearchButton);

        // Check for null views
        if (toggleGroup == null || viewPager == null || searchEditText == null || clearSearchButton == null) {
            return;
        }

        // Initially hide the clear button
        clearSearchButton.setVisibility(View.GONE);

        // Set up the MainActivity's Toolbar
        setupToolbar();

        // Set up the ViewPager and segmented buttons
        setupViewPagerAndButtons();

        // Set up search functionality
        setupSearch();
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null) {
                TextView appTitle = toolbar.findViewById(R.id.app_title);
                if (appTitle != null) {
                    appTitle.setText("Study Material"); // Directly set the TextView text
                }
                // Set subtitle via ActionBar
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setSubtitle("Premium Study Materials for Abroad Education");
                }
            }
        }
    }

    private void setupViewPagerAndButtons() {
        // Create adapter
        pagerAdapter = new StudyMaterialPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Set initial checked button (Notes)
        toggleGroup.check(R.id.btnNotes);

        // Sync toggle group with ViewPager2
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btnNotes) {
                        viewPager.setCurrentItem(0);
                    } else if (checkedId == R.id.btnVideos) {
                        viewPager.setCurrentItem(1);
                    }
                    // Re-apply search if query exists
                    if (!currentSearchQuery.isEmpty()) {
                        performSearch(currentSearchQuery);
                    }
                }
            }
        });

        // Sync ViewPager2 with toggle group
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int buttonId = position == 0 ? R.id.btnNotes : R.id.btnVideos;
                toggleGroup.check(buttonId);
                // Re-apply search if query exists
                if (!currentSearchQuery.isEmpty()) {
                    performSearch(currentSearchQuery);
                }
            }
        });
    }

    private void setupSearch() {
        // Clear button functionality
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearSearchButton.setVisibility(View.GONE);
            currentSearchQuery = "";
            resetSearch();
            hideKeyboard();
        });

        // Handle search action on keyboard "Search" button
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentSearchQuery = searchEditText.getText().toString().trim();
                if (!currentSearchQuery.isEmpty()) {
                    performSearch(currentSearchQuery);
                } else {
                    resetSearch();
                }
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Real-time search as user types
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button based on text
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Cancel any pending search
                if (searchRunnable != null) {
                    searchEditText.removeCallbacks(searchRunnable);
                }

                // Delay search to avoid searching on every character
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    currentSearchQuery = query;

                    if (query.length() >= 2) {
                        performSearch(query);
                    } else if (query.isEmpty()) {
                        resetSearch();
                    }
                };

                searchEditText.postDelayed(searchRunnable, 300); // 300ms delay
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    // Method to hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && searchEditText != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    // Perform search on the current visible fragment
    public void performSearch(String query) {
        if (pagerAdapter != null) {
            Fragment currentFragment = pagerAdapter.getFragmentAt(viewPager.getCurrentItem());
            if (currentFragment instanceof SearchableFragment) {
                ((SearchableFragment) currentFragment).performSearch(query);
            }
        }
    }

    // Reset search on the current visible fragment
    public void resetSearch() {
        if (pagerAdapter != null) {
            Fragment currentFragment = pagerAdapter.getFragmentAt(viewPager.getCurrentItem());
            if (currentFragment instanceof SearchableFragment) {
                ((SearchableFragment) currentFragment).resetSearch();
            }
        }
    }

    // Getter for current search query
    public String getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    // Interface for fragments that support search
    public interface SearchableFragment {
        void performSearch(String query);
        void resetSearch();
    }


}