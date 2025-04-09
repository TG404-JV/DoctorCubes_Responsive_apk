package com.tvm.doctorcube.adminpannel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tvm.doctorcube.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

public class SortFilterDialogManager {
    private final Context context;
    private final FilterManager filterManager;
    private Runnable onApplyCallback;

    public SortFilterDialogManager(Context context, FilterManager filterManager, Runnable onApplyCallback) {
        this.context = context;
        this.filterManager = filterManager;
        this.onApplyCallback = onApplyCallback;
    }

    public SortFilterDialogManager(Context context, FilterManager filterManager) {
        this.context = context;
        this.filterManager = filterManager;
    }

    public void setOnApplyCallback(Runnable callback) {
        this.onApplyCallback = callback;
    }

    public void showSortFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sort_filter, null);
        dialog.setContentView(dialogView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        // Sort Chips
        ChipGroup sortGroup = dialogView.findViewById(R.id.sort_chip_group);
        Chip sortNameChip = dialogView.findViewById(R.id.chip_sort_name); // Updated ID
        Chip sortSubmissionDateChip = dialogView.findViewById(R.id.chip_sort_submission_date); // Updated ID
        Chip sortLastCallDateChip = dialogView.findViewById(R.id.chip_sort_last_call_date); // Updated ID
        Chip sortFirebasePushDateChip = dialogView.findViewById(R.id.chip_sort_firebase_push_date); // Updated ID

        String currentSortBy = filterManager.getCurrentSortBy();
        if ("name".equals(currentSortBy)) sortNameChip.setChecked(true);
        else if ("submission_date".equals(currentSortBy)) sortSubmissionDateChip.setChecked(true);
        else if ("last_call_date".equals(currentSortBy)) sortLastCallDateChip.setChecked(true);
        else if ("firebase_push_date".equals(currentSortBy)) sortFirebasePushDateChip.setChecked(true);

        // Filter Chips
        Chip filterInterestedChip = dialogView.findViewById(R.id.chip_filter_interested);
        Chip filterNotInterestedChip = dialogView.findViewById(R.id.chip_filter_not_interested);
        Chip filterAdmittedChip = dialogView.findViewById(R.id.chip_filter_admitted);
        Chip filterNotAdmittedChip = dialogView.findViewById(R.id.chip_filter_not_admitted);
        Chip filterCallMadeChip = dialogView.findViewById(R.id.chip_filter_call_made);
        Chip filterCallNotMadeChip = dialogView.findViewById(R.id.chip_filter_call_not_made);

        filterInterestedChip.setChecked(filterManager.isFilterInterested());
        filterNotInterestedChip.setChecked(filterManager.isFilterNotInterested());
        filterAdmittedChip.setChecked(filterManager.isFilterAdmitted());
        filterNotAdmittedChip.setChecked(filterManager.isFilterNotAdmitted());
        filterCallMadeChip.setChecked(filterManager.isFilterCalled());
        filterCallNotMadeChip.setChecked(filterManager.isFilterNotCalled());

        // Date Filters
        RadioGroup dateRadioGroup = dialogView.findViewById(R.id.date_filter_radio_group);
        RadioButton radioAllDates = dialogView.findViewById(R.id.radio_all_dates);
        RadioButton radioToday = dialogView.findViewById(R.id.radio_today);
        RadioButton radioYesterday = dialogView.findViewById(R.id.radio_yesterday);
        RadioButton radioLastWeek = dialogView.findViewById(R.id.radio_last_week);
        RadioButton radioLastMonth = dialogView.findViewById(R.id.radio_last_month);
        RadioButton radioLastCallDate = dialogView.findViewById(R.id.radio_last_call_date); // Updated ID
        RadioButton radioFirebasePush = dialogView.findViewById(R.id.radio_firebase_push);
        RadioButton radioCustomRange = dialogView.findViewById(R.id.radio_custom_range);

        LinearLayout dateRangeLayout = dialogView.findViewById(R.id.date_range_layout);
      //  TextView tvFromDate = dialogView.findViewById(R.id.tv_from_date);
     //   TextView tvToDate = dialogView.findViewById(R.id.tv_to_date);

        switch (filterManager.getDateFilter()) {
            case "today":
                radioToday.setChecked(true);
                break;
            case "yesterday":
                radioYesterday.setChecked(true);
                break;
            case "last_week":
                radioLastWeek.setChecked(true);
                break;
            case "last_month":
                radioLastMonth.setChecked(true);
                break;
            case "last_updated":
                radioLastCallDate.setChecked(true);
                break;
            case "firebase_push":
                radioFirebasePush.setChecked(true);
                break;
            case "custom":
                radioCustomRange.setChecked(true);
                dateRangeLayout.setVisibility(View.VISIBLE);
                if (filterManager.getFromDate() != null)
                 //   tvFromDate.setText(filterManager.displayFormat.format(filterManager.getFromDate()));
                if (filterManager.getToDate() != null)
                 //   tvToDate.setText(filterManager.displayFormat.format(filterManager.getToDate()));
                break;
            default:
                radioAllDates.setChecked(true);
        }

        dateRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            dateRangeLayout.setVisibility(checkedId == R.id.radio_custom_range ? View.VISIBLE : View.GONE);
        });

      //  tvFromDate.setOnClickListener(v -> showDatePickerDialog(tvFromDate, true));
      //  tvToDate.setOnClickListener(v -> showDatePickerDialog(tvToDate, false));

        // Buttons
        Button btnApply = dialogView.findViewById(R.id.btn_apply);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnClear = dialogView.findViewById(R.id.btn_clear_filters); // New button

        btnApply.setOnClickListener(v -> {
            // Apply Sort
            if (sortNameChip.isChecked()) filterManager.setCurrentSortBy("name");
            else if (sortSubmissionDateChip.isChecked()) filterManager.setCurrentSortBy("submission_date");
            else if (sortLastCallDateChip.isChecked()) filterManager.setCurrentSortBy("last_call_date");
            else if (sortFirebasePushDateChip.isChecked()) filterManager.setCurrentSortBy("firebase_push_date");
            else filterManager.setCurrentSortBy(null);

            // Apply Filters
            filterManager.setFilterInterested(filterInterestedChip.isChecked());
            filterManager.setFilterNotInterested(filterNotInterestedChip.isChecked());
            filterManager.setFilterAdmitted(filterAdmittedChip.isChecked());
            filterManager.setFilterNotAdmitted(filterNotAdmittedChip.isChecked());
            filterManager.setFilterCalled(filterCallMadeChip.isChecked());
            filterManager.setFilterNotCalled(filterCallNotMadeChip.isChecked());

            // Apply Date Filter
            String dateFilter;
            if (radioToday.isChecked()) dateFilter = "today";
            else if (radioYesterday.isChecked()) dateFilter = "yesterday";
            else if (radioLastWeek.isChecked()) dateFilter = "last_week";
            else if (radioLastMonth.isChecked()) dateFilter = "last_month";
            else if (radioLastCallDate.isChecked()) dateFilter = "last_updated"; // Matches FilterManager
            else if (radioFirebasePush.isChecked()) dateFilter = "firebase_push";
            else if (radioCustomRange.isChecked()) dateFilter = "custom";
            else dateFilter = "all";

            filterManager.setDateFilter(dateFilter);
            filterManager.setUseCustomDateRange("custom".equals(dateFilter));

            // Apply all filters and sorting
            filterManager.applyFiltersAndSorting();

            if (onApplyCallback != null) {
                onApplyCallback.run();
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnClear.setOnClickListener(v -> {
            // Reset sort
            sortGroup.clearCheck();
            filterManager.setCurrentSortBy(null);

            // Reset filters
            filterInterestedChip.setChecked(false);
            filterNotInterestedChip.setChecked(false);
            filterAdmittedChip.setChecked(false);
            filterNotAdmittedChip.setChecked(false);
            filterCallMadeChip.setChecked(false);
            filterCallNotMadeChip.setChecked(false);
            filterManager.setFilterInterested(false);
            filterManager.setFilterNotInterested(false);
            filterManager.setFilterAdmitted(false);
            filterManager.setFilterNotAdmitted(false);
            filterManager.setFilterCalled(false);
            filterManager.setFilterNotCalled(false);

            // Reset date filter
            dateRadioGroup.check(R.id.radio_all_dates);
            dateRangeLayout.setVisibility(View.GONE);
            //tvFromDate.setText("");
          //  tvToDate.setText("");
            filterManager.setDateFilter("all");
            filterManager.setUseCustomDateRange(false);
            filterManager.setFromDate(null);
            filterManager.setToDate(null);

            // Apply reset
            filterManager.applyFiltersAndSorting();
            if (onApplyCallback != null) {
                onApplyCallback.run();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDatePickerDialog(TextView targetTextView, boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        if ((isFromDate && filterManager.getFromDate() != null) || (!isFromDate && filterManager.getToDate() != null)) {
            calendar.setTime(isFromDate ? filterManager.getFromDate() : filterManager.getToDate());
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                    if (isFromDate) {
                        filterManager.setFromDate(selectedCalendar.getTime());
                    } else {
                        filterManager.setToDate(selectedCalendar.getTime());
                    }
                    targetTextView.setText(filterManager.displayFormat.format(selectedCalendar.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}