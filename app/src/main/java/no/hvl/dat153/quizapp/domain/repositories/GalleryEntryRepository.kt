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

    private val dao by lazy { AppDatabase.db.galleryEntryDao() }
    private val imageLocalDataSource = ImageLocalDataSource()

    fun initialize(context: Context) {
        AppDatabase.initialize(context)
    }

    suspend fun initializeData(context: Context) {
        val existingEntries = getAll()

        getResourceEntries(context).filter { entry ->
            entry.uriString !in existingEntries.map { it.uriString }
        }.forEach { dao.insert(it) }
    }

    suspend fun getAll(): List<GalleryEntry> {
        return dao.getAll()
    }

    fun getAllStream() = dao.getAllStream()

    suspend fun getCount(): Int = withContext(Dispatchers.IO) { dao.getCount() }

    suspend fun getByName(name: String): GalleryEntry? = withContext(Dispatchers.IO) { dao.getByName(name) }

    suspend fun insertEntry(name: String, inputStream: InputStream, storageDir: File) {
        withContext(Dispatchers.IO) {
            val imageUri = imageLocalDataSource.saveImage(inputStream, storageDir)
            val entry = GalleryEntry(name = name, uriString = imageUri.toString())
            dao.insert(entry)
        }
    }

    suspend fun deleteEntry(entry: GalleryEntry) {
        withContext(Dispatchers.IO) {
            dao.delete(entry)
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
