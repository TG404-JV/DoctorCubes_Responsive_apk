package com.tvm.doctorcube.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Feature;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

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
        Feature feature = featureList.get(position);
        holder.bind(feature, listener);
    }

    @Override
    public int getItemCount() {
        return featureList.size();
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
        final ShapeableImageView iconView;
        final TextView titleView;
        final TextView descriptionView;
        final MaterialCardView cardView;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.feature_icon);
            titleView = itemView.findViewById(R.id.feature_title);
            descriptionView = itemView.findViewById(R.id.feature_description);
            cardView = itemView.findViewById(R.id.feature_card);
        }

        public void bind(Feature feature, OnFeatureClickListener listener) {
            iconView.setImageResource(feature.getIconResource());
            iconView.setBackgroundColor(feature.getIconBackgroundColor());
            titleView.setText(feature.getTitle());
            descriptionView.setText(feature.getDescription());

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFeatureClick(feature);
                }
            });
        }

        public void clear() {
            iconView.setImageDrawable(null);
            titleView.setText(null);
            descriptionView.setText(null);
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return featureList.get(position).getId();
    }
}
