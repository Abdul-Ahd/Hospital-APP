plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.gis"
    compileSdk = 34

    defaultConfig {
        buildConfigField("String", "ARC_GIS_API_KEY", "\"AAPK0cade6e9e7f544b58c5df3449dcb342e--yR7rdK6ksLuMsKWUspaSefSbzmckv6kbPItLjT3rf2dLJNjnUYFg2iZ19Wv9t0\"")
        applicationId = "com.example.gis"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        buildConfig = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes.add("META-INF/**") // Exclude META-INF files
        }
    }
//    kotlinOptions {
//        jvmTarget = "17"
//    }
    namespace = "com.example.app"
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
//    packagingOptions {
//        exclude("/lib/arm64-v8a/libruntimecore.so") // Adjust the path as needed
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/lib/arm64-v8a/libruntimecore.so"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")

//    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.6.1") {
//        exclude group: "com.mapbox.android.core", module: "common"
//    }

    implementation("com.mapbox.maps:android:11.2.1")
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-services:6.15.0")
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-core:6.15.0")
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-geojson:6.15.0")
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-turf:6.15.0")

    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.fragment:fragment:1.6.2")
    implementation ("ir.beigirad:zigzagview:1.2.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    implementation ("com.esri.arcgisruntime:arcgis-android:100.11.0")
    implementation ("com.airbnb.android:lottie:4.2.0")
    implementation("com.daimajia.androidanimations:library:2.4@aar")




}

configurations.all{
    resolutionStrategy{
        force("com.mapbox.maps:android:11.2.1")
        force("com.mapbox.mapboxsdk:mapbox-android-sdk:9.6.1")
    }
}