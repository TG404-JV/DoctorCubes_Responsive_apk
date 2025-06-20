package com.tvm.doctorcube.home.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Feature;

import java.util.List;

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.FeatureViewHolder> {

    private List<Feature> featureList;
    private OnFeatureClickListener listener;
    private final RecyclerView.RecycledViewPool viewPool;

    public interface OnFeatureClickListener {
        void onFeatureClick(Feature feature);
    }

    public FeaturesAdapter(List<Feature> featureList, OnFeatureClickListener listener) {
        this.featureList = featureList;
        this.listener = listener;
        this.viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_item, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        if (featureList == null || position >= featureList.size()) {
            return; // Prevent crashes if list is null or position is invalid
        }

        Feature feature = featureList.get(position);

        // Bind icon
        if (feature.getIconResource() != 0) {
            holder.iconView.setImageResource(feature.getIconResource());
        } else {
            holder.iconView.setImageDrawable(null); // Clear if no valid resource
        }

        // Handle background color for icon
        // Note: XML uses @drawable/circle_background, so we tint it with the color
        if (feature.getIconBackgroundColor() != 0) {
            holder.iconView.setBackgroundTintList(
                    ColorStateList.valueOf(feature.getIconBackgroundColor())
            );
        } else {
            // Fallback to default background if no color is provided
            holder.iconView.setBackgroundResource(R.drawable.circle_background);
        }

        // Bind title
        holder.titleView.setText(feature.getTitle() != null ? feature.getTitle() : "");

        // Bind description
        holder.descriptionView.setText(feature.getDescription() != null ? feature.getDescription() : "");

        // Set click listener on the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeatureClick(feature);
            }
        });

        // Optional: Set click listener on arrow for specific action
        holder.arrowView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeatureClick(feature);
            }
        });

        // Accessibility: Update content description for icon
        holder.iconView.setContentDescription(
                feature.getTitle() != null ? feature.getTitle() + " icon" : "Feature icon"
        );
    }

    @Override
    public int getItemCount() {
        return featureList != null ? featureList.size() : 0;
    }

    @Override
    public void onViewRecycled(@NonNull FeatureViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        viewPool.clear();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView titleView;
        final TextView descriptionView;
        final ImageView arrowView;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iv_feature_icon);
            titleView = itemView.findViewById(R.id.tv_feature_title);
            descriptionView = itemView.findViewById(R.id.tv_feature_description);
            arrowView = itemView.findViewById(R.id.iv_arrow);
        }

        public void clear() {
            iconView.setImageDrawable(null);
            iconView.setBackgroundTintList(null);
            titleView.setText(null);
            descriptionView.setText(null);
            itemView.setOnClickListener(null);
            arrowView.setOnClickListener(null);
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return (featureList != null && position < featureList.size())
                ? featureList.get(position).getId()
                : position;
    }
}