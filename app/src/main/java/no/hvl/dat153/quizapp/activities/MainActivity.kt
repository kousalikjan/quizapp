package no.hvl.dat153.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.repositories.GalleryEntryRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        GalleryEntryRepository.initialize(this)

        val buttonGallery = findViewById<Button>(R.id.buttonGallery)
        val buttonQuiz = findViewById<Button>(R.id.buttonQuiz)

        buttonGallery.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
        buttonQuiz.setOnClickListener {
            if (GalleryEntryRepository.entries.size < MIN_NUMBER_ENTRIES) {
                handleNotEnoughEntries()
                return@setOnClickListener
            }
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

    private fun handleNotEnoughEntries() {
        val message = getString(R.string.not_enough_entries)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val MIN_NUMBER_ENTRIES = 3
    }
}
