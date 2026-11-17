plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
    id("com.ladamengh.catlas.play-publisher")
}

playPublisher {
    packageName = "com.ladamengh.petproject"
    artifactPath = "build/outputs/bundle/release/androidApp-release.aab"
    track = providers.gradleProperty("playTrack").orElse("internal").get()
    rolloutFraction = providers.gradleProperty("playRolloutFraction").orElse("1.0").get().toDouble()
    serviceAccountFile = providers.gradleProperty("playServiceAccountFile").orNull
    releaseNotes("ru-RU", "Улучшена работа приложения и исправлены ошибки.")
    releaseNotes("en-US", "App improvements and bug fixes.")
}

tasks.named("publishToGooglePlay") {
    dependsOn("bundleRelease")
}

detekt {
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.named<dev.detekt.gradle.Detekt>("detekt") {
    setSource(files("src"))
    include("**/*.kt")
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
    androidTestImplementation(project(":feature:profile"))
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
