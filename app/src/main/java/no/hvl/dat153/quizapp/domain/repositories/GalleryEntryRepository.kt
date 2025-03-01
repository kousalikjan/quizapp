package no.hvl.dat153.quizapp.domain.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.data.filesystem.ImageLocalDataSource
import no.hvl.dat153.quizapp.data.room.AppDatabase
import no.hvl.dat153.quizapp.domain.models.GalleryEntry
import java.io.File
import java.io.InputStream

object GalleryEntryRepository {

    private val imageLocalDataSource = ImageLocalDataSource()

    fun initialize(context: Context) {
        AppDatabase.initialize(context)
    }

    suspend fun initializeData(context: Context) {
        val existingEntries = getAll()

        getResourceEntries(context).filter { entry ->
            entry.uriString !in existingEntries.map { it.uriString }
        }.forEach { AppDatabase.db.galleryEntryDao().insert(it) }
    }

    suspend fun getAll(): List<GalleryEntry> {
        return AppDatabase.db.galleryEntryDao().getAll()
    }

    fun getAllStream() = AppDatabase.db.galleryEntryDao().getAllStream()

    suspend fun getCount(): Int = withContext(Dispatchers.IO) { AppDatabase.db.galleryEntryDao().getCount() }

    suspend fun getByName(name: String): GalleryEntry? = withContext(Dispatchers.IO) { AppDatabase.db.galleryEntryDao().getByName(name) }

    suspend fun insertEntry(name: String, inputStream: InputStream, storageDir: File) {
        withContext(Dispatchers.IO) {
            val imageUri = imageLocalDataSource.saveImage(inputStream, storageDir)
            val entry = GalleryEntry(name = name, uriString = imageUri.toString())
            AppDatabase.db.galleryEntryDao().insert(entry)
        }
    }

    suspend fun deleteEntry(entry: GalleryEntry) {
        withContext(Dispatchers.IO) {
            AppDatabase.db.galleryEntryDao().delete(entry)
            imageLocalDataSource.deleteImage(entry.getUri())
        }
    }

    private fun getResourceEntries(context: Context): List<GalleryEntry> {
        val packageName = context.packageName
        return buildList {
            add(
                GalleryEntry(
                    name = "Ulriken",
                    uriString = "android.resource://$packageName/${R.drawable.ulriken}"
                )
            )
            add(
                GalleryEntry(
                    name = "Lovstakken",
                    uriString = "android.resource://$packageName/${R.drawable.lovstakken}"
                )
            )
            add(
                GalleryEntry(
                    name = "Lyderhorn",
                    uriString = "android.resource://$packageName/${R.drawable.lyderhorn}"
                )
            )
        }
    }
}
