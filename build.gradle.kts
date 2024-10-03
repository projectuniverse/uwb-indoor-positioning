// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Hilt
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    // Kotlin Serialization
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization) apply false
    // For the Maps API key
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}

buildscript {
    dependencies {
        // For the Maps API key
        classpath(libs.secrets.gradle.plugin)
    }
}
