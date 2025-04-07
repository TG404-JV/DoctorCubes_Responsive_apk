package com.tvm.doctorcube.adminpannel;



import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ChipManager {
    private final ChipGroup chipGroup;
    private final FilterManager filterManager;
    private final EditText searchEditText;

    public ChipManager(ChipGroup chipGroup, FilterManager filterManager1, EditText searchEditText) {
        this.chipGroup = chipGroup;
        filterManager = filterManager1;
        this.searchEditText = searchEditText;
    }

    public void updateChips() {
        chipGroup.removeAllViews();

        if (!filterManager.getSearchQuery().isEmpty()) addChip("Search: " + filterManager.getSearchQuery());
        if (filterManager.getCurrentSortBy() != null) {
            switch (filterManager.getCurrentSortBy()) {
                case "name_asc": addChip("Sort: Name (A-Z)"); break;
                case "name_desc": addChip("Sort: Name (Z-A)"); break;
                case "date_newest": addChip("Sort: Date (Newest)"); break;
                case "date_oldest": addChip("Sort: Date (Oldest)"); break;
                case "last_updated_newest": addChip("Sort: Last Updated (Newest)"); break;
                case "last_updated_oldest": addChip("Sort: Last Updated (Oldest)"); break;
                case "firebase_push_newest": addChip("Sort: Firebase Push (Newest)"); break;
                case "firebase_push_oldest": addChip("Sort: Firebase Push (Oldest)"); break;
            }
        }
        if (filterManager.isFilterInterested()) addChip("Interested");
        if (filterManager.isFilterNotInterested()) addChip("Not Interested");
        if (filterManager.isFilterAdmitted()) addChip("Admitted");
        if (filterManager.isFilterNotAdmitted()) addChip("Not Admitted");
        if (filterManager.isFilterCalled()) addChip("Called");
        if (filterManager.isFilterNotCalled()) addChip("Not Called");
        switch (filterManager.getDateFilter()) {
            case "today": addChip("Today"); break;
            case "yesterday": addChip("Yesterday"); break;
            case "last_week": addChip("Last Week"); break;
            case "last_month": addChip("Last Month"); break;
            case "last_updated": addChip("Last Updated"); break;
            case "firebase_push": addChip("Firebase Push"); break;
            case "custom":
                if (filterManager.getFromDate() != null && filterManager.getToDate() != null)
                    addChip("Date: " + filterManager.displayFormat.format(filterManager.getFromDate()) + " - " +
                            filterManager.displayFormat.format(filterManager.getToDate()));
                break;
        }
    }

    private void addChip(String text) {
        Chip chip = new Chip(chipGroup.getContext());
        chip.setText(text);
        chip.setTextColor(0xFF212121); // Dark Gray
        chip.setChipBackgroundColorResource(android.R.color.white);
        chip.setCloseIconVisible(true);
        chip.setCloseIconTintResource(android.R.color.darker_gray);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            switch (text) {
                case "Sort: Name (A-Z)": case "Sort: Name (Z-A)": case "Sort: Date (Newest)": case "Sort: Date (Oldest)":
                case "Sort: Last Updated (Newest)": case "Sort: Last Updated (Oldest)":
                case "Sort: Firebase Push (Newest)": case "Sort: Firebase Push (Oldest)":
                    filterManager.setCurrentSortBy(null); break;
                case "Interested": filterManager.setFilterInterested(false); break;
                case "Not Interested": filterManager.setFilterNotInterested(false); break;
                case "Admitted": filterManager.setFilterAdmitted(false); break;
                case "Not Admitted": filterManager.setFilterNotAdmitted(false); break;
                case "Called": filterManager.setFilterCalled(false); break;
                case "Not Called": filterManager.setFilterNotCalled(false); break;
                case "Today": case "Yesterday": case "Last Week": case "Last Month": case "Last Updated": case "Firebase Push":
                    filterManager.setDateFilter("all"); break;
                default:
                    if (text.startsWith("Search: ")) searchEditText.setText("");
                    else if (text.startsWith("Date: ")) {
                        filterManager.setUseCustomDateRange(false);
                        filterManager.setFromDate(null);
                        filterManager.setToDate(null);
                        filterManager.setDateFilter("all");
                    }
                    break;
            }
            filterManager.applyFiltersAndSorting();
            updateChips();
        });
        chipGroup.addView(chip);
    }
}

