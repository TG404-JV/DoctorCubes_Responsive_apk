package com.tvm.doctorcube.university.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.tvm.doctorcube.university.model.University;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private List<University> universities;
    private List<University> originalUniversities;
    private final Context context;
    private final NavController navController;
    private final OnItemClickListener listener;

    public UniversityAdapter(Context context, List<University> universities, OnItemClickListener listener, NavController navController) {
        this.context = context;
        this.universities = new ArrayList<>(universities);
        this.originalUniversities = new ArrayList<>(universities);
        this.listener = listener;
        this.navController = navController;
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_university, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityViewHolder holder, int position) {
        University university = universities.get(position);
        if (university == null) {
            Log.e("UniversityAdapter", "University at position " + position + " is null");
            return;
        }

        // Bind data with validation
        try {
            holder.universityBanner.setImageResource(university.getBannerResourceId() != 0 ? university.getBannerResourceId() : R.drawable.university_campus);
            holder.universityLogo.setImageResource(university.getLogoResourceId() != 0 ? university.getLogoResourceId() : R.drawable.university_campus);
            holder.universityType.setText(university.getUniversityType() != null ? university.getUniversityType() : "N/A");
            holder.countryFlag.setImageResource(university.getFlagResourceId() != 0 ? university.getFlagResourceId() : R.drawable.icon_university);
            holder.countryName.setText(university.getCountry() != null ? university.getCountry() : "N/A");
            holder.universityName.setText(university.getName() != null ? university.getName() : "Unknown University");
            holder.courseName.setText(university.getCourseName() != null ? university.getCourseName() : "N/A");
            holder.degreeType.setText(university.getDegreeType() != null ? university.getDegreeType() : "N/A");
            holder.field.setText(university.getField() != null ? university.getField() : "N/A");
            holder.rankingTag.setText(university.getRanking() != null ? university.getRanking() : "N/A");
            holder.duration.setText(university.getDuration() != null ? university.getDuration() : "N/A");
            holder.grade.setText(university.getGrade() != null ? university.getGrade() : "N/A");
            holder.language.setText(university.getLanguage() != null ? university.getLanguage() : "N/A");
            holder.intake.setText(university.getIntake() != null ? university.getIntake() : "N/A");
            holder.scholarshipText.setText(university.getScholarshipInfo() != null ? university.getScholarshipInfo() : "N/A");
        } catch (Exception e) {
            Log.e("UniversityAdapter", "Error binding data for " + university.getName(), e);
        }

        SocialActions openSocial = new SocialActions();

        // Card click
        holder.university_card_container.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(university);
            }
        });

        // Call Button
        holder.btnCall.setOnClickListener(v -> {
            try {
                openSocial.makeDirectCall(context);
            } catch (Exception e) {
                Log.e("UniversityAdapter", "Error initiating call", e);
                CustomToast.showToast((Activity) context, "Failed to initiate call");
            }
        });

        // WhatsApp Button
        holder.btnWhatsapp.setOnClickListener(v -> {
            try {
                openSocial.openWhatsApp(context);
            } catch (Exception e) {
                Log.e("UniversityAdapter", "Error opening WhatsApp", e);
                CustomToast.showToast((Activity) context, "Failed to open WhatsApp");
            }
        });

        // Apply Button
        holder.btnApply.setOnClickListener(v -> {
            try {
                Bundle args = new Bundle();
                args.putSerializable("UNIVERSITY", university);
                args.putString("universityId", university.getId());
                Log.d("UniversityAdapter", "Navigating to details for: " + university.getName() + ", ID: " + university.getId());
                navController.navigate(R.id.action_universityFragment_to_universityDetailsBottomSheet, args);
            } catch (Exception e) {
                Log.e("UniversityAdapter", "Navigation failed for " + university.getName() + ": " + e.getMessage());
                CustomToast.showToast((Activity) context, "Failed to navigate to university details");
            }
        });

        // Brochure Button
        holder.btnBrochure.setOnClickListener(v -> {
            CustomToast.showToast((Activity) context, "Brochure download not implemented yet");
        });
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    public void filterByName(String query) {
        universities.clear();
        if (query == null || query.trim().isEmpty()) {
            universities.addAll(originalUniversities);
        } else {
            String searchQuery = query.toLowerCase(Locale.getDefault()).trim();
            for (University university : originalUniversities) {
                if (university != null &&
                        ((university.getName() != null && university.getName().toLowerCase(Locale.getDefault()).contains(searchQuery)) ||
                                (university.getCourseName() != null && university.getCourseName().toLowerCase(Locale.getDefault()).contains(searchQuery)))) {
                    universities.add(university);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<University> newUniversities) {
        this.universities.clear();
        this.universities.addAll(newUniversities != null ? newUniversities : new ArrayList<>());
        this.originalUniversities.clear();
        this.originalUniversities.addAll(newUniversities != null ? newUniversities : new ArrayList<>());
        notifyDataSetChanged();
    }

    public void sortByName(final boolean ascending) {
        universities.sort((u1, u2) -> {
            String name1 = u1.getName() != null ? u1.getName() : "";
            String name2 = u2.getName() != null ? u2.getName() : "";
            return ascending ? name1.compareToIgnoreCase(name2) : name2.compareToIgnoreCase(name1);
        });
        notifyDataSetChanged();
    }

    public void sortByGrade(final boolean descending) {
        List<String> gradeOrder = Arrays.asList("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F");
        universities.sort((u1, u2) -> {
            String grade1 = u1.getGrade() != null ? u1.getGrade() : "F";
            String grade2 = u2.getGrade() != null ? u2.getGrade() : "F";
            int index1 = gradeOrder.indexOf(grade1);
            int index2 = gradeOrder.indexOf(grade2);
            if (index1 == -1) index1 = gradeOrder.size();
            if (index2 == -1) index2 = gradeOrder.size();
            return descending ? Integer.compare(index2, index1) : Integer.compare(index1, index2);
        });
        notifyDataSetChanged();
    }

    static class UniversityViewHolder extends RecyclerView.ViewHolder {
        ImageView universityBanner, universityLogo, countryFlag;
        TextView universityType, countryName;
        TextView universityName, courseName, degreeType, field, rankingTag;
        TextView duration, grade, language, intake, scholarshipText;
        Button btnBrochure, btnApply;
        LinearLayout btnWhatsapp, btnCall, university_card_container;

        UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            universityBanner = itemView.findViewById(R.id.university_banner);
            universityLogo = itemView.findViewById(R.id.university_logo);
            universityType = itemView.findViewById(R.id.university_type);
            countryFlag = itemView.findViewById(R.id.country_flag);
            countryName = itemView.findViewById(R.id.country_name);
            universityName = itemView.findViewById(R.id.university_name);
            courseName = itemView.findViewById(R.id.course_name);
            degreeType = itemView.findViewById(R.id.degree_type);
            field = itemView.findViewById(R.id.field);
            rankingTag = itemView.findViewById(R.id.ranking_tag);
            duration = itemView.findViewById(R.id.duration);
            grade = itemView.findViewById(R.id.grade);
            language = itemView.findViewById(R.id.language);
            intake = itemView.findViewById(R.id.intake);
            scholarshipText = itemView.findViewById(R.id.scholarship_text);
            btnBrochure = itemView.findViewById(R.id.btn_brochure);
            btnApply = itemView.findViewById(R.id.btn_apply);
            btnWhatsapp = itemView.findViewById(R.id.btn_whatsapp);
            btnCall = itemView.findViewById(R.id.btn_call);
            university_card_container = itemView.findViewById(R.id.university_card_container);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(University university);
    }
}