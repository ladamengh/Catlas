package com.ladamengh.petproject.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class PlayPublisherPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("playPublisher", PlayPublisherExtension::class.java)

        project.tasks.register("publishToGooglePlay", PublishToGooglePlayTask::class.java)
            .configure {
                group = "publishing"
                description = "Uploads the configured APK or AAB to Google Play."
                packageName.set(project.provider { extension.packageName })
                artifactFile.set(
                    project.layout.file(project.provider { project.file(extension.artifactPath) }),
                )
                track.set(project.provider { extension.track })
                rolloutFraction.set(project.provider { extension.rolloutFraction })
                releaseNotes.set(project.provider { extension.releaseNotes.toMap() })
                serviceAccountFile.set(
                    project.provider {
                        extension.serviceAccountFile
                            ?.takeIf(String::isNotBlank)
                            ?: System.getenv("GOOGLE_PLAY_SERVICE_ACCOUNT_JSON").orEmpty()
                    },
                )
            }
    }
}
