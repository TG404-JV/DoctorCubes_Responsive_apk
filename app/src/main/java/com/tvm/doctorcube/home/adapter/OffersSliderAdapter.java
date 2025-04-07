package com.tvm.doctorcube.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.OfferSlide;

import java.util.List;

public class OffersSliderAdapter extends RecyclerView.Adapter<OffersSliderAdapter.OfferViewHolder> {

    private List<OfferSlide> offerSlides;

    public OffersSliderAdapter(List<OfferSlide> offerSlides) {
        this.offerSlides = offerSlides;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_slide_item, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        OfferSlide offerSlide = offerSlides.get(position);
        holder.offerImage.setImageResource(offerSlide.getImageResId());
        holder.offerTitle.setText(offerSlide.getTitle());
        holder.offerDescription.setText(offerSlide.getDescription());
    }

    @Override
    public int getItemCount() {
        return offerSlides.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView offerImage;
        TextView offerTitle;
        TextView offerDescription;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offer_image);
            offerTitle = itemView.findViewById(R.id.offer_title);
            offerDescription = itemView.findViewById(R.id.offer_description);
        }
    }
}