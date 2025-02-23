package no.hvl.dat153.quizapp.presentation.gallery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.domain.repositories.GalleryEntryRepository
import no.hvl.dat153.quizapp.presentation.addentry.AddEntryActivity

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GalleryAdapter

    private val sortStream = MutableStateFlow(SortType.ASC)
    private val entriesStream = GalleryEntryRepository.getAllStream()
    private val sortedEntriesStream = combine(
        entriesStream,
        sortStream
    ) { entries, sort ->
        when (sort) {
            SortType.ASC -> entries.sortedBy { it.name }
            SortType.DESC -> entries.sortedByDescending { it.name }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GalleryAdapter(
            entries = emptyList(),
            onEntryDelete = { entry ->
                lifecycleScope.launch {
                    GalleryEntryRepository.deleteEntry(entry)
                }
            }
        )
        recyclerView.adapter = adapter

        val buttonAddEntry: Button = findViewById(R.id.buttonAddEntry)
        val buttonSortAsc: Button = findViewById(R.id.buttonSortAsc)
        val buttonSortDesc: Button = findViewById(R.id.buttonSortDesc)

        buttonAddEntry.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }

        buttonSortAsc.setOnClickListener { sortStream.value = SortType.ASC }
        buttonSortDesc.setOnClickListener { sortStream.value = SortType.DESC }

        lifecycleScope.launch {
            sortedEntriesStream.collect { entries ->
                adapter.setData(entries)
            }
        }
    }

    enum class SortType {
        ASC, DESC
    }
}
