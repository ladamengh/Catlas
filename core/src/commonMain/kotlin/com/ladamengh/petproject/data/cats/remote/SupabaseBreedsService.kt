package com.ladamengh.petproject.data.cats.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class SupabaseBreedsService(
    private val httpClient: HttpClient,
    projectUrl: String,
    private val publishableKey: String,
) {
    private val breedsUrl = "${projectUrl.trimEnd('/')}/rest/v1/cat_breeds"

    suspend fun getBreeds(): List<CatBreedDto> =
        httpClient.get(breedsUrl) {
            parameter("select", "id,name,origin,temperament,description,image_url")
            header("apikey", publishableKey)
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }.body()
}
