plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
}

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    exclude("**/generated/**")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

android {
    namespace = "com.ladamengh.petproject"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ladamengh.petproject"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.shared)
    implementation(project(":core"))
    implementation(libs.koin.core)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.koin.core)
    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.kaspresso.compose.support)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.ktor.client.okhttp)
    androidTestImplementation(libs.ktor.client.content.negotiation)
    androidTestImplementation(libs.ktor.serialization.kotlinx.json)
    androidTestImplementation(libs.room.runtime)
    debugImplementation(libs.compose.ui.test.manifest)
}
