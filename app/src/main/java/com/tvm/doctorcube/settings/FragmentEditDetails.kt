package com.tvm.doctorcube.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tvm.doctorcube.R
import com.tvm.doctorcube.databinding.FragmentEditDetailsBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmentEditDetails : Fragment() {
    private var _binding: FragmentEditDetailsBinding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth? = null
    private var userRef: DocumentReference? = null
    private var storageRef: StorageReference? = null
    private var documentListener: ListenerRegistration? = null

    private var isEditMode = false
    private var imageUri: Uri? = null
    private var cachedUserData: DocumentSnapshot? = null
    private var hasUnsavedChanges = false
    private var lastKnownData = mutableMapOf<String, Any?>()

    private val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            imageUri?.let {
                Glide.with(this).load(it).into(binding.profileImageView)
                hasUnsavedChanges = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFirebase()
        setupRealtimeListener()
        setupClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        documentListener?.remove()
        _binding = null
    }

    private fun initializeFirebase() {
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth?.currentUser?.uid ?: run {
            showToast("User not authenticated")
            setDefaultUI()
            return
        }
        userRef = FirebaseFirestore.getInstance().collection("app_submissions").document(userId)
        storageRef = FirebaseStorage.getInstance().getReference("profile_images")
            .child("$userId/profile.jpg")
    }

    // Use real-time listener instead of manual refresh
    private fun setupRealtimeListener() {
        userRef?.let { ref ->
            documentListener = ref.addSnapshotListener { documentSnapshot, error ->
                if (!isAdded) return@addSnapshotListener

                error?.let {
                    handleFirebaseError(it)
                    return@addSnapshotListener
                }

                documentSnapshot?.let { snapshot ->
                    if (snapshot.exists()) {
                        cachedUserData = snapshot
                        updateUIFromCache()
                        cacheCurrentData(snapshot)
                    } else {
                        showToast("No user data found")
                        setDefaultUI()
                    }
                }
            }
        }
    }

    private fun cacheCurrentData(snapshot: DocumentSnapshot) {
        lastKnownData.clear()
        snapshot.data?.let { data ->
            lastKnownData.putAll(data)
        }
    }

    private fun updateUIFromCache() {
        cachedUserData?.let { snapshot ->
            val userData = UserData.fromSnapshot(snapshot)
            populateUI(userData)
            loadProfileImageOnce()
        }
    }

    private fun populateUI(userData: UserData) {
        with(binding) {
            // Header Section
            setTextAndVisibility(userNameDisplay, userData.name, "Unknown User")
            applicationStatusText.text = userData.status
            setTextAndVisibility(neetScoreDisplay, userData.neetScore, "0")
            setTextAndVisibility(universityDisplay, userData.universityName, "Unknown University")
            setTextAndVisibility(studyCountryDisplay, userData.studyCountry, "Unknown")

            // Application Status Section
            appliedDateText.text = userData.appliedDate
            lastUpdatedText.text = userData.lastUpdated
            setTextAndVisibility(lastCallDateText, userData.lastCallDate, hideIfDefault = "Not Called Yet")
            setTextAndVisibility(universityNameText, userData.universityName, "Unknown University")

            // Edit Profile Section - only populate if in edit mode or first load
            if (!isEditMode || !hasUnsavedChanges) {
                populateEditFields(userData)
            }

            // Set visibility based on edit mode and data availability
            updateFieldVisibility(userData)

            // Set status color
            val statusColor = when {
                userData.isAdmitted -> R.color.medical_accent_green
                userData.status == "Under Review" -> R.color.status_warning
                else -> R.color.medical_accent_green
            }
            applicationStatusText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), statusColor)
        }
    }

    private fun populateEditFields(userData: UserData) {
        with(binding) {
            nameEditText.setText(userData.name)
            emailEditText.setText(userData.email)
            mobileEditText.setText(userData.mobile)
            countryEditText.setText(userData.country)
            stateEditText.setText(userData.state)
            cityEditText.setText(userData.city)
            neetScoreEditText.setText(userData.neetScore)
            studyCountryEditText.setText(userData.studyCountry)
            hasNeetScoreCheckbox.isChecked = userData.hasNeetScore
            hasPassportCheckbox.isChecked = userData.hasPassport
        }
    }

    private fun updateFieldVisibility(userData: UserData) {
        with(binding) {
            if (isEditMode) {
                // Show all fields in edit mode
                listOf(
                    nameInputLayout, emailInputLayout, mobileInputLayout,
                    countryInputLayout, stateInputLayout, cityInputLayout,
                    neetScoreInputLayout, studyCountryInputLayout,
                    hasNeetScoreCheckbox, hasPassportCheckbox
                ).forEach { it.visibility = View.VISIBLE }
            } else {
                // Hide empty fields in view mode
                nameInputLayout.visibility = if (userData.name.isNullOrBlank()) View.GONE else View.VISIBLE
                emailInputLayout.visibility = if (userData.email.isNullOrBlank()) View.GONE else View.VISIBLE
                mobileInputLayout.visibility = if (userData.mobile.isNullOrBlank()) View.GONE else View.VISIBLE
                countryInputLayout.visibility = if (userData.country.isNullOrBlank()) View.GONE else View.VISIBLE
                stateInputLayout.visibility = if (userData.state.isNullOrBlank()) View.GONE else View.VISIBLE
                cityInputLayout.visibility = if (userData.city.isNullOrBlank()) View.GONE else View.VISIBLE
                neetScoreInputLayout.visibility = if (userData.neetScore.isNullOrBlank()) View.GONE else View.VISIBLE
                studyCountryInputLayout.visibility = if (userData.studyCountry.isNullOrBlank()) View.GONE else View.VISIBLE
                hasNeetScoreCheckbox.visibility = if (!userData.hasNeetScore) View.GONE else View.VISIBLE
                hasPassportCheckbox.visibility = if (!userData.hasPassport) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setTextAndVisibility(view: View, value: String?, defaultValue: String = "", hideIfDefault: String? = null) {
        when (view) {
            is androidx.appcompat.widget.AppCompatTextView -> {
                view.text = value ?: defaultValue
                val shouldHide = value.isNullOrBlank() || (hideIfDefault != null && value == hideIfDefault)
                (view.parent as? View)?.visibility = if (shouldHide) View.GONE else View.VISIBLE
            }
        }
    }

    private var profileImageLoaded = false
    private fun loadProfileImageOnce() {
        if (profileImageLoaded) return

        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            if (isAdded) {
                Glide.with(this@FragmentEditDetails)
                    .load(uri)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.profileImageView)
                profileImageLoaded = true
            }
        }?.addOnFailureListener {
            if (isAdded) {
                binding.profileImageView.setImageResource(R.drawable.ic_profile)
                profileImageLoaded = true
            }
        }
    }

    private fun setDefaultUI() {
        val defaultUserData = UserData()
        populateUI(defaultUserData)
        binding.applicationStatusText.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.status_warning)
        loadProfileImageOnce()
    }

    private fun setupClickListeners() {
        with(binding) {
            editModeToggleBtn.setOnClickListener { toggleEditMode() }
            cancelBtn.setOnClickListener { cancelChanges() }
            saveBtn.setOnClickListener { saveChanges() }
            editProfileImageBtn.setOnClickListener { pickImage() }
            callBtn.setOnClickListener { initiateCall() }
            whatsappBtn.setOnClickListener { initiateWhatsApp() }
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        hasUnsavedChanges = false

        with(binding) {
            editModeToggleBtn.text = if (isEditMode) "Done" else "Edit"
            actionButtonsLayout.visibility = if (isEditMode) View.VISIBLE else View.GONE
            editProfileImageBtn.visibility = if (isEditMode) View.VISIBLE else View.GONE
        }

        setFieldsEditable(isEditMode)
        cachedUserData?.let {
            val userData = UserData.fromSnapshot(it)
            updateFieldVisibility(userData)
        }
    }

    private fun cancelChanges() {
        hasUnsavedChanges = false
        imageUri = null
        cachedUserData?.let {
            val userData = UserData.fromSnapshot(it)
            populateEditFields(userData)
        }
        toggleEditMode()
    }

    private fun setFieldsEditable(editable: Boolean) {
        with(binding) {
            listOf(
                nameEditText, emailEditText, mobileEditText,
                countryEditText, stateEditText, cityEditText,
                neetScoreEditText, studyCountryEditText,
                hasNeetScoreCheckbox, hasPassportCheckbox
            ).forEach { it.isEnabled = editable }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }

    private fun saveChanges() {
        userRef ?: return

        val newData = collectFormData()
        val changedFields = getChangedFields(newData)

        if (changedFields.isEmpty() && imageUri == null) {
            showToast("No changes to save")
            toggleEditMode()
            return
        }

        lifecycleScope.launch {
            try {
                // Only update changed fields
                if (changedFields.isNotEmpty()) {
                    changedFields["lastUpdatedDate"] = dateFormat.format(Date())
                    userRef?.update(changedFields)?.await()
                }

                // Upload image if changed
                imageUri?.let { uri ->
                    storageRef?.putFile(uri)?.await()
                    profileImageLoaded = false // Force reload
                    imageUri = null
                }

                if (isAdded) {
                    showToast("Profile updated successfully")
                    hasUnsavedChanges = false
                    toggleEditMode()
                }
            } catch (e: Exception) {
                if (isAdded) {
                    handleFirebaseError(e)
                }
            }
        }
    }

    private fun collectFormData(): Map<String, Any?> {
        return with(binding) {
            mapOf(
                "name" to nameEditText.text.toString().trim(),
                "email" to emailEditText.text.toString().trim(),
                "mobile" to mobileEditText.text.toString().trim(),
                "country" to countryEditText.text.toString().trim(),
                "state" to stateEditText.text.toString().trim(),
                "city" to cityEditText.text.toString().trim(),
                "neetScore" to neetScoreEditText.text.toString().trim(),
                "studyCountry" to studyCountryEditText.text.toString().trim(),
                "hasNeetScore" to hasNeetScoreCheckbox.isChecked,
                "hasPassport" to hasPassportCheckbox.isChecked
            )
        }
    }

    private fun getChangedFields(newData: Map<String, Any?>): MutableMap<String, Any?> {
        val changes = mutableMapOf<String, Any?>()

        newData.forEach { (key, newValue) ->
            val oldValue = lastKnownData[key]
            if (oldValue != newValue) {
                changes[key] = newValue
            }
        }

        return changes
    }

    private fun initiateCall() {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${getString(R.string.whatsapp_number)}")
        }
        startActivity(callIntent)

        // Only update if call date has actually changed
        val today = dateFormat.format(Date())
        val lastCallDate = lastKnownData["lastCallDate"] as? String

        if (lastCallDate != today) {
            lifecycleScope.launch {
                try {
                    userRef?.update("lastCallDate", today)?.await()
                } catch (e: Exception) {
                    if (isAdded) {
                        showToast("Failed to update call date: ${e.message}")
                    }
                }
            }
        }
    }

    private fun initiateWhatsApp() {
        val phoneNumber = getString(R.string.whatsapp_number)
        val url = "https://wa.me/$phoneNumber"
        val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(whatsappIntent)
    }

    private fun handleFirebaseError(exception: Exception) {
        val message = when (exception) {
            is FirebaseFirestoreException -> {
                if (exception.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    "Permission denied. Please check your authentication status."
                } else {
                    "Database error: ${exception.message}"
                }
            }
            else -> "Failed to process request: ${exception.message}"
        }
        showToast(message)
    }

    private fun showToast(message: String) {
        if (isAdded) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Data class to structure user data
    private data class UserData(
        val name: String? = null,
        val email: String? = null,
        val mobile: String? = null,
        val country: String? = null,
        val state: String? = null,
        val city: String? = null,
        val neetScore: String? = null,
        val studyCountry: String? = null,
        val universityName: String? = null,
        val lastCallDate: String = "Not Called Yet",
        val hasNeetScore: Boolean = false,
        val hasPassport: Boolean = false,
        val isAdmitted: Boolean = false,
        val isApplied: Boolean = false,
        val appliedDate: String = "N/A",
        val lastUpdated: String = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())
    ) {
        val status: String
            get() = when {
                isAdmitted -> "Admitted"
                isApplied -> "Application Submitted"
                lastCallDate == "Not Called Yet" -> "Under Review"
                else -> "Applied"
            }

        companion object {
            fun fromSnapshot(snapshot: DocumentSnapshot): UserData {
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

                return UserData(
                    name = snapshot.getString("name"),
                    email = snapshot.getString("email"),
                    mobile = snapshot.getString("mobile"),
                    country = snapshot.getString("country"),
                    state = snapshot.getString("state"),
                    city = snapshot.getString("city"),
                    neetScore = snapshot.getString("neetScore"),
                    studyCountry = snapshot.getString("studyCountry"),
                    universityName = snapshot.getString("universityName"),
                    lastCallDate = snapshot.getString("lastCallDate") ?: "Not Called Yet",
                    hasNeetScore = snapshot.getBoolean("hasNeetScore") ?: false,
                    hasPassport = snapshot.getBoolean("hasPassport") ?: false,
                    isAdmitted = snapshot.getBoolean("isAdmitted") ?: false,
                    isApplied = snapshot.getBoolean("isApplied") ?: false,
                    appliedDate = snapshot.getTimestamp("timestamp")?.let {
                        try { dateFormat.format(it.toDate()) } catch (e: Exception) { "N/A" }
                    } ?: "N/A",
                    lastUpdated = snapshot.getString("lastUpdatedDate")?.let {
                        try {
                            dateFormat.parse(it)?.let { dateFormat.format(it) } ?: dateFormat.format(Date())
                        } catch (e: ParseException) {
                            dateFormat.format(Date())
                        }
                    } ?: dateFormat.format(Date())
                )
            }
        }
    }
}