plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.trackpoint"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.trackpoint"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // AWS S3 SDK for Backblaze B2 Compatibility
    implementation("com.amazonaws:aws-android-sdk-s3:2.75.0")
    implementation("com.amazonaws:aws-android-sdk-core:2.75.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
