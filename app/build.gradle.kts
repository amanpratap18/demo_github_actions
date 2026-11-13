plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.demogithubactions"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.demogithubactions"
        minSdk = 24
        targetSdk = 37
        // CI passes VERSION_CODE / VERSION_NAME; local builds fall back to these defaults.
        versionCode = providers.environmentVariable("VERSION_CODE").orNull?.toInt() ?: 1
        versionName = providers.environmentVariable("VERSION_NAME").orNull ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Release signing comes from environment variables (set from GitHub Secrets in CI).
    // Never commit the keystore or its passwords.
    val keystorePath = providers.environmentVariable("KEYSTORE_PATH").orNull
    val releaseSigning = if (keystorePath != null) {
        signingConfigs.create("release") {
            storeFile = file(keystorePath)
            storePassword = providers.environmentVariable("KEYSTORE_PASSWORD").get()
            keyAlias = providers.environmentVariable("KEY_ALIAS").get()
            keyPassword = providers.environmentVariable("KEY_PASSWORD").get()
        }
    } else {
        null
    }

    buildTypes {
        release {
            signingConfig = releaseSigning
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}