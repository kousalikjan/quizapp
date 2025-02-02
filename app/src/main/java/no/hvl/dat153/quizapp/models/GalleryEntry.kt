package no.hvl.dat153.quizapp.models

import android.graphics.Bitmap

data class GalleryEntry(
    val name: Name,
    val image: Bitmap
) {

    @JvmInline
    value class Name(val value: String)
}
