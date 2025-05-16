package com.tvm.doctorcube.adminpannel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.tvm.doctorcube.adminpannel.databsemanager.FirestoreDataHelper;
import com.tvm.doctorcube.adminpannel.databsemanager.Student;
import com.tvm.doctorcube.adminpannel.databsemanager.StudentDatabase;
import com.tvm.doctorcube.adminpannel.databsemanager.StudentEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentDataLoader {
    private static final String TAG = "StudentDataLoader";
    private static final String COLLECTION_REGISTRATIONS = "registrations";
    private static final String COLLECTION_XL_DATA = "xl_data";
    private static final String COLLECTION_APP_SUBMISSIONS = "app_submissions";
    private static final String COLLECTION_WEB_SUBMISSIONS = "web_submissions";
    private static final String LAST_SYNC_TIMESTAMP = "lastSyncTimestamp";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final FirebaseFirestore firestoreDB;
    private final StudentDatabase localDB;
    private final ExecutorService executorService;
    private final Context context;
    private List<Student> studentList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    private final List<ListenerRegistration> snapshotListeners;

    public interface DataLoadListener {
        void onDataLoaded(List<Student> students);
        void onDataLoadFailed(String error);
    }

    public StudentDataLoader(Context context) {
        this.context = context;
        firestoreDB = FirebaseFirestore.getInstance();
        localDB = StudentDatabase.getDatabase(context);
        executorService = Executors.newSingleThreadExecutor();
        studentList = new ArrayList<>();
        snapshotListeners = new ArrayList<>();
    }

    public void loadStudents(final DataLoadListener listener) {
        studentList.clear();
        executorService.execute(() -> {
            try {
                List<StudentEntity> cachedStudents = localDB.studentDao().getAll();
                List<Student> students = convertEntitiesToStudents(cachedStudents);
                if (!students.isEmpty()) {
                    mainHandler.post(() -> {
                        listener.onDataLoaded(students);
                        startRealtimeListeners(listener);
                    });
                } else {
                    loadAllStudentData(listener, Source.SERVER);
                }
            } catch (Exception e) {
                mainHandler.post(() -> listener.onDataLoadFailed("Error loading cached data: " + e.getMessage()));
                loadAllStudentData(listener, Source.SERVER);
            }
        });
    }

    private void startRealtimeListeners(final DataLoadListener listener) {
        stopRealtimeListeners();
        String[] collections = {COLLECTION_REGISTRATIONS, COLLECTION_XL_DATA,
                COLLECTION_APP_SUBMISSIONS, COLLECTION_WEB_SUBMISSIONS};

        for (String collection : collections) {
            ListenerRegistration registration = firestoreDB.collection(collection)
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            Log.e(TAG, "Realtime listener failed for " + collection + ": " + error.getMessage());
                            mainHandler.post(() -> listener.onDataLoadFailed("Realtime listener error: " + error.getMessage()));
                            return;
                        }
                        if (snapshot != null) {
                            processRealtimeChanges(snapshot.getDocumentChanges(), collection, listener);
                        }
                    });
            snapshotListeners.add(registration);
        }
    }

    private void processRealtimeChanges(List<DocumentChange> changes, String collection, DataLoadListener listener) {
        executorService.execute(() -> {
            List<Student> updatedStudents = new ArrayList<>();
            for (DocumentChange change : changes) {
                DocumentSnapshot document = change.getDocument();
                try {
                    switch (change.getType()) {
                        case ADDED:
                        case MODIFIED:
                            Student student = createStudentFromDocument(document, collection);
                            if (student != null) {
                                student.setCollection(collection);
                                saveToLocalDB(student);
                                updatedStudents.add(student);
                            }
                            break;
                        case REMOVED:
                            String documentId = document.getId();
                            localDB.studentDao().deleteById(documentId);
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing change for document " + document.getId() + ": " + e.getMessage());
                }
            }
            if (!updatedStudents.isEmpty()) {
                updateLastSyncTime();
                List<StudentEntity> cachedStudents = localDB.studentDao().getAll();
                List<Student> allStudents = convertEntitiesToStudents(cachedStudents);
                mainHandler.post(() -> listener.onDataLoaded(allStudents));
            }
        });
    }

    private void loadAllStudentData(final DataLoadListener listener, Source source) {
        final Task<QuerySnapshot> registrationsTask = firestoreDB.collection(COLLECTION_REGISTRATIONS).get(source);
        final Task<QuerySnapshot> xlDataTask = firestoreDB.collection(COLLECTION_XL_DATA).get(source);
        final Task<QuerySnapshot> appSubmissionsTask = firestoreDB.collection(COLLECTION_APP_SUBMISSIONS).get(source);
        final Task<QuerySnapshot> webSubmissionsTask = firestoreDB.collection(COLLECTION_WEB_SUBMISSIONS).get(source);

        Tasks.whenAllSuccess(registrationsTask, xlDataTask, appSubmissionsTask, webSubmissionsTask)
                .addOnSuccessListener(results -> {
                    List<Student> allStudents = new ArrayList<>();
                    int index = 0;
                    String[] collectionNames = {COLLECTION_REGISTRATIONS, COLLECTION_XL_DATA,
                            COLLECTION_APP_SUBMISSIONS, COLLECTION_WEB_SUBMISSIONS};

                    for (Object result : results) {
                        if (result instanceof QuerySnapshot) {
                            QuerySnapshot snapshot = (QuerySnapshot) result;
                            String collectionName = collectionNames[index];
                            for (DocumentSnapshot document : snapshot) {
                                try {
                                    Student student = createStudentFromDocument(document, collectionName);
                                    if (student != null) {
                                        student.setCollection(collectionName);
                                        allStudents.add(student);
                                        saveToLocalDB(student);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing document in " + collectionName + ": " + e.getMessage());
                                    mainHandler.post(() -> listener.onDataLoadFailed("Error parsing data: " + e.getMessage()));
                                    return;
                                }
                            }
                            index++;
                        }
                    }
                    updateLastSyncTime();
                    mainHandler.post(() -> {
                        listener.onDataLoaded(allStudents);
                        startRealtimeListeners(listener);
                    });
                })
                .addOnFailureListener(e -> mainHandler.post(() -> listener.onDataLoadFailed("Failed to load data: " + e.getMessage())));
    }

    private Student createStudentFromDocument(DocumentSnapshot document, String collectionName) throws ParseException {
        switch (collectionName) {
            case COLLECTION_REGISTRATIONS:
                return createStudentFromRegistration(document);
            case COLLECTION_XL_DATA:
                return createStudentFromXlData(document);
            case COLLECTION_APP_SUBMISSIONS:
                return createStudentFromAppSubmissions(document);
            case COLLECTION_WEB_SUBMISSIONS:
                return createStudentFromWebSubmissions(document);
            default:
                Log.w(TAG, "Unknown collection: " + collectionName);
                return null;
        }
    }

    private Student createStudentFromRegistration(DocumentSnapshot document) throws ParseException {
        Student student = new Student();
        student.setId(document.getId());
        student.setName(FirestoreDataHelper.getString(document, "name"));
        student.setMobile(FirestoreDataHelper.getString(document, "mobile"));
        student.setEmail(FirestoreDataHelper.getString(document, "email"));
        student.setState(FirestoreDataHelper.getString(document, "state"));
        student.setCity(FirestoreDataHelper.getString(document, "city"));
        student.setInterestedCountry(FirestoreDataHelper.getString(document, "interestedCountry"));
        student.setHasNeetScore(FirestoreDataHelper.getString(document, "hasNeetScore"));
        student.setNeetScore(FirestoreDataHelper.getString(document, "neetScore"));
        student.setHasPassport(FirestoreDataHelper.getString(document, "hasPassport"));
        student.setCallStatus(FirestoreDataHelper.getString(document, "callStatus", "pending"));
        student.setIsInterested(FirestoreDataHelper.getBoolean(document, "isInterested", false));
        student.setAdmitted(FirestoreDataHelper.getBoolean(document, "isAdmitted", false));
        Date registrationDate = FirestoreDataHelper.getTimestamp(document, "registrationDate");
        if (registrationDate != null) {
            student.setSubmissionDate(dateFormat.format(registrationDate));
            student.setFirebasePushDate(dateFormat.format(registrationDate));
        }
        String lastCallDate = FirestoreDataHelper.getString(document, "lastCallDate", "Not Called Yet");
        student.setLastCallDate(lastCallDate);
        return student;
    }

    private Student createStudentFromXlData(DocumentSnapshot document) throws ParseException {
        Student student = new Student();
        student.setId(document.getId());
        student.setName(FirestoreDataHelper.getString(document, "name"));
        student.setMobile(FirestoreDataHelper.getString(document, "mobile"));
        student.setEmail(FirestoreDataHelper.getString(document, "email"));
        student.setState(FirestoreDataHelper.getString(document, "state"));
        student.setCity(FirestoreDataHelper.getString(document, "city"));
        student.setInterestedCountry(FirestoreDataHelper.getString(document, "interestedCountry"));
        student.setHasNeetScore(FirestoreDataHelper.getString(document, "hasNeetScore"));
        student.setNeetScore(FirestoreDataHelper.getString(document, "neetScore"));
        student.setHasPassport(FirestoreDataHelper.getString(document, "hasPassport"));
        student.setCallStatus(FirestoreDataHelper.getString(document, "callStatus", "pending"));
        student.setIsInterested(FirestoreDataHelper.getBoolean(document, "isInterested", false));
        student.setAdmitted(FirestoreDataHelper.getBoolean(document, "isAdmitted", false));
        Date uploadDate = FirestoreDataHelper.getTimestamp(document, "uploadDate");
        if (uploadDate != null) {
            student.setSubmissionDate(dateFormat.format(uploadDate));
            student.setFirebasePushDate(dateFormat.format(uploadDate));
        }
        String lastCallDate = FirestoreDataHelper.getString(document, "lastCallDate", "Not Called Yet");
        student.setLastCallDate(lastCallDate);
        return student;
    }

    private Student createStudentFromAppSubmissions(DocumentSnapshot document) throws ParseException {
        Student student = new Student();
        student.setId(document.getId());
        student.setName(FirestoreDataHelper.getString(document, "name"));
        student.setMobile(FirestoreDataHelper.getString(document, "mobile"));
        student.setEmail(FirestoreDataHelper.getString(document, "email"));
        student.setState(FirestoreDataHelper.getString(document, "state"));
        student.setCity(FirestoreDataHelper.getString(document, "city"));
        student.setInterestedCountry(FirestoreDataHelper.getString(document, "interestedCountry"));
        student.setHasNeetScore(FirestoreDataHelper.getString(document, "hasNeetScore"));
        student.setNeetScore(FirestoreDataHelper.getString(document, "neetScore"));
        student.setHasPassport(FirestoreDataHelper.getString(document, "hasPassport"));
        student.setCallStatus(FirestoreDataHelper.getString(document, "callStatus", "pending"));
        student.setIsInterested(FirestoreDataHelper.getBoolean(document, "isInterested", false));
        student.setAdmitted(FirestoreDataHelper.getBoolean(document, "isAdmitted", false));
        Date submissionDate = FirestoreDataHelper.getTimestamp(document, "submissionDate");
        if (submissionDate != null) {
            student.setSubmissionDate(dateFormat.format(submissionDate));
            student.setFirebasePushDate(dateFormat.format(submissionDate));
        }
        String lastCallDate = FirestoreDataHelper.getString(document, "lastCallDate", "Not Called Yet");
        student.setLastCallDate(lastCallDate);
        return student;
    }

    private Student createStudentFromWebSubmissions(DocumentSnapshot document) throws ParseException {
        Student student = new Student();
        student.setId(document.getId());
        student.setName(FirestoreDataHelper.getString(document, "name"));
        student.setMobile(FirestoreDataHelper.getString(document, "mobile"));
        student.setEmail(FirestoreDataHelper.getString(document, "email"));
        student.setState(FirestoreDataHelper.getString(document, "state"));
        student.setCity(FirestoreDataHelper.getString(document, "city"));
        student.setInterestedCountry(FirestoreDataHelper.getString(document, "interestedCountry"));
        student.setHasNeetScore(FirestoreDataHelper.getString(document, "hasNeetScore"));
        student.setNeetScore(FirestoreDataHelper.getString(document, "neetScore"));
        student.setHasPassport(FirestoreDataHelper.getString(document, "hasPassport"));
        student.setCallStatus(FirestoreDataHelper.getString(document, "callStatus", "pending"));
        student.setIsInterested(FirestoreDataHelper.getBoolean(document, "isInterested", false));
        student.setAdmitted(FirestoreDataHelper.getBoolean(document, "isAdmitted", false));
        Date webSubmissionDate = FirestoreDataHelper.getTimestamp(document, "webSubmissionDate");
        if (webSubmissionDate != null) {
            student.setSubmissionDate(dateFormat.format(webSubmissionDate));
            student.setFirebasePushDate(dateFormat.format(webSubmissionDate));
        }
        String lastCallDate = FirestoreDataHelper.getString(document, "lastCallDate", "Not Called Yet");
        student.setLastCallDate(lastCallDate);
        return student;
    }

    private void saveToLocalDB(Student student) {
        StudentEntity entity = new StudentEntity();
        entity.id = student.getId();
        entity.collection = student.getCollection();
        entity.data = studentToJson(student);
        entity.lastUpdated = System.currentTimeMillis();

        executorService.execute(() -> localDB.studentDao().insertAll(Collections.singletonList(entity)));
    }

    public void updateStudent(String collection, String documentId, String field, Object value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        updates.put("lastCallDate", new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date()));

        firestoreDB.collection(collection).document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Student data updated successfully for document: " + documentId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update student data for document: " + documentId, e));
    }

    private List<Student> convertEntitiesToStudents(List<StudentEntity> entities) {
        List<Student> students = new ArrayList<>();
        for (StudentEntity entity : entities) {
            Student student = jsonToStudent(entity.data);
            if (student != null) {
                student.setCollection(entity.collection);
                students.add(student);
            }
        }
        return students;
    }

    private String studentToJson(Student student) {
        return new Gson().toJson(student);
    }

    private Student jsonToStudent(String json) {
        try {
            return new Gson().fromJson(json, Student.class);
        } catch (Exception e) {
            Log.e(TAG, "Error converting JSON to Student: " + e.getMessage());
            return null;
        }
    }

    private long getLastSyncTime() {
        SharedPreferences prefs = context.getSharedPreferences("SyncPrefs", Context.MODE_PRIVATE);
        return prefs.getLong(LAST_SYNC_TIMESTAMP, 0);
    }

    private void updateLastSyncTime() {
        SharedPreferences prefs = context.getSharedPreferences("SyncPrefs", Context.MODE_PRIVATE);
        prefs.edit().putLong(LAST_SYNC_TIMESTAMP, System.currentTimeMillis()).apply();
    }

    public void stopRealtimeListeners() {
        for (ListenerRegistration listener : snapshotListeners) {
            listener.remove();
        }
        snapshotListeners.clear();
    }

    public void cleanup() {
        stopRealtimeListeners();
        executorService.shutdown();
        mainHandler.removeCallbacksAndMessages(null);
    }
}