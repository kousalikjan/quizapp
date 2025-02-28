package no.hvl.dat153.quizapp.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import no.hvl.dat153.quizapp.domain.models.GalleryEntry

@Dao
interface GalleryEntryDao {

    @Query("SELECT * FROM GalleryEntry")
    suspend fun getAll(): List<GalleryEntry>

    @Query("SELECT * FROM GalleryEntry")
    fun getAllStream(): Flow<List<GalleryEntry>>

    @Query("SELECT COUNT(*) FROM GalleryEntry")
    suspend fun getCount(): Int

    @Query("SELECT * FROM GalleryEntry WHERE name = :name")
    suspend fun getByName(name: String): GalleryEntry?

    @Insert
    suspend fun insert(galleryEntry: GalleryEntry)

    @Delete
    suspend fun delete(galleryEntry: GalleryEntry)

    suspend fun deleteAll() {
        getAll().forEach { delete(it) }
    }

}
