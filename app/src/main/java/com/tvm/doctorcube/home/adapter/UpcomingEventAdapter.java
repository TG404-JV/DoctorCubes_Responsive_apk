package com.tvm.doctorcube.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.util.List;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.EventViewHolder> {
    private List<UpcomingEvent> eventList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, UpcomingEvent event);
    }

    public UpcomingEventAdapter(List<UpcomingEvent> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    public UpcomingEventAdapter(List<UpcomingEvent> eventList) {
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        UpcomingEvent event = eventList.get(position);

        // Load image from Firebase Storage URL using Glide
        Glide.with(holder.eventImage.getContext())
                .load(event.getImageUrl())
                .placeholder(R.drawable.ic_profile) // Add a placeholder image in res/drawable
                .error(R.drawable.date_badge_premium_bg) // Add an error image in res/drawable
                .into(holder.eventImage);

        holder.dateText.setText(event.getDate() + " • " + event.getTime());
        holder.eventTitle.setText(event.getTitle());
        holder.locationText.setText(event.getLocation());
        holder.attendeesText.setText(event.getAttendees());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<UpcomingEvent> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView dateText, eventTitle, locationText, attendeesText;

        EventViewHolder(View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            dateText = itemView.findViewById(R.id.dateText);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            locationText = itemView.findViewById(R.id.locationText);
            attendeesText = itemView.findViewById(R.id.attendeesText);
        }
    }
}