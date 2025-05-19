package com.tvm.doctorcube.university.model;

import android.content.Context;
import android.util.Log;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniversityData {

    public static List<University> getUniversities(Context context) {
        List<University> universities = new ArrayList<>();
        try {
            // Read JSON file
            InputStream is = context.getResources().openRawResource(R.raw.universities);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Parse JSON
            JSONObject jsonObject = new JSONObject(json);
            JSONObject countries = jsonObject.getJSONObject("countries");

            // Iterate through countries
            String[] countryNames = {"Russia", "Uzbekistan", "Georgia", "China", "Kazakhstan"};
            for (String country : countryNames) {
                if (countries.has(country)) {
                    JSONArray uniArray = countries.getJSONArray(country);
                    for (int i = 0; i < uniArray.length(); i++) {
                        JSONObject uni = uniArray.getJSONObject(i);
                        try {
                            String id = uni.getString("name").toLowerCase().replace(" ", "_") + "_" + country.toLowerCase();
                            String name = uni.optString("name", "Unknown University");
                            String city = uni.optString("city", "Unknown City");
                            String program = uni.optString("program", "General Medicine (MBBS)");
                            String degree = uni.optString("degree", "MBBS");
                            String duration = uni.optString("duration", "Unknown");
                            String rating = uni.optString("rating", "N/A");
                            String intake = uni.optString("intake", "September");
                            String medium = uni.optString("medium", "English");
                            String type = uni.optString("type", "Public");
                            String imageResourceId = uni.optString("imageResourceId", "university_campus");
                            String logoResourceId = uni.optString("logoResourceId", "university_campus");
                            String worldRanking = uni.optString("worldRanking", "N/A");
                            String scholarship = uni.optString("scholarship", "Not Available");
                            String description = uni.optString("description", "No description available.");
                            String established = uni.optString("established", "N/A");
                            String detailedRanking = uni.optString("ranking", "N/A");
                            String address = uni.optString("address", "N/A");
                            String phone = uni.optString("phone", "N/A");
                            String email = uni.optString("email", "N/A");
                            String admissionRequirements = uni.optString("admissionRequirements", "N/A");

                            University university = new University(
                                    id,
                                    name,
                                    city,
                                    country,
                                    program,
                                    degree,
                                    duration,
                                    rating,
                                    intake,
                                    medium,
                                    type,
                                    getDrawableResourceId(context, imageResourceId),
                                    getDrawableResourceId(context, logoResourceId),
                                    getDrawableResourceId(context, "flag_" + country.toLowerCase()),
                                    "Medical",
                                    worldRanking,
                                    scholarship
                            );
                            university.setDescription(description);
                            university.setEstablished(established);
                            university.setDetailedRanking(detailedRanking);
                            university.setAddress(address);
                            university.setPhone(phone);
                            university.setEmail(email);
                            university.setAdmissionRequirements(admissionRequirements);
                            universities.add(university);
                        } catch (Exception e) {
                            Log.e("UniversityData", "Error parsing university in " + country + ": " + uni.toString(), e);
                        }
                    }
                } else {
                    Log.w("UniversityData", "Country not found in JSON: " + country);
                }
            }
        } catch (Exception e) {
            Log.e("UniversityData", "Error loading universities", e);
        }
        Log.d("UniversityData", "Loaded " + universities.size() + " universities");
        return universities;
    }

    public static List<Country> getCountries(Context context) {
        List<University> allUniversities = getUniversities(context);
        Map<String, List<University>> countryMap = new HashMap<>();
        for (University university : allUniversities) {
            String countryName = university.getCountry();
            if (countryName != null && !countryName.isEmpty()) {
                countryMap.computeIfAbsent(countryName, k -> new ArrayList<>()).add(university);
            }
        }

        List<Country> countries = new ArrayList<>();
        int[] flagResources = {
                R.drawable.flag_russia, R.drawable.flag_uzbekistan, R.drawable.flag_georgia,
                R.drawable.flag_china, R.drawable.flag_kazakhstan
        };
        String[] countryNames = {"Russia", "Uzbekistan", "Georgia", "China", "Kazakhstan"};

        for (int i = 0; i < countryNames.length; i++) {
            String countryName = countryNames[i];
            List<University> unis = countryMap.getOrDefault(countryName, new ArrayList<>());
            if (!unis.isEmpty()) {
                float averageRating = calculateAverageRating(unis);
                countries.add(new Country(countryName, unis, averageRating, flagResources[i]));
            }
        }
        Log.d("UniversityData", "Loaded " + countries.size() + " countries");
        return countries;
    }

    private static float calculateAverageRating(List<University> universities) {
        if (universities.isEmpty()) return 0f;
        float total = 0f;
        Map<String, Float> ratingMap = new HashMap<>();
        ratingMap.put("A+", 4.3f); ratingMap.put("A", 4.0f); ratingMap.put("A-", 3.7f);
        ratingMap.put("B+", 3.3f); ratingMap.put("B", 3.0f); ratingMap.put("B-", 2.7f);
        ratingMap.put("C+", 2.3f); ratingMap.put("C", 2.0f); ratingMap.put("C-", 1.7f);
        ratingMap.put("D", 1.0f); ratingMap.put("F", 0.0f);

        for (University university : universities) {
            total += ratingMap.getOrDefault(university.getGrade(), 0f);
        }
        return total / universities.size();
    }

    private static int getDrawableResourceId(Context context, String name) {
        try {
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (resId == 0) {
                Log.w("UniversityData", "Drawable not found: " + name);
                return R.drawable.university_campus;
            }
            return resId;
        } catch (Exception e) {
            Log.e("UniversityData", "Error getting drawable: " + name, e);
            return R.drawable.university_campus;
        }
    }
}