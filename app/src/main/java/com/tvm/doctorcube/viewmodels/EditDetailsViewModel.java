package com.tvm.doctorcube.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tvm.doctorcube.models.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditDetailsViewModel extends ViewModel {
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<UiState> uiState = new MutableLiveData<>();
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final StorageReference storageRef;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public enum UiState {
        LOADING, SUCCESS, ERROR
    }

    public EditDetailsViewModel() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String uid = getCurrentUserId();
        storageRef = uid.isEmpty() ? null : FirebaseStorage.getInstance().getReference("profile_images/" + uid + "/profile.jpg");
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<UiState> getUiState() {
        return uiState;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void fetchUserProfile() {
        String uid = getCurrentUserId();
        if (uid.isEmpty()) {
            uiState.setValue(UiState.ERROR);
            return;
        }

        uiState.setValue(UiState.LOADING);
        DocumentReference ref = db.collection("app_submissions").document(uid);
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                UserProfile profile = snapshot.toObject(UserProfile.class);
                userProfile.setValue(profile);
                uiState.setValue(UiState.SUCCESS);
            } else {
                uiState.setValue(UiState.ERROR);
            }
        }).addOnFailureListener(e -> uiState.setValue(UiState.ERROR));
    }

    public void saveUserProfile(UserProfile profile, Uri imageUri) {
        String uid = getCurrentUserId();
        if (uid.isEmpty()) {
            uiState.setValue(UiState.ERROR);
            return;
        }

        executor.execute(() -> {
            uiState.postValue(UiState.LOADING);
            try {
                profile.setLastUpdatedDate(dateFormat.format(new Date()));
                db.collection("app_submissions").document(uid).set(profile)
                        .addOnSuccessListener(aVoid -> {
                            if (imageUri != null && storageRef != null) {
                                storageRef.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> uiState.postValue(UiState.SUCCESS))
                                        .addOnFailureListener(e -> uiState.postValue(UiState.ERROR));
                            } else {
                                uiState.postValue(UiState.SUCCESS);
                            }
                        })
                        .addOnFailureListener(e -> uiState.postValue(UiState.ERROR));
            } catch (Exception e) {
                uiState.postValue(UiState.ERROR);
            }
        });
    }

    public void updateLastCallDate() {
        String uid = getCurrentUserId();
        if (uid.isEmpty()) return;

        executor.execute(() -> {
            try {
                db.collection("app_submissions").document(uid)
                        .update("lastCallDate", dateFormat.format(new Date()))
                        .addOnFailureListener(e -> {});
            } catch (Exception ignored) {}
        });
    }

    private String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}