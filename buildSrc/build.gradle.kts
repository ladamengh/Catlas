plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20261026-2.9.2")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.41.0")
}

gradlePlugin {
    plugins {
        register("catlasPlayPublisher") {
            id = "com.ladamengh.catlas.play-publisher"
            implementationClass = "com.ladamengh.petproject.gradleplugin.PlayPublisherPlugin"
            displayName = "Catlas Google Play Publisher"
            description = "Uploads an APK or AAB and publishes a release to Google Play."
        }
    }
}
