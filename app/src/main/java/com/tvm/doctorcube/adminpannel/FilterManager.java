package com.tvm.doctorcube.adminpannel;

import android.content.Context;

import com.tvm.doctorcube.adminpannel.databsemanager.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilterManager {
    private List<Student> studentList;
    private List<Student> filteredList;
    private List<String> currentSortBy;
    private List<String> dateFilters;
    private boolean filterInterested, filterNotInterested, filterAdmitted, filterNotAdmitted, filterCalled, filterNotCalled;
    private boolean useCustomDateRange;
    private Date fromDate, toDate;
    private String searchQuery = "";
    private final SimpleDateFormat dateFormat; // Added for parsing
    public final SimpleDateFormat displayFormat; // For displaying dates
    private final StudentSorter sorter;
    private final Context context; // Added for Context

    // Updated constructor
    public FilterManager(List<Student> studentList, List<Student> filteredList, StudentSorter sorter,
                         SimpleDateFormat dateFormat, SimpleDateFormat displayFormat, Context context) {
        this.studentList = studentList;
        this.filteredList = filteredList != null ? filteredList : new ArrayList<>(); // Initialize if null
        this.currentSortBy = new ArrayList<>();
        this.dateFilters = new ArrayList<>();
        this.sorter = sorter;
        this.dateFormat = dateFormat;
        this.displayFormat = displayFormat;
        this.context = context;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
    }

    public List<String> getCurrentSortByList() {
        return currentSortBy;
    }

    public void setCurrentSortByList(List<String> sortBy) {
        this.currentSortBy = sortBy != null ? new ArrayList<>(sortBy) : new ArrayList<>();
    }

    public List<String> getDateFilters() {
        return dateFilters;
    }

    public void setDateFilters(List<String> filters) {
        this.dateFilters = filters != null ? new ArrayList<>(filters) : new ArrayList<>();
    }

    public void setFilterInterested(boolean filterInterested) { this.filterInterested = filterInterested; }
    public boolean isFilterInterested() { return filterInterested; }
    public void setFilterNotInterested(boolean filterNotInterested) { this.filterNotInterested = filterNotInterested; }
    public boolean isFilterNotInterested() { return filterNotInterested; }
    public void setFilterAdmitted(boolean filterAdmitted) { this.filterAdmitted = filterAdmitted; }
    public boolean isFilterAdmitted() { return filterAdmitted; }
    public void setFilterNotAdmitted(boolean filterNotAdmitted) { this.filterNotAdmitted = filterNotAdmitted; }
    public boolean isFilterNotAdmitted() { return filterNotAdmitted; }
    public void setFilterCalled(boolean filterCalled) { this.filterCalled = filterCalled; }
    public boolean isFilterCalled() { return filterCalled; }
    public void setFilterNotCalled(boolean filterNotCalled) { this.filterNotCalled = filterNotCalled; }
    public boolean isFilterNotCalled() { return filterNotCalled; }
    public void setUseCustomDateRange(boolean useCustomDateRange) { this.useCustomDateRange = useCustomDateRange; }
    public Date getFromDate() { return fromDate; }
    public void setFromDate(Date fromDate) { this.fromDate = fromDate; }
    public Date getToDate() { return toDate; }
    public void setToDate(Date toDate) { this.toDate = toDate; }

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

            // Search
            if (!searchQuery.isEmpty()) {
                String queryLower = searchQuery.toLowerCase(Locale.getDefault());
                boolean matchesSearch = (student.getName() != null && student.getName().toLowerCase(Locale.getDefault()).contains(queryLower)) ||
                        (student.getMobile() != null && student.getMobile().toLowerCase(Locale.getDefault()).contains(queryLower));
                if (!matchesSearch) matchesFilter = false;
            }

            // Status Filters
            if (filterInterested && !student.isInterested()) matchesFilter = false;
            if (filterNotInterested && student.isInterested()) matchesFilter = false;
            if (filterAdmitted && !student.isAdmitted()) matchesFilter = false;
            if (filterNotAdmitted && student.isAdmitted()) matchesFilter = false;
            if (filterCalled && (student.getCallStatus() == null || !student.getCallStatus().equalsIgnoreCase("called")))
                matchesFilter = false;
            if (filterNotCalled && (student.getCallStatus() != null && student.getCallStatus().equalsIgnoreCase("called")))
                matchesFilter = false;

            // Date Filters
            if (!dateFilters.isEmpty()) {
                boolean matchesAnyDateFilter = false;
                for (String dateFilter : dateFilters) {
                    try {
                        Date submissionDate = student.getSubmissionDate() != null && !student.getSubmissionDate().isEmpty() ?
                                dateFormat.parse(student.getSubmissionDate()) : null; // Use dateFormat here
                        Date lastCallDate = student.getLastCallDate() != null && !student.getLastCallDate().isEmpty() &&
                                !student.getLastCallDate().equals("Not Called Yet") ? dateFormat.parse(student.getLastCallDate()) : null;
                        Date firebasePushDate = student.getFirebasePushDate() != null && !student.getFirebasePushDate().isEmpty() ?
                                dateFormat.parse(student.getFirebasePushDate()) : null;

                        switch (dateFilter) {
                            case "today":
                                if (submissionDate != null && today.equals(displayFormat.format(submissionDate)))
                                    matchesAnyDateFilter = true;
                                break;
                            case "yesterday":
                                if (submissionDate != null && yesterday.equals(displayFormat.format(submissionDate)))
                                    matchesAnyDateFilter = true;
                                break;
                            case "last_week":
                                if (submissionDate != null && !submissionDate.before(weekAgo))
                                    matchesAnyDateFilter = true;
                                break;
                            case "last_month":
                                if (submissionDate != null && !submissionDate.before(monthAgo))
                                    matchesAnyDateFilter = true;
                                break;
                            case "last_updated":
                                if (lastCallDate != null && today.equals(displayFormat.format(lastCallDate)))
                                    matchesAnyDateFilter = true;
                                break;
                            case "firebase_push":
                                if (firebasePushDate != null && today.equals(displayFormat.format(firebasePushDate)))
                                    matchesAnyDateFilter = true;
                                break;
                            case "custom":
                                if (useCustomDateRange && fromDate != null && toDate != null && submissionDate != null) {
                                    Calendar toCal = Calendar.getInstance();
                                    toCal.setTime(toDate);
                                    toCal.set(Calendar.HOUR_OF_DAY, 23);
                                    toCal.set(Calendar.MINUTE, 59);
                                    toCal.set(Calendar.SECOND, 59);
                                    if (!submissionDate.before(fromDate) && !submissionDate.after(toCal.getTime()))
                                        matchesAnyDateFilter = true;
                                }
                                break;
                            case "all":
                                matchesAnyDateFilter = true;
                                break;
                        }
                    } catch (Exception e) {
                        // Log error if needed
                    }
                }
                if (!matchesAnyDateFilter) matchesFilter = false;
            }

            if (matchesFilter) {
                filteredList.add(student);
            }
        }

        if (currentSortBy != null && !currentSortBy.isEmpty()) {
            for (String sortBy : currentSortBy) {
                sorter.sortStudents(filteredList, sortBy);
            }
        }
    }

    public List<Student> getFilteredList() {
        return filteredList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
        filteredList.clear(); // Reset filtered list when student list changes
        applyFiltersAndSorting(); // Optional: reapply filters immediately
    }
}