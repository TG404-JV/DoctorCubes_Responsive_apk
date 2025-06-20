package com.tvm.doctorcube.authentication;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.adapter.SlideAdapter;
import com.tvm.doctorcube.authentication.adapter.SlideItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class GetStartedFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout indicator;
    private TextView imageCaption;
    private List<SlideItem> slideItems;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private static final long SLIDE_DELAY = 3000; // 3 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_started, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up UI components
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        imageCaption = view.findViewById(R.id.image_caption);

        // Initialize slide items (add your images and captions here)
        setupSlideItems();

        // Set up viewpager with the slides
        SlideAdapter slideAdapter = new SlideAdapter(slideItems);
        viewPager.setAdapter(slideAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(indicator, viewPager, (tab, position) -> {
            // No additional setup needed here
        }).attach();

        // Set up automatic sliding
        setupAutoSliding();

        // Show the first image caption
        updateCaption(0);

        // Setup page change listener to update caption
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateCaption(position);
            }
        });

        // Set up Get Started button
        view.findViewById(R.id.get_started_button).setOnClickListener(v -> {
            // Add button click animation
            Animation buttonAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_pulse);
            v.startAnimation(buttonAnimation);

            // Navigate to next screen
            Navigation.findNavController(view).navigate(R.id.action_getStartedFragment_to_fragmentAskUser);
        });

        // Add entrance animations for main elements
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        view.findViewById(R.id.heading_text).startAnimation(fadeIn);
        view.findViewById(R.id.subheading_text).startAnimation(fadeIn);
        view.findViewById(R.id.button_container).startAnimation(slideUp);
    }

    private void setupSlideItems() {
        slideItems = new ArrayList<>();

        // Add your slides (use your own drawable resources)
        slideItems.add(new SlideItem(R.drawable.slide_img_get_started_1, "Your Gateway to Global Medical Education"));
        slideItems.add(new SlideItem(R.drawable.slide_img_get_started_2, "Turning Dreams into Doctors"));
        slideItems.add(new SlideItem(R.drawable.slide_img_get_started_3, "Bridging You to the Best Medical Universities"));




    }

    private void updateCaption(int position) {
        if (position < slideItems.size()) {
            String caption = slideItems.get(position).getCaption();
            imageCaption.setText(caption);

            // Add caption animation
            Animation textFade = AnimationUtils.loadAnimation(getContext(), R.anim.text_fade);
            imageCaption.startAnimation(textFade);
        }
    }

    private void setupAutoSliding() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() == slideItems.size() - 1) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
                sliderHandler.postDelayed(this, SLIDE_DELAY);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}