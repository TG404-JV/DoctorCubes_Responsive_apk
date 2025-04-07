package com.tvm.doctorcube.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity; // Add this import
import androidx.appcompat.widget.Toolbar; // Already imported
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.google.android.material.button.MaterialButton;


public class FragmentAbout extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private NavController navController;
    private Toolbar fragmentToolbar; // Renamed for clarity

    public FragmentAbout() {
        // Required empty public constructor
    }

    public static FragmentAbout newInstance(String param1, String param2) {
        FragmentAbout fragment = new FragmentAbout();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Initialize NavController
        navController = NavHostFragment.findNavController(this);

        // Hide MainActivity Toolbar
        hideMainActivityToolbar();

        // Initialize Fragment's Toolbar and set back button listener
        fragmentToolbar = view.findViewById(R.id.toolbar);
        if (fragmentToolbar != null) {
            fragmentToolbar.setNavigationOnClickListener(v -> navigateToHome());
        }

        // Initialize UI elements and set listeners
        MaterialButton emailButton = view.findViewById(R.id.btnEmail);
        MaterialButton whatsappButton = view.findViewById(R.id.btnWhatsApp);
        MaterialButton callButton = view.findViewById(R.id.btnCall);

        SocialActions openMedia=new SocialActions();

        emailButton.setOnClickListener(v -> openMedia.openEmailApp(requireActivity()));
        whatsappButton.setOnClickListener(v -> openMedia.openWhatsApp(requireActivity()));
        callButton.setOnClickListener(v -> openMedia.makeDirectCall(requireActivity()));
        view.findViewById(R.id.btnInstagram).setOnClickListener(v ->openMedia.openInstagram(requireActivity()));
        view.findViewById(R.id.btnTwitter).setOnClickListener(v ->openMedia.openTwitter(requireActivity()));
        view.findViewById(R.id.btnYoutube).setOnClickListener(v ->openMedia.openYouTube(requireActivity()));


        return view;
    }

    private void hideMainActivityToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar mainToolbar = activity.findViewById(R.id.toolbar); // MainActivity's Toolbar ID
            if (mainToolbar != null) {
                mainToolbar.setVisibility(View.GONE); // Hide the MainActivity Toolbar
            }
            // Optionally hide the ActionBar if it's being used
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
                mainToolbar.setVisibility(View.VISIBLE); // Show it back
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

    private void navigateToHome() {
        if (navController != null) {
            navController.navigate(R.id.action_global_settingsHome);
        }
    }
}