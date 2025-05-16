package com.tvm.doctorcube.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.data.Testimonial;

import java.util.List;

public class TestimonialsSliderAdapter extends RecyclerView.Adapter<TestimonialsSliderAdapter.TestimonialViewHolder> {

    private List<Testimonial> testimonials;

    public TestimonialsSliderAdapter(List<Testimonial> testimonials) {
        this.testimonials = testimonials;
    }

    @NonNull
    @Override
    public TestimonialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_testimonial_slide, parent, false);


        return new TestimonialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestimonialViewHolder holder, int position) {
        Testimonial testimonial = testimonials.get(position);
        holder.avatarImage.setImageResource(testimonial.getAvatarResId());
        holder.nameText.setText(testimonial.getName());
        holder.testimonialText.setText(testimonial.getTestimonial());
        holder.flagImage.setImageResource(Integer.parseInt(testimonial.getFlagResId()));
        holder.universityText.setText(testimonial.getUniversity());
        holder.batchText.setText(testimonial.getBatch());
        holder.ratingBar.setRating(testimonial.getRating());
    }

    /**
     * Returns the total number of items in the testimonial list.
     *
     * @return The number of testimonials.
     */
    @Override
    public int getItemCount() {
        return testimonials.size();
    }

    public List<Testimonial> getTestimonials() {
        return testimonials;
    }

    static class TestimonialViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nameText;
        TextView testimonialText;
        ImageView flagImage;
        TextView universityText;
        TextView batchText;
        RatingBar ratingBar;

        TestimonialViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.testimonial_avatar);
            nameText = itemView.findViewById(R.id.testimonial_name);
            testimonialText = itemView.findViewById(R.id.testimonial_text);
            flagImage = itemView.findViewById(R.id.testimonial_flag);
            universityText = itemView.findViewById(R.id.testimonial_university);
            batchText = itemView.findViewById(R.id.testimonial_batch);
            ratingBar = itemView.findViewById(R.id.testimonial_rating);
        }
    }
}