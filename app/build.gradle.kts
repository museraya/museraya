plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.museraya"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.museraya"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.test.espresso:espresso-core:3.5.0")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.runtime.android)
    implementation(libs.firebase.database)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.firebase.messaging.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.material)

    implementation("io.github.sceneview:arsceneview:2.0.3")
    implementation(libs.kotlin.coroutines)
    implementation("com.google.ar:core:1.46.0")
    implementation(libs.cardview)
    implementation("com.google.android.material:material:1.12.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Compose dependencies
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    // Accompanist (Optional - for extra Compose functionality)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.0")

    // ViewPager2 for the carousel
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Material Components for Android
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}
