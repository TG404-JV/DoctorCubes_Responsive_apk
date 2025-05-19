package com.tvm.doctorcube.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Country;

import java.util.List;
import java.util.Locale;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private final List<Country> countries;
    private final OnCountryClickListener listener;

    public CountryAdapter(List<Country> countries, OnCountryClickListener listener) {
        this.countries = countries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country_list, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.countryName.setText(country.getName());
        holder.universityCount.setText(String.format(Locale.getDefault(), "%d Universities", country.getUniversities().size()));
        holder.countryRating.setText(String.format(Locale.getDefault(), "%.1f", country.getAverageRating()));
        holder.countryFlag.setImageResource(country.getFlagResourceId());
        holder.cardView.setOnClickListener(v -> listener.onCountryClick(country));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    static class CountryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView countryFlag;
        TextView countryName, universityCount, countryRating;

        CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.country_card);
            countryFlag = itemView.findViewById(R.id.country_flag);
            countryName = itemView.findViewById(R.id.country_name);
            universityCount = itemView.findViewById(R.id.university_count);
            countryRating = itemView.findViewById(R.id.country_rating);
        }
    }

    public interface OnCountryClickListener {
        void onCountryClick(Country country);
    }
}