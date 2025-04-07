package com.tvm.doctorcube.home.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.HomeFragment;

import java.util.List;

class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<HomeFragment.SearchItem> searchItems;
    private final OnItemClickListener listener;

    public SearchResultsAdapter(List<HomeFragment.SearchItem> searchItems, OnItemClickListener listener) {
        this.searchItems = searchItems;
        this.listener = listener;
    }

    public void updateData(List<HomeFragment.SearchItem> newItems) {
        this.searchItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeFragment.SearchItem item = searchItems.get(position);
        holder.textView.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    interface OnItemClickListener {
        void onItemClick(HomeFragment.SearchItem item);
    }
}
