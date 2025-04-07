package com.tvm.doctorcube.study.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private List<String> videoIds = new ArrayList<>();
    private YouTubePlayerView youtubePlayerView;
    private MaterialToolbar toolbar;
    private MaterialButton btnPrevious;
    private MaterialButton btnNext;
    private FloatingActionButton fullscreenToggle;
    private CircularProgressIndicator progressBar;
    private YouTubePlayer activePlayer;
    private boolean isFullScreen = false;
    private int currentVideoIndex = 0;
    private Handler handler; // For delayed hiding of controls
    private static final int CONTROL_HIDE_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Initialize views
        initializeViews();

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Video Player");
        }

        // Add YouTubePlayerView to lifecycle
        getLifecycle().addObserver(youtubePlayerView);

        // Get videoId from intent and populate videoIds list
        String videoId = getIntent().getStringExtra("videoId");
        if (videoId != null && !videoId.isEmpty()) {
            videoIds.add(videoId);
        } else {
            finish();
            return;
        }

        // Setup YouTube Player with tap listener
        setupYouTubePlayer();

        // Setup controls
        setupControls();
    }

    private void initializeViews() {
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        toolbar = findViewById(R.id.toolbar);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        fullscreenToggle = findViewById(R.id.fullscreenToggle);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupYouTubePlayer() {
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                activePlayer = youTubePlayer;
                progressBar.setVisibility(View.VISIBLE);
                loadVideo(currentVideoIndex);
            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer,
                                      com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState state) {
                switch (state) {
                    case BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case PLAYING:
                    case PAUSED:
                    case ENDED:
                        progressBar.setVisibility(View.GONE);
                        if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.ENDED) {
                            playNextVideo();
                        }
                        break;
                }
            }

            @Override
            public void onError(YouTubePlayer youTubePlayer,
                                com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError error) {
                progressBar.setVisibility(View.GONE);
                CustomToast.showToast(VideoPlayerActivity.this, "Error loading video");
                playNextVideo();
            }
        });

        // Add tap listener to toggle controls in fullscreen
        youtubePlayerView.setOnClickListener(v -> {
            if (isFullScreen) {
                toggleControlsVisibility();
            }
        });
    }

    private void setupControls() {
        btnPrevious.setOnClickListener(v -> playPreviousVideo());
        btnNext.setOnClickListener(v -> playNextVideo());
        fullscreenToggle.setOnClickListener(v -> toggleFullScreen());
        updateButtonStates();
    }

    private void loadVideo(int index) {
        if (activePlayer == null || videoIds.isEmpty()) return;

        if (index >= 0 && index < videoIds.size()) {
            currentVideoIndex = index;
            activePlayer.loadVideo(videoIds.get(currentVideoIndex), 0);
            toolbar.setTitle("Playing: Video " + (currentVideoIndex + 1));
            updateButtonStates();
        }
        else {
            CustomToast.showToast(this, "Invalid video index");
        }
    }

    private void playNextVideo() {
        if (!videoIds.isEmpty() && currentVideoIndex < videoIds.size() - 1) {
            loadVideo(currentVideoIndex + 1);
        } else {
            CustomToast.showToast(this, "At the end of playlist");
        }

    }

    private void playPreviousVideo() {
        if (!videoIds.isEmpty() && currentVideoIndex > 0) {
            loadVideo(currentVideoIndex - 1);
        } else {
            CustomToast.showToast(this, "At the beginning of playlist");
        }
    }

    private void updateButtonStates() {
        btnPrevious.setEnabled(currentVideoIndex > 0);
        btnNext.setEnabled(currentVideoIndex < videoIds.size() - 1);
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            // Exit fullscreen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            toolbar.setVisibility(View.VISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            fullscreenToggle.setVisibility(View.VISIBLE);
            fullscreenToggle.setImageResource(R.drawable.ic_video_orientation); // Use correct icon
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            isFullScreen = false;
            handler.removeCallbacksAndMessages(null); // Cancel any hide tasks
        } else {
            // Enter fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            toolbar.setVisibility(View.GONE);
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            fullscreenToggle.setVisibility(View.GONE); // Initially hidden in fullscreen
            fullscreenToggle.setImageResource(R.drawable.ic_video_orientation); // Use correct icon
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            isFullScreen = true;
        }
    }

    private void toggleControlsVisibility() {
        if (fullscreenToggle.getVisibility() == View.VISIBLE) {
            // Hide controls and schedule hiding again
            fullscreenToggle.setVisibility(View.GONE);
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        } else {
            // Show controls and schedule hiding
            fullscreenToggle.setVisibility(View.VISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            scheduleControlsHide();
        }
    }

    private void scheduleControlsHide() {
        handler.removeCallbacksAndMessages(null); // Clear previous tasks
        handler.postDelayed(() -> {
            if (isFullScreen) {
                fullscreenToggle.setVisibility(View.GONE);
                btnPrevious.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            }
        }, CONTROL_HIDE_DELAY);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            toggleFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
        handler.removeCallbacksAndMessages(null); // Clean up handler
    }
}