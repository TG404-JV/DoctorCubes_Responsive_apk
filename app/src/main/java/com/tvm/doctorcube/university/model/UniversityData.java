package com.tvm.doctorcube.university.model;

import android.content.Context;
import android.content.res.AssetManager;

import com.tvm.doctorcube.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UniversityData {
    private static List<University> universities = null;
    private static final Map<String, Integer> FLAG_RESOURCE_MAP = new HashMap<>();

    static {
        FLAG_RESOURCE_MAP.put("China", R.drawable.flag_china);
        FLAG_RESOURCE_MAP.put("Russia", R.drawable.flag_russia);
        FLAG_RESOURCE_MAP.put("Georgia", R.drawable.flag_georgia);
        FLAG_RESOURCE_MAP.put("Kazakhstan", R.drawable.flag_kazakhstan);
        FLAG_RESOURCE_MAP.put("Nepal", R.drawable.flag_nepal);
        FLAG_RESOURCE_MAP.put("Uzbekistan", R.drawable.flag_uzbekistan);
        // Add more as needed
    }

    public static synchronized List<University> getUniversities(Context context) {
        if (universities == null) {
            universities = loadUniversitiesFromJson(context);
        }
        return new ArrayList<>(universities);
    }

    private static List<University> loadUniversitiesFromJson(Context context) {
        try {
            String jsonString = readJsonFromAssets(context, "universities.json");
            if (jsonString == null) {
                return new ArrayList<>();
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject countriesObject = jsonObject.getJSONObject("countries");
            List<University> universityList = new ArrayList<>();

            // Use keys() to get an Iterator<String> for the country keys
            Iterator<String> countryIterator = countriesObject.keys();
            while (countryIterator.hasNext()) {
                String country = countryIterator.next();
                JSONArray universitiesArray = countriesObject.getJSONArray(country);
                for (int i = 0; i < universitiesArray.length(); i++) {
                    JSONObject uniJson = universitiesArray.getJSONObject(i);

                    University university = new University();
                    university.setId(country.toLowerCase() + "_" + uniJson.optString("name", "").replace(" ", "_").toLowerCase());
                    university.setName(uniJson.optString("name", ""));
                    university.setCity(uniJson.optString("city", ""));
                    university.setCountry(country);
                    int imageResId = getDrawableResourceId(context, uniJson.optString("imageResourceId", ""));
                    university.setLogoResourceId(imageResId);
                    university.setBannerResourceId(imageResId); // Use same image for simplicity
                    university.setFlagResourceId(FLAG_RESOURCE_MAP.getOrDefault(country, R.drawable.icon_university));
                    university.setDescription(uniJson.optString("description", ""));
                    university.setEstablished(uniJson.optString("established", ""));
                    university.setRanking(uniJson.optString("ranking", ""));
                    university.setGrade(uniJson.optString("rating", ""));
                    university.setAddress(uniJson.optString("address", ""));
                    university.setPhone(uniJson.optString("phone", ""));
                    university.setEmail(uniJson.optString("email", ""));
                    university.setAdmissionRequirements(uniJson.optString("admissionRequirements", ""));
                    university.setCourseName(uniJson.optString("program", ""));
                    university.setDegreeType(uniJson.optString("degree", ""));
                    university.setDuration(uniJson.optString("duration", ""));
                    university.setIntake(uniJson.optString("intake", ""));
                    university.setLanguage(uniJson.optString("medium", ""));
                    university.setUniversityType(uniJson.optString("type", ""));
                    university.setWorldRanking(uniJson.optString("worldRanking", ""));
                    university.setScholarshipInfo(uniJson.optString("scholarship", ""));
                    university.setField("Medicine"); // Default, as not in JSON

                    universityList.add(university);
                }
            }

            return universityList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    private static String readJsonFromAssets(Context context, String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            inputStream.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getDrawableResourceId(Context context, String imageResourceId) {
        if (imageResourceId.isEmpty()) {
            return R.drawable.icon_university;
        }
        try {
            return context.getResources().getIdentifier(imageResourceId, "drawable", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.icon_university;
        }
    }
}