package com.tvm.doctorcube.study.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.StudyMaterialFragment;
import com.tvm.doctorcube.study.fragment.adapter.NotesAdapter;
import com.tvm.doctorcube.study.fragment.models.NoteItem;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment implements StudyMaterialFragment.SearchableFragment {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<NoteItem> notesList;
    private List<NoteItem> originalNotesList;
    private TextView emptyView;

    private LinearLayout NoItemsAvailable;
    private FirebaseFirestore db;
    private boolean isDataLoaded = false;
    private String pendingSearchQuery = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        NoItemsAvailable = view.findViewById(R.id.NoItemsAvailable);


        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notesList = new ArrayList<>();
        originalNotesList = new ArrayList<>();
        adapter = new NotesAdapter(requireContext(), notesList);
        recyclerView.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        fetchNotesFromFirestore();

        return view;
    }

    private void fetchNotesFromFirestore() {
        if (getContext() == null) {
            return;
        }

        emptyView.setText(R.string.loading_notes);
        NoItemsAvailable.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        db.collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (!isAdded() || getContext() == null) {
                        return;
                    }

                    if (task.isSuccessful()) {
                        List<NoteItem> fetchedNotes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("notesName");
                            String notesUrl = document.getString("notesUrl");
                            String category = document.getString("category");
                            String author = document.getString("author");
                            String pageCount = document.getString("pages");
                            String fileSize = document.getString("size");
                            String description = document.getString("description");

                            // Fetching the timestamp as a Timestamp object
                            Timestamp timestampObj = document.getTimestamp("timestamp");
                            String timestamp = "";
                            if (timestampObj != null) {
                                timestamp = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                        .format(timestampObj.toDate());
                            }

                            if (title != null && notesUrl != null) {
                                fetchedNotes.add(new NoteItem(title, notesUrl, category, author, pageCount, fileSize, timestamp, description));
                            }
                        }
                        updateAdapterAndFinishLoading(fetchedNotes);
                    } else {
                        CustomToast.showToast(requireActivity(), getString(R.string.failed_to_load_notes));
                        emptyView.setText(R.string.failed_to_load_notes);
                        NoItemsAvailable.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        isDataLoaded = true;
                    }
                });
    }

    private void updateAdapterAndFinishLoading(List<NoteItem> fetchedNotes) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        notesList.clear();
        notesList.addAll(fetchedNotes);
        originalNotesList.clear();
        originalNotesList.addAll(fetchedNotes);

        adapter.updateData(notesList);

        isDataLoaded = true;

        if (!fetchedNotes.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            NoItemsAvailable.setVisibility(View.GONE);
        } else {
            updateEmptyState("", true);
        }

        if (pendingSearchQuery != null && !pendingSearchQuery.isEmpty()) {
            performSearch(pendingSearchQuery);
            pendingSearchQuery = null;
        }
    }

    @Override
    public void performSearch(String query) {
        if (!isAdded() || getContext() == null) return;

        if (!isDataLoaded) {
            pendingSearchQuery = query;
            return;
        }

        List<NoteItem> filteredList = new ArrayList<>();
        query = query.toLowerCase();

        for (NoteItem note : originalNotesList) {
            if (note.getTitle().toLowerCase().contains(query) ||
                    note.getUrl().toLowerCase().contains(query)) {
                filteredList.add(note);
            }
        }

        adapter.updateData(filteredList);
        updateEmptyState(query, filteredList.isEmpty());
    }

    @Override
    public void resetSearch() {
        if (!isAdded() || getContext() == null) return;

        pendingSearchQuery = null;
        adapter.updateData(originalNotesList);
        updateEmptyState("", adapter.getItemCount() == 0);
    }

    private void updateEmptyState(String query, boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            NoItemsAvailable.setVisibility(View.VISIBLE);
            if (!query.isEmpty()) {
                emptyView.setText("No notes found for: " + query);
            } else {
                emptyView.setText(getString(R.string.no_notes_available));
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            NoItemsAvailable.setVisibility(View.GONE);
        }
    }
}