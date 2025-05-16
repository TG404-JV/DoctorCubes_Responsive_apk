package com.tvm.doctorcube;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tvm.doctorcube.adminpannel.adminhome.FragmentAddNewUser;
import com.tvm.doctorcube.adminpannel.adminhome.FragmentAdminHome;
import com.tvm.doctorcube.adminpannel.adminhome.FragmentExportXLData;
import com.tvm.doctorcube.adminpannel.adminhome.FragmentImportXLData;
import com.tvm.doctorcube.adminpannel.adminhome.FragmentUploadStudyMaterial;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;


import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminActivity";
    private static final String PREFS_NAME = "DoctorCubePrefs";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String USER_COLLECTION = "Users";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SharedPreferences prefs;
    private boolean isSuperAdmin = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        try {
            MasterKey masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    this,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            CustomToast.showToast(this, "Error In Storing The data");
            isSuperAdmin = false;
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Panel");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setupNavigationHeader();

        if (savedInstanceState == null) {
            loadFragment(new FragmentAdminHome());
        }
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView adminNameTextView = headerView.findViewById(R.id.tv_admin_name);
        TextView adminEmailTextView = headerView.findViewById(R.id.tv_admin_email);

        if (currentUser != null) {
            String email = currentUser.getEmail();
            adminEmailTextView.setText(email != null ? email : getString(R.string.admin_email));

            firestoreDB.collection(USER_COLLECTION)
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String role = document.getString("role");
                                adminNameTextView.setText(name != null ? name : "Admin");
                                isSuperAdmin = "superadmin".equals(role);
                                prefs.edit().putString(KEY_USER_ROLE, role).apply();
                            } else {
                                adminNameTextView.setText("Admin");
                                isSuperAdmin = false;
                            }
                        } else {
                            adminNameTextView.setText("Admin");
                            isSuperAdmin = false;
                        }
                    });

        } else {
            adminEmailTextView.setText(getString(R.string.admin_email));
            adminNameTextView.setText("Admin");
            isSuperAdmin = false;
            CustomToast.showToast(this, "User not logged in");
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new FragmentAdminHome());
        } else if (id == R.id.nav_add_new) {
            EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(this);
            String role = encryptedSharedPreferencesManager.getString("role", "User");

            if (role.equals("superadmin")) {
                loadFragment(new FragmentAddNewUser());
            } else {
                CustomToast.showToast(this, "You are not authorized to access this feature.");
            }

        } else if (id == R.id.nav_import_excel) {
            loadFragment(new FragmentImportXLData());
        } else if (id == R.id.nav_export_excel) {
            loadFragment(new FragmentExportXLData());
        } else if (id == R.id.nav_upload_study_material) {
            loadFragment(new FragmentUploadStudyMaterial());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed(); //remove this
            finishAffinity(); // add this
        }
    }
}
