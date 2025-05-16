package com.tvm.doctorcube.adminpannel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.adminpannel.databsemanager.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StudentSorter {
    private static final String TAG = "StudentSorter";
    private final SimpleDateFormat dateFormat; // ddMMyy for submissionDate, firebasePushDate
    private final SimpleDateFormat displayFormat; // dd/MM/yy for lastCallDate
    private final Context context;

    public StudentSorter(SimpleDateFormat dateFormat, Context context) {
        this.dateFormat = dateFormat;
        this.displayFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        this.context = context;
    }

    public void sortStudents(List<Student> students, String sortBy) {
        if (students == null || students.isEmpty()) {
            if (context != null) {
                CustomToast.showToast((Activity) context, "No students to sort.");
            }
            Log.w(TAG, "No students provided for sorting");
            return;
        }

        if (sortBy == null) {
            Log.w(TAG, "Sort criteria is null");
            return;
        }

        Comparator<Student> comparator;
        switch (sortBy.toLowerCase(Locale.getDefault())) {
            case "name":
                comparator = (s1, s2) -> {
                    String name1 = s1.getName() != null ? s1.getName() : "";
                    String name2 = s2.getName() != null ? s2.getName() : "";
                    return name1.compareToIgnoreCase(name2);
                };
                break;

            case "submission_date":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date2Str).compareTo(dateFormat.parse(date1Str)); // Newest first
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by submission date");
                        }
                        Log.w(TAG, "Error parsing submission dates", e);
                        return 0;
                    }
                };
                break;

            case "last_call_date":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getLastCallDate() != null ? s1.getLastCallDate() : "01/01/00";
                        String date2Str = s2.getLastCallDate() != null ? s2.getLastCallDate() : "01/01/00";
                        return displayFormat.parse(date2Str).compareTo(displayFormat.parse(date1Str)); // Newest first
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by last call date");
                        }
                        Log.w(TAG, "Error parsing last call dates", e);
                        return 0;
                    }
                };
                break;

            case "firebase_push_date":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getFirebasePushDate() != null ? s1.getFirebasePushDate() : "010100";
                        String date2Str = s2.getFirebasePushDate() != null ? s2.getFirebasePushDate() : "010100";
                        return dateFormat.parse(date2Str).compareTo(dateFormat.parse(date1Str)); // Newest first
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by Firebase push date");
                        }
                        Log.w(TAG, "Error parsing Firebase push dates", e);
                        return 0;
                    }
                };
                break;

            default:
                if (context != null) {
                    CustomToast.showToast((Activity) context, "Invalid sort option: " + sortBy);
                }
                Log.w(TAG, "Invalid sort criteria: " + sortBy);
                return;
        }

        Collections.sort(students, comparator);
        Log.d(TAG, "Sorted " + students.size() + " students by " + sortBy);
    }
}