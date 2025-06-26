import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") // 👈 Required
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
        versionCode = 5
        versionName = "1.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String", "YOUTUBE_API_KEY",
            "\"${localProperties["YOUTUBE_API_KEY"] ?: ""}\""
        )

        ndkVersion = "29.0.13599879 rc2" // ✅ Minimum required for 16 KB alignment support

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

            println("Keystore path: $keystorePath")
            println("Alias: ${localProperties["KEY_ALIAS"]}")

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



    sourceSets["main"].assets.srcDirs("src/main/assets")

    buildFeatures {
        buildConfig = true
        viewBinding =true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    ndkVersion = "29.0.13599879 rc2"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.google.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)

    // UI
    implementation(libs.core)
    implementation(libs.webkit)
    implementation(libs.swiperefreshlayout)
    implementation(libs.android.pdf.viewer)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.bubblenavigation)
    implementation(libs.circleimageview)

    // Security
    implementation(libs.security.crypto)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.picasso)
    implementation(libs.volley)

    // Background tasks
    implementation(libs.work.runtime)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.appcheck.debug)
    annotationProcessor(libs.room.compiler)

    // Animation
    implementation(libs.lottie)

    // Excel, Word, etc.
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.xmlbeans)
    implementation(libs.commons.compress)
    implementation(libs.library)

    // Lifecycle ViewModel KTX for viewModelScope support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")

    // Kotlin Coroutines for Android (optional but recommended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22") // ✅ Kotlin standard library
    implementation("io.github.justson:agentweb-core:v5.1.1-androidx")     // AgentWeb core
    implementation("io.github.justson:agentweb-filechooser:v5.1.1-androidx") // optional file chooser
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.activity:activity-ktx:1.10.1")


}
