package no.hvl.dat153.quizapp.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GalleryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val uriString: String
) {

    fun getUri(): Uri = Uri.parse(uriString)
}
