package com.tvm.doctorcube.adminpannel.adminhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentUploadStudyMaterial extends Fragment {

    private RecyclerView contentRecyclerView;
    private FloatingActionButton fabAddContent;
    private TabLayout tabLayout;
    private FirebaseFirestore db;
    private ContentAdapter contentAdapter;
    private List<ContentItem> contentList = new ArrayList<>();
    private String currentContentId;
    private String currentCollection = "videos"; // Keep track of the current collection

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_study_material, container, false);

        db = FirebaseFirestore.getInstance();

        initializeViews(view);
        setupRecyclerView();
        setupTabs();
        setupFab();
        fetchContent(currentCollection); // Use the currentCollection, default is "videos"

        return view;
    }

    private void initializeViews(View view) {
        contentRecyclerView = view.findViewById(R.id.contentRecyclerView);
        fabAddContent = view.findViewById(R.id.fabAddContent);
        tabLayout = view.findViewById(R.id.tabLayout);
    }

    private void setupRecyclerView() {
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentAdapter = new ContentAdapter(contentList);
        contentRecyclerView.setAdapter(contentAdapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentCollection = tab.getPosition() == 0 ? "videos" : "notes"; // Update the current collection
                fetchContent(currentCollection);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFab() {
        fabAddContent.setOnClickListener(v -> showBottomSheetDialog(null));
    }

    private void fetchContent(String collection) {
        if (getContext() == null) return;

        db.collection(collection)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        contentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString(collection.equals("videos") ? "title" : "notesName");
                            String author = document.getString("author");
                            String size = document.getString("size");
                            String pages = document.getString("pages");
                            String category = document.getString("category");
                            String description = document.getString("description");
                            String videoId = document.getString("videoId");
                            String notesUrl = document.getString("notesUrl");
                            contentList.add(new ContentItem(id, title, author, size, pages, category, description, videoId, notesUrl));
                        }
                    } else {
                        CustomToast.showToast(requireActivity(), "Error fetching content");
                    }
                });
    }

    private void showBottomSheetDialog(ContentItem itemToEdit) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload_content, null);
        bottomSheetDialog.setContentView(dialogView);

        TextInputEditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        TextInputEditText authorEditText = dialogView.findViewById(R.id.authorEditText);
        TextInputEditText sizeEditText = dialogView.findViewById(R.id.sizeEditText);
        TextInputEditText pagesEditText = dialogView.findViewById(R.id.pagesEditText);
        TextInputEditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        TextInputEditText urlEditText = dialogView.findViewById(R.id.urlEditText);
        RadioButton videoRadioButton = dialogView.findViewById(R.id.videoRadioButton);
        RadioButton notesRadioButton = dialogView.findViewById(R.id.notesRadioButton);
        MaterialButton actionButton = dialogView.findViewById(R.id.actionButton);
        CircularProgressIndicator progressBar = dialogView.findViewById(R.id.progressBar);

        if (itemToEdit != null) {
            titleEditText.setText(itemToEdit.title != null ? itemToEdit.title : itemToEdit.notesUrl); // Ensure no null
            urlEditText.setText(itemToEdit.videoId != null ? "https://www.youtube.com/watch?v=" + itemToEdit.videoId : itemToEdit.notesUrl);
            videoRadioButton.setChecked(itemToEdit.videoId != null);
            notesRadioButton.setChecked(itemToEdit.notesUrl != null);
            if (itemToEdit.notesUrl != null) {
                authorEditText.setText(itemToEdit.author);
                sizeEditText.setText(itemToEdit.size);
                pagesEditText.setText(itemToEdit.pages);
                categoryEditText.setText(itemToEdit.category);
                descriptionEditText.setText(itemToEdit.description);
            }
            actionButton.setText("Update");
            currentContentId = itemToEdit.id;
        } else {
            videoRadioButton.setChecked(true);
            actionButton.setText("Upload");
            currentContentId = null;
        }

        updateInputFieldsVisibility(videoRadioButton.isChecked(), authorEditText, sizeEditText, pagesEditText, categoryEditText, descriptionEditText);

        videoRadioButton.setOnClickListener(v -> {
            updateInputFieldsVisibility(true, authorEditText, sizeEditText, pagesEditText, categoryEditText, descriptionEditText);
        });
        notesRadioButton.setOnClickListener(v -> {
            updateInputFieldsVisibility(false, authorEditText, sizeEditText, pagesEditText, categoryEditText, descriptionEditText);
        });

        actionButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String url = urlEditText.getText().toString().trim();
            boolean isVideo = videoRadioButton.isChecked();

            if (!validateInputs(title, url, isVideo, authorEditText, sizeEditText, pagesEditText, categoryEditText, descriptionEditText)) return;

            progressBar.setVisibility(View.VISIBLE);
            actionButton.setEnabled(false);

            String collection = isVideo ? "videos" : "notes";
            Map<String, Object> content = new HashMap<>();
            if (isVideo) {
                content.put("title", title);
                String videoId = extractYouTubeId(url);
                if (videoId == null) {

                    CustomToast.showToast(requireActivity(), "Invalid YouTube URL");
                    progressBar.setVisibility(View.GONE);
                    actionButton.setEnabled(true);
                    return;
                }
                content.put("videoId", videoId);
                content.put("notesUrl", null);
                content.put("author", null);
                content.put("size", null);
                content.put("pages", null);
                content.put("category", null);
                content.put("description", null);
            } else {
                content.put("notesName", title);
                content.put("author", authorEditText.getText().toString().trim());
                content.put("size", sizeEditText.getText().toString().trim());
                content.put("pages", pagesEditText.getText().toString().trim());
                content.put("category", categoryEditText.getText().toString().trim());
                content.put("description", descriptionEditText.getText().toString().trim());
                content.put("notesUrl", url);
                content.put("videoId", null);
            }
            content.put("timestamp", FieldValue.serverTimestamp());

            if (currentContentId == null) {
                db.collection(collection)
                        .add(content)
                        .addOnSuccessListener(documentReference -> {
                            progressBar.setVisibility(View.GONE);
                            actionButton.setEnabled(true);
                            CustomToast.showToast(requireActivity(), "Content uploaded successfully");
                            bottomSheetDialog.dismiss();
                            fetchContent(collection);
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            actionButton.setEnabled(true);

                            CustomToast.showToast(requireActivity(), "Upload failed: " + e.getMessage());                        });
            } else {
                db.collection(collection)
                        .document(currentContentId)
                        .update(content)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            actionButton.setEnabled(true);
                            CustomToast.showToast(requireActivity(), "Content updated successfully");
                            bottomSheetDialog.dismiss();
                            fetchContent(collection);
                            currentContentId = null;
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            actionButton.setEnabled(true);
                            CustomToast.showToast(requireActivity(), "Update failed: " + e.getMessage());
                        });
            }
        });

        bottomSheetDialog.show();
    }

    private void deleteContent(String contentId, String collection) {
        if (getContext() == null) return;

        db.collection(collection)
                .document(contentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    CustomToast.showToast(requireActivity(), "Content deleted successfully");
                    fetchContent(collection);
                })
                .addOnFailureListener(e -> {
                    CustomToast.showToast(requireActivity(), "Delete failed: " + e.getMessage());
                });
    }

    private void updateInputFieldsVisibility(boolean isVideo, TextInputEditText authorEditText, TextInputEditText sizeEditText, TextInputEditText pagesEditText, TextInputEditText categoryEditText, TextInputEditText descriptionEditText) {
        authorEditText.setVisibility(isVideo ? View.GONE : View.VISIBLE);
        sizeEditText.setVisibility(isVideo ? View.GONE : View.VISIBLE);
        pagesEditText.setVisibility(isVideo ? View.GONE : View.VISIBLE);
        categoryEditText.setVisibility(isVideo ? View.GONE : View.VISIBLE);
        descriptionEditText.setVisibility(isVideo ? View.GONE : View.VISIBLE);
    }

    private boolean validateInputs(String title, String url, boolean isVideo, TextInputEditText authorEditText, TextInputEditText sizeEditText, TextInputEditText pagesEditText, TextInputEditText categoryEditText, TextInputEditText descriptionEditText) {
        if (title.isEmpty() || url.isEmpty()) {
            CustomToast.showToast(requireActivity(), "Title and URL are required");
            return false;
        }
        if (!isVideo) {
            String author = authorEditText.getText().toString().trim();
            String size = sizeEditText.getText().toString().trim();
            String pages = pagesEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            if (author.isEmpty() || size.isEmpty() || pages.isEmpty() || category.isEmpty()) {
                CustomToast.showToast(requireActivity(), "All fields are required");
                return false;
            }
            if (!size.matches("\\d+(\\.\\d+)?\\s*(KB|MB|GB)")) {
                CustomToast.showToast(requireActivity(), "Size must be in a valid format");
                return false;
            }
            if (!pages.matches("\\d+")) {
                CustomToast.showToast(requireActivity(), "Pages must be a valid number");
                return false;
            }
        }
        return true;
    }

    private String extractYouTubeId(String url) {
        String videoId = null;
        if (url != null && !url.isEmpty()) {
            String regex = "(?:youtube\\.com\\/(?:[^/\\n\\s]+\\/)?(?:m\\/)?v\\/|(?:youtu\\.be\\/))?([a-zA-Z0-9_-]{11})";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                videoId = matcher.group(1);
            }
        }
        return videoId;
    }

    private static class ContentItem {
        String id, title, author, size, pages, category, description, videoId, notesUrl;

        ContentItem(String id, String title, String author, String size, String pages, String category, String description, String videoId, String notesUrl) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.size = size;
            this.pages = pages;
            this.category = category;
            this.description = description;
            this.videoId = videoId;
            this.notesUrl = notesUrl;
        }
    }

    private class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
        private List<ContentItem> items;

        ContentAdapter(List<ContentItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_study_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ContentItem item = items.get(position);
            holder.titleTextView.setText(item.title != null ? item.title : item.notesUrl);
            if (item.videoId != null) {
                holder.urlTextView.setText("Video: " + item.videoId);
                holder.authorTextView.setVisibility(View.GONE);
                holder.sizeTextView.setVisibility(View.GONE);
                holder.pagesTextView.setVisibility(View.GONE);
                holder.categoryTextView.setVisibility(View.GONE);
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.urlTextView.setText("Download Link: " + item.notesUrl);
                holder.authorTextView.setText("Author: " + item.author);
                holder.sizeTextView.setText("Size: " + item.size);
                holder.pagesTextView.setText("Pages: " + item.pages);
                holder.categoryTextView.setText("Category: " + item.category);
                holder.descriptionTextView.setText(item.description != null && !item.description.isEmpty() ? "Description: " + item.description: "No description");
                holder.authorTextView.setVisibility(View.VISIBLE);
                holder.sizeTextView.setVisibility(View.VISIBLE);
                holder.pagesTextView.setVisibility(View.VISIBLE);
                holder.categoryTextView.setVisibility(View.VISIBLE);
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            }

            String collection = item.videoId != null ? "videos" : "notes";
            holder.editButton.setOnClickListener(v -> showBottomSheetDialog(item));
            holder.deleteButton.setOnClickListener(v -> deleteContent(item.id, collection));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, authorTextView, sizeTextView, pagesTextView, categoryTextView, descriptionTextView, urlTextView;
            MaterialButton editButton, deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                sizeTextView = itemView.findViewById(R.id.sizeTextView);
                pagesTextView = itemView.findViewById(R.id.pagesTextView);
                categoryTextView = itemView.findViewById(R.id.categoryTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                urlTextView = itemView.findViewById(R.id.urlTextView);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
}

