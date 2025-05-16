package com.tvm.doctorcube.settings.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tvm.doctorcube.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationItem> notifications;

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications != null ? notifications : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.time.setText(formatTimestamp(notification.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String formatTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        if (timeDifference < 60 * 1000) {
            return "Just now";
        } else if (timeDifference < 60 * 60 * 1000) {
            long minutes = timeDifference / (60 * 1000);
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (timeDifference < 24 * 60 * 60 * 1000) {
            long hours = timeDifference / (60 * 60 * 1000);
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_notification_title);
            message = itemView.findViewById(R.id.text_notification_message);
            time = itemView.findViewById(R.id.text_notification_time);
        }
    }

    public void updateNotifications(List<NotificationItem> newNotifications) {
        this.notifications = newNotifications != null ? newNotifications : new ArrayList<>();
        notifyDataSetChanged();
    }
}