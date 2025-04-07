package com.tvm.doctorcube.university.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.tvm.doctorcube.UniversityDetailsActivity;
import com.tvm.doctorcube.university.ApplyBottomSheetFragment;
import com.tvm.doctorcube.university.model.University;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private List<University> universities;
    private List<University> originalUniversities;
    private Context context;
    private String countryFilter;

    public UniversityAdapter(Context context, List<University> universities, String countryFilter) {
        this.context = context;
        this.universities = new ArrayList<>(universities);
        this.originalUniversities = new ArrayList<>(universities);
        this.countryFilter = countryFilter;
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

        // Bind data (keeping existing bindings)
        holder.universityBanner.setImageResource(university.getBannerResourceId());
        holder.universityLogo.setImageResource(university.getLogoResourceId());
        holder.universityType.setText(university.getUniversityType());
        holder.countryFlag.setImageResource(university.getFlagResourceId());
        holder.countryName.setText(university.getCountry());
        holder.universityName.setText(university.getName());
        holder.courseName.setText(university.getCourseName());
        holder.degreeType.setText(university.getDegreeType());
        holder.field.setText(university.getField());
        holder.rankingTag.setText(university.getRanking());
        holder.duration.setText(university.getDuration());
        holder.grade.setText(university.getGrade());
        holder.language.setText(university.getLanguage());
        holder.intake.setText(university.getIntake());
        holder.scholarshipText.setText(university.getScholarshipInfo());
        SocialActions openSocial = new SocialActions();


        holder.university_card_container.setOnClickListener(v -> {
            Intent intent = new Intent(context, UniversityDetailsActivity.class);
            intent.putExtra("UNIVERSITY_ID", university.getId());
            intent.putExtra("UNIVERSITY_NAME", university.getName());
            intent.putExtra("UNIVERSITY_LOCATION", university.getLocation());
            intent.putExtra("UNIVERSITY_COUNTRY", university.getCountry());
            intent.putExtra("UNIVERSITY_COURSE", university.getCourseName());
            intent.putExtra("UNIVERSITY_DEGREE", university.getDegreeType());
            intent.putExtra("UNIVERSITY_DURATION", university.getDuration());
            intent.putExtra("UNIVERSITY_GRADE", university.getGrade());
            intent.putExtra("UNIVERSITY_INTAKE", university.getIntake());
            context.startActivity(intent);
        });


        // Call Button - Initiates phone call
        holder.btnCall.setOnClickListener(v -> {
            openSocial.makeDirectCall(context);
        });

        // WhatsApp Button - Opens WhatsApp with pre-filled message
        holder.btnWhatsapp.setOnClickListener(v -> {
                openSocial.openWhatsApp(context);
        });

        // Apply Button - Opens Bottom Sheet Dialog
        // In UniversityAdapter.java, update the btnApply click listener:
        holder.btnApply.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            ApplyBottomSheetFragment bottomSheet = new ApplyBottomSheetFragment(university);
            Bundle args = new Bundle();
            args.putString("event_title", university.getName());
            bottomSheet.show(fragmentManager, "ApplyBottomSheet");
        });

        // Brochure Button (keeping as placeholder)
        holder.btnBrochure.setOnClickListener(v -> {
            // Implement brochure download logic here
            CustomToast.showToast((Activity) context, "Brochure download not implemented yet");
        });
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    // Search/Filter by name
    public void filterByName(String query) {
        universities.clear();
        if (query == null || query.trim().isEmpty()) {
            universities.addAll(originalUniversities);
        } else {
            String searchQuery = query.toLowerCase(Locale.getDefault()).trim();
            for (University university : originalUniversities) {
                if (university.getName().toLowerCase(Locale.getDefault()).contains(searchQuery) ||
                        university.getCourseName().toLowerCase(Locale.getDefault()).contains(searchQuery)) {
                    universities.add(university);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Existing sort/filter methods remain unchanged
    // 🔹 Update the adapter's data with a new list
    public void updateData(List<University> newUniversities) {
        this.universities.clear();
        this.universities.addAll(newUniversities);
        this.originalUniversities.clear();
        this.originalUniversities.addAll(newUniversities); // Sync originalUniversities with filtered list
        notifyDataSetChanged();
    }

    // 🔹 Sort universities by name (A-Z or Z-A)
    public void sortByName(final boolean ascending) {
        Collections.sort(universities, (u1, u2) -> ascending
                ? u1.getName().compareToIgnoreCase(u2.getName())
                : u2.getName().compareToIgnoreCase(u1.getName()));
        notifyDataSetChanged();
    }

    // 🔹 Sort universities by grade (High-Low or Low-High)
    public void sortByGrade(final boolean descending) {
        List<String> gradeOrder = Arrays.asList("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F");

        universities.sort((u1, u2) -> {
            int index1 = gradeOrder.indexOf(u1.getGrade());
            int index2 = gradeOrder.indexOf(u2.getGrade());

            if (index1 == -1) index1 = gradeOrder.size(); // If not found, push to the end
            if (index2 == -1) index2 = gradeOrder.size();

            return descending ? Integer.compare(index2, index1) : Integer.compare(index1, index2);
        });

        notifyDataSetChanged();
    }

    // 🔹 Filter universities by country (not used directly now, but kept for potential reuse)
    public void filterByCountry(String country) {
        universities.clear();
        if (country.equals("All")) {
            universities.addAll(originalUniversities);
        } else {
            for (University university : originalUniversities) {
                if (university.getCountry().equalsIgnoreCase(country)) {
                    universities.add(university);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resetFilters() {
        // Restore original data
        this.universities.clear();
        this.universities.addAll(originalUniversities);

        // Notify adapter of data change
        notifyDataSetChanged();
    }



    static class UniversityViewHolder extends RecyclerView.ViewHolder {
        // Keep existing view declarations
        ImageView universityBanner, universityLogo, countryFlag;
        TextView universityType, countryName;
        TextView universityName, courseName, degreeType, field, rankingTag;
        TextView duration, grade, language, intake, scholarshipText;
        Button btnBrochure, btnApply;
        LinearLayout btnWhatsapp, btnCall,university_card_container;

        UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            // Keep existing view bindings
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
            university_card_container =itemView.findViewById(R.id.university_card_container);
        }
    }
}