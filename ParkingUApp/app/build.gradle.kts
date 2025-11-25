plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // ELIMINADO: alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.parkinguapp"
    compileSdk = 34 // <-- CAMBIADO A 34

    defaultConfig {
        applicationId = "com.example.parkinguapp"
        minSdk = 24
        targetSdk = 34 // <-- CAMBIADO A 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        // ELIMINADO: compose = true
        viewBinding = true
    }
}

dependencies {

    // Dependencias Principales de Vistas (XML)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Esta es la que usamos para Material 3 (Vistas)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)

    // Navegación (para Vistas)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle (ViewModels, LiveData - útil para Vistas)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation("com.android.support:support-annotations:28.0.0")

    // Testing (Estándar)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.zxing:core:3.5.3")

    //dependencia para sccaner qr
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // --- DEPENDENCIAS DE COMPOSE ELIMINADAS ---
}