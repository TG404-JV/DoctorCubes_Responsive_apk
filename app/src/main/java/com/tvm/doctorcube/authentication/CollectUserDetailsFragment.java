package com.tvm.doctorcube.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.MainActivity;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CollectUserDetailsFragment extends Fragment {

    private TextInputEditText nameEditText, emailEditText, phoneEditText, neetScoreEditText;
    private AutoCompleteTextView stateEditText, cityEditText, studyCountrySpinner;
    private TextInputLayout countryInputLayout, stateInputLayout, cityInputLayout, neetScoreLayout;
    private RadioGroup neetScoreGroup, passportGroup;
    private RadioButton neetScoreYes, neetScoreNo, passportYes, passportNo;
    private MaterialButton submitButton;
    private MaterialButton nextButtonStep1, nextButtonStep2, nextButtonStep3, nextButtonStep4, nextButtonStep5, nextButtonStep6;
    private MaterialButton prevButtonStep2, prevButtonStep3, prevButtonStep4, prevButtonStep5, prevButtonStep6, prevButtonStep7;
    private ImageView backButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private LinearLayout personalInfoSection, contactInfoSection, locationSection, medicalEducationSection, passportSection, studyDestinationSection, submitSection;

    private String userId;
    private String userEmail;
    private String userPhone;
    private boolean isBottomSheet = false;
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth mAuth;
    private NavController navController;

    // JSON data
    private Map<String, List<String>> stateCityMap = new HashMap<>();
    private List<String> states = new ArrayList<>();
    private final String COUNTRY = "India";

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
        studyCountrySpinner = view.findViewById(R.id.studyCountrySpinner);
        countryInputLayout = view.findViewById(R.id.countryInputLayout);
        stateInputLayout = view.findViewById(R.id.stateInputLayout);
        cityInputLayout = view.findViewById(R.id.cityInputLayout);
        neetScoreGroup = view.findViewById(R.id.neetScoreGroup);
        passportGroup = view.findViewById(R.id.passportGroup);
        neetScoreYes = view.findViewById(R.id.neetScoreYes);
        neetScoreNo = view.findViewById(R.id.neetScoreNo);
        passportYes = view.findViewById(R.id.passportYes);
        passportNo = view.findViewById(R.id.passportNo);
        neetScoreLayout = view.findViewById(R.id.neetScoreLayout);
        submitButton = view.findViewById(R.id.submitButton);
        nextButtonStep1 = view.findViewById(R.id.nextButtonStep1);
        nextButtonStep2 = view.findViewById(R.id.nextButtonStep2);
        nextButtonStep3 = view.findViewById(R.id.nextButtonStep3);
        nextButtonStep4 = view.findViewById(R.id.nextButtonStep4);
        nextButtonStep5 = view.findViewById(R.id.nextButtonStep5);
        nextButtonStep6 = view.findViewById(R.id.nextButtonStep6);
        prevButtonStep2 = view.findViewById(R.id.prevButtonStep2);
        prevButtonStep3 = view.findViewById(R.id.prevButtonStep3);
        prevButtonStep4 = view.findViewById(R.id.prevButtonStep4);
        prevButtonStep5 = view.findViewById(R.id.prevButtonStep5);
        prevButtonStep6 = view.findViewById(R.id.prevButtonStep6);
        prevButtonStep7 = view.findViewById(R.id.prevButtonStep7);
        backButton = view.findViewById(R.id.backButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        personalInfoSection = view.findViewById(R.id.personalInfoSection);
        contactInfoSection = view.findViewById(R.id.contactInfoSection);
        locationSection = view.findViewById(R.id.locationSection);
        medicalEducationSection = view.findViewById(R.id.medicalEducationSection);
        passportSection = view.findViewById(R.id.passportSection);
        studyDestinationSection = view.findViewById(R.id.studyDestinationSection);
        submitSection = view.findViewById(R.id.submitSection);

        // Load JSON data
        loadIndiaCityStateJson();

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

        // Load saved data
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(getContext());
        nameEditText.setText(prefs.getString("name", ""));
        emailEditText.setText(prefs.getString("email", ""));
        phoneEditText.setText(prefs.getString("mobile", ""));
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);

        // Set country to India (non-editable)
        countryInputLayout.setEnabled(false);
        TextInputEditText countrySpinner = view.findViewById(R.id.countrySpinner);
        countrySpinner.setText(COUNTRY);
        countrySpinner.setEnabled(false);

        // Set up UI components
        setUpStateSpinner();
        setUpStudyCountrySpinner();
        setUpListeners();
    }

    private void loadIndiaCityStateJson() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.india_city_state);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            inputStream.close();

            JSONObject jsonObject = new JSONObject(jsonString.toString());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String state = keys.next();
                states.add(state);
                List<String> cities = new ArrayList<>();
                org.json.JSONArray cityArray = jsonObject.getJSONArray(state);
                for (int i = 0; i < cityArray.length(); i++) {
                    cities.add(cityArray.getString(i));
                }
                stateCityMap.put(state, cities);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomToast.showToast(requireActivity(), "Error loading city/state data");
        }
    }

    private void setUpStateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, states);
        stateEditText.setAdapter(adapter);
        stateEditText.setThreshold(1);
        stateEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = stateEditText.getText().toString().trim();
            updateCitySuggestions(selectedState);
        });
    }

    private void setUpStudyCountrySpinner() {
        String[] studyCountries = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, studyCountries);
        studyCountrySpinner.setAdapter(adapter);
        studyCountrySpinner.setThreshold(1);
    }

    private void updateCitySuggestions(String state) {
        List<String> cities = stateCityMap.getOrDefault(state, new ArrayList<>());
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityEditText.setAdapter(cityAdapter);
        cityEditText.setThreshold(1);
    }

    private void revealSection(LinearLayout section, int step) {
        if (section.getVisibility() != View.VISIBLE) {
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            section.startAnimation(fadeIn);
            section.setVisibility(View.VISIBLE);
            section.setAlpha(1.0f);
            progressBar.setProgress(step);
            progressText.setText(step + "/7");
        }
    }

    private void hideSection(LinearLayout section) {
        if (section.getVisibility() == View.VISIBLE) {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            section.startAnimation(fadeOut);
            section.setVisibility(View.GONE);
            section.setAlpha(0.0f);
        }
    }

    private void setUpListeners() {
        // Step 1: Personal Information
        nextButtonStep1.setOnClickListener(v -> {
            if (validateStep1()) {
                hideSection(personalInfoSection);
                revealSection(contactInfoSection, 2);
            }
        });

        // Step 2: Contact Information
        prevButtonStep2.setOnClickListener(v -> {
            hideSection(contactInfoSection);
            revealSection(personalInfoSection, 1);
        });
        nextButtonStep2.setOnClickListener(v -> {
            if (validateStep2()) {
                hideSection(contactInfoSection);
                revealSection(locationSection, 3);
            }
        });

        // Step 3: Location
        prevButtonStep3.setOnClickListener(v -> {
            hideSection(locationSection);
            revealSection(contactInfoSection, 2);
        });
        nextButtonStep3.setOnClickListener(v -> {
            if (validateStep3()) {
                hideSection(locationSection);
                revealSection(medicalEducationSection, 4);
            }
        });

        // Step 4: NEET Score
        neetScoreGroup.setOnCheckedChangeListener((group, checkedId) -> {
            neetScoreLayout.setVisibility(checkedId == R.id.neetScoreYes ? View.VISIBLE : View.GONE);
        });
        prevButtonStep4.setOnClickListener(v -> {
            hideSection(medicalEducationSection);
            revealSection(locationSection, 3);
        });
        nextButtonStep4.setOnClickListener(v -> {
            if (validateStep4()) {
                hideSection(medicalEducationSection);
                revealSection(passportSection, 5);
            }
        });

        // Step 5: Passport
        prevButtonStep5.setOnClickListener(v -> {
            hideSection(passportSection);
            revealSection(medicalEducationSection, 4);
        });
        nextButtonStep5.setOnClickListener(v -> {
            if (validateStep5()) {
                hideSection(passportSection);
                revealSection(studyDestinationSection, 6);
            }
        });

        // Step 6: Study Destination
        prevButtonStep6.setOnClickListener(v -> {
            hideSection(studyDestinationSection);
            revealSection(passportSection, 5);
        });
        nextButtonStep6.setOnClickListener(v -> {
            if (validateStep6()) {
                hideSection(studyDestinationSection);
                revealSection(submitSection, 7);
            }
        });

        // Step 7: Submit
        prevButtonStep7.setOnClickListener(v -> {
            hideSection(submitSection);
            revealSection(studyDestinationSection, 6);
        });
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                Map<String, Object> userData = getUserData();
                if (userId != null && !userId.isEmpty()) {
                    firestoreDB.collection("app_submissions")
                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .set(userData)
                            .addOnSuccessListener(unused -> {
                                CustomToast.showToast(requireActivity(), "Data Uploaded Successfully");
                                EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(getContext());
                                prefs.putBoolean("isdataloaded", true);
                                for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    if (value instanceof String) {
                                        prefs.putString(key, (String) value);
                                    } else if (value instanceof Boolean) {
                                        prefs.putBoolean(key, (Boolean) value);
                                    } else if (value instanceof Integer) {
                                        prefs.putInt(key, (Integer) value);
                                    } else if (value instanceof Long) {
                                        prefs.putLong(key, (Long) value);
                                    } else {
                                        prefs.putString(key, value.toString());
                                    }
                                }
                                prefs.putBoolean("isFormSubmitted", true);
                                firestoreDB.collection("Users")
                                        .document(mAuth.getCurrentUser().getUid())
                                        .update("isFormSubmitted", true);
                                startActivity(new Intent(getContext(), MainActivity.class));
                            });
                } else {
                    CustomToast.showToast(requireActivity(), "Missing user information. Please sign in again.");
                }
            }
        });

        backButton.setOnClickListener(v -> navController.popBackStack());
    }

    private boolean validateStep1() {
        String name = nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Enter your name");
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            emailEditText.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            phoneEditText.setError("Enter a valid phone number");
            return false;
        }
        return true;
    }

    private boolean validateStep3() {
        String state = stateEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(state) || !states.contains(state)) {
            stateEditText.setError("Select a valid state from the list");
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            cityEditText.setError("Enter your city");
            return false;
        }
        return true;
    }

    private boolean validateStep4() {
        if (neetScoreGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), "Select whether you have a NEET score");
            return false;
        }
        if (neetScoreYes.isChecked() && TextUtils.isEmpty(neetScoreEditText.getText().toString().trim())) {
            neetScoreEditText.setError("Enter your NEET score");
            return false;
        }
        return true;
    }

    private boolean validateStep5() {
        if (passportGroup.getCheckedRadioButtonId() == -1) {
            CustomToast.showToast(requireActivity(), "Select whether you have a passport");
            return false;
        }
        return true;
    }

    private boolean validateStep6() {
        String studyCountry = studyCountrySpinner.getText().toString().trim();
        if (TextUtils.isEmpty(studyCountry)) {
            studyCountrySpinner.setError("Select your preferred study country");
            return false;
        }
        return true;
    }

    private boolean validateInputs() {
        return validateStep1() && validateStep2() && validateStep3() && validateStep4() && validateStep5() && validateStep6();
    }

    private Map<String, Object> getUserData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String neetScore = neetScoreEditText.getText().toString().trim();
        boolean hasNeetScore = neetScoreYes.isChecked();
        boolean hasPassport = passportYes.isChecked();
        String studyCountry = studyCountrySpinner.getText().toString().trim();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("mobile", phone);
        userData.put("country", COUNTRY);
        userData.put("state", state);
        userData.put("city", city);
        userData.put("hasNeetScore", hasNeetScore);
        if (hasNeetScore) {
            userData.put("neetScore", neetScore);
        }
        userData.put("hasPassport", hasPassport);
        userData.put("studyCountry", studyCountry);
        userData.put("userId", userId);
        userData.put("isAdmitted", false);
        userData.put("lastCallDate", "Not Called Yet");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        userData.put("lastUpdatedDate", sdf.format(System.currentTimeMillis()));
        userData.put("timestamp", FieldValue.serverTimestamp());

        return userData;
    }
}