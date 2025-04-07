package com.tvm.doctorcube.home.search;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class SearchUtils<T> {
    private final Activity activity;
    private final EditText searchEditText;
    private final List<T> fullList;
    private final SearchCallback<T> callback;

    public SearchUtils(Activity activity, EditText searchEditText, List<T> fullList, SearchCallback<T> callback) {
        this.activity = activity;
        this.searchEditText = searchEditText;
        this.fullList = fullList;
        this.callback = callback;
    }

    public void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String query) {
        List<T> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (T item : fullList) {
                String searchText = callback.getSearchText(item).toLowerCase();
                if (searchText.contains(query.toLowerCase())) {
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
