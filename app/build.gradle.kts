import java.util.Properties

val apiKeyFile = project.rootProject.file("apikey.properties")
val apiKeyFileTemplate = project.rootProject.file("apikey.properties.template")

if (apiKeyFile.exists()) {
    println("apikey.properties already exist")
} else {
    apiKeyFile.writeText(apiKeyFileTemplate.readText())
    println("apikey.properties has been created from the apikey.properties.template")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.cse476.weatherapphw4"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cse476.weatherapphw4"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val properties = Properties()
        properties.load(apiKeyFile.inputStream())
        val apiKey = properties.getProperty("API_KEY") ?: ""

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"${apiKey}\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.gson.v2110)
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.hilt.android.compiler)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}