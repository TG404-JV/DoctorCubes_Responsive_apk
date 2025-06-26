package com.tvm.doctorcube.home.search

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Locale

class SearchUtils<T>(
    private val searchEditText: EditText,
    private val showSearchIcon: Boolean = true,
    private val fullList: MutableList<T>,
    private val callback: SearchQueryListener<T>
) {
    init {
        // Optionally handle search icon visibility
        if (!showSearchIcon) {
            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    fun setupSearchBar() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
    }

    fun filter(query: String) {
        val filteredList = if (query.isBlank()) {
            fullList.toList()
        } else {
            fullList.filter { item ->
                callback.getSearchText(item)
                    .orEmpty()
                    .lowercase(Locale.US)
                    .contains(query.lowercase(Locale.US))
            }
        }
        callback.onSearchQuery(filteredList)
    }

    fun clear() {
        searchEditText.removeTextChangedListener(null)
        searchEditText.text?.clear()
    }

    interface SearchQueryListener<T> {
        fun onSearchQuery(filteredList: List<T>)
        fun getSearchText(item: T): String
    }
}