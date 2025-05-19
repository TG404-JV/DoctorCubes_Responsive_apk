package com.tvm.doctorcube.communication;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchUtils<T> {
    private final Activity activity;
    private final EditText searchEditText;
    private final List<T> fullList;
    private final SearchCallback<T> callback;

    public SearchUtils(Activity activity, EditText searchEditText, List<T> fullList, SearchCallback<T> callback) {
        this.activity = activity;
        this.searchEditText = searchEditText;
        this.fullList = fullList != null ? fullList : new ArrayList<>();
        this.callback = callback;
    }

    public void setupSearchBar() {
        if (searchEditText == null || callback == null) {
            return;
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
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
        });
    }

    private void filter(String query) {
        List<T> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String searchQuery = query.toLowerCase(Locale.getDefault()).trim();
            for (T item : fullList) {
                if (item != null && callback.getSearchText(item).toLowerCase(Locale.getDefault()).contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        callback.onSearchResults(filteredList);
    }

    public interface SearchCallback<T> {
        void onSearchResults(List<T> filteredList);
        String getSearchText(T item);
    }
}