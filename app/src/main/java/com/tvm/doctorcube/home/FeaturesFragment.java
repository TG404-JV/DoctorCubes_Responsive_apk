package com.tvm.doctorcube.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.communication.CommunicationUtils;
import com.tvm.doctorcube.home.data.FeatureData;
import com.tvm.doctorcube.home.model.Feature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FeaturesFragment extends Fragment {

    private ImageView ivFeatureImage;
    private TextView tvFeatureTitle, tvFeatureDescription;
    private RecyclerView rvBenefits, rvSteps, rvRelatedFeatures;
    private MaterialButton btnContact;
    private FloatingActionButton fabContact;
    private NavController navController;

    private Feature feature;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_features, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        ivFeatureImage = view.findViewById(R.id.ivFeatureImage);
        tvFeatureTitle = view.findViewById(R.id.tvFeatureTitle);
        tvFeatureDescription = view.findViewById(R.id.tvFeatureDescription);
        rvBenefits = view.findViewById(R.id.rvBenefits);
        rvSteps = view.findViewById(R.id.rvSteps);
        rvRelatedFeatures = view.findViewById(R.id.rvRelatedFeatures);
        btnContact = view.findViewById(R.id.btnContact);


        if (getArguments() != null) {
            feature = (Feature) getArguments().getSerializable("FEATURE");
        }

        if (feature == null) {
            CustomToast.showToast(requireActivity(), "Feature data not found");
            navController.navigateUp();
            return;
        }

        loadFeatureData();



    }

    private void loadFeatureData() {
        FeatureDetails featureDetails = loadFeatureDetailsFromJson(feature.getId());
        if (featureDetails != null) {
            tvFeatureTitle.setText(featureDetails.title);
            tvFeatureDescription.setText(featureDetails.description);
            int imageId = getResourceId(featureDetails.imageResId);
            ivFeatureImage.setImageResource(imageId);
            setupBenefitsRecyclerView(featureDetails.benefits);
            setupStepsRecyclerView(featureDetails.steps);
        } else {
            tvFeatureTitle.setText(feature.getTitle());
            tvFeatureDescription.setText(feature.getDescription());
            ivFeatureImage.setImageResource(feature.getIconResource() != 0 ? feature.getIconResource() : R.drawable.ic_waving_bg);
            setupBenefitsRecyclerView(new ArrayList<>());
            setupStepsRecyclerView(new ArrayList<>());
        }

        setupRelatedFeaturesRecyclerView();
    }

    private int getResourceId(String resName) {
        try {
            if (resName == null || resName.isEmpty()) {
                return R.drawable.ic_waving_bg;
            }
            int resId = getResources().getIdentifier(resName, "drawable", requireContext().getPackageName());
            return resId != 0 ? resId : R.drawable.ic_waving_bg;
        } catch (Exception e) {
            Log.e("FeaturesFragment", "Invalid resource name: " + resName, e);
            return R.drawable.ic_waving_bg;
        }
    }

    private FeatureDetails loadFeatureDetailsFromJson(int featureId) {
        try {
            InputStream is = getResources().openRawResource(R.raw.feature_details);
            byte[] buffer = new byte[is.available()];
            int bytesRead = is.read(buffer);
            is.close();
            if (bytesRead <= 0) {
                Log.e("FeaturesFragment", "No data read from feature_details.json");
                CustomToast.showToast(requireActivity(), "Error loading feature details: Empty file");
                return null;
            }
            String json = new String(buffer, StandardCharsets.UTF_8);
            Log.d("FeaturesFragment", "JSON Content: " + json.substring(0, Math.min(json.length(), 50)));

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getInt("id") == featureId) {
                    String title = jsonObject.getString("title");
                    String description = jsonObject.getString("description");
                    String imageResId = jsonObject.optString("imageResId", "");

                    List<Benefit> benefits = new ArrayList<>();
                    JSONArray benefitsArray = jsonObject.getJSONArray("benefits");
                    for (int j = 0; j < benefitsArray.length(); j++) {
                        JSONObject benefitObj = benefitsArray.getJSONObject(j);
                        benefits.add(new Benefit(
                                benefitObj.getString("title"),
                                benefitObj.getString("description")
                        ));
                    }

                    List<Step> steps = new ArrayList<>();
                    JSONArray stepsArray = jsonObject.getJSONArray("steps");
                    for (int j = 0; j < stepsArray.length(); j++) {
                        JSONObject stepObj = stepsArray.getJSONObject(j);
                        steps.add(new Step(
                                stepObj.getString("title"),
                                stepObj.getString("description")
                        ));
                    }

                    return new FeatureDetails(title, description, imageResId, benefits, steps);
                }
            }
            Log.w("FeaturesFragment", "No feature found with id: " + featureId);
        } catch (Exception e) {
            Log.e("FeaturesFragment", "Error loading feature_details.json", e);
            CustomToast.showToast(requireActivity(), "Error loading feature details");
        }
        return null;
    }

    private void setupBenefitsRecyclerView(List<Benefit> benefits) {
        rvBenefits.setLayoutManager(new LinearLayoutManager(getContext()));
        BenefitAdapter benefitAdapter = new BenefitAdapter(benefits);
        rvBenefits.setAdapter(benefitAdapter);
    }

    private void setupStepsRecyclerView(List<Step> steps) {
        rvSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        StepAdapter stepAdapter = new StepAdapter(steps);
        rvSteps.setAdapter(stepAdapter);
    }

    private void setupRelatedFeaturesRecyclerView() {
        rvRelatedFeatures.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Feature> relatedFeatures = getRelatedFeatures();
        RelatedFeatureAdapter relatedFeatureAdapter = new RelatedFeatureAdapter(relatedFeatures, feature1 -> {
            Bundle args = new Bundle();
            args.putSerializable("FEATURE", feature1);
            navController.navigate(R.id.action_featuresFragment_self, args);
        });
        rvRelatedFeatures.setAdapter(relatedFeatureAdapter);
    }

    private List<Feature> getRelatedFeatures() {
        List<Feature> relatedFeatures = new ArrayList<>();
        List<Feature> allFeatures = FeatureData.getInstance().getFeatures(requireContext());
        for (Feature f : allFeatures) {
            if (f.getId() != feature.getId()) {
                relatedFeatures.add(f);
            }
        }
        return relatedFeatures;
    }

    private static class FeatureDetails {
        String title;
        String description;
        String imageResId;
        List<Benefit> benefits;
        List<Step> steps;

        FeatureDetails(String title, String description, String imageResId, List<Benefit> benefits, List<Step> steps) {
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
            this.benefits = benefits;
            this.steps = steps;
        }
    }

    private static class Benefit {
        private final String title;
        private final String description;

        public Benefit(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class BenefitAdapter extends RecyclerView.Adapter<BenefitAdapter.ViewHolder> {
        private final List<Benefit> benefits;

        public BenefitAdapter(List<Benefit> benefits) {
            this.benefits = benefits;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_benifits, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Benefit benefit = benefits.get(position);
            holder.tvBenefitNumber.setText(String.valueOf(position + 1));
            holder.tvBenefitTitle.setText(benefit.getTitle());
            holder.tvBenefitDescription.setText(benefit.getDescription());
            if (position == benefits.size() - 1) {
                holder.viewConnector.setVisibility(View.GONE);
            } else {
                holder.viewConnector.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return benefits.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBenefitNumber, tvBenefitTitle, tvBenefitDescription;
            View viewConnector;

            ViewHolder(View itemView) {
                super(itemView);
                tvBenefitNumber = itemView.findViewById(R.id.tvStepNumber);
                tvBenefitTitle = itemView.findViewById(R.id.tvStepTitle);
                tvBenefitDescription = itemView.findViewById(R.id.tvStepDescription);
                viewConnector = itemView.findViewById(R.id.viewConnector);
            }
        }
    }

    private static class Step {
        private final String title;
        private final String description;

        public Step(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
        private final List<Step> steps;

        public StepAdapter(List<Step> steps) {
            this.steps = steps;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_step, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Step step = steps.get(position);
            holder.tvStepNumber.setText(String.valueOf(position + 1));
            holder.tvStepTitle.setText(step.getTitle());
            holder.tvStepDescription.setText(step.getDescription());
            holder.ivStepImage.setVisibility(View.GONE);
            if (position == steps.size() - 1) {
                holder.viewConnector.setVisibility(View.GONE);
            } else {
                holder.viewConnector.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return steps.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvStepNumber, tvStepTitle, tvStepDescription;
            ImageView ivStepImage;
            View viewConnector;

            ViewHolder(View itemView) {
                super(itemView);
                tvStepNumber = itemView.findViewById(R.id.tvStepNumber);
                tvStepTitle = itemView.findViewById(R.id.tvStepTitle);
                tvStepDescription = itemView.findViewById(R.id.tvStepDescription);
                ivStepImage = itemView.findViewById(R.id.ivStepImage);
                viewConnector = itemView.findViewById(R.id.viewConnector);
            }
        }
    }

    private static class RelatedFeatureAdapter extends RecyclerView.Adapter<RelatedFeatureAdapter.ViewHolder> {
        private final List<Feature> features;
        private final OnFeatureClickListener listener;

        public RelatedFeatureAdapter(List<Feature> features, OnFeatureClickListener listener) {
            this.features = features;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_related_feature, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Feature feature = features.get(position);
            if (feature.getIconResource() != 0) {
                holder.ivRelatedFeature.setImageResource(feature.getIconResource());
                holder.ivFeatureIcon.setImageResource(feature.getIconResource());
            }
            holder.tvRelatedFeatureTitle.setText(feature.getTitle());
            holder.tvRelatedFeatureDescription.setText(feature.getDescription());
            holder.itemView.setOnClickListener(v -> listener.onFeatureClick(feature));
        }

        @Override
        public int getItemCount() {
            return features.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivRelatedFeature, ivFeatureIcon;
            TextView tvRelatedFeatureTitle, tvRelatedFeatureDescription;

            ViewHolder(View itemView) {
                super(itemView);
                ivRelatedFeature = itemView.findViewById(R.id.ivRelatedFeature);
                ivFeatureIcon = itemView.findViewById(R.id.ivFeatureIcon);
                tvRelatedFeatureTitle = itemView.findViewById(R.id.tvRelatedFeatureTitle);
                tvRelatedFeatureDescription = itemView.findViewById(R.id.tvRelatedFeatureDescription);
            }
        }

        interface OnFeatureClickListener {
            void onFeatureClick(Feature feature);
        }
    }
}