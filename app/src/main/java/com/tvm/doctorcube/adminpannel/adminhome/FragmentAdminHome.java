package com.tvm.doctorcube.adminpannel.adminhome;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.adminpannel.ChipManager;
import com.tvm.doctorcube.adminpannel.FilterManager;
import com.tvm.doctorcube.adminpannel.SortFilterDialogManager;
import com.tvm.doctorcube.adminpannel.StudentAdapter;
import com.tvm.doctorcube.adminpannel.StudentDataLoader;
import com.tvm.doctorcube.adminpannel.StudentSorter;
import com.google.android.material.chip.ChipGroup;
import com.tvm.doctorcube.adminpannel.databsemanager.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentAdminHome extends Fragment {

    private static final String TAG = "FragmentAdminHome";
    private static final int REQUEST_CALL_PHONE = 1;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private List<Student> filteredList;
    private StudentDataLoader dataLoader;
    private StudentSorter sorter;
    private ImageButton filterButton;
    private ChipGroup activeFiltersChipGroup;
    private EditText searchEditText;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    private FilterManager filterManager;
    private SortFilterDialogManager dialogManager;
    private ChipManager chipManager;

    public FragmentAdminHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // UI Setup
        recyclerView = view.findViewById(R.id.students_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        filterButton = view.findViewById(R.id.filter_button);
        activeFiltersChipGroup = view.findViewById(R.id.active_filters_chip_group);
        searchEditText = view.findViewById(R.id.search_edit_text);

        // RecyclerView setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Initialize data structures
        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize StudentDataLoader with context
        dataLoader = new StudentDataLoader(requireContext());
        adapter = new StudentAdapter(filteredList, requireContext(), dataLoader);
        recyclerView.setAdapter(adapter);

        // Initialize helpers
        sorter = new StudentSorter(dateFormat, getContext());
        filterManager = new FilterManager(studentList, filteredList, sorter, dateFormat, displayFormat, getContext());
        dialogManager = new SortFilterDialogManager(getContext(), filterManager, this::updateUIAfterFilter);
        chipManager = new ChipManager(activeFiltersChipGroup, filterManager, searchEditText);

        // Swipe refresh setup
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this::loadStudentData);

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                filterManager.setSearchQuery(query);
                filterManager.applyFiltersAndSorting();
                updateUIAfterFilter();
            }
        });

        // Filter button click
        filterButton.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        dialogManager.showSortFilterDialog();
                    })
                    .start();
        });

        // Initial data load
        loadStudentData();

        return view;
    }

    private void loadStudentData() {
        swipeRefreshLayout.setRefreshing(true);
        dataLoader.loadStudents(new StudentDataLoader.DataLoadListener() {
            @Override
            public void onDataLoaded(List<Student> students) {
                Log.d(TAG, "Data loaded successfully, size: " + students.size());
                studentList.clear();
                studentList.addAll(students);
                filterManager.applyFiltersAndSorting();
                updateUIAfterFilter();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onDataLoadFailed(String error) {
                Log.e(TAG, "Data load failed: " + error);
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.showToast(requireActivity(), "Unable to load student data: " + error);
            }
        });
    }

    // Callback to update UI after filter changes
    private void updateUIAfterFilter() {
        adapter.updateStudentList(new ArrayList<>(filteredList)); // Update adapter with filtered list
        chipManager.updateChips();
        if (filteredList.isEmpty()) {
            showNoStudentsPopup();
        }
    }

    public List<Student> getFilteredList() {
        return new ArrayList<>(filteredList);
    }

    private void showNoStudentsPopup() {
        new AlertDialog.Builder(getContext())
                .setTitle("No Students Found")
                .setMessage("No student registrations are available or no matches for current filters.")
                .setPositiveButton("Refresh", (dialog, which) -> loadStudentData())
                .setNegativeButton("OK", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            adapter.handlePermissionResult(requestCode, grantResults);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup resources
        dataLoader.cleanup();
    }
}