package com.tvm.doctorcube.university;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.tvm.doctorcube.university.model.University;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ApplyBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ApplyBottomSheet";
    private EditText nameEditText, emailEditText, phoneEditText, stateEditText, cityEditText, neetScoreEditText;
    private AutoCompleteTextView countrySpinner;
    private RadioGroup neetScoreGroup, passportGroup;
    private RadioButton neetScoreYes, neetScoreNo, passportYes, passportNo;
    private TextInputLayout neetScoreLayout;
    private Button submitButton;
    private LinearLayout headerUniversity;
    private EncryptedSharedPreferencesManager encryptedSharedPreferencesManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private University university;
    private String offer;
    private boolean isExistingData = false;
    private String existingDocumentId;
    private String[] countries;
    private Context context;

    public ApplyBottomSheetFragment(University university) {
        this.university = university;
    }

    public ApplyBottomSheetFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(context);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        neetScoreEditText = view.findViewById(R.id.neetScore);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        neetScoreGroup = view.findViewById(R.id.neetScoreGroup);
        passportGroup = view.findViewById(R.id.passportGroup);
        neetScoreYes = view.findViewById(R.id.neetScoreYes);
        neetScoreNo = view.findViewById(R.id.neetScoreNo);
        passportYes = view.findViewById(R.id.passportYes);
        passportNo = view.findViewById(R.id.passportNo);
        neetScoreLayout = view.findViewById(R.id.neetScoreLayout);
        submitButton = view.findViewById(R.id.submitButton);
        headerUniversity = view.findViewById(R.id.headerUniversity);
        headerUniversity.setVisibility(View.GONE);

        countries = getResources().getStringArray(R.array.countries_array);

        if (getArguments() != null) {
            offer = getArguments().getString("event_title", "");
        }

        if (!TextUtils.isEmpty(offer)) {
            TextView t1 = view.findViewById(R.id.PersonlizedTxt);
            t1.setText(offer);
        }

        setUpCountrySpinner();
        setUpListeners();
        setupTextWatchers();

        // Load data if not already loaded from SharedPreferences
        if (!encryptedSharedPreferencesManager.getBoolean("isdataloaded", false)) {
            loadDataFromFirestoreAndSave();
        } else {
            applySavedDataToViews();
            submitButton.setText("Update Application");
            isExistingData = true;
        }

        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveOrUpdateApplication();
                dismiss();
            }
        });
    }

    private void loadDataFromFirestoreAndSave() {
        if (context == null || mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("app_submissions")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        existingDocumentId = document.getId();
                        isExistingData = true;
                        submitButton.setText("Update Application");
                        populateViews(document);
                        saveDataToSharedPreferences(document.getData());
                        encryptedSharedPreferencesManager.putBoolean("isdataloaded", true);
                    } else {
                        encryptedSharedPreferencesManager.putBoolean("isdataloaded", true); // Mark as loaded even if no data
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading application data from Firestore", e);
                    if (context != null) {
                        CustomToast.showToast(requireActivity(), "Failed to load existing data.");
                    }
                });
    }

    private void populateViews(DocumentSnapshot document) {
        nameEditText.setText(document.getString("name"));
        emailEditText.setText(document.getString("email"));
        phoneEditText.setText(document.getString("mobile"));
        stateEditText.setText(document.getString("state"));
        cityEditText.setText(document.getString("city"));
        countrySpinner.setText(document.getString("country"));
        neetScoreEditText.setText(document.getString("neetScore"));

        Boolean hasNeetScore = document.getBoolean("hasNeetScore");
        if (hasNeetScore != null) {
            if (hasNeetScore) {
                neetScoreYes.setChecked(true);
                neetScoreLayout.setVisibility(View.VISIBLE);
            } else {
                neetScoreNo.setChecked(true);
                neetScoreLayout.setVisibility(View.GONE);
            }
        }

        Boolean hasPassport = document.getBoolean("hasPassport");
        if (hasPassport != null) {
            if (hasPassport) {
                passportYes.setChecked(true);
            } else {
                passportNo.setChecked(true);
            }
        }
    }

    private void applySavedDataToViews() {
        nameEditText.setText(encryptedSharedPreferencesManager.getString("name", ""));
        emailEditText.setText(encryptedSharedPreferencesManager.getString("email", ""));
        phoneEditText.setText(encryptedSharedPreferencesManager.getString("mobile", ""));
        stateEditText.setText(encryptedSharedPreferencesManager.getString("state", ""));
        cityEditText.setText(encryptedSharedPreferencesManager.getString("city", ""));
        countrySpinner.setText(encryptedSharedPreferencesManager.getString("country", ""));
        neetScoreEditText.setText(encryptedSharedPreferencesManager.getString("neetScore", ""));
        if (encryptedSharedPreferencesManager.getBoolean("hasNeetScore", false)) {
            neetScoreYes.setChecked(true);
            neetScoreLayout.setVisibility(View.VISIBLE);
        } else {
            neetScoreNo.setChecked(true);
            neetScoreLayout.setVisibility(View.GONE);
        }
        if (encryptedSharedPreferencesManager.getBoolean("hasPassport", false)) {
            passportYes.setChecked(true);
        } else {
            passportNo.setChecked(true);
        }
    }

    private void saveDataToSharedPreferences(Map<String, Object> data) {
        if (data == null) return;
        encryptedSharedPreferencesManager.putString("name", Objects.toString(data.get("name"), ""));
        encryptedSharedPreferencesManager.putString("email", Objects.toString(data.get("email"), ""));
        encryptedSharedPreferencesManager.putString("mobile", Objects.toString(data.get("mobile"), ""));
        encryptedSharedPreferencesManager.putString("state", Objects.toString(data.get("state"), ""));
        encryptedSharedPreferencesManager.putString("city", Objects.toString(data.get("city"), ""));
        encryptedSharedPreferencesManager.putString("country", Objects.toString(data.get("country"), ""));
        encryptedSharedPreferencesManager.putString("neetScore", Objects.toString(data.get("neetScore"), ""));
        encryptedSharedPreferencesManager.putBoolean("hasNeetScore", Boolean.TRUE.equals(data.get("hasNeetScore")));
        encryptedSharedPreferencesManager.putBoolean("hasPassport", Boolean.TRUE.equals(data.get("hasPassport")));
    }

    private void setUpCountrySpinner() {
        if (context != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, countries);
            countrySpinner.setAdapter(adapter);
            countrySpinner.setThreshold(1);
        }
    }

    private void setupTextWatchers() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("name", s.toString());
                // Consider debouncing or throttling these updates for performance
                updateFirestore("name", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("email", s.toString());
                updateFirestore("email", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("mobile", s.toString());
                updateFirestore("mobile", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        countrySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("country", s.toString());
                updateFirestore("country", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        stateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("state", s.toString());
                updateFirestore("state", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("city", s.toString());
                updateFirestore("city", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        neetScoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                encryptedSharedPreferencesManager.putString("neetScore", s.toString());
                updateFirestore("neetScore", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setUpListeners() {
        neetScoreGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean hasNeet = checkedId == R.id.neetScoreYes;
            neetScoreLayout.setVisibility(hasNeet ? View.VISIBLE : View.GONE);
            encryptedSharedPreferencesManager.putBoolean("hasNeetScore", hasNeet);
            updateFirestore("hasNeetScore", hasNeet);
            if (!hasNeet) {
                encryptedSharedPreferencesManager.putString("neetScore", "");
                neetScoreEditText.setText("");
                updateFirestore("neetScore", "");
            }
        });

        passportGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean hasPassport = checkedId == R.id.passportYes;
            encryptedSharedPreferencesManager.putBoolean("hasPassport", hasPassport);
            updateFirestore("hasPassport", hasPassport);
        });
    }

    private boolean validateInputs() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String country = countrySpinner.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String neetScore = neetScoreEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.enter_your_name));
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.enter_valid_email));
            return false;
        }
        if (TextUtils.isEmpty(phone) || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError(getString(R.string.enter_valid_phone));
            return false;
        }
        if (TextUtils.isEmpty(country)) {
            countrySpinner.setError(getString(R.string.select_your_country));
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            stateEditText.setError(getString(R.string.enter_your_state));
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            cityEditText.setError(getString(R.string.enter_your_city));
            return false;
        }
        if (neetScoreGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), getString(R.string.select_neet_score_status));
            return false;
        }
        if (neetScoreYes.isChecked() && TextUtils.isEmpty(neetScore)) {
            neetScoreEditText.setError(getString(R.string.enter_your_neet_score));
            return false;
        }
        if (passportGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), getString(R.string.select_passport_status));
            return false;
        }
        return true;
    }

    private void saveOrUpdateApplication() {
        if (context == null || mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String country = countrySpinner.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String neetScore = neetScoreEditText.getText().toString().trim();
        boolean hasNeetScore = neetScoreYes.isChecked();
        boolean hasPassport = passportYes.isChecked();
        String applicationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("userId", userId);
        applicationData.put("name", name);
        applicationData.put("email", email);
        applicationData.put("mobile", phone);
        applicationData.put("country", country);
        applicationData.put("state", state);
        applicationData.put("city", city);
        applicationData.put("neetScore", hasNeetScore ? neetScore : "");
        applicationData.put("hasNeetScore", hasNeetScore);
        applicationData.put("hasPassport", hasPassport);
        applicationData.put("applicationDate", applicationDate);
        if (university != null) {
            applicationData.put("universityId", university.getId());
            applicationData.put("universityName", university.getName());
        }
        if (!TextUtils.isEmpty(offer)) {
            applicationData.put("appliedForOffer", offer);
        }

        if (isExistingData && existingDocumentId != null) {
            firestore.collection("app_submissions").document(existingDocumentId)
                    .update(applicationData)
                    .addOnSuccessListener(aVoid -> {
                        if (context != null) {
                            CustomToast.showToast(requireActivity(), "Application updated successfully");
                        }
                    })
                    .addOnFailureListener(e ->  {
                        Log.e(TAG, "Error updating application", e);
                        if (context != null) {
                            CustomToast.showToast(requireActivity(), "Failed to update application: " + e.getMessage());
                        }
                    });
        } else {
            firestore.collection("app_submissions")
                    .add(applicationData)
                    .addOnSuccessListener(documentReference -> {
                        if (context != null) {
                            CustomToast.showToast(requireActivity(), "Application submitted successfully");
                        }
                        existingDocumentId = documentReference.getId();
                        isExistingData = true;
                        submitButton.setText("Update Application");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error submitting application", e);
                        if (context != null) {
                            CustomToast.showToast(requireActivity(), "Failed to submit application: " + e.getMessage());
                        }
                    });
        }
        //update shared preferences
        saveDataToSharedPreferences(applicationData);
    }

    private void updateFirestore(String field, Object value) {
        if (context == null || mAuth.getCurrentUser() == null) return;
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        if (isExistingData && existingDocumentId != null) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put(field, value);
            firestore.collection("app_submissions").document(existingDocumentId)
                    .update(updateData)
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update " + field, e);
                        if (context != null) {
                            // Log the error or show a generic message if needed
                            CustomToast.showToast(requireActivity(), "Failed to update " + field + ": " + e.getMessage());
                        }
                    });
        } else if (mAuth.getCurrentUser() != null) {
            // If no existing document, try to find one based on user ID
            firestore.collection("app_submissions")
                    .whereEqualTo("userId", userId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            existingDocumentId = documentSnapshot.getId();
                            isExistingData = true;
                            submitButton.setText("Update Application");
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put(field, value);
                            firestore.collection("app_submissions").document(existingDocumentId) // Corrected collection name
                                    .update(updateData)
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to update " + field, e);
                                        if (context != null) {
                                            // Log the error
                                            CustomToast.showToast(requireActivity(), "Failed to update " + field + ": " + e.getMessage());
                                        }
                                    });
                        } else {
                            //No document found.  You might want to create a new one.
                            Map<String, Object> newData = new HashMap<>();
                            newData.put(field, value);
                            newData.put("userId", userId); //  Include userId
                            firestore.collection("app_submissions")
                                    .add(newData)
                                    .addOnSuccessListener(documentReference -> {
                                        if (context != null) {
                                            existingDocumentId = documentReference.getId();
                                            isExistingData = true;
                                            submitButton.setText("Update Application");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to create new document", e);
                                        if (context != null){
                                            CustomToast.showToast(requireActivity(), "Failed to create new document: " + e.getMessage());
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null; // Prevent further operations on a detached context.
    }
}
