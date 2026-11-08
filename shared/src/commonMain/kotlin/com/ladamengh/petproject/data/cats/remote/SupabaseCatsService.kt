package com.ladamengh.petproject.data.cats.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlin.random.Random

class SupabaseCatsService(
    private val httpClient: HttpClient,
    projectUrl: String,
    private val publishableKey: String,
) {
    private val baseUrl = projectUrl.trimEnd('/')
    private val catsUrl = "$baseUrl/rest/v1/cats"
    private val storageUrl = "$baseUrl/storage/v1/object/cat-images"

    suspend fun getCats(): List<CatDto> = httpClient.get(catsUrl) {
        parameter("select", "id,name,breed,location,description,image_url")
        parameter("order", "id.desc")
        withApiKey()
    }.body()

    suspend fun createCat(
        name: String,
        breed: String,
        location: String,
        description: String,
        image: ByteArray,
    ): CatDto {
        val imagePath = "${randomPath()}.jpg"
        uploadImage(imagePath, image)
        return httpClient.post(catsUrl) {
            withApiKey()
            header(HttpHeaders.Prefer, "return=representation")
            contentType(ContentType.Application.Json)
            setBody(
                listOf(
                    CreateCatDto(
                        name = name.trim(),
                        breed = breed.trim(),
                        location = location.trim(),
                        description = description.trim(),
                        imageUrl = "$storageUrl/public/$imagePath",
                    ),
                ),
            )
        }.body<List<CatDto>>().first()
    }

    private suspend fun uploadImage(path: String, bytes: ByteArray) {
        httpClient.post("$storageUrl/$path") {
            withApiKey()
            contentType(ContentType.Image.JPEG)
            setBody(bytes)
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.withApiKey() {
        header("apikey", publishableKey)
        header(HttpHeaders.Accept, ContentType.Application.Json)
    }

    private fun randomPath(): String = Random.nextBytes(16)
        .joinToString(separator = "") { byte -> byte.toUByte().toString(16).padStart(2, '0') }
}
