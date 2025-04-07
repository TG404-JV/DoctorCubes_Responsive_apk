package com.tvm.doctorcube.communication;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchUtils<T> {
    private final Context context;
    private final EditText searchEditText;
    private final List<T> fullList;
    private final SearchCallback<T> callback;

    // Interface for handling search results
    public interface SearchCallback<T> {
        void onSearchResults(List<T> filteredList);
        String getSearchText(T item); // Method to extract searchable text from item
    }

    public SearchUtils(Context context, EditText searchEditText, List<T> fullList, SearchCallback<T> callback) {
        this.context = context;
        this.searchEditText = searchEditText;
        this.fullList = fullList != null ? new ArrayList<>(fullList) : new ArrayList<>();
        this.callback = callback;
    }

    public void setupSearchBar() {
        if (searchEditText == null) {
            Toast.makeText(context, "Search EditText not found", Toast.LENGTH_SHORT).show();
            return;
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void filterItems(String query) {
        if (fullList == null || callback == null) return;

        List<T> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (T item : fullList) {
            String searchText = callback.getSearchText(item);
            if (searchText != null && searchText.toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(item);
            }
        }

        callback.onSearchResults(filteredList);
    }
}
