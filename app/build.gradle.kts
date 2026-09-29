plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.lemon.cardscanner"
    compileSdk = 34

    defaultConfig {
        // PROVISIONAL app id - not final, same as the tv-remote project started.
        applicationId = "com.lemon.cardscanner"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        // Backend offer feed. Modus operandi: the app is a thin display client.
        // Card facts are compiled in from the bank modules; this feed carries
        // only the daily offer snippets the backend scan writes to
        // configs/offers.json. Change this when the repo moves.
        buildConfigField(
            "String", "CONFIG_BASE_URL",
            "\"https://raw.githubusercontent.com/mashhadazam/card-scanner/main/configs\""
        )
    }

    signingConfigs {
        // Fixed debug key so CI builds keep one signature and install as updates.
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "tvremote-debug-2026"
            keyAlias = "tvremote-debug"
            keyPassword = "tvremote-debug-2026"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    sourceSets {
        // Single source of truth: the app bundles the same configs/ the backend scans.
        getByName("main").assets.srcDir("../configs")
    }
}

dependencies {
    implementation(project(":core"))
    // One module per bank/provider — the app rolls them into one catalog.
    implementation(project(":banks:td"))
    implementation(project(":banks:rbc"))
    implementation(project(":banks:cibc"))
    implementation(project(":banks:bmo"))
    implementation(project(":banks:amex"))

    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Card scan: CameraX capture + on-device ML Kit OCR.
    // The photo never leaves the phone; only BIN + last4 are kept.
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("com.google.mlkit:text-recognition:16.0.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
