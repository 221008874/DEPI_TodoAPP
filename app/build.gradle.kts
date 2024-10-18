plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    // build.gradle.kts (Kotlin):
    alias(libs.plugins.org.jetbrains.kotlin.kapt)

    id ("androidx.navigation.safeargs.kotlin")

}

android {
    namespace = "com.abdoahmed.todoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.abdoahmed.todoapp"
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
    buildFeatures{
        dataBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)






    // fragment navigation


    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")


    //Room Database
    implementation("androidx.room:room-runtime:2.6.1")

    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    /* Co-routines Dependencies */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")






    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")


    implementation("androidx.core:core-splashscreen:1.2.0-alpha01")



    implementation ("com.airbnb.android:lottie:6.0.0")




    implementation ("androidx.recyclerview:recyclerview:1.3.2")



    // for adding cardview
    implementation ("androidx.cardview:cardview:1.0.0")


    implementation (libs.androidx.lifecycle.lifecycle.viewmodel.ktx)  // Use the latest version
    implementation (libs.lifecycle.livedata.ktx)   // Use the latest version

    implementation(libs.lifecycle.runtime.ktx)  // Add this if missing
    implementation ("androidx.core:core-ktx:1.6.0") // or latest version






}