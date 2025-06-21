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
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tvm.doctorcube.R
import com.tvm.doctorcube.databinding.FragmentEditDetailsBinding
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
    private var isEditMode = false
    private var imageUri: Uri? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            imageUri?.let {
                Glide.with(this).load(it).into(binding.profileImageView)
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
        loadUserData()
        setupClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeFirebase() {
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth?.currentUser?.uid ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            setDefaultUI()
            return
        }
        userRef = FirebaseFirestore.getInstance().collection("app_submissions").document(userId)
        storageRef = FirebaseStorage.getInstance().getReference("profile_images")
            .child("$userId/profile.jpg")
    }

    private fun loadUserData() {
        userRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (!isAdded) return@addOnSuccessListener
            if (documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name") ?: "Unknown User"
                val email = documentSnapshot.getString("email") ?: "No Email"
                val mobile = documentSnapshot.getString("mobile") ?: "No Mobile"
                val country = documentSnapshot.getString("country") ?: "Unknown"
                val state = documentSnapshot.getString("state") ?: "Unknown"
                val city = documentSnapshot.getString("city") ?: "Unknown"
                val neetScore = documentSnapshot.getString("neetScore") ?: "0"
                val studyCountry = documentSnapshot.getString("studyCountry") ?: "Unknown"
                val universityName = documentSnapshot.getString("universityName") ?: "Unknown University"
                val lastCallDate = documentSnapshot.getString("lastCallDate") ?: "Not Called Yet"
                val hasNeetScore = documentSnapshot.getBoolean("hasNeetScore") ?: false
                val hasPassport = documentSnapshot.getBoolean("hasPassport") ?: false
                val isAdmitted = documentSnapshot.getBoolean("isAdmitted") ?: false
                val isApplied = documentSnapshot.getBoolean("isApplied") ?: false

                val appliedDate = documentSnapshot.getTimestamp("timestamp")?.let { formatTimestamp(it) } ?: "N/A"
                val lastUpdated = documentSnapshot.getString("lastUpdatedDate")?.let { formatStringDate(it) }
                    ?: dateFormat.format(Date())

                val status = when {
                    isAdmitted -> "Admitted"
                    isApplied -> "Application Submitted"
                    lastCallDate == "Not Called Yet" -> "Under Review"
                    else -> "Applied"
                }

                with(binding) {
                    userNameDisplay.text = name
                    applicationStatusText.text = status
                    neetScoreDisplay.text = neetScore
                    universityDisplay.text = universityName
                    studyCountryDisplay.text = studyCountry
                    appliedDateText.text = appliedDate
                    lastUpdatedText.text = lastUpdated
                    lastCallDateText.text = lastCallDate
                    universityNameText.text = universityName
                    nameEditText.setText(name)
                    emailEditText.setText(email)
                    mobileEditText.setText(mobile)
                    countryEditText.setText(country)
                    stateEditText.setText(state)
                    cityEditText.setText(city)
                    neetScoreEditText.setText(neetScore)
                    studyCountryEditText.setText(studyCountry)
                    hasNeetScoreCheckbox.isChecked = hasNeetScore
                    hasPassportCheckbox.isChecked = hasPassport

                    val statusColor = when {
                        isAdmitted -> R.color.medical_accent_green
                        status == "Under Review" -> R.color.status_warning
                        else -> R.color.medical_accent_green
                    }
                    applicationStatusText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), statusColor)
                }

                loadProfileImage()
            } else {
                Toast.makeText(context, "No user data found", Toast.LENGTH_SHORT).show()
                setDefaultUI()
            }
        }?.addOnFailureListener { e ->
            if (!isAdded) return@addOnFailureListener
            when (e) {
                is FirebaseFirestoreException -> if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    Toast.makeText(context, "Permission denied. Please check your authentication status.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            setDefaultUI()
        }
    }

    private fun setDefaultUI() {
        val defaultValue = "N/A"
        with(binding) {
            userNameDisplay.text = "Unknown User"
            applicationStatusText.text = "Under Review"
            neetScoreDisplay.text = "0"
            universityDisplay.text = "Unknown University"
            studyCountryDisplay.text = "Unknown"
            appliedDateText.text = defaultValue
            lastUpdatedText.text = dateFormat.format(Date())
            lastCallDateText.text = "Not Called Yet"
            universityNameText.text = "Unknown University"
            nameEditText.setText("Unknown User")
            emailEditText.setText("No Email")
            mobileEditText.setText("No Mobile")
            countryEditText.setText("Unknown")
            stateEditText.setText("Unknown")
            cityEditText.setText("Unknown")
            neetScoreEditText.setText("0")
            studyCountryEditText.setText("Unknown")
            hasNeetScoreCheckbox.isChecked = false
            hasPassportCheckbox.isChecked = false
            applicationStatusText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.status_warning)
        }
        loadProfileImage()
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        return try {
            dateFormat.format(timestamp.toDate())
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun formatStringDate(dateStr: String): String {
        return try {
            dateFormat.parse(dateStr)?.let { dateFormat.format(it) } ?: "N/A"
        } catch (e: ParseException) {
            "N/A"
        }
    }

    private fun loadProfileImage() {
        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            if (isAdded) {
                Glide.with(this@FragmentEditDetails)
                    .load(uri)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.profileImageView)
            }
        }?.addOnFailureListener {
            if (isAdded) {
                binding.profileImageView.setImageResource(R.drawable.ic_profile)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            editModeToggleBtn.setOnClickListener { toggleEditMode() }
            cancelBtn.setOnClickListener { toggleEditMode() }
            saveBtn.setOnClickListener { saveChanges() }
            editProfileImageBtn.setOnClickListener { pickImage() }
            callBtn.setOnClickListener { initiateCall() }
            whatsappBtn.setOnClickListener { initiateWhatsApp() }
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        with(binding) {
            editModeToggleBtn.text = if (isEditMode) "Done" else "Edit"
            actionButtonsLayout.visibility = if (isEditMode) View.VISIBLE else View.GONE
            editProfileImageBtn.visibility = if (isEditMode) View.VISIBLE else View.GONE
        }
        setFieldsEditable(isEditMode)
    }

    private fun setFieldsEditable(editable: Boolean) {
        with(binding) {
            nameEditText.isEnabled = editable
            emailEditText.isEnabled = editable
            mobileEditText.isEnabled = editable
            countryEditText.isEnabled = editable
            stateEditText.isEnabled = editable
            cityEditText.isEnabled = editable
            neetScoreEditText.isEnabled = editable
            studyCountryEditText.isEnabled = editable
            hasNeetScoreCheckbox.isEnabled = editable
            hasPassportCheckbox.isEnabled = editable
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun saveChanges() {
        userRef ?: run {
            Toast.makeText(context, "Cannot save: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "name" to binding.nameEditText.text.toString().trim(),
            "email" to binding.emailEditText.text.toString().trim(),
            "mobile" to binding.mobileEditText.text.toString().trim(),
            "country" to binding.countryEditText.text.toString().trim(),
            "state" to binding.stateEditText.text.toString().trim(),
            "city" to binding.cityEditText.text.toString().trim(),
            "neetScore" to binding.neetScoreEditText.text.toString().trim(),
            "studyCountry" to binding.studyCountryEditText.text.toString().trim(),
            "hasNeetScore" to binding.hasNeetScoreCheckbox.isChecked,
            "hasPassport" to binding.hasPassportCheckbox.isChecked,
            "lastUpdatedDate" to dateFormat.format(Date())
        )

        userRef?.set(updates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                toggleEditMode()
            } else {
                when (val exception = task.exception) {
                    is FirebaseFirestoreException -> if (exception.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        Toast.makeText(context, "Permission denied. Please check your authentication status.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(context, "Failed to update profile: Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        imageUri?.let { uri ->
            storageRef?.putFile(uri)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loadProfileImage()
                } else {
                    Toast.makeText(context, "Failed to upload image: ${task.exception?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initiateCall() {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse(getString(R.string.whatsapp_number))
        }
        startActivity(callIntent)

        userRef?.update("lastCallDate", dateFormat.format(Date()))?.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to update call date: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun viewUniversityDetails() {
        Toast.makeText(context, "Navigating to university details", Toast.LENGTH_SHORT).show()
    }
}