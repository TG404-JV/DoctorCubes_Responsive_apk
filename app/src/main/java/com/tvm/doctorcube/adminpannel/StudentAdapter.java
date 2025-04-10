package com.tvm.doctorcube.adminpannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.adminpannel.databsemanager.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private static final String TAG = "StudentAdapter";
    private static final int REQUEST_CALL_PHONE = 1;
    private List<Student> studentList;
    private Context context;
    private StudentDataLoader studentDataLoader;
    private Student studentToCall;

    public StudentAdapter(List<Student> studentList, Context context, StudentDataLoader studentDataLoader) {
        this.studentList = studentList;
        this.context = context;
        this.studentDataLoader = studentDataLoader;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        if (student == null) {
            return;
        }

        holder.name.setText(student.getName() != null ? student.getName() : "N/A");
        holder.state.setText(student.getState() != null ? student.getState() : "N/A");
        holder.contact.setText(student.getMobile() != null ? student.getMobile() : "N/A");
        holder.lastUpdated.setText("Last Updated: " + (student.getLastCallDate() != null ? student.getLastCallDate() : "N/A"));

        holder.btnCall.setOnClickListener(v -> {
            if (student.getMobile() != null && !student.getMobile().isEmpty() && !student.getMobile().equals("N/A")) {
                showCallConfirmationDialog(student);
            } else {
                CustomToast.showToast((Activity) context, "Mobile number not available");
            }
        });

        // Reset listeners to prevent recycling issues
        holder.checkBoxCallMade.setOnCheckedChangeListener(null);
        holder.checkBoxCallMade.setChecked("called".equals(student.getCallStatus()));
        holder.checkBoxCallMade.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                String newStatus = isChecked ? "called" : "pending";
                currentStudent.setCallStatus(newStatus);
                updateStudentData(currentStudent, "callStatus", newStatus);
            }
        });

        holder.checkBoxInterested.setOnCheckedChangeListener(null);
        holder.checkBoxInterested.setChecked(student.isInterested());
        holder.checkBoxInterested.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                currentStudent.setIsInterested(isChecked);
                updateStudentData(currentStudent, "isInterested", isChecked);
            }
        });

        holder.checkBoxAdmitted.setOnCheckedChangeListener(null);
        holder.checkBoxAdmitted.setChecked(student.isAdmitted());
        holder.checkBoxAdmitted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                currentStudent.setAdmitted(isChecked);
                updateStudentData(currentStudent, "isAdmitted", isChecked);
            }
        });

        // Handle View Details button click
        holder.btnViewDetails.setOnClickListener(v -> showStudentDetailsBottomSheet(student));
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    private void showCallConfirmationDialog(Student student) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Call")
                .setMessage("Are you sure you want to call " + (student.getName() != null ? student.getName() : "this student") +
                        " at " + student.getMobile() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        studentToCall = student;
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                REQUEST_CALL_PHONE);
                    } else {
                        makeCall(student);
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(true)
                .show();
    }

    private void makeCall(Student student) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + student.getMobile()));
        try {
            context.startActivity(intent);
            updateLastCallDate(student);
        } catch (SecurityException e) {
            CustomToast.showToast((Activity) context, "Unable to make call: " + e.getMessage());
        }
    }

    private void updateLastCallDate(Student student) {
        String collection = getCollectionName(student);
        if (collection == null || student.getId() == null) {
            CustomToast.showToast((Activity) context, "Cannot update: Invalid student data");
            return;
        }
        String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        student.setLastCallDate(currentDate);
        updateStudentData(student, "lastCallDate", currentDate);
    }

    private void updateStudentData(Student student, String field, Object value) {
        String collection = getCollectionName(student);
        if (collection == null || student.getId() == null) {
            CustomToast.showToast((Activity) context, "Cannot update: Invalid student data");
            return;
        }
        studentDataLoader.updateStudent(collection, student.getId(), field, value);
    }

    private String getCollectionName(Student student) {
        if (student == null || student.getId() == null || student.getCollection() == null) {
            return null;
        }
        return student.getCollection();
    }

    private void showStudentDetailsBottomSheet(Student student) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.bottomsheet_student_details, null);
        bottomSheetDialog.setContentView(sheetView);

        // Populate student details
        TextView tvName = sheetView.findViewById(R.id.tv_detail_name);
        TextView tvMobile = sheetView.findViewById(R.id.tv_detail_mobile);
        TextView tvEmail = sheetView.findViewById(R.id.tv_detail_email);
        TextView tvState = sheetView.findViewById(R.id.tv_detail_state);
        TextView tvCity = sheetView.findViewById(R.id.tv_detail_city);
        TextView tvCountry = sheetView.findViewById(R.id.tv_detail_country);
        TextView tvNeetScore = sheetView.findViewById(R.id.tv_detail_neet_score);
        TextView tvPassport = sheetView.findViewById(R.id.tv_detail_passport);
        TextView tvSubmissionDate = sheetView.findViewById(R.id.tv_detail_submission_date);
        TextView tvCallStatus = sheetView.findViewById(R.id.tv_detail_call_status);
        TextView tvLastCallDate = sheetView.findViewById(R.id.tv_detail_last_call_date);
        TextView tvInterested = sheetView.findViewById(R.id.tv_detail_interested);
        TextView tvAdmitted = sheetView.findViewById(R.id.tv_detail_admitted);

        tvName.setText("Name: " + (student.getName() != null ? student.getName() : "N/A"));
        tvMobile.setText("Mobile: " + (student.getMobile() != null ? student.getMobile() : "N/A"));
        tvEmail.setText("Email: " + (student.getEmail() != null ? student.getEmail() : "N/A"));
        tvState.setText("State: " + (student.getState() != null ? student.getState() : "N/A"));
        tvCity.setText("City: " + (student.getCity() != null ? student.getCity() : "N/A"));
        tvCountry.setText("Interested Country: " + (student.getInterestedCountry() != null ? student.getInterestedCountry() : "N/A"));
        if (student.getHasNeetScore() != null) {
            if (student.getHasNeetScore().equals("Yes"))
                tvNeetScore.setText("NEET Score: " + student.getNeetScore());
            else
                tvNeetScore.setText("NEET Score: " + "No");
        }
        else
            tvNeetScore.setText(new StringBuilder().append("NEET Score: ").append("N/A").toString());
        if (student.getHasPassport() != null) {
            if (student.getHasPassport().equals("Yes"))
                tvPassport.setText(new StringBuilder().append("Has Passport: ").append("Yes").toString());
            else
                tvPassport.setText(new StringBuilder().append("Has Passport: ").append("No").toString());
        }
        else
            tvPassport.setText(new StringBuilder().append("Has Passport: ").append("N/A").toString());
        tvSubmissionDate.setText("Submission Date: " + (student.getSubmissionDate() != null ? student.getSubmissionDate() : "N/A"));
        tvCallStatus.setText("Call Status: " + (student.getCallStatus() != null ? student.getCallStatus() : "N/A"));
        tvLastCallDate.setText("Last Call Date: " + (student.getLastCallDate() != null ? student.getLastCallDate() : "N/A"));
        tvInterested.setText("Interested: " + (student.isInterested() ? "Yes" : "No"));
        tvAdmitted.setText("Admitted: " + (student.isAdmitted() ? "Yes" : "No"));

        // Close button
        Button btnClose = sheetView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (studentToCall != null) {
                makeCall(studentToCall);
                studentToCall = null;
            }
        } else {
            CustomToast.showToast((Activity) context, "Permission denied");
            studentToCall = null;
        }
    }

    public void updateStudentList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, state, contact, lastUpdated;
        Button btnCall, btnViewDetails;
        CheckBox checkBoxCallMade, checkBoxInterested, checkBoxAdmitted;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_student_name);
            state = itemView.findViewById(R.id.tv_student_state);
            contact = itemView.findViewById(R.id.tv_student_contact);
            lastUpdated = itemView.findViewById(R.id.tv_last_updated);
            btnCall = itemView.findViewById(R.id.btn_call_student);
            checkBoxCallMade = itemView.findViewById(R.id.checkbox_call_made);
            checkBoxInterested = itemView.findViewById(R.id.checkbox_interested);
            checkBoxAdmitted = itemView.findViewById(R.id.checkbox_admitted);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}
