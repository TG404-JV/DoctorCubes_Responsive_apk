import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Google Services (Firebase)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.tvm.doctorcube"
    compileSdk = 35

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) load(file.inputStream())
    }

    defaultConfig {
        applicationId = "com.tvm.doctorcube"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String", "YOUTUBE_API_KEY",
            "\"${localProperties["YOUTUBE_API_KEY"] ?: ""}\""
        )
    }

    signingConfigs {
        getByName("debug") {
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {
            // The storeFile path should be relative to the project root, not an absolute path like "C:/".
            // This makes the project portable and avoids hardcoding paths specific to a developer's machine.
            val keystorePath = localProperties["KEYSTORE_PATH"] as String? ?: "doctorcubes.jks"
            storeFile = file(keystorePath)
            storePassword = localProperties["KEYSTORE_PASSWORD"] as String? ?: "default_password"
            keyAlias = localProperties["KEY_ALIAS"] as String? ?: "default_alias"
            keyPassword = localProperties["KEY_PASSWORD"] as String? ?: "default_key_password"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].assets.srcDirs("src/main/assets")

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation(libs.firebase.firestore)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // UI
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("androidx.webkit:webkit:1.13.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation(libs.android.pdf.viewer)
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")
    implementation("com.gauravk.bubblenavigation:bubblenavigation:1.0.7")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.android.volley:volley:1.2.1")

    // Background tasks
    implementation("androidx.work:work-runtime:2.10.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Animation
    implementation("com.airbnb.android:lottie:6.6.0")

    // Excel, Word, etc.
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.xmlbeans:xmlbeans:5.2.0")
    implementation("org.apache.commons:commons-compress:1.26.0")

    implementation ("com.google.android.play:integrity:1.4.0")
    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Avoid applying plugin twice (already used in plugins block)
// apply(plugin = "com.google.gms.google-services") // ❌ REMOVE THIS LINE
