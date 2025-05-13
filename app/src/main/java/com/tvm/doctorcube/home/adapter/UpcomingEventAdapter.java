package com.tvm.doctorcube.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.EventViewHolder> {

    private List<UpcomingEvent> upcomingEvents;

    public UpcomingEventAdapter(List<UpcomingEvent> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public void updateUpcomingEvents(List<UpcomingEvent> newUpcomingEvents) {
        this.upcomingEvents = newUpcomingEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        UpcomingEvent event = upcomingEvents.get(position);
        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDetails.setText(event.getTime() + " • " + event.getLocation());

        // Format date for badge
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            holder.tvMonth.setText(monthFormat.format(sdf.parse(event.getDate())).toUpperCase());
            holder.tvDay.setText(dayFormat.format(sdf.parse(event.getDate())));
        } catch (ParseException e) {
            holder.tvMonth.setText("");
            holder.tvDay.setText("");
        }

        // Handle premium badge
        holder.premiumBadge.setVisibility(event.isPremium() ? View.VISIBLE : View.GONE);
        holder.dateBadge.setBackgroundResource(event.isPremium() ? R.drawable.date_badge_premium_bg : R.drawable.date_badge_bg);

        // Handle bookmark
        holder.ivBookmark.setImageResource(event.isBookmarked() ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        holder.ivBookmark.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                event.isBookmarked() ? R.color.accent_dark_color : R.color.icon_tint_secondary));

        // Action text
        holder.tvAction.setText(event.isPremium() ? "Limited spots available" : "Register");
    }

    @Override
    public int getItemCount() {
        return upcomingEvents.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDetails, tvAction, tvMonth, tvDay;
        ImageView ivBookmark;
        LinearLayout premiumBadge, dateBadge;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDetails = itemView.findViewById(R.id.tvEventDetails);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvDay = itemView.findViewById(R.id.tvDay);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
            premiumBadge = itemView.findViewById(R.id.premiumBadge);
            dateBadge = itemView.findViewById(R.id.dateBadge);
        }
    }
}