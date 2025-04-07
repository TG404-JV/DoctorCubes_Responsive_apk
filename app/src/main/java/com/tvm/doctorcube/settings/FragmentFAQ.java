package com.tvm.doctorcube.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.settings.faq.FAQAdapter;
import com.tvm.doctorcube.settings.faq.FAQViewModel;
import com.google.android.material.button.MaterialButton;

public class FragmentFAQ extends Fragment {

    private RecyclerView recyclerView;
    private ConstraintLayout emptyStateLayout;
    private MaterialButton btnWhatsAppContact;
    private FAQAdapter faqAdapter;
    private FAQViewModel faqViewModel;
    private Toolbar fragmentToolbar; // Renamed for clarity
    private ProgressBar progressBar;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the updated layout
        View view = inflater.inflate(R.layout.fragment_f_a_q, container, false);

        // Initialize NavController
        navController = NavHostFragment.findNavController(this);

        // Hide MainActivity Toolbar
        hideMainActivityToolbar();

        // Initialize views
        initViews(view);

        // Setup toolbar with back button
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize ViewModel
        faqViewModel = new ViewModelProvider(this).get(FAQViewModel.class);

        // Show loading state
        showLoading();

        // Observe FAQ data
        observeFAQData();

        // Setup WhatsApp button
        setupWhatsAppButton();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewFAQ);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnWhatsAppContact = view.findViewById(R.id.btnWhatsAppContact);
        fragmentToolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        if (fragmentToolbar != null) {
            fragmentToolbar.setNavigationOnClickListener(v -> navigateToHome());
            fragmentToolbar.setTitle("FAQ");
        }
    }

    private void setupRecyclerView() {
        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void observeFAQData() {
        faqViewModel.getFaqList().observe(getViewLifecycleOwner(), faqItems -> {
            // Hide loading state
            hideLoading();

            // Check if data exists
            if (faqItems == null || faqItems.isEmpty()) {
                showEmptyState();
            } else {
                showContent();
                faqAdapter = new FAQAdapter(faqItems);
                recyclerView.setAdapter(faqAdapter);
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void setupWhatsAppButton() {
        btnWhatsAppContact.setOnClickListener(v -> openWhatsApp());
    }

    private void openWhatsApp() {
        try {
            // Replace with your support phone number (with country code)
            String phoneNumber = "+1234567890";
            String message = "Hello, I have a question regarding..."; // Pre-filled message

            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber +
                    "&text=" + Uri.encode(message));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {


            // Fallback option - open a URL or dial the number
            try {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:+1234567890"));
                startActivity(dialIntent);
            } catch (Exception ex) {
                // Handle gracefully

            }
        }
    }

    private void hideMainActivityToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar mainToolbar = activity.findViewById(R.id.toolbar);
            if (mainToolbar != null) {
                mainToolbar.setVisibility(View.GONE);
            }
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
    }

    private void showMainActivityToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar mainToolbar = activity.findViewById(R.id.toolbar);
            if (mainToolbar != null) {
                mainToolbar.setVisibility(View.VISIBLE);
            }
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show MainActivity Toolbar when leaving this Fragment
        showMainActivityToolbar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (navController != null) {
            navigateToHome();
        }
    }

    private void navigateToHome() {
        if (navController != null) {
            navController.navigate(R.id.action_global_settingsHome);
        }
    }
}