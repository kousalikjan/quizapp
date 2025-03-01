package no.hvl.dat153.quizapp.presentation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import no.hvl.dat153.quizapp.data.room.AppDatabase
import no.hvl.dat153.quizapp.domain.models.GalleryEntry

abstract class ActivityTestWithInMemoryDatabase {

    protected fun initializeDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        AppDatabase.initializeInMemory(context)

        val dao = AppDatabase.db.galleryEntryDao()

        runBlocking {
            dao.deleteAll()
            dao.insert(GalleryEntry(name = "Option 1", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 2", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 3", uriString = "file://someuri"))
        }
    }

    protected abstract fun launchActivityAndSetupVariables()
}
