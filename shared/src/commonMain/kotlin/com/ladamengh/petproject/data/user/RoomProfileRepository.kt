package com.ladamengh.petproject.data.user

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.LocalProfileEntity
import com.ladamengh.petproject.data.local.SubmittedCatEntity
import com.ladamengh.petproject.domain.model.UserProfile
import com.ladamengh.petproject.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

/** External cat image reference saved after the user submits a cat. */
data class SubmittedCatRecord(
    val imageId: String,
    val imageUrl: String,
    val breedId: String? = null,
    val breedName: String? = null,
    val createdAtEpochMillis: Long,
)

/** Local profile and submitted-cat history persisted in the shared Room database. */
class RoomProfileRepository(
    database: CatlasDatabase,
) : ProfileRepository {
    private val dao = database.dao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val profile: StateFlow<UserProfile> = dao.observeProfile()
        .map { entity ->
            entity?.let {
                UserProfile(
                    id = it.localId,
                    displayName = it.displayName,
                    description = it.description,
                    city = it.city,
                    avatarImage = it.avatarImage,
                )
            } ?: UserProfile(
                id = "",
                displayName = DEFAULT_DISPLAY_NAME,
                description = null,
                city = null,
                avatarImage = null,
            )
        }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            UserProfile(
                id = "",
                displayName = DEFAULT_DISPLAY_NAME,
                description = null,
                city = null,
                avatarImage = null,
            ),
        )

    val submittedCats: StateFlow<List<SubmittedCatRecord>> = dao.observeSubmittedCats()
        .map { records -> records.map(SubmittedCatEntity::toRecord) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            ensureProfileExists()
        }
    }

    override fun saveProfile(
        displayName: String,
        description: String?,
        city: String?,
        avatarImage: ByteArray?,
    ) {
        val trimmedName = displayName.trim()
        if (trimmedName.isBlank()) return
        scope.launch {
            ensureProfileExists()
            val oldProfile = requireNotNull(dao.getProfile())
            dao.saveProfile(
                LocalProfileEntity(
                    localId = oldProfile.localId,
                    displayName = trimmedName,
                    description = description?.trim()?.takeIf(String::isNotEmpty),
                    city = city?.trim()?.takeIf(String::isNotEmpty),
                    avatarImage = avatarImage,
                ),
            )
        }
    }

    fun saveSubmittedCat(record: SubmittedCatRecord) {
        scope.launch {
            dao.saveSubmittedCat(
                SubmittedCatEntity(
                    imageId = record.imageId,
                    imageUrl = record.imageUrl,
                    breedId = record.breedId,
                    breedName = record.breedName,
                    createdAtEpochMillis = record.createdAtEpochMillis,
                ),
            )
        }
    }

    fun removeSubmittedCat(imageId: String) {
        scope.launch { dao.removeSubmittedCat(imageId) }
    }

    private suspend fun ensureProfileExists() {
        dao.createProfileIfMissing(
            LocalProfileEntity(
                localId = createLocalId(),
                displayName = DEFAULT_DISPLAY_NAME,
            ),
        )
    }

    private fun createLocalId(): String = Random.nextBytes(16)
        .joinToString(separator = "") { byte -> byte.toUByte().toString(16).padStart(2, '0') }

    private companion object {
        const val DEFAULT_DISPLAY_NAME = "Любитель котиков"
    }
}

private fun SubmittedCatEntity.toRecord() = SubmittedCatRecord(
    imageId = imageId,
    imageUrl = imageUrl,
    breedId = breedId,
    breedName = breedName,
    createdAtEpochMillis = createdAtEpochMillis,
)
