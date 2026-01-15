plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.26"
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.quotevault"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quotevault"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.generativeai)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.31.5-beta")
    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

// Firebase Core (required)
    implementation("com.google.firebase:firebase-common-ktx:21.0.0")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")

    // (Optional) Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx:22.0.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("androidx.room:room-ktx:2.6.0")

    // Supabase
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.2.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.2.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.2.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Widget
    implementation("androidx.glance:glance-appwidget:1.0.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}