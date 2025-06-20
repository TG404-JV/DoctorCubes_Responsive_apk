package com.tvm.doctorcube.study.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.google.android.material.appbar.MaterialToolbar;
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
    private CircularProgressIndicator progressBar;
    private YouTubePlayer activePlayer;
    private boolean isFullScreen = false;
    private int currentVideoIndex = 0;
    private Handler handler;
    private static final int CONTROL_HIDE_DELAY = 3000;
    private WindowInsetsControllerCompat insetsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_video_player);

        insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Video Player");
        }

        handler = new Handler(Looper.getMainLooper());
        getLifecycle().addObserver(youtubePlayerView);

        String videoId = getIntent().getStringExtra("videoId");
        if (videoId != null && !videoId.isEmpty()) {
            videoIds.add(videoId);
        } else {
            finish();
            return;
        }

        setupYouTubePlayer();
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

        youtubePlayerView.setOnClickListener(v -> {
            if (isFullScreen) toggleControlsVisibility();
        });
    }

    private void loadVideo(int index) {
        if (activePlayer == null || videoIds.isEmpty()) return;

        if (index >= 0 && index < videoIds.size()) {
            currentVideoIndex = index;
            activePlayer.loadVideo(videoIds.get(index), 0);
            toolbar.setTitle("Playing: Video " + (index + 1));
        } else {
            CustomToast.showToast(this, "Invalid video index");
        }
    }

    private void playNextVideo() {
        if (currentVideoIndex < videoIds.size() - 1) {
            loadVideo(currentVideoIndex + 1);
        } else {
            CustomToast.showToast(this, "At the end of playlist");
        }
    }

    private void playPreviousVideo() {
        if (currentVideoIndex > 0) {
            loadVideo(currentVideoIndex - 1);
        } else {
            CustomToast.showToast(this, "At the beginning of playlist");
        }
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            insetsController.show(WindowInsetsCompat.Type.systemBars());
            toolbar.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            handler.removeCallbacksAndMessages(null);
        } else {
            insetsController.hide(WindowInsetsCompat.Type.systemBars());
            insetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            toolbar.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            scheduleControlsHide();
        }

        isFullScreen = !isFullScreen;
        youtubePlayerView.getParent().requestLayout();
    }

    private void toggleControlsVisibility() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            scheduleControlsHide();
        }
    }

    private void scheduleControlsHide() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            if (isFullScreen) {
                toolbar.setVisibility(View.GONE);
            }
        }, CONTROL_HIDE_DELAY);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        youtubePlayerView.getParent().requestLayout();

        if (isFullScreen) {
            insetsController.hide(WindowInsetsCompat.Type.systemBars());
            insetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            toggleControlsVisibility();
        }
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
        handler.removeCallbacksAndMessages(null);
    }
}
