package com.tvm.doctorcube.adminpannel.adminhome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.adminpannel.FilterManager;
import com.tvm.doctorcube.adminpannel.SortFilterDialogManager;
import com.tvm.doctorcube.adminpannel.Student;
import com.tvm.doctorcube.adminpannel.StudentDataLoader;
import com.tvm.doctorcube.adminpannel.StudentSorter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentExportXLData extends Fragment {

    private List<Student> studentList;
    private List<Student> filteredStudentList;
    private Button exportButton, viewButton;
    private TextView statusTextView, progressStatusTextView;
    private FloatingActionButton fabSortFilter;
    private View progressLayout;
    private Uri exportedFileUri;
    private StudentDataLoader dataLoader;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    public SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Reused classes from FragmentAdminHome
    private FilterManager filterManager;
    private SortFilterDialogManager dialogManager;

    // Activity Result Launcher for SAF
    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        exportedFileUri = uri;
                        exportToExcel(uri);
                    }
                } else {
                    CustomToast.showToast(requireActivity(), "File Creation Canceled");
                    statusTextView.setText("Export canceled by user.");
                }
            });

    public static FragmentExportXLData newInstance(Bundle args) {
        FragmentExportXLData fragment = new FragmentExportXLData();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLoader = new StudentDataLoader();
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();

        filterManager = new FilterManager(studentList, filteredStudentList, new StudentSorter(dateFormat, getContext()), dateFormat, displayFormat, getContext());
        dialogManager = new SortFilterDialogManager(getContext(), filterManager);
        dialogManager.setOnApplyCallback(this::updateFilterDetailsAndStatus);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_xl_data, container, false);

        exportButton = view.findViewById(R.id.btn_export_data);
        viewButton = view.findViewById(R.id.btn_view_exported);
        statusTextView = view.findViewById(R.id.tv_export_status);
        fabSortFilter = view.findViewById(R.id.fab_filter);
        progressLayout = view.findViewById(R.id.progress_layout);
        progressStatusTextView = view.findViewById(R.id.tv_progress_status);

        viewButton.setEnabled(false);
        statusTextView.setText("Loading student data from Firebase...");

        loadStudentDataFromFirebase();

        exportButton.setOnClickListener(v -> startExportProcess());
        viewButton.setOnClickListener(v -> viewExportedFile());
        fabSortFilter.setOnClickListener(v -> dialogManager.showSortFilterDialog());

        return view;
    }

    private void loadStudentDataFromFirebase() {
        dataLoader.loadStudents(new StudentDataLoader.DataLoadListener() {
            @Override
            public void onDataLoaded(List<Student> students) {
                studentList.clear();
                studentList.addAll(students);
                filterManager.setStudentList(studentList);
                filterManager.applyFiltersAndSorting();
                updateFilterDetailsAndStatus();
            }

            @Override
            public void onDataLoadFailed(String error) {
                CustomToast.showToast(requireActivity(), "Unable to Load Data");
                statusTextView.setText("Failed to load data from Firebase.");
                exportButton.setEnabled(false);
            }
        });
    }

    public void updateFilterDetailsAndStatus() {
        if (filterManager != null) {
            filterManager.applyFiltersAndSorting();
            StringBuilder filterDetails = new StringBuilder();
            if (filterManager.getCurrentSortBy() != null) {
                filterDetails.append("Sort By: ").append(filterManager.getCurrentSortBy()).append("\n");
            }
            if (!filterManager.getSearchQuery().isEmpty()) {
                filterDetails.append("Search: ").append(filterManager.getSearchQuery()).append("\n");
            }
            if (filterManager.isFilterInterested()) filterDetails.append("Interested: Yes\n");
            if (filterManager.isFilterNotInterested()) filterDetails.append("Not Interested: Yes\n");
            if (filterManager.isFilterAdmitted()) filterDetails.append("Admitted: Yes\n");
            if (filterManager.isFilterNotAdmitted()) filterDetails.append("Not Admitted: Yes\n");
            if (filterManager.isFilterCalled()) filterDetails.append("Called: Yes\n");
            if (filterManager.isFilterNotCalled()) filterDetails.append("Not Called: Yes\n");
            String dateFilter = filterManager.getDateFilter();
            if (dateFilter != null) {
                switch (dateFilter) {
                    case "today":
                        filterDetails.append("Date: Today\n");
                        break;
                    case "yesterday":
                        filterDetails.append("Date: Yesterday\n");
                        break;
                    case "last_week":
                        filterDetails.append("Date: Last Week\n");
                        break;
                    case "last_month":
                        filterDetails.append("Date: Last Month\n");
                        break;
                    case "last_updated":
                        filterDetails.append("Date: Last Updated\n");
                        break;
                    case "firebase_push":
                        filterDetails.append("Date: Firebase Push\n");
                        break;
                    case "custom":
                        if (filterManager.getFromDate() != null && filterManager.getToDate() != null) {
                            filterDetails.append("Date Range: ")
                                    .append(displayFormat.format(filterManager.getFromDate()))
                                    .append(" - ")
                                    .append(displayFormat.format(filterManager.getToDate()))
                                    .append("\n");
                        }
                        break;
                    default:
                        filterDetails.append("Date: All Dates\n");
                }
            }

            statusTextView.setText("Ready to export " + filteredStudentList.size() + " student records.");
            exportButton.setEnabled(!filteredStudentList.isEmpty());
            viewButton.setEnabled(exportedFileUri != null);
        }
    }

    private void startExportProcess() {
        if (filteredStudentList == null || filteredStudentList.isEmpty()) {
            CustomToast.showToast(requireActivity(), "No Data To Export");
            statusTextView.setText("No data to export!");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Students_" + timeStamp + ".xlsx";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        createFileLauncher.launch(intent);
    }

    private void exportToExcel(Uri uri) {
        progressLayout.setVisibility(View.VISIBLE);
        progressStatusTextView.setText("Exporting... (0/" + filteredStudentList.size() + ")");
        exportButton.setEnabled(false);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Filtered Students");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Mobile", "Email", "State", "City", "Interested Country",
                "Has NEET Score", "NEET Score", "Has Passport", "Submission Date", "Call Status",
                "Last Call Date", "Is Interested", "Is Admitted"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        for (int i = 0; i < filteredStudentList.size(); i++) {
            Student student = filteredStudentList.get(i);
            Row row = sheet.createRow(i + 1);
            setCellValue(row, 0, student.getId());
            setCellValue(row, 1, student.getName());
            setCellValue(row, 2, student.getMobile());
            setCellValue(row, 3, student.getEmail());
            setCellValue(row, 4, student.getState());
            setCellValue(row, 5, student.getCity());
            setCellValue(row, 6, student.getInterestedCountry());
            setCellValue(row, 7, student.getHasNeetScore());
            setCellValue(row, 8, student.getNeetScore());
            setCellValue(row, 9, student.getHasPassport());
            setCellValue(row, 10, student.getSubmissionDate());
            setCellValue(row, 11, student.getCallStatus());
            setCellValue(row, 12, student.getLastCallDate());
            row.createCell(13).setCellValue(student.getIsInterested() ? "Yes" : "No");
            row.createCell(14).setCellValue(student.isAdmitted() ? "Yes" : "No");

            progressStatusTextView.setText("Exporting... (" + (i + 1) + "/" + filteredStudentList.size() + ")");
        }

        try (OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri)) {
            workbook.write(outputStream);
            workbook.close();
            progressLayout.setVisibility(View.GONE);
            statusTextView.setText("Export successful! File saved.");
            CustomToast.showToast(requireActivity(), "file saved");
            viewButton.setEnabled(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            progressLayout.setVisibility(View.GONE);
            statusTextView.setText("Export failed: File not found.");
            CustomToast.showToast(requireActivity(), "file not found");
            exportButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
            progressLayout.setVisibility(View.GONE);
            statusTextView.setText("Export failed: " + e.getMessage());
            CustomToast.showToast(requireActivity(), "Failed to Store File");
            exportButton.setEnabled(true);
        }
    }

    private void setCellValue(Row row, int cellIndex, Object value) {
        if (value != null) {
            if (value instanceof String) {
                row.createCell(cellIndex).setCellValue((String) value);
            } else if (value instanceof Integer) {
                row.createCell(cellIndex).setCellValue((Integer) value);
            } else if (value instanceof Long) {
                row.createCell(cellIndex).setCellValue((Long) value);
            } else if (value instanceof Double) {
                row.createCell(cellIndex).setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                row.createCell(cellIndex).setCellValue((Boolean) value);
            } else if (value instanceof Date) {
                row.createCell(cellIndex).setCellValue((Date) value);
            } else {
                row.createCell(cellIndex).setCellValue(value.toString());
            }
        } else {
            row.createCell(cellIndex).setCellValue("");
        }
    }

    private void viewExportedFile() {
        if (exportedFileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(exportedFileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (Exception e) {
                CustomToast.showToast(requireActivity(), "No App Found To View ");
            }
        } else {
            CustomToast.showToast(requireActivity(), "No Exported File Available");
        }
    }
}