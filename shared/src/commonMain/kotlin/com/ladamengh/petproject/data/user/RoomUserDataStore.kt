package com.ladamengh.petproject.data.user

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.LocalProfileEntity
import com.ladamengh.petproject.data.local.SubmittedCatEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class LocalProfile(
    val id: String,
    val displayName: String,
)

/** TheCatAPI image reference saved after the user submits a cat. */
data class SubmittedCatRecord(
    val imageId: String,
    val imageUrl: String,
    val breedId: String? = null,
    val breedName: String? = null,
    val createdAtEpochMillis: Long,
)

/** Local profile and submitted-cat history persisted in the shared Room database. */
class RoomUserDataStore(
    database: CatlasDatabase,
) {
    private val dao = database.dao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val profile: StateFlow<LocalProfile> = dao.observeProfile()
        .map { entity ->
            entity?.let { LocalProfile(it.localId, it.displayName) }
                ?: LocalProfile(id = "", displayName = DEFAULT_DISPLAY_NAME)
        }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            LocalProfile(id = "", displayName = DEFAULT_DISPLAY_NAME),
        )

    val submittedCats: StateFlow<List<SubmittedCatRecord>> = dao.observeSubmittedCats()
        .map { records -> records.map(SubmittedCatEntity::toRecord) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            ensureProfileExists()
        }
    }

    fun saveDisplayName(displayName: String) {
        val trimmedName = displayName.trim()
        if (trimmedName.isBlank()) return
        scope.launch {
            ensureProfileExists()
            val oldProfile = requireNotNull(dao.getProfile())
            dao.saveProfile(
                LocalProfileEntity(
                    localId = oldProfile.localId,
                    displayName = trimmedName,
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
