package no.hvl.dat153.quizapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.repositories.GalleryEntryRepository

class GalleryAdapter: RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = GalleryEntryRepository.entries[position]
        holder.image.setImageBitmap(entry.image)
        holder.name.text = entry.name.value

        holder.buttonDelete.setOnClickListener {
            GalleryEntryRepository.entries.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun getItemCount() = GalleryEntryRepository.entries.size
}
