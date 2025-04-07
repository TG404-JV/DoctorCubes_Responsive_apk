package com.tvm.doctorcube.study.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.BuildConfig;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.StudyMaterialFragment;
import com.tvm.doctorcube.study.fragment.adapter.VideosAdapter;
import com.tvm.doctorcube.study.fragment.models.VideoItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideosFragment extends Fragment implements StudyMaterialFragment.SearchableFragment {

    private static final String TAG = "VideosFragment";
    private static final String VIDEOS_COLLECTION = "videos";
    private static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY; // Replace with your key
    private static final String YOUTUBE_VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos";

    private RecyclerView recyclerView;
    private VideosAdapter adapter;
    private List<VideoItem> videoList;
    private List<VideoItem> originalVideoList;
    private TextView emptyView;
    private boolean isDataLoaded = false;
    private FirebaseFirestore firestoreDB;
    private CollectionReference videosCollectionRef;
    private String pendingSearchQuery = null;
    private ExecutorService executorService;
    private OkHttpClient okHttpClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        videoList = new ArrayList<>();
        originalVideoList = new ArrayList<>();
        adapter = new VideosAdapter(videoList, getContext());
        recyclerView.setAdapter(adapter);

        firestoreDB = FirebaseFirestore.getInstance();
        videosCollectionRef = firestoreDB.collection(VIDEOS_COLLECTION);

        executorService = Executors.newFixedThreadPool(5); // Adjust thread pool size as needed
        okHttpClient = new OkHttpClient();

        fetchVideosFromFirestore();


        return view;
    }

    private void fetchVideosFromFirestore() {
        videosCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!isAdded() || getContext() == null) {
                            executorService.shutdownNow();
                            return;
                        }

                        if (queryDocumentSnapshots.isEmpty()) {
                            isDataLoaded = true;
                            if (emptyView != null) {
                                emptyView.setText("No videos found.");
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            executorService.shutdownNow();
                            return;
                        }

                        final List<VideoItem> allVideoItems = new ArrayList<>();
                        final List<Future<?>> futures = new ArrayList<>(); // To track tasks

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String videoId = document.getString("videoId");
                            if (videoId != null) {
                                // Use executorService.submit for background tasks
                                Future<?> future = executorService.submit(() -> {
                                    try {
                                        VideoItem videoItem = fetchVideoDetailsFromYouTube(videoId);
                                        if (videoItem != null) {
                                            synchronized (allVideoItems) {
                                                allVideoItems.add(videoItem);
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Handle errors, e.g., log, show a toast
                                        if (isAdded() && getContext() != null) {
                                            getActivity().runOnUiThread(() ->
                                                    CustomToast.showToast(requireActivity(), "Error fetching video details" ));
                                        }
                                    }
                                });
                                futures.add(future);
                            }
                        }

                        // Wait for all tasks to complete
                        for (Future<?> future : futures) {
                            try {
                                future.get(); // Wait for each task to complete
                            } catch (Exception e) {
                                // Handle the error as needed
                            }
                        }
                        executorService.shutdown();

                        if (isAdded() && getContext() != null) {
                            getActivity().runOnUiThread(() -> updateAdapterAndFinishLoading(allVideoItems));
                        }
                        else{
                            updateAdapterAndFinishLoading(allVideoItems);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!isAdded() || getContext() == null) {
                            executorService.shutdownNow();
                            return;
                        }
                        CustomToast.showToast(requireActivity(), "Failed to load videos. Please check your internet connection.");
                        if (emptyView != null) {
                            emptyView.setText("Failed to load videos. Please check your internet connection.");
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        executorService.shutdownNow();
                    }
                });
    }

    private VideoItem fetchVideoDetailsFromYouTube(String videoId) {
        try {
            // Construct the YouTube API request URL
            String url = YOUTUBE_VIDEO_URL + "?part=snippet&id=" + videoId + "&key=" + YOUTUBE_API_KEY;

            // Use OkHttp to make the request
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute(); //synchronous call

            if (!response.isSuccessful()) {
                return null;
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray items = jsonResponse.getJSONArray("items");

            if (items.length() > 0) {
                JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
                String title = snippet.getString("title");
                String thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                String description = snippet.getString("description");
                return new VideoItem(title, videoId, thumbnailUrl, description);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void updateAdapterAndFinishLoading(List<VideoItem> allVideoItems) {
        if (!isAdded() || getContext() == null) return;

        videoList.clear();
        videoList.addAll(allVideoItems);
        originalVideoList.clear();
        originalVideoList.addAll(allVideoItems);
        adapter.notifyDataSetChanged();
        isDataLoaded = true;
        if (allVideoItems.isEmpty()) {
            emptyView.setText("No videos found.");
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        if (pendingSearchQuery != null && !pendingSearchQuery.isEmpty()) {
            performSearch(pendingSearchQuery);
            pendingSearchQuery = null;
        }
    }

    @Override
    public void performSearch(String query) {
        if (!isAdded() || getContext() == null) return;

        if (!isDataLoaded) {
            pendingSearchQuery = query;
            return;
        }

        List<VideoItem> filteredList = new ArrayList<>();
        for (VideoItem video : originalVideoList) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    (video.getDescription() != null && video.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(video);
            }
        }
        updateSearchResults(filteredList, query);
    }

    @Override
    public void resetSearch() {
        if (!isAdded() || getContext() == null) return;

        pendingSearchQuery = null;
        if (isDataLoaded) {
            videoList.clear();
            videoList.addAll(originalVideoList);
            adapter.notifyDataSetChanged();
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private void updateSearchResults(List<VideoItem> filteredList, String query) {
        if (!isAdded() || getContext() == null) return;

        videoList.clear();
        videoList.addAll(filteredList);
        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            if (emptyView != null) {
                emptyView.setText("No videos found for: " + query);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                CustomToast.showToast(requireActivity(), "No videos found for: " + query);
            }
        } else if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow(); // Clean up the executor
        }
    }
}