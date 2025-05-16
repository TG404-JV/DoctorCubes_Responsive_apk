package com.tvm.doctorcube.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.university.model.University;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.UniversityViewHolder> {

    private final Context context;
    private final List<University> universities;
    private final OnItemClickListener listener;

    public UniversityListAdapter(Context context, List<University> universities, OnItemClickListener listener) {
        this.context = context;
        this.universities = universities;
        this.listener = listener;
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
            listener.onItemClick(university);
        });
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

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

    public interface OnItemClickListener {
        void onItemClick(University university);
    }
}