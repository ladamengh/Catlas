package com.ladamengh.petproject.data.cats.remote

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString

class CatDtoTest {
    @Test
    fun decodesSupabasePayloadAndMapsSnakeCaseImageUrl() {
        // Arrange
        val json =
            """
            {
              "id": 42,
              "name": "Барсик",
              "breed": "Мейн-кун",
              "location": "Москва",
              "description": "Спокойный",
              "image_url": "https://example.test/cat.jpg",
              "created_at": "2026-11-08T10:00:00Z"
            }
            """.trimIndent()

        // Act
        val dto = supabaseJson.decodeFromString<CatDto>(json)

        // Assert
        assertEquals(
            CatDto(
                id = 42,
                name = "Барсик",
                breed = "Мейн-кун",
                location = "Москва",
                description = "Спокойный",
                imageUrl = "https://example.test/cat.jpg",
            ),
            dto,
        )
        assertEquals("https://example.test/cat.jpg", dto.toDomain().imageUrl)
    }

    @Test
    fun rejectsPayloadMissingRequiredFields() {
        // Arrange
        val incompleteJson = """{"id":42,"name":"Барсик"}"""

        // Act / Assert
        assertFailsWith<SerializationException> {
            supabaseJson.decodeFromString<CatDto>(incompleteJson)
        }
    }
}
