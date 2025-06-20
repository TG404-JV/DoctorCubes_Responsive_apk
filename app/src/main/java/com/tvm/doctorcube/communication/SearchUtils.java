package com.tvm.doctorcube.communication;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchUtils<T> {
    private final Activity activity;
    private final EditText searchEditText;
    private final List<T> fullList;
    private final SearchCallback<T> callback;
    private TextWatcher textWatcher;

    public SearchUtils(Activity activity, EditText searchEditText, List<T> fullList, SearchCallback<T> callback) {
        this.activity = activity;
        this.searchEditText = searchEditText;
        this.fullList = fullList != null ? fullList : new ArrayList<>();
        this.callback = callback;
    }

    public void setupSearchBar() {
        if (searchEditText == null || callback == null) {
            Log.w("SearchUtils", "Cannot setup search bar: searchEditText or callback is null");
            return;
        }

        // Remove existing TextWatcher
        if (textWatcher != null) {
            searchEditText.removeTextChangedListener(textWatcher);
        }

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        };
        searchEditText.addTextChangedListener(textWatcher);
        Log.d("SearchUtils", "TextWatcher attached to searchEditText");
    }

    public void filter(String query) {
        List<T> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            // Return empty list for empty query to hide results
            callback.onSearchResults(filteredList);
            return;
        }
        String searchQuery = query.toLowerCase(Locale.getDefault()).trim();
        for (T item : fullList) {
            if (item != null && callback.getSearchText(item).toLowerCase(Locale.getDefault()).contains(searchQuery)) {
                filteredList.add(item);
            }
        }
        callback.onSearchResults(filteredList);
        Log.d("SearchUtils", "Filtered list with " + filteredList.size() + " items for query: " + query);
    }

    public void clear() {
        if (searchEditText != null && textWatcher != null) {
            searchEditText.removeTextChangedListener(textWatcher);
            textWatcher = null;
            Log.d("SearchUtils", "TextWatcher cleared");
        }
    }

    public interface SearchCallback<T> {
        void onSearchResults(List<T> filteredList);
        String getSearchText(T item);
    }
}