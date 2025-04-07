package com.tvm.doctorcube.home.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.UniversityDetailsActivity;
import com.tvm.doctorcube.university.model.University;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.UniversityViewHolder> {

    private Context context;
    private List<University> universities;

    public UniversityListAdapter(Context context, List<University> universities) {
        this.context = context;
        this.universities = universities;
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_universities_home, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityViewHolder holder, int position) {
        University university = universities.get(position);

        holder.flagImageView.setImageResource(university.getLogoResourceId());
        holder.nameTextView.setText(university.getName());
        holder.locationTextView.setText(String.format("%s, %s", university.getLocation(), university.getCountry()));
        holder.courseTextView.setText(university.getCourseName());
        holder.gradeTextView.setText(university.getGrade());

        holder.viewDetailsButton.setOnClickListener(v -> {
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
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    // Method to update data if needed
    public void updateData(List<University> newUniversities) {
        this.universities.clear();
        this.universities.addAll(newUniversities);
        notifyDataSetChanged();
    }

    static class UniversityViewHolder extends RecyclerView.ViewHolder {
        ImageView flagImageView;
        TextView nameTextView;
        TextView locationTextView;
        TextView courseTextView;
        TextView gradeTextView;
        MaterialButton viewDetailsButton;

        UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            flagImageView = itemView.findViewById(R.id.university_flag);
            nameTextView = itemView.findViewById(R.id.university_name);
            locationTextView = itemView.findViewById(R.id.university_location);
            courseTextView = itemView.findViewById(R.id.university_course);
            gradeTextView = itemView.findViewById(R.id.university_grade);
            viewDetailsButton = itemView.findViewById(R.id.btn_view_details);
        }
    }
}