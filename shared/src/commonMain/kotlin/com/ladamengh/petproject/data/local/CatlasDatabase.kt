package com.ladamengh.petproject.data.local

import androidx.room.ConstructedBy
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Entity
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_cats")
data class FavoriteCatEntity(
    @PrimaryKey val catId: Int,
    val name: String,
    val breed: String,
    val location: String,
    val description: String,
    val imageUrl: String? = null,
)

@Entity(tableName = "favorite_breeds")
data class FavoriteBreedEntity(
    @PrimaryKey val breed: String,
)

@Entity(tableName = "submitted_cats")
data class SubmittedCatEntity(
    @PrimaryKey val imageId: String,
    val imageUrl: String,
    val breedId: String?,
    val breedName: String?,
    val createdAtEpochMillis: Long,
)

@Entity(tableName = "local_profile")
data class LocalProfileEntity(
    @PrimaryKey val key: Int = PROFILE_KEY,
    val localId: String,
    val displayName: String,
    val description: String? = null,
    val city: String? = null,
    val avatarImage: ByteArray? = null,
) {
    companion object {
        const val PROFILE_KEY = 1
    }
}

@Dao
interface CatlasDao {
    @Query("SELECT * FROM favorite_cats ORDER BY catId")
    fun observeFavoriteCats(): Flow<List<FavoriteCatEntity>>

    @Query("SELECT * FROM favorite_cats WHERE catId = :catId LIMIT 1")
    suspend fun getFavoriteCat(catId: Int): FavoriteCatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavoriteCat(cat: FavoriteCatEntity)

    @Query("DELETE FROM favorite_cats WHERE catId = :catId")
    suspend fun removeFavoriteCat(catId: Int)

    @Query("SELECT breed FROM favorite_breeds ORDER BY breed")
    fun observeFavoriteBreeds(): Flow<List<String>>

    @Query("SELECT breed FROM favorite_breeds WHERE breed = :breed LIMIT 1")
    suspend fun getFavoriteBreed(breed: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavoriteBreed(breed: FavoriteBreedEntity)

    @Query("DELETE FROM favorite_breeds WHERE breed = :breed")
    suspend fun removeFavoriteBreed(breed: String)

    @Query("SELECT * FROM local_profile WHERE key = :key LIMIT 1")
    fun observeProfile(key: Int = LocalProfileEntity.PROFILE_KEY): Flow<LocalProfileEntity?>

    @Query("SELECT * FROM local_profile WHERE key = :key LIMIT 1")
    suspend fun getProfile(key: Int = LocalProfileEntity.PROFILE_KEY): LocalProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: LocalProfileEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createProfileIfMissing(profile: LocalProfileEntity)

    @Query("SELECT * FROM submitted_cats ORDER BY createdAtEpochMillis DESC")
    fun observeSubmittedCats(): Flow<List<SubmittedCatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSubmittedCat(cat: SubmittedCatEntity)

    @Query("DELETE FROM submitted_cats WHERE imageId = :imageId")
    suspend fun removeSubmittedCat(imageId: String)

    @Query("SELECT * FROM cats ORDER BY id DESC")
    fun observeCats(): Flow<List<CatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCats(cats: List<CatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCat(cat: CatEntity)
}

@Entity(tableName = "cats")
data class CatEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val breed: String,
    val location: String,
    val description: String,
    val imageUrl: String,
)

@Database(
    entities = [
        FavoriteCatEntity::class,
        FavoriteBreedEntity::class,
        SubmittedCatEntity::class,
        LocalProfileEntity::class,
        CatEntity::class,
    ],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ],
    exportSchema = true,
)
@ConstructedBy(CatlasDatabaseConstructor::class)
abstract class CatlasDatabase : RoomDatabase() {
    abstract fun dao(): CatlasDao
}

@Suppress("KotlinNoActualForExpect")
expect object CatlasDatabaseConstructor : RoomDatabaseConstructor<CatlasDatabase> {
    override fun initialize(): CatlasDatabase
}
