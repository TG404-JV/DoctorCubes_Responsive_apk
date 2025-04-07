package com.tvm.doctorcube.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CollectUserDetailsFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, stateEditText, cityEditText, neetScoreEditText;
    private AutoCompleteTextView countrySpinner;
    private RadioGroup neetScoreGroup, passportGroup;
    private RadioButton neetScoreYes, neetScoreNo, passportYes, passportNo;
    private TextInputLayout neetScoreLayout;
    private Button submitButton;
    private NavController navController;
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth mAuth;
    private ImageView backButton;

    private  String[] countries ;
    private String userId;
    private String userEmail;
    private String userPhone;

    private boolean isBottomSheet = false;

    public CollectUserDetailsFragment() {
        // Required empty public constructor
    }

    public CollectUserDetailsFragment(boolean isBottomSheet) {
        this.isBottomSheet = isBottomSheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);

        // Initialize UI elements
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
        backButton = view.findViewById(R.id.backButton);

        countries = getResources().getStringArray(R.array.countries_array);

        String name,email,phone;

        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager =new EncryptedSharedPreferencesManager(getContext());
        name = encryptedSharedPreferencesManager.getString("name", "");
        email = encryptedSharedPreferencesManager.getString("email", "");
        phone = encryptedSharedPreferencesManager.getString("mobile", "");

        nameEditText.setText(name);
        emailEditText.setText(email);
        phoneEditText.setText(phone);
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);


        // Initialize FirestoreHelper

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userEmail = currentUser.getEmail();
            userPhone = currentUser.getPhoneNumber();
        } else {
            CustomToast.showToast(requireActivity(), "Please Login");
            navController.navigate(R.id.loginFragment2);
            return;
        }

        // Set up UI components and fetch user data
        setUpCountrySpinner();
        setUpListeners();



    }

    private void setUpCountrySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, countries);
        countrySpinner.setAdapter(adapter);
        countrySpinner.setThreshold(1);
    }

    private void setUpListeners() {
        neetScoreGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.neetScoreYes) {
                neetScoreLayout.setVisibility(View.VISIBLE);
            } else {
                neetScoreLayout.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                Map<String, Object> userData = getUserData();
                if (userId != null && !userId.isEmpty()) {
                    //  Move the navigation and shared preference updates into the success listener
                    firestoreDB.collection("app_submissions").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            CustomToast.showToast(requireActivity(),"Data Uploaded Successfully");

                            EncryptedSharedPreferencesManager encryptedSharedPreferencesManager =new EncryptedSharedPreferencesManager(getContext());
                            encryptedSharedPreferencesManager.putBoolean("isdataloaded",true);

                            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();

                                if (value instanceof String) {
                                    encryptedSharedPreferencesManager.putString(key, (String) value);
                                } else if (value instanceof Boolean) {
                                    encryptedSharedPreferencesManager.putBoolean(key, (Boolean) value);
                                } else if (value instanceof Integer) {
                                    encryptedSharedPreferencesManager.putInt(key, (Integer) value);
                                } else if (value instanceof Long) {
                                    encryptedSharedPreferencesManager.putLong(key, (Long) value);
                                } else {
                                    // Optional: Convert other types (like FieldValue) to String or handle them accordingly
                                    encryptedSharedPreferencesManager.putString(key, value.toString());
                                }
                            }
                            encryptedSharedPreferencesManager.putBoolean("isFormSubmitted",true);
                            firestoreDB.collection("Users").document(mAuth.getCurrentUser().getUid()).update("isFormSubmitted",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                            navController.navigate(R.id.mainActivity2);

                        }

                    });
                } else {
                    CustomToast.showToast(requireActivity(), "Missing user information. Please sign in again.");
                }
            }
        });

        backButton.setOnClickListener(v -> navController.popBackStack());
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
            nameEditText.setError("Enter your name");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Enter your phone number");
            return false;
        }
        if (TextUtils.isEmpty(country)) {
            countrySpinner.setError("Select your country");
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            stateEditText.setError("Enter your state/province");
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            cityEditText.setError("Enter your city");
            return false;
        }
        if (neetScoreGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), "Select whether you have a NEET score");
            return false;
        }
        if (neetScoreYes.isChecked() && TextUtils.isEmpty(neetScore)) {
            neetScoreEditText.setError("Enter your NEET score");
            return false;
        }
        if (passportGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), "Select whether you have a passport");
            return false;
        }
        return true;
    }

    private Map<String, Object> getUserData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String country = countrySpinner.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String neetScore = neetScoreEditText.getText().toString().trim();
        boolean hasNeetScore = neetScoreYes.isChecked();
        boolean hasPassport = passportYes.isChecked();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name); // Match UserDataManager key
        userData.put("email", email);   // Match UserDataManager key
        userData.put("mobile", phone);   // Match UserDataManager key
        userData.put("country", country);
        userData.put("state", state);
        userData.put("city", city);
        userData.put("hasNeetScore", hasNeetScore);
        if (hasNeetScore) {
            userData.put("neetScore", neetScore);
        }
        userData.put("hasPassport", hasPassport);
        userData.put("userId", userId);
        userData.put("isAdmitted",false);
        userData.put("lastCallDate","Not Called Yet");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        userData.put("lastUpdatedDate",String.valueOf(sdf.format(System.currentTimeMillis())));
        userData.put("timestamp", FieldValue.serverTimestamp());

        return userData;
    }

}
