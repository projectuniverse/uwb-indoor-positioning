plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Hilt
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.google.dagger.hilt.android)
    // Kotlin Serialization
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    // For the Maps API key
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.uwbindoorpositioning"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uwbindoorpositioning"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // Added to be able to use a TopAppBar
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        // Added to be able to use rememberPagerState
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Google Fonts (Roboto Flex)
    implementation(libs.androidx.ui.text.google.fonts)
    // Material icons (not symbols)
    implementation(libs.androidx.compose.material.material.icons.extended)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Preferences DataStore (to toggle dark mode/light mode)
    implementation(libs.androidx.datastore.preferences)
    // Hilt (dependency injection)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // Hilt integration with Navigation Compose library
    implementation(libs.androidx.hilt.navigation.compose)
    // To use rememberDrawablePainter() for the connection animation
    implementation(libs.accompanist.drawablepainter)
    // Google Maps Compose library
    implementation(libs.maps.compose)
    // Google Maps Compose utility library
    implementation(libs.maps.compose.utils)
    // Google Maps Compose widgets library
    implementation(libs.maps.compose.widgets)
    // Nearby Connections
    implementation(libs.play.services.nearby)
    // UWB API
    implementation(libs.androidx.uwb)
    // JSON Serialization
    implementation(libs.kotlinx.serialization.json)
    // Getting device window size
    implementation(libs.androidx.adaptive.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

