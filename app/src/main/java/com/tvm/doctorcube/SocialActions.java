package com.tvm.doctorcube;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log; // Import Log for error handling
import android.widget.Toast;

import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SocialActions {

    private static final String TAG = "SocialActions"; // Add a TAG for logging





    public void openWhatsApp(Context context) {
        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(context);
        String fullName = encryptedSharedPreferencesManager.getString("name", "");
        String phone = encryptedSharedPreferencesManager.getString("phone", "");
        String city = encryptedSharedPreferencesManager.getString("city", "");
        String state = encryptedSharedPreferencesManager.getString("state", "");
        String country = encryptedSharedPreferencesManager.getString("country", "");

        // Construct the personalized message
        String personalizedMessage = "Hello dear future doctors 👩‍⚕️👨‍⚕️, Thank you for contacting Doctorcubes Travel Education MBBS Abroad Pvt Ltd! " +
                "Please provide the following information so we can assist you:\n\n" +
                "Your Name: " + (fullName.isEmpty() ? "" : fullName) + "\n" +
                "From Which City: " + (city.isEmpty() ? "" : city + (state.isEmpty() ? "" : ", " + state)) + "\n" +
                "Preferred Country: " + (country.isEmpty() ? "" : country) + "\n" +
                "🌍 Our Office Addresses:\n\n" +
                "🇷🇺 Russia (Head Office): Кемерово, бульвар строителей 43 32 дом.\n" +
                "📞 Contact: +79996482721\n\n" +
                "🇮🇳 India:\n" +
                "📍 Delhi NCR, Haryana: Sec 87, near Vidhya Bhawan High School, Bharat Colony, Kheri Road, Faridabad 121002\n" +
                "📞 Contact: 9667763157\n\n" +
                "📍 Maharashtra: Aurangabad, Kranti Chowk, near Sant Eknath Mandir, above Punjab and Sindh Bank, 431003\n" +
                "📞 Contact: 917517036564\n\n" +
                "🌐 Visit our website: Doctorcubes.com\n" +
                "📱 Download our app: Doctorcubes\n" +
                "▶️ Watch our YouTube video: https://youtu.be/3gMOmU6uYx4?si=-1sw0NeZEk1UoC89";

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            // Construct the WhatsApp URL with the phone number and encoded message
            String url = "https://api.whatsapp.com/send?phone=+917517036564" +
                    "&text=" + URLEncoder.encode(personalizedMessage, StandardCharsets.UTF_8.toString());

            intent.setData(Uri.parse(url));

            // Check for WhatsApp Messenger and WhatsApp Business
            String[] whatsappPackages = {"com.whatsapp", "com.whatsapp.w4b"};
            String targetPackage = null;

            for (String pkg : whatsappPackages) {
                try {
                    packageManager.getPackageInfo(pkg, 0);
                    targetPackage = pkg;
                    break; // Found an installed variant
                } catch (PackageManager.NameNotFoundException e) {
                    // Package not found, try the next one
                }
            }

            if (targetPackage != null) {
                intent.setPackage(targetPackage);
                if (intent.resolveActivity(packageManager) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, targetPackage + " is installed but cannot handle this action", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show();
                // Optional: Redirect to Play Store
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp"));
                context.startActivity(marketIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to open WhatsApp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void makeDirectCall(Context context) {
        String phoneNumber = context.getString(R.string.whatsapp_number); // Use string resource
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        // Use try-catch for SecurityException, and check for Activity
        try {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                CustomToast.showToast((Activity) context, "Cannot make call");
                Log.e(TAG, "No app can handle calls on this device.");
            }

        } catch (SecurityException e) {
            CustomToast.showToast((Activity) context, "Permission denied.  Make sure you have CALL_PHONE permission.");
            Log.e(TAG, "SecurityException: ", e); // Log the exception
        }
    }

    public void openInstagram(Context context) {
        openSocialMediaApp(context, context.getString(R.string.instagram_link), "Instagram not installed");
    }

    public void openTwitter(Context context) {
        openSocialMediaApp(context, context.getString(R.string.twitter_link), "Twitter not installed");
    }

    public void openYouTube(Context context) {
        openSocialMediaApp(context, context.getString(R.string.youtube_link), "YouTube not installed");
    }

    // Refactor common social media opening logic
    private void openSocialMediaApp(Context context, String urlString, String errorMessage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlString));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            CustomToast.showToast((Activity) context, errorMessage);
            Log.e(TAG, errorMessage);
        }
    }

    public void openEmailApp(Context context) {
        String email = context.getString(R.string.email_address);
        String subject = "Consultancy for Taking Admission in MBBS Abroad";
        StringBuilder message = new StringBuilder(); // Use StringBuilder for efficiency

        // Get user data *before* starting the async operation.
        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(context);
        String fullName = encryptedSharedPreferencesManager.getString("name", "");
        String phone = encryptedSharedPreferencesManager.getString("phone", "");
        String city = encryptedSharedPreferencesManager.getString("city", "");
        String state = encryptedSharedPreferencesManager.getString("state", "");
        String country = encryptedSharedPreferencesManager.getString("country", "");

        message.append("Hello,\n\nI would like to know more about the consultancy services for MBBS admissions abroad. ")
                .append("Here are my details:\n")
                .append("Name: ").append(fullName).append("\n")
                .append("Phone: ").append(phone).append("\n")
                .append("City: ").append(city).append("\n")
                .append("State: ").append(state).append("\n")
                .append("Country Where I Want To Study: ").append(country).append("\n\n")
                .append("Please provide the necessary details.\nThank you.");

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(message.toString())));
        context.startActivity(Intent.createChooser(intent, "Send Email"));

    }
}

