# Preserve essential Android classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class **.R$* {
    *;
}
-keep public class * extends android.app.Application {
    *;
}
-keep public class * extends android.app.Activity {
    *;
}
-keep public class * extends android.app.Fragment {
    *;
}
-keep public class * extends androidx.fragment.app.Fragment {
    *;
}
-keep public class * extends android.app.Service {
    *;
}
-keep public class * extends android.content.BroadcastReceiver {
    *;
}
-keep public class * extends android.content.ContentProvider {
    *;
}

# Keep annotations
-keepattributes *Annotation*

# Keep Serializable classes
-keep class * implements java.io.Serializable {
    *;
}

# Keep AndroidX libraries
-keep class androidx.security.crypto.** {
    *;
} # For EncryptedSharedPreferences
-keep class androidx.preference.** {
    *;
}
-keep class androidx.core.content.** {
    *;
}
-keep class androidx.appcompat.** {
    *;
}
-keep class androidx.lifecycle.** {
    *;
}
-keep class androidx.navigation.** {
    *;
} # For Navigation component
-keep class androidx.swiperefreshlayout.** {
    *;
} # For SwipeRefreshLayout
-keep class androidx.webkit.** {
    *;
} # For Webkit

# Keep Firebase and Google Play Services
-keep class com.google.firebase.** {
    *;
}
-keep class com.google.android.gms.** {
    *;
}

# Keep Retrofit, Gson, OkHttp, and Volley
-keep class com.google.gson.** {
    *;
}
-keep class retrofit2.** {
    *;
}
-keep class okhttp3.** {
    *;
}
-keep class com.android.volley.** {
    *;
}

# Keep Room Database
-keep class androidx.room.** {
    *;
}

# Keep WorkManager
-keep class androidx.work.** {
    *;
}

# Keep Glide and Picasso
-keep class com.bumptech.glide.** {
    *;
}
-keep class com.squareup.picasso.** {
    *;
}

# Keep YouTube Player
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** {
    *;
}

# Keep Apache POI and dependencies
-keep class org.apache.poi.** {
    *;
}
-keep class org.apache.xmlbeans.** { # Add this
    *;
}
-keep class org.apache.commons.compress.** { # Add this
    *;
}

# Keep Microsoft Office schemas (more specific)
-keep class com.microsoft.schemas.office.** {
    *;
}
-keep class com.microsoft.schemas.vml.** {
    *;
}

# Keep ETSI classes
-keep class org.etsi.uri.x01903.v13.** {
    *;
}

# Keep OpenXML classes
-keep class org.openxmlformats.schemas.** {
    *;
}

-keep class java.awt.** { *; }


# Keep XML Digital Signature classes
-keep class org.w3.x2000.x09.xmldsig.** {
    *;
}

# Keep UI libraries
-keep class com.gauravk.bubblenavigation.** {
    *;
} # Bubble Navigation
-keep class de.hdodenhof.circleimageview.** {
    *;
} # CircleImageView

# Keep Lottie animations
-keep class com.airbnb.lottie.** {
    *;
}

# Keep WhatsApp-related classes for Intent handling
-keep class android.content.Intent {
    *;
}
-keep class android.net.Uri {
    *;
}
-keep class android.content.pm.PackageManager {
    *;
}

# Keep your custom classes
-keep class com.android.doctorcube.** {
    *;
} # Matches your applicationId

# Remove all log messages in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Suppress warnings (use with caution, prefer proper -keep rules)
-dontwarn com.google.**
-dontwarn androidx.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.android.volley.**
-dontwarn org.apache.poi.**
-dontwarn com.bumptech.glide.**
-dontwarn com.squareup.picasso.**
-dontwarn com.pierfrancescosoffritti.androidyoutubeplayer.**
-dontwarn com.gauravk.bubblenavigation.**
-dontwarn de.hdodenhof.circleimageview.**
-dontwarn com.airbnb.lottie.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.apache.commons.compress.**
-dontwarn com.microsoft.schemas.office.**
-dontwarn com.microsoft.schemas.vml.**
-dontwarn org.etsi.uri.x01903.v13.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn org.w3.x2000.x09.xmldsig.**
-ignorewarnings