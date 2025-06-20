package com.tvm.doctorcube.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class Animations {

    /**
     * Fades in a view from transparent to opaque.
     *
     * @param view     The view to animate
     * @param duration Animation duration in milliseconds
     * @param delay    Delay before starting animation in milliseconds
     */
    public static void fadeIn(View view, long duration, long delay) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Fades out a view and sets it to GONE after animation.
     *
     * @param view     The view to animate
     * @param duration Animation duration in milliseconds
     * @param delay    Delay before starting animation in milliseconds
     */
    public static void fadeOut(View view, long duration, long delay) {
        if (view == null) return;
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Slides a view in from the top with a fade effect.
     *
     * @param view     The view to animate
     * @param duration Animation duration in milliseconds
     * @param delay    Delay before starting animation in milliseconds
     */
    public static void slideInFromTop(View view, long duration, long delay) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setTranslationY(-100f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Slides a view out to the top with a fade effect and sets it to GONE.
     *
     * @param view     The view to animate
     * @param duration Animation duration in milliseconds
     * @param delay    Delay before starting animation in milliseconds
     */
    public static void slideOutToTop(View view, long duration, long delay) {
        if (view == null) return;
        view.animate()
                .alpha(0f)
                .translationY(-100f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Scales a view from a smaller size to its original size with a slight bounce effect.
     *
     * @param view     The view to animate
     * @param duration Animation duration in milliseconds
     * @param delay    Delay before starting animation in milliseconds
     */
    public static void scaleIn(View view, long duration, long delay) {
        if (view == null) return;
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Staggered animation for a list of views (e.g., buttons) with fade and slight upward slide.
     *
     * @param views    Array of views to animate
     * @param duration Animation duration per view in milliseconds
     * @param stagger  Delay between each view's animation in milliseconds
     */
    public static void staggeredFadeSlide(View[] views, long duration, long stagger) {
        if (views == null) return;
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            if (view != null) {
                view.setAlpha(0f);
                view.setTranslationY(50f);
                view.setVisibility(View.VISIBLE);
                view.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(duration)
                        .setStartDelay(i * stagger)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }
        }
    }

    /**
     * Animates a ViewPager2 page transition with a fade effect.
     *
     * @param view     The view to animate (e.g., current page's root view)
     * @param duration Animation duration in milliseconds
     */
    public static void fadePageTransition(View view, long duration) {
        if (view == null) return;
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.start();
    }
}