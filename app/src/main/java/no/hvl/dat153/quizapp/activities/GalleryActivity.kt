package no.hvl.dat153.quizapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.adapters.GalleryAdapter
import no.hvl.dat153.quizapp.repositories.GalleryEntryRepository

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        GalleryEntryRepository.initialize(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GalleryAdapter()
    }
}