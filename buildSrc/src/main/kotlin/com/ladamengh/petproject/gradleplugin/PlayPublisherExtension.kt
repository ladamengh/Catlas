package com.ladamengh.petproject.gradleplugin

open class PlayPublisherExtension {
    var packageName: String = ""
    var artifactPath: String = ""
    var track: String = "internal"
    var rolloutFraction: Double = 1.0
    var serviceAccountFile: String? = null
    val releaseNotes: MutableMap<String, String> = linkedMapOf()

    fun releaseNotes(language: String, text: String) {
        releaseNotes[language] = text
    }
}
