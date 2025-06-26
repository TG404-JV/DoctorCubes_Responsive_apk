package com.tvm.doctorcube.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.tvm.doctorcube.models.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EditDetailsViewModel : ViewModel() {
    private val userProfile = MutableLiveData<UserProfile?>()
    private val uiState = MutableLiveData<UiState?>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore
    val storageRef: StorageReference?
    private val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.US)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    enum class UiState {
        LOADING, SUCCESS, ERROR
    }

    init {
        db = FirebaseFirestore.getInstance()
        val uid = this.currentUserId
        storageRef = if (uid.isEmpty()) null else FirebaseStorage.getInstance()
            .getReference("profile_images/" + uid + "/profile.jpg")
    }

    fun getUserProfile(): LiveData<UserProfile?> {
        return userProfile
    }

    fun getUiState(): LiveData<UiState?> {
        return uiState
    }

    fun fetchUserProfile() {
        val uid = this.currentUserId
        if (uid.isEmpty()) {
            uiState.setValue(UiState.ERROR)
            return
        }

        uiState.setValue(UiState.LOADING)
        val ref = db.collection("app_submissions").document(uid)
        ref.get().addOnSuccessListener(OnSuccessListener { snapshot: DocumentSnapshot? ->
            if (snapshot!!.exists()) {
                val profile = snapshot.toObject<UserProfile?>(UserProfile::class.java)
                userProfile.setValue(profile)
                uiState.setValue(UiState.SUCCESS)
            } else {
                uiState.setValue(UiState.ERROR)
            }
        })
            .addOnFailureListener(OnFailureListener { e: Exception? -> uiState.setValue(UiState.ERROR) })
    }

    fun saveUserProfile(profile: UserProfile, imageUri: Uri?) {
        val uid = this.currentUserId
        if (uid.isEmpty()) {
            uiState.setValue(UiState.ERROR)
            return
        }

        executor.execute(Runnable {
            uiState.postValue(UiState.LOADING)
            try {
                profile.lastUpdatedDate = dateFormat.format(Date())
                db.collection("app_submissions").document(uid).set(profile)
                    .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                        if (imageUri != null && storageRef != null) {
                            storageRef.putFile(imageUri)
                                .addOnSuccessListener(OnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                                    uiState.postValue(
                                        UiState.SUCCESS
                                    )
                                })
                                .addOnFailureListener(OnFailureListener { e: Exception? ->
                                    uiState.postValue(
                                        UiState.ERROR
                                    )
                                })
                        } else {
                            uiState.postValue(UiState.SUCCESS)
                        }
                    })
                    .addOnFailureListener(OnFailureListener { e: Exception? ->
                        uiState.postValue(
                            UiState.ERROR
                        )
                    })
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
            }
        })
    }

    fun updateLastCallDate() {
        val uid = this.currentUserId
        if (uid.isEmpty()) return

        executor.execute(Runnable {
            try {
                db.collection("app_submissions").document(uid)
                    .update("lastCallDate", dateFormat.format(Date()))
                    .addOnFailureListener(OnFailureListener { e: Exception? -> })
            } catch (ignored: Exception) {
            }
        })
    }

    private val currentUserId: String
        get() = if (auth.getCurrentUser() != null) auth.getCurrentUser()!!.getUid() else ""

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}