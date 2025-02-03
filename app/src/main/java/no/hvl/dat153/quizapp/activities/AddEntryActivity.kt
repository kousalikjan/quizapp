package no.hvl.dat153.quizapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.models.GalleryEntry
import no.hvl.dat153.quizapp.repositories.GalleryEntryRepository

class AddEntryActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        imageView = findViewById(R.id.addEntryImageView)
        val editText: EditText = findViewById(R.id.addEntryEditText)
        val buttonAdd: Button = findViewById(R.id.addEntryButtonAdd)
        val buttonSave: Button = findViewById(R.id.addEntryButtonSave)

        buttonAdd.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
        }

        buttonSave.setOnClickListener {
            val name = GalleryEntry.Name(editText.text.toString())

            if (name.value.isBlank()) {
                handleNameError()
                return@setOnClickListener
            }

            if (name in GalleryEntryRepository.entries.map { it.name }) {
                handleNameAlreadyExists()
                return@setOnClickListener
            }

            selectedImage?.let { image ->
                GalleryEntryRepository.entries.add(GalleryEntry(name, image))
                finish()
            } ?: run {
                handleImageError()
            }
        }
    }

    private fun handleNameError() {
        val message = getString(R.string.please_enter_a_name)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleImageError() {
        val message = getString(R.string.please_select_an_image)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleNameAlreadyExists() {
        val message = getString(R.string.name_already_exists)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads the selected image into the imageView preview
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                val inputStream = contentResolver.openInputStream(uri)
                selectedImage = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(selectedImage)
            }
        }
    }
}
