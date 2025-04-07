package com.tvm.doctorcube.adminpannel;


import android.app.Activity;
import android.content.Context;

import com.tvm.doctorcube.CustomToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentSorter {
    private SimpleDateFormat dateFormat;
    private Context context;

    public StudentSorter(SimpleDateFormat dateFormat, Context context) {
        this.dateFormat = dateFormat;
        this.context = context;
    }

    public void sortStudents(List<Student> students, String sortBy) {
        if (students == null || students.isEmpty()) {
            if (context != null) {
                CustomToast.showToast((Activity) context, "No students to sort.");
            }
            return;
        }

        Comparator<Student> comparator;
        switch (sortBy) {
            case "name_asc":
                comparator = (s1, s2) -> {
                    String name1 = s1.getName() != null ? s1.getName() : "";
                    String name2 = s2.getName() != null ? s2.getName() : "";
                    return name1.compareToIgnoreCase(name2);
                };
                break;

            case "name_desc":
                comparator = (s1, s2) -> {
                    String name1 = s1.getName() != null ? s1.getName() : "";
                    String name2 = s2.getName() != null ? s2.getName() : "";
                    return name2.compareToIgnoreCase(name1);
                };
                break;

            case "date_newest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100"; // Default to oldest possible date
                        String date2Str = s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date2Str).compareTo(dateFormat.parse(date1Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by submission date: " + e.getMessage());
                        }
                        return 0;
                    }
                };
                break;

            case "date_oldest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date1Str).compareTo(dateFormat.parse(date2Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by submission date" );
                        }
                        return 0;
                    }
                };
                break;

            case "last_updated_newest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getLastUpdatedDate() != null ? s1.getLastUpdatedDate() : s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getLastUpdatedDate() != null ? s2.getLastUpdatedDate() : s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date2Str).compareTo(dateFormat.parse(date1Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by last updated date" );
                        }
                        return 0;
                    }
                };
                break;

            case "last_updated_oldest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getLastUpdatedDate() != null ? s1.getLastUpdatedDate() : s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getLastUpdatedDate() != null ? s2.getLastUpdatedDate() : s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date1Str).compareTo(dateFormat.parse(date2Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by last updated date" );
                        }
                        return 0;
                    }
                };
                break;

            case "firebase_push_newest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getFirebasePushDate() != null ? s1.getFirebasePushDate() : s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getFirebasePushDate() != null ? s2.getFirebasePushDate() : s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date2Str).compareTo(dateFormat.parse(date1Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by Firebase push date");
                        }
                        return 0;
                    }
                };
                break;

            case "firebase_push_oldest":
                comparator = (s1, s2) -> {
                    try {
                        String date1Str = s1.getFirebasePushDate() != null ? s1.getFirebasePushDate() : s1.getSubmissionDate() != null ? s1.getSubmissionDate() : "010100";
                        String date2Str = s2.getFirebasePushDate() != null ? s2.getFirebasePushDate() : s2.getSubmissionDate() != null ? s2.getSubmissionDate() : "010100";
                        return dateFormat.parse(date1Str).compareTo(dateFormat.parse(date2Str));
                    } catch (ParseException e) {
                        if (context != null) {
                            CustomToast.showToast((Activity) context, "Error sorting by Firebase push date");
                        }
                        return 0;
                    }
                };
                break;

            default:
                if (context != null) {
                    CustomToast.showToast((Activity) context, "Invalid sort option");
                }
                return;
        }

        Collections.sort(students, comparator);
    }
}
