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

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private static final String TAG = "StudentAdapter";
    private static final int REQUEST_CALL_PHONE = 1;
    private List<Student> studentList;
    private Context context;
    private FirebaseFirestore firestoreDB;
    private Student studentToCall;

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        this.firestoreDB = FirebaseFirestore.getInstance();
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

        holder.name.setText(student.getName());
        holder.state.setText(student.getState());
        holder.contact.setText(student.getMobile());
        holder.lastUpdated.setText("Last Updated: " + (student.getLastCallDate() != null ? student.getLastCallDate() : "N/A"));

        holder.btnCall.setOnClickListener(v -> {
            if (student.getMobile() != null && !student.getMobile().equals("N/A")) {
                showCallConfirmationDialog(student);
            } else {
                CustomToast.showToast((Activity) context, "Mobile number not available");
            }
        });

        holder.checkBoxCallMade.setOnCheckedChangeListener(null);
        holder.checkBoxCallMade.setChecked("called".equals(student.getCallStatus()));
        holder.checkBoxCallMade.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                currentStudent.setCallStatus(isChecked ? "called" : "pending");
                updateStudentData(currentStudent, "callStatus", currentStudent.getCallStatus());
            }
        });

        holder.checkBoxInterested.setOnCheckedChangeListener(null);
        holder.checkBoxInterested.setChecked(student.getIsInterested());
        holder.checkBoxInterested.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                currentStudent.setIsInterested(isChecked);
                updateStudentData(currentStudent, "isInterested", currentStudent.getIsInterested());
            }
        });

        holder.checkBoxAdmitted.setOnCheckedChangeListener(null);
        holder.checkBoxAdmitted.setChecked(student.isAdmitted());
        holder.checkBoxAdmitted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Student currentStudent = studentList.get(adapterPosition);
                currentStudent.setAdmitted(isChecked);
                updateStudentData(currentStudent, "isAdmitted", currentStudent.isAdmitted());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    private void showCallConfirmationDialog(Student student) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Call")
                .setMessage("Are you sure you want to call " + student.getName() + " at " + student.getMobile() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        studentToCall = student;
                        ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + student.getMobile()));
                        context.startActivity(intent);
                        updateLastCallDate(student);
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(true)
                .show();
    }

    private void updateLastCallDate(Student student) {
        String collection = getCollectionName(student);
        if (collection == null || student.getId() == null) {
            return;
        }
        String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        student.setLastCallDate(currentDate);
        updateStudentData(student, "lastCallDate", currentDate);
    }

    private void updateStudentData(Student student, String field, Object value) {
        String collection = getCollectionName(student);
        if (collection == null || student.getId() == null) {
            return;
        }

        // Use the method from StudentDataLoader to update the data
        new StudentDataLoader().updateStudent(collection, student.getId(), field, value);
    }

    private String getCollectionName(Student student) {
        if (student == null || student.getId() == null) {
            return null;
        }
        return student.getCollection(); //changed
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (studentToCall != null) {
                new SocialActions().makeDirectCall(context);
            }
        } else {
            CustomToast.showToast((Activity) context, "Permission denied");
        }
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, state, contact, lastUpdated;
        Button btnCall;
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
        }
    }
}

