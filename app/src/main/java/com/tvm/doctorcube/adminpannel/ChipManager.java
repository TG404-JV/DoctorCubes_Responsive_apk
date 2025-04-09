package com.tvm.doctorcube.adminpannel;

import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ChipManager {
    private final ChipGroup chipGroup;
    private final FilterManager filterManager;
    private final EditText searchEditText;

    public ChipManager(ChipGroup chipGroup, FilterManager filterManager, EditText searchEditText) {
        this.chipGroup = chipGroup;
        this.filterManager = filterManager;
        this.searchEditText = searchEditText;
    }

    public void updateChips() {
        chipGroup.removeAllViews();

        // Search Chip
        if (!filterManager.getSearchQuery().isEmpty()) {
            addChip("Search: " + filterManager.getSearchQuery(), "search");
        }

        // Sort Chips (Multiple possible)
        List<String> currentSortBy = filterManager.getCurrentSortByList();
        if (currentSortBy != null && !currentSortBy.isEmpty()) {
            for (String sort : currentSortBy) {
                switch (sort) {
                    case "name":
                        addChip("Sort: Name", "sort_name");
                        break;
                    case "submission_date":
                        addChip("Sort: Submission Date", "sort_submission_date");
                        break;
                    case "last_call_date":
                        addChip("Sort: Last Call Date", "sort_last_call_date");
                        break;
                    case "firebase_push_date":
                        addChip("Sort: Firebase Push Date", "sort_firebase_push_date");
                        break;
                }
            }
        }

        // Status Filter Chips
        if (filterManager.isFilterInterested()) addChip("Interested", "interested");
        if (filterManager.isFilterNotInterested()) addChip("Not Interested", "not_interested");
        if (filterManager.isFilterAdmitted()) addChip("Admitted", "admitted");
        if (filterManager.isFilterNotAdmitted()) addChip("Not Admitted", "not_admitted");
        if (filterManager.isFilterCalled()) addChip("Called", "called");
        if (filterManager.isFilterNotCalled()) addChip("Not Called", "not_called");

        // Date Filter Chips (Multiple possible)
        List<String> dateFilters = filterManager.getDateFilters();
        if (dateFilters != null && !dateFilters.isEmpty()) {
            for (String dateFilter : dateFilters) {
                switch (dateFilter) {
                    case "today":
                        addChip("Today", "today");
                        break;
                    case "yesterday":
                        addChip("Yesterday", "yesterday");
                        break;
                    case "last_week":
                        addChip("Last Week", "last_week");
                        break;
                    case "last_month":
                        addChip("Last Month", "last_month");
                        break;
                    case "last_updated":
                        addChip("Last Call Date", "last_updated");
                        break;
                    case "firebase_push":
                        addChip("Firebase Push", "firebase_push");
                        break;
                    case "custom":
                        if (filterManager.getFromDate() != null && filterManager.getToDate() != null)
                            addChip("Date: " + filterManager.displayFormat.format(filterManager.getFromDate()) + " - " +
                                    filterManager.displayFormat.format(filterManager.getToDate()), "custom");
                        break;
                    case "all":
                        addChip("All Dates", "all");
                        break;
                }
            }
        }
    }

    private void addChip(String text, String tag) {
        Chip chip = new Chip(chipGroup.getContext());
        chip.setText(text);
        chip.setTextColor(0xFF212121); // Dark Gray
        chip.setChipBackgroundColorResource(android.R.color.white);
        chip.setCloseIconVisible(true);
        chip.setCloseIconTintResource(android.R.color.darker_gray);
        chip.setTag(tag); // Use tag to identify the chip type
        chip.setOnCloseIconClickListener(v -> {
            String chipTag = (String) chip.getTag();
            chipGroup.removeView(chip);

            switch (chipTag) {
                // Sort Chips
                case "sort_name":
                    removeSortCriterion("name");
                    break;
                case "sort_submission_date":
                    removeSortCriterion("submission_date");
                    break;
                case "sort_last_call_date":
                    removeSortCriterion("last_call_date");
                    break;
                case "sort_firebase_push_date":
                    removeSortCriterion("firebase_push_date");
                    break;

                // Status Filter Chips
                case "interested":
                    filterManager.setFilterInterested(false);
                    break;
                case "not_interested":
                    filterManager.setFilterNotInterested(false);
                    break;
                case "admitted":
                    filterManager.setFilterAdmitted(false);
                    break;
                case "not_admitted":
                    filterManager.setFilterNotAdmitted(false);
                    break;
                case "called":
                    filterManager.setFilterCalled(false);
                    break;
                case "not_called":
                    filterManager.setFilterNotCalled(false);
                    break;

                // Date Filter Chips
                case "today":
                case "yesterday":
                case "last_week":
                case "last_month":
                case "last_updated":
                case "firebase_push":
                case "all":
                    removeDateFilter(chipTag);
                    break;
                case "custom":
                    filterManager.setUseCustomDateRange(false);
                    filterManager.setFromDate(null);
                    filterManager.setToDate(null);
                    removeDateFilter("custom");
                    break;

                // Search Chip
                case "search":
                    searchEditText.setText("");
                    break;
            }

            filterManager.applyFiltersAndSorting();
            updateChips();
        });
        chipGroup.addView(chip);
    }

    private void removeSortCriterion(String criterion) {
        List<String> sortBy = filterManager.getCurrentSortByList();
        if (sortBy != null) {
            sortBy.remove(criterion);
            filterManager.setCurrentSortByList(sortBy.isEmpty() ? null : sortBy);
        }
    }

    private void removeDateFilter(String filter) {
        List<String> dateFilters = filterManager.getDateFilters();
        if (dateFilters != null) {
            dateFilters.remove(filter);
            filterManager.setDateFilters(dateFilters.isEmpty() ? null : dateFilters);
        }
    }
}