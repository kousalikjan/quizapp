package no.hvl.dat153.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.adapters.GalleryAdapter

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GalleryAdapter()

        val buttonAddEntry: Button = findViewById(R.id.buttonAddEntry)
        val buttonSortAsc: Button = findViewById(R.id.buttonSortAsc)
        val buttonSortDesc: Button = findViewById(R.id.buttonSortDesc)

        buttonAddEntry.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }

        buttonSortAsc.setOnClickListener{
            (recyclerView.adapter as? GalleryAdapter)?.sortItemsAscending()
        }

        buttonSortDesc.setOnClickListener {
            (recyclerView.adapter as? GalleryAdapter)?.sortItemsDescending()
        }
    }

    /**
     * Calling notifyDataSetChanged()
     * because activity is resumed after adding image (could call notifyItemInserted)
     * but also when user backs from adding entry after deleting some images (inconsistency error)
     */
    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }
}
