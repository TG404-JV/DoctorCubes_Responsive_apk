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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.tvm.doctorcube.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        // Sort Chips (Single selection as per XML)
        ChipGroup sortGroup = dialogView.findViewById(R.id.sort_chip_group);
        Chip sortNameChip = dialogView.findViewById(R.id.chip_sort_name);
        Chip sortSubmissionDateChip = dialogView.findViewById(R.id.chip_sort_submission_date);
        Chip sortLastCallDateChip = dialogView.findViewById(R.id.chip_sort_last_call_date);
        Chip sortFirebasePushDateChip = dialogView.findViewById(R.id.chip_sort_firebase_push_date);

        List<String> currentSortBy = filterManager.getCurrentSortByList();
        if (currentSortBy != null && !currentSortBy.isEmpty()) {
            String sort = currentSortBy.get(0); // Take first item since XML enforces single selection
            if ("name".equals(sort)) sortNameChip.setChecked(true);
            else if ("submission_date".equals(sort)) sortSubmissionDateChip.setChecked(true);
            else if ("last_call_date".equals(sort)) sortLastCallDateChip.setChecked(true);
            else if ("firebase_push_date".equals(sort)) sortFirebasePushDateChip.setChecked(true);
        }

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

        // Date Filters (Single selection via RadioGroup)
        RadioGroup dateRadioGroup = dialogView.findViewById(R.id.date_filter_radio_group);
        RadioButton radioAllDates = dialogView.findViewById(R.id.radio_all_dates);
        RadioButton radioToday = dialogView.findViewById(R.id.radio_today);
        RadioButton radioYesterday = dialogView.findViewById(R.id.radio_yesterday);
        RadioButton radioLastWeek = dialogView.findViewById(R.id.radio_last_week);
        RadioButton radioLastMonth = dialogView.findViewById(R.id.radio_last_month);
        RadioButton radioLastCallDate = dialogView.findViewById(R.id.radio_last_call_date);
        RadioButton radioFirebasePush = dialogView.findViewById(R.id.radio_firebase_push);
        RadioButton radioCustomRange = dialogView.findViewById(R.id.radio_custom_range);

        LinearLayout dateRangeLayout = dialogView.findViewById(R.id.date_range_layout);
        TextInputEditText etFromDate = dialogView.findViewById(R.id.et_from_date);
        TextInputEditText etToDate = dialogView.findViewById(R.id.et_to_date);

        List<String> currentDateFilters = filterManager.getDateFilters();
        String dateFilter = currentDateFilters != null && !currentDateFilters.isEmpty() ? currentDateFilters.get(0) : "all";
        switch (dateFilter) {
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
                    etFromDate.setText(filterManager.displayFormat.format(filterManager.getFromDate()));
                if (filterManager.getToDate() != null)
                    etToDate.setText(filterManager.displayFormat.format(filterManager.getToDate()));
                break;
            default:
                radioAllDates.setChecked(true);
        }

        dateRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            dateRangeLayout.setVisibility(checkedId == R.id.radio_custom_range ? View.VISIBLE : View.GONE);
        });

        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate, true));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate, false));

        // Buttons
        Button btnApply = dialogView.findViewById(R.id.btn_apply);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnClear = dialogView.findViewById(R.id.btn_clear_filters);

        btnApply.setOnClickListener(v -> {
            // Apply Sort (Single selection)
            List<String> sortCriteria = new ArrayList<>();
            if (sortNameChip.isChecked()) sortCriteria.add("name");
            else if (sortSubmissionDateChip.isChecked()) sortCriteria.add("submission_date");
            else if (sortLastCallDateChip.isChecked()) sortCriteria.add("last_call_date");
            else if (sortFirebasePushDateChip.isChecked()) sortCriteria.add("firebase_push_date");
            filterManager.setCurrentSortByList(sortCriteria.isEmpty() ? null : sortCriteria);

            // Apply Filters
            filterManager.setFilterInterested(filterInterestedChip.isChecked());
            filterManager.setFilterNotInterested(filterNotInterestedChip.isChecked());
            filterManager.setFilterAdmitted(filterAdmittedChip.isChecked());
            filterManager.setFilterNotAdmitted(filterNotAdmittedChip.isChecked());
            filterManager.setFilterCalled(filterCallMadeChip.isChecked());
            filterManager.setFilterNotCalled(filterCallNotMadeChip.isChecked());

            // Apply Date Filter (Single selection)
            List<String> dateFilters = new ArrayList<>();
            String selectedDateFilter;
            if (radioToday.isChecked()) selectedDateFilter = "today";
            else if (radioYesterday.isChecked()) selectedDateFilter = "yesterday";
            else if (radioLastWeek.isChecked()) selectedDateFilter = "last_week";
            else if (radioLastMonth.isChecked()) selectedDateFilter = "last_month";
            else if (radioLastCallDate.isChecked()) selectedDateFilter = "last_updated";
            else if (radioFirebasePush.isChecked()) selectedDateFilter = "firebase_push";
            else if (radioCustomRange.isChecked()) selectedDateFilter = "custom";
            else selectedDateFilter = "all";
            dateFilters.add(selectedDateFilter);
            filterManager.setDateFilters(dateFilters);
            filterManager.setUseCustomDateRange("custom".equals(selectedDateFilter));

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
            filterManager.setCurrentSortByList(null);

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
            etFromDate.setText("");
            etToDate.setText("");
            List<String> resetDateFilters = new ArrayList<>();
            resetDateFilters.add("all");
            filterManager.setDateFilters(resetDateFilters);
            filterManager.setUseCustomDateRange(false);
            filterManager.setFromDate(null);
            filterManager.setToDate(null);

            filterManager.applyFiltersAndSorting();
            if (onApplyCallback != null) {
                onApplyCallback.run();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDatePickerDialog(TextInputEditText targetEditText, boolean isFromDate) {
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
                    targetEditText.setText(filterManager.displayFormat.format(selectedCalendar.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}