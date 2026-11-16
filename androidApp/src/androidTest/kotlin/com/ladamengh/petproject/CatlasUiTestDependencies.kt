package com.ladamengh.petproject

import android.content.Context
import androidx.room.Room
import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.di.appModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

object CatlasUiTestDependencies {
    private const val TEST_API_KEY = "catlas-ui-test-key"

    lateinit var mockWebServer: MockWebServer
        private set
    lateinit var isolatedKoinApplication: KoinApplication
        private set
    private lateinit var database: CatlasDatabase
    private lateinit var httpClient: HttpClient

    fun start(context: Context) {
        mockWebServer = MockWebServer().apply { start() }
        database = Room.inMemoryDatabaseBuilder(context, CatlasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        httpClient = HttpClient(OkHttp) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        mockWebServer.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """[{"id":101,"name":"Луна","breed":"Сибирская","location":"Казань","description":"Спокойная кошка ищет дом","image_url":""}]""",
                ),
        )

        isolatedKoinApplication = koinApplication {
            modules(
                appModule(
                    database = database,
                    projectUrl = mockWebServer.url("/").toString().trimEnd('/'),
                    publishableKey = TEST_API_KEY,
                    httpClientFactory = { httpClient },
                ),
            )
        }
    }

    fun stop() {
        isolatedKoinApplication.close()
        mockWebServer.shutdown()
    }
}
