package no.hvl.dat153.quizapp.repositories

import android.content.Context
import android.graphics.BitmapFactory
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.models.GalleryEntry

object GalleryEntryRepository {
    val entries = mutableListOf<GalleryEntry>()

    fun initialize(context: Context)
    {
        if (entries.isEmpty()) {
            entries.add(GalleryEntry(
                GalleryEntry.Name("Ulriken"),
                BitmapFactory.decodeResource(context.resources, R.drawable.ulriken))
            )
            entries.add(GalleryEntry(
                GalleryEntry.Name("Lovstakken"),
                BitmapFactory.decodeResource(context.resources, R.drawable.lovstakken))
            )
            entries.add(GalleryEntry(
                GalleryEntry.Name("Lyderhorn"),
                BitmapFactory.decodeResource(context.resources, R.drawable.lyderhorn))
            )
        }
    }
}
