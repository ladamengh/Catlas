package com.ladamengh.petproject.data.user

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.LocalProfileEntity
import com.ladamengh.petproject.data.local.SubmittedCatEntity
import com.ladamengh.petproject.domain.model.UserProfile
import com.ladamengh.petproject.domain.repository.ProfileRepository
import com.ladamengh.petproject.utils.createRandomHexId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) : ProfileRepository {
    private val profileDao = database.profileDao()
    private val submittedCatDao = database.submittedCatDao()

    override val profile: StateFlow<UserProfile> = profileDao.observeProfile()
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

    val submittedCats: StateFlow<List<SubmittedCatRecord>> = submittedCatDao.observeSubmittedCats()
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
            val oldProfile = requireNotNull(profileDao.getProfile())
            profileDao.saveProfile(
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
            submittedCatDao.saveSubmittedCat(
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
        scope.launch { submittedCatDao.removeSubmittedCat(imageId) }
    }

    private suspend fun ensureProfileExists() {
        profileDao.createProfileIfMissing(
            LocalProfileEntity(
                localId = createLocalId(),
                displayName = DEFAULT_DISPLAY_NAME,
            ),
        )
    }

    private fun createLocalId(): String = createRandomHexId()

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
