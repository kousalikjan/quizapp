package no.hvl.dat153.quizapp.presentation.addentry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.domain.repositories.GalleryEntryRepository
import java.io.File

class AddEntryActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var selectedImageUri = MutableStateFlow<Uri?>(null)

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
            val name = editText.text.toString()

            if (name.isBlank()) {
                handleNameError()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (GalleryEntryRepository.getByName(name) != null) {
                    handleNameAlreadyExists()
                    return@launch
                }
                val inputStream = selectedImageUri.value?.let(contentResolver::openInputStream)
                if (inputStream == null) {
                    handleImageError()
                    return@launch
                }

                GalleryEntryRepository.insertEntry(
                    name = name,
                    inputStream = inputStream,
                    storageDir = File(filesDir, "gallery")
                )
                finish()
            }
        }

        setupObservingData()
    }

    private fun setupObservingData() {
        lifecycleScope.launch {
            selectedImageUri.collect { uri ->
                if (uri != null) {
                    imageView.setImageURI(uri)
                }
            }
        }
    }

    private fun handleNameError() {
        handleError(getString(R.string.please_enter_a_name))
    }

    private fun handleImageError() {
        handleError(getString(R.string.please_select_an_image))
    }

    private fun handleNameAlreadyExists() {
        handleError(getString(R.string.name_already_exists))
    }

    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads the selected image into the imageView preview
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri.value = data.data
        }
    }
}
