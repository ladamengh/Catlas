package com.ladamengh.petproject.data.cats.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val REQUEST_TIMEOUT_MILLIS = 30_000L
private const val CONNECT_TIMEOUT_MILLIS = 15_000L
private const val SOCKET_TIMEOUT_MILLIS = 30_000L

internal val supabaseJson = Json {
    ignoreUnknownKeys = true
}

fun createSupabaseHttpClient(): HttpClient =
    HttpClient {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("CatlasHttp: $message")
                }
            }
            level = LogLevel.HEADERS
            filter { request -> request.url.host.endsWith("supabase.co") }
            sanitizeHeader { header ->
                header.equals("apikey", ignoreCase = true) ||
                    header.equals("Authorization", ignoreCase = true)
            }
        }
        install(ContentNegotiation) {
            json(supabaseJson)
        }
    }
