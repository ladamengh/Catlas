package com.ladamengh.petproject.gradleplugin

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.GeneralSecurityException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Publishing changes Google Play state and uploads an external artifact")
abstract class PublishToGooglePlayTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val artifactFile: RegularFileProperty

    @get:Input
    abstract val track: Property<String>

    @get:Input
    abstract val rolloutFraction: Property<Double>

    @get:Input
    abstract val releaseNotes: MapProperty<String, String>

    @get:Internal
    abstract val serviceAccountFile: Property<String>

    @TaskAction
    fun publish() {
        validateConfiguration()
        val applicationId = packageName.get()
        val trackName = track.get()
        val artifact = artifactFile.get().asFile
        val artifactName = artifact.name.lowercase()
        val isBundle = artifactName.endsWith(".aab")
        if (!isBundle && !artifactName.endsWith(".apk")) {
            throw GradleException("Artifact must be an .aab or .apk file: ${artifact.name}")
        }

        val publisher = createPublisher()
        var editId: String? = null
        var committed = false
        try {
            val edit: AppEdit = publisher.edits().insert(applicationId, null).execute()
            editId = edit.id

            val contentType = if (isBundle) "application/octet-stream" else "application/vnd.android.package-archive"
            val content = FileContent(contentType, artifact)
            val versionCode = if (isBundle) {
                publisher.edits().bundles().upload(applicationId, editId, content)
                    .execute()
                    .versionCode
                    ?.toLong()
            } else {
                publisher.edits().apks().upload(applicationId, editId, content)
                    .execute()
                    .versionCode
                    ?.toLong()
            } ?: throw GradleException("Google Play did not return the uploaded artifact version code.")

            val notes = releaseNotes.get().map { (language, text) ->
                LocalizedText().setLanguage(language).setText(text)
            }
            val fraction = rolloutFraction.get()
            val release = TrackRelease()
                .setVersionCodes(listOf(versionCode))
                .setStatus(if (fraction < 1.0) "inProgress" else "completed")
            if (fraction < 1.0) {
                release.setUserFraction(fraction)
            }
            if (notes.isNotEmpty()) {
                release.setReleaseNotes(notes)
            }

            publisher.edits().tracks().update(
                applicationId,
                editId,
                trackName,
                Track().setTrack(trackName).setReleases(listOf(release)),
            ).execute()
            publisher.edits().commit(applicationId, editId).execute()
            committed = true
            logger.lifecycle("Published version {} to Google Play track '{}' for {}.", versionCode, trackName, applicationId)
        } catch (exception: IOException) {
            throw GradleException("Google Play publishing failed: ${exception.message}", exception)
        } finally {
            if (editId != null && !committed) {
                try {
                    publisher.edits().delete(applicationId, editId).execute()
                } catch (cleanupException: IOException) {
                    logger.warn("Could not delete incomplete Google Play edit {}: {}", editId, cleanupException.message)
                }
            }
        }
    }

    private fun validateConfiguration() {
        if (packageName.get().isBlank()) {
            throw GradleException("Set playPublisher.packageName to the app's applicationId.")
        }
        if (track.get().isBlank()) {
            throw GradleException("Set playPublisher.track to a Google Play track.")
        }
        val fraction = rolloutFraction.get()
        if (fraction <= 0.0 || fraction > 1.0) {
            throw GradleException("playPublisher.rolloutFraction must be greater than 0 and at most 1.0.")
        }
        val credentialsPath = serviceAccountFile.get()
        if (credentialsPath.isBlank()) {
            throw GradleException(
                "Set GOOGLE_PLAY_SERVICE_ACCOUNT_JSON to the service account JSON file path, " +
                    "or configure playPublisher.serviceAccountFile.",
            )
        }
        if (!File(credentialsPath).isFile) {
            throw GradleException("Google Play service account JSON file does not exist: $credentialsPath")
        }
    }

    private fun createPublisher(): AndroidPublisher {
        try {
            val credentials = FileInputStream(serviceAccountFile.get()).use { input ->
                GoogleCredentials.fromStream(input)
                    .createScoped(listOf(AndroidPublisherScopes.ANDROIDPUBLISHER))
            }
            val credentialsAdapter = HttpCredentialsAdapter(credentials)
            val requestInitializer = HttpRequestInitializer { request ->
                credentialsAdapter.initialize(request)
                request.readTimeout = UPLOAD_TIMEOUT_MILLIS
            }
            return AndroidPublisher.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer,
            ).setApplicationName(APPLICATION_NAME).build()
        } catch (exception: IOException) {
            throw GradleException("Could not initialize Google Play API client: ${exception.message}", exception)
        } catch (exception: GeneralSecurityException) {
            throw GradleException("Could not initialize Google Play API client: ${exception.message}", exception)
        }
    }

    private companion object {
        const val APPLICATION_NAME = "Catlas Gradle Publisher"
        const val UPLOAD_TIMEOUT_MILLIS = 120_000
    }
}
