package no.hvl.dat153.quizapp.presentation.addentry

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddEntryViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    fun setImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
}
