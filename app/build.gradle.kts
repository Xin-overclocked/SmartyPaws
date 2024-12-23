plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smartypaws"
    compileSdk = (project.findProperty("COMPILE_SDK_VERSION") as String).toInt()

    defaultConfig {
        applicationId = "com.example.smartypaws"
        minSdk = (project.findProperty("MIN_SDK_VERSION") as String).toInt()
        targetSdk = (project.findProperty("TARGET_SDK_VERSION") as String).toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}