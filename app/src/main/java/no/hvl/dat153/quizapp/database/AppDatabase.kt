package no.hvl.dat153.quizapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.hvl.dat153.quizapp.database.dao.GalleryEntryDao
import no.hvl.dat153.quizapp.models.GalleryEntry

@Database(entities = [GalleryEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galleryEntryDao(): GalleryEntryDao

    companion object {

        lateinit var db: AppDatabase

        fun initialize(context: Context) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }
}
