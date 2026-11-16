enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Catlas"

include(":androidApp")
include(":shared")
include(":core")
include(":feature")
include(":feature:home-feed")
include(":feature:catalog")
include(":feature:favorites")
include(":feature:profile")
include(":feature:camera")
