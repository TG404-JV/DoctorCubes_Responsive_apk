package com.tvm.doctorcube.adminpannel;

import android.content.Context;
import android.util.Log;

import com.tvm.doctorcube.adminpannel.databsemanager.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilterManager {
    private static final String TAG = "FilterManager";

    private List<Student> studentList;
    private final List<Student> filteredList;
    private final StudentSorter sorter;
    private final SimpleDateFormat dateFormat; // Input format: ddMMyy
    public final SimpleDateFormat displayFormat; // Display format: dd/MM/yy
    private final Context context;

    private String currentSortBy = null;
    private boolean filterInterested, filterNotInterested, filterAdmitted, filterNotAdmitted, filterCalled, filterNotCalled;
    private String dateFilter = "all";
    private String searchQuery = "";
    private boolean useCustomDateRange = false;
    private Date fromDate = null;
    private Date toDate = null;

    public FilterManager(List<Student> studentList, List<Student> filteredList, StudentSorter sorter,
                         SimpleDateFormat dateFormat, SimpleDateFormat displayFormat, Context context) {
        this.studentList = studentList;
        this.filteredList = filteredList;
        this.sorter = sorter;
        this.dateFormat = dateFormat;
        this.displayFormat = displayFormat;
        this.context = context;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void applyFiltersAndSorting() {
        filteredList.clear();

        Calendar todayCal = Calendar.getInstance();
        String today = displayFormat.format(todayCal.getTime());
        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DAY_OF_MONTH, -1);
        String yesterday = displayFormat.format(yesterdayCal.getTime());

        Calendar weekCal = Calendar.getInstance();
        weekCal.add(Calendar.DAY_OF_MONTH, -7);
        Date weekAgo = weekCal.getTime();

        Calendar monthCal = Calendar.getInstance();
        monthCal.add(Calendar.MONTH, -1);
        Date monthAgo = monthCal.getTime();

        for (Student student : studentList) {
            boolean matchesFilter = true;

            // Search (unchanged)
            if (!searchQuery.isEmpty()) {
                String queryLower = searchQuery.toLowerCase(Locale.getDefault());
                boolean matchesSearch = (student.getName() != null && student.getName().toLowerCase(Locale.getDefault()).contains(queryLower)) ||
                        (student.getMobile() != null && student.getMobile().toLowerCase(Locale.getDefault()).contains(queryLower));
                if (!matchesSearch) {
                    matchesFilter = false;
                }
            }

            // Filters (unchanged)
            if (filterInterested && !student.isInterested()) matchesFilter = false;
            if (filterNotInterested && student.isInterested()) matchesFilter = false;
            if (filterAdmitted && !student.isAdmitted()) matchesFilter = false;
            if (filterNotAdmitted && student.isAdmitted()) matchesFilter = false;
            if (filterCalled && (student.getCallStatus() == null || !student.getCallStatus().equalsIgnoreCase("called")))
                matchesFilter = false;
            if (filterNotCalled && (student.getCallStatus() != null && student.getCallStatus().equalsIgnoreCase("called")))
                matchesFilter = false;

            // Date Filters
            try {
                Date submissionDate = null;
                Date lastCallDate = null;
                Date firebasePushDate = null;

                if (student.getSubmissionDate() != null && !student.getSubmissionDate().isEmpty()) {
                    try {
                        submissionDate = dateFormat.parse(student.getSubmissionDate());
                    } catch (ParseException e) {
                        Log.w(TAG, "Failed to parse submissionDate: " + student.getSubmissionDate(), e);
                    }
                }
                if (student.getLastCallDate() != null && !student.getLastCallDate().isEmpty()) {
                    // Check if lastCallDate is a valid date string
                    if (!student.getLastCallDate().equals("Not Called Yet")) {
                        try {
                            lastCallDate = displayFormat.parse(student.getLastCallDate());
                        } catch (ParseException e) {
                            Log.w(TAG, "Failed to parse lastCallDate: " + student.getLastCallDate(), e);
                        }
                    }
                }
                if (student.getFirebasePushDate() != null && !student.getFirebasePushDate().isEmpty()) {
                    try {
                        firebasePushDate = dateFormat.parse(student.getFirebasePushDate());
                    } catch (ParseException e) {
                        Log.w(TAG, "Failed to parse firebasePushDate: " + student.getFirebasePushDate(), e);
                    }
                }

                switch (dateFilter) {
                    case "today":
                        if (submissionDate == null || !today.equals(displayFormat.format(submissionDate)))
                            matchesFilter = false;
                        break;
                    case "yesterday":
                        if (submissionDate == null || !yesterday.equals(displayFormat.format(submissionDate)))
                            matchesFilter = false;
                        break;
                    case "last_week":
                        if (submissionDate == null || submissionDate.before(weekAgo)) matchesFilter = false;
                        break;
                    case "last_month":
                        if (submissionDate == null || submissionDate.before(monthAgo)) matchesFilter = false;
                        break;
                    case "last_updated":
                        if (lastCallDate == null || !today.equals(displayFormat.format(lastCallDate)))
                            matchesFilter = false;
                        break;
                    case "firebase_push":
                        if (firebasePushDate == null || !today.equals(displayFormat.format(firebasePushDate)))
                            matchesFilter = false;
                        break;
                    case "custom":
                        if (useCustomDateRange && fromDate != null && toDate != null) {
                            Calendar toCal = Calendar.getInstance();
                            toCal.setTime(toDate);
                            toCal.set(Calendar.HOUR_OF_DAY, 23);
                            toCal.set(Calendar.MINUTE, 59);
                            toCal.set(Calendar.SECOND, 59);
                            if (submissionDate == null || submissionDate.before(fromDate) || submissionDate.after(toCal.getTime()))
                                matchesFilter = false;
                        }
                        break;
                    case "all":
                        break;
                    default:
                        Log.w(TAG, "Unknown date filter: " + dateFilter);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing date filters for student: " + student.getId(), e);
                matchesFilter = false;
            }

            if (matchesFilter) {
                filteredList.add(student);
            }
        }

        // Sorting (unchanged)
        if (currentSortBy != null) {
            sorter.sortStudents(filteredList, currentSortBy);
        }
    }
    // Getters and Setters
    public String getCurrentSortBy() {
        return currentSortBy;
    }

    public void setCurrentSortBy(String sortBy) {
        this.currentSortBy = sortBy;
    }

    public boolean isFilterInterested() {
        return filterInterested;
    }

    public void setFilterInterested(boolean filterInterested) {
        this.filterInterested = filterInterested;
    }

    public boolean isFilterNotInterested() {
        return filterNotInterested;
    }

    public void setFilterNotInterested(boolean filterNotInterested) {
        this.filterNotInterested = filterNotInterested;
    }

    public boolean isFilterAdmitted() {
        return filterAdmitted;
    }

    public void setFilterAdmitted(boolean filterAdmitted) {
        this.filterAdmitted = filterAdmitted;
    }

    public boolean isFilterNotAdmitted() {
        return filterNotAdmitted;
    }

    public void setFilterNotAdmitted(boolean filterNotAdmitted) {
        this.filterNotAdmitted = filterNotAdmitted;
    }

    public boolean isFilterCalled() {
        return filterCalled;
    }

    public void setFilterCalled(boolean filterCalled) {
        this.filterCalled = filterCalled;
    }

    public boolean isFilterNotCalled() {
        return filterNotCalled;
    }

    public void setFilterNotCalled(boolean filterNotCalled) {
        this.filterNotCalled = filterNotCalled;
    }

    public String getDateFilter() {
        return dateFilter;
    }

    public void setDateFilter(String dateFilter) {
        this.dateFilter = dateFilter;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setUseCustomDateRange(boolean useCustomDateRange) {
        this.useCustomDateRange = useCustomDateRange;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}