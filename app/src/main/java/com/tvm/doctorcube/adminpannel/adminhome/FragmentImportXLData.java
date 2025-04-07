package com.tvm.doctorcube.adminpannel.adminhome;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.adminpannel.Student;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FragmentImportXLData extends Fragment {

    private static final String TAG = "FragmentImportXLData";

    private TextView statusTextView, tvFileName, tvFileInfo, tvProgressStatus;
    private Button selectExcelButton, importDataButton;
    private CardView fileInfoCard;
    private View progressLayout;
    private FirebaseFirestore firestoreDB;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    private SimpleDateFormat[] excelDateFormats = {
            new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()),
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
            new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()),
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
    };
    private Uri selectedFileUri;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleFileSelection);

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openFilePicker();
                } else {
                    statusTextView.setText("Storage permission denied. Cannot import Excel files.");
                    Toast.makeText(getContext(), "Permission denied! File access unavailable.", Toast.LENGTH_LONG).show();
                }
            });

    public FragmentImportXLData() {
        // Required empty public constructor
    }

    public static FragmentImportXLData newInstance() {
        return new FragmentImportXLData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestoreDB = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_x_l_data, container, false);

        statusTextView = view.findViewById(R.id.tv_import_status);
        selectExcelButton = view.findViewById(R.id.btn_select_excel);
        importDataButton = view.findViewById(R.id.btn_import_data);
        tvFileName = view.findViewById(R.id.tv_file_name);
        tvFileInfo = view.findViewById(R.id.tv_file_info);
        fileInfoCard = view.findViewById(R.id.card_file_info);
        progressLayout = view.findViewById(R.id.progress_layout);
        tvProgressStatus = view.findViewById(R.id.tv_progress_status);

        selectExcelButton.setOnClickListener(v -> checkStoragePermission());
        importDataButton.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                importDataButton.setEnabled(false);
                selectExcelButton.setEnabled(false);
                parseAndUploadExcel(selectedFileUri);
            } else {
                Toast.makeText(getContext(), "Please select an Excel file first.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Storage Permission Required")
                        .setMessage("This app needs access to your storage to import Excel files containing student data. Please grant the permission to proceed.")
                        .setPositiveButton("OK", (dialog, which) -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            statusTextView.setText("Permission denied. Cannot access files.");
                            Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            openFilePicker();
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void handleFileSelection(Uri uri) {
        if (uri == null) {
            Toast.makeText(getContext(), "No file selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedFileUri = uri;
        updateFileInfoUI(uri);
        importDataButton.setEnabled(true);
    }

    private void updateFileInfoUI(Uri uri) {
        try {
            String fileName = getFileName(uri);
            String fileSize = getFileSize(uri);
            String fileModifiedDate = getFileModifiedDate(uri);

            tvFileName.setText(fileName);
            tvFileInfo.setText("Size: " + fileSize + " | Modified: " + fileModifiedDate);
            fileInfoCard.setVisibility(View.VISIBLE);
            statusTextView.setText("File selected. Ready to import.");
        } catch (Exception e) {
            e.printStackTrace();
            statusTextView.setText("Error getting file information.");
            fileInfoCard.setVisibility(View.GONE);
            importDataButton.setEnabled(false);
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            android.database.Cursor cursor = null;
            try {
                cursor = getContext().getContentResolver().query(uri, new String[]{android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME);
                    fileName = cursor.getString(index);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return fileName != null ? fileName : uri.getLastPathSegment();
    }

    private String getFileSize(Uri uri) {
        long fileSize = -1;
        android.database.Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(uri, new String[]{android.provider.MediaStore.Files.FileColumns.SIZE}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.SIZE);
                fileSize = cursor.getLong(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        if (fileSize <= 0) return "Unknown";
        else if (fileSize < 1024) return fileSize + " Bytes";
        else if (fileSize < 1024 * 1024) return String.format(Locale.getDefault(), "%.2f KB", fileSize / 1024.0);
        else return String.format(Locale.getDefault(), "%.2f MB", fileSize / (1024.0 * 1024));
    }

    private String getFileModifiedDate(Uri uri) {
        long fileModifiedDate = -1;
        android.database.Cursor cursor = null;
        try {
            cursor = requireContext().getContentResolver().query(uri, new String[]{android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED);
                fileModifiedDate = cursor.getLong(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        if (fileModifiedDate <= 0) return "Unknown";
        Date date = new Date(fileModifiedDate * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    private void parseAndUploadExcel(Uri uri) {
        progressLayout.setVisibility(View.VISIBLE);
        tvProgressStatus.setText("Parsing Excel file...");
        List<Student> students = parseExcelFile(uri);
        if (students.isEmpty()) {
            progressLayout.setVisibility(View.GONE);
            statusTextView.setText("No valid data found in the Excel file.");
            importDataButton.setEnabled(true);
            selectExcelButton.setEnabled(true);
            return;
        }
        uploadToFirestore(students);
    }

    private List<Student> parseExcelFile(Uri uri) {
        List<Student> students = new ArrayList<>();
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null || !validateHeaders(headerRow)) {
                throw new IllegalArgumentException("Excel file headers do not match expected format.");
            }

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Student student = new Student();
                student.setId(getCellValue(row, 0));
                student.setName(getCellValue(row, 1));
                student.setMobile(getCellValue(row, 2));
                student.setEmail(getCellValue(row, 3));
                student.setState(getCellValue(row, 4));
                student.setCity(getCellValue(row, 5));
                student.setInterestedCountry(getCellValue(row, 6));
                student.setHasNeetScore(getCellValue(row, 7));
                student.setNeetScore(getCellValue(row, 8));
                student.setHasPassport(getCellValue(row, 9));
                String submissionDate = parseDate(getCellValue(row, 10));
                student.setSubmissionDate(submissionDate);
                student.setCallStatus(getCellValue(row, 11));
                student.setLastCallDate(parseDate(getCellValue(row, 12)));
                String isInterested = getCellValue(row, 13).toLowerCase();
                student.setIsInterested("yes".equals(isInterested) || "true".equals(isInterested) || "y".equals(isInterested) || "1".equals(isInterested));
                String isAdmitted = getCellValue(row, 14).toLowerCase();
                student.setAdmitted("yes".equals(isAdmitted) || "true".equals(isAdmitted) || "y".equals(isAdmitted) || "1".equals(isAdmitted));

                if (TextUtils.isEmpty(student.getName()) || TextUtils.isEmpty(student.getEmail()) || TextUtils.isEmpty(student.getMobile())) {
                    Log.w(TAG, "Skipping invalid row " + i + ": Missing required fields.");
                    continue;
                }

                students.add(student);
                tvProgressStatus.setText("Parsing... (" + i + "/" + totalRows + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressLayout.setVisibility(View.GONE);
            statusTextView.setText("Error reading Excel file: " + e.getMessage());
            importDataButton.setEnabled(true);
            selectExcelButton.setEnabled(true);
        }
        return students;
    }

    private boolean validateHeaders(Row headerRow) {
        String[] expectedHeaders = {"ID", "Name", "Mobile", "Email", "State", "City", "Interested Country",
                "Has NEET Score", "NEET Score", "Has Passport", "Submission Date", "Call Status",
                "Last Call Date", "Is Interested", "Is Admitted"};
        if (headerRow.getLastCellNum() < expectedHeaders.length) {
            return false;
        }
        for (int i = 0; i < expectedHeaders.length; i++) {
            String header = getCellValue(headerRow, i);
            if (!expectedHeaders[i].equalsIgnoreCase(header.trim())) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Row row, int cellIndex) {
        if (row.getCell(cellIndex) == null) return "";
        try {
            return row.getCell(cellIndex).toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String parseDate(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return dateFormat.format(new Date());
        }
        for (SimpleDateFormat format : excelDateFormats) {
            try {
                Date date = format.parse(dateStr);
                if (date != null) {
                    String formattedDate = dateFormat.format(date);
                    return formattedDate;
                }
            } catch (ParseException ignored) {
                // Try next format
            }
        }
        // If all parsing fails, check if it's already in ddMMyy format
        if (dateStr.matches("\\d{6}")) {
            return dateStr;
        }
        return dateFormat.format(new Date());
    }

    private void uploadToFirestore(List<Student> students) {
        int total = students.size();
        AtomicInteger uploadedCount = new AtomicInteger();
        String currentDate = dateFormat.format(new Date());
        CollectionReference xlDataRef = firestoreDB.collection("xl_data");

        progressLayout.setVisibility(View.VISIBLE);
        tvProgressStatus.setText("Uploading data... (0/" + total + ")");

        for (Student student : students) {
            String studentId = TextUtils.isEmpty(student.getId()) ? firestoreDB.collection("xl_data").document().getId() : student.getId();
            String submissionDate = TextUtils.isEmpty(student.getSubmissionDate()) ? currentDate : student.getSubmissionDate();

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", studentId);
            studentData.put("name", student.getName());
            studentData.put("email", student.getEmail());
            studentData.put("mobile", student.getMobile());
            studentData.put("state", student.getState());
            studentData.put("city", student.getCity());
            studentData.put("interestedCountry", student.getInterestedCountry());
            studentData.put("hasNeetScore", student.getHasNeetScore());
            studentData.put("neetScore", student.getNeetScore());
            studentData.put("hasPassport", student.getHasPassport());
            studentData.put("submissionDate", submissionDate);
            studentData.put("callStatus", TextUtils.isEmpty(student.getCallStatus()) ? "pending" : student.getCallStatus());
            studentData.put("lastCallDate", student.getLastCallDate());
            studentData.put("isInterested", student.getIsInterested());
            studentData.put("isAdmitted", student.isAdmitted());
            studentData.put("firebasePushDate", currentDate);

            xlDataRef.document(studentId)
                    .set(studentData)
                    .addOnSuccessListener(aVoid -> {
                        uploadedCount.getAndIncrement();
                        tvProgressStatus.setText("Uploading data... (" + uploadedCount + "/" + total + ")");
                        if (uploadedCount.get() == total) {
                            progressLayout.setVisibility(View.GONE);
                            statusTextView.setText("Data upload completed! " + total + " students imported.");
                            Toast.makeText(getContext(), "Imported " + total + " students!", Toast.LENGTH_SHORT).show();
                            fileInfoCard.setVisibility(View.GONE);
                            importDataButton.setEnabled(false);
                            selectExcelButton.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressLayout.setVisibility(View.GONE);
                        statusTextView.setText("Upload failed: " + e.getMessage());
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        importDataButton.setEnabled(true);
                        selectExcelButton.setEnabled(true);
                    });
        }
    }
}