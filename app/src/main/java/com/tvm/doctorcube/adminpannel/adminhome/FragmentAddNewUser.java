package com.tvm.doctorcube.adminpannel.adminhome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // Import Firestore
// Import DocumentReference
import com.google.firebase.firestore.FieldValue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentAddNewUser extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDB; // Use Firestore
    private SharedPreferences sharedPreferences;

    // Use TextInputLayout instead of EditText for proper error display
    private TextInputLayout tilUserName, tilEmail, tilPhone, tilPassword, tilRole;
    private EditText etUserName, etEmail, etPhone, etPassword;
    private AutoCompleteTextView etRole;
    private Button btnAddUser;
    private String selectedRole;

    public FragmentAddNewUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Initialize Views using TextInputLayout
        tilUserName = view.findViewById(R.id.tilUserName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilRole = view.findViewById(R.id.tilRole);
        etUserName = view.findViewById(R.id.etUserName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        etRole = view.findViewById(R.id.etRole);
        btnAddUser = view.findViewById(R.id.btnAddUser);

        // Initialize Encrypted SharedPreferences

        // Set up the role dropdown
        setupRoleDropdown();

        // Handle Add User Button Click
        btnAddUser.setOnClickListener(v -> registerUser());
    }

    private void setupRoleDropdown() {
        // Define the roles
        String[] roles = new String[]{"admin", "superadmin"};
        // Create an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_menu_popup_item, roles);
        // Set the adapter to the AutoCompleteTextView
        etRole.setAdapter(adapter);

        // Set a listener to get the selected role
        etRole.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = (String) parent.getItemAtPosition(position);
        });
    }

    private void registerUser() {
        String fullName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs using TextInputLayout's setError()
        if (TextUtils.isEmpty(fullName)) {
            tilUserName.setError("Enter Full Name");
            return;
        } else {
            tilUserName.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Enter Email");
            return;
        } else {
            tilEmail.setError(null);
        }
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Enter Phone Number");
            return;
        } else {
            tilPhone.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Enter Password");
            return;
        } else {
            tilPassword.setError(null);
        }
        if (TextUtils.isEmpty(selectedRole)) {
            tilRole.setError("Select a Role");
            return;
        } else {
            tilRole.setError(null);
        }

        // Create user in Firebase Authentication
        // Use AuthResult
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> { // Use AuthResult
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserDetails(user.getUid(), fullName, email, phone, selectedRole);
                            // Removed saveUserLoginStatus(role);  // Do not automatically log in.

                            // Reset fields after successful registration
                            etUserName.setText("");
                            etEmail.setText("");
                            etPhone.setText("");
                            etPassword.setText("");
                            etRole.setText("");
                            selectedRole = null;

                            CustomToast.showToast(requireActivity(), "Account Created Successfully");
                        }
                    } else {
                        CustomToast.showToast(requireActivity(), "SignUp Failed");
                    }
                });
    }

    private void saveUserDetails(String userId, String fullName, String email, String phone, String role) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", fullName); // Changed from "fullName" to "name" to match Firestore field
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("role", role);
        userData.put("timestamp", FieldValue.serverTimestamp());
        userData.put("isVerified", true);
        userData.put("isPhoneNumberVerified", true);
        userData.put("isFormSubmitted",true);

        firestoreDB.collection("Users").document(userId) // Use Firestore
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
}

