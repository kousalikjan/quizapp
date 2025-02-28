package no.hvl.dat153.quizapp.presentation.addentry

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.domain.repositories.GalleryEntryRepository
import java.io.File

class AddEntryFragment : Fragment(R.layout.fragment_add_entry) {

    private lateinit var imageView: ImageView
    private lateinit var editText: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonSave: Button

    private lateinit var viewModel: AddEntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AddEntryViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.addEntryImageView)
        editText = view.findViewById(R.id.addEntryEditText)
        buttonAdd = view.findViewById(R.id.addEntryButtonAdd)
        buttonSave = view.findViewById(R.id.addEntryButtonSave)

        setupOnClickListeners()
        setupObservingData()
        restoreState()
    }

    private fun setupOnClickListeners() {
        buttonAdd.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
        }

        buttonSave.setOnClickListener {
            val activity = activity ?: return@setOnClickListener
            val name = viewModel.name.value

            if (name.isBlank()) {
                handleNameError()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (GalleryEntryRepository.getByName(name) != null) {
                    handleNameAlreadyExists()
                    return@launch
                }
                val inputStream = viewModel.selectedImageUri.value?.let(activity.contentResolver::openInputStream)
                if (inputStream == null) {
                    handleImageError()
                    return@launch
                }

                GalleryEntryRepository.insertEntry(
                    name = name,
                    inputStream = inputStream,
                    storageDir = File(activity.filesDir, "gallery")
                )
                activity.finish()
            }
        }
    }

    private fun setupObservingData() {
        viewModel.selectedImageUri
            .flowWithLifecycle(lifecycle)
            .onEach(imageView::setImageURI)
            .launchIn(lifecycleScope)

        editText.addTextChangedListener {
            viewModel.setName(it.toString())
        }
    }

    private fun restoreState() {
        editText.setText(viewModel.name.value)
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads the selected image into the imageView preview
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            viewModel.setImageUri(data.data)
        }
    }
}
