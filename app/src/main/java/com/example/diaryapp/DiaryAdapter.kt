package com.example.diaryapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
/**
 * DiaryAdapter
 * A RecyclerView adapter responsible for displaying a list of diary entries.
 *
 * This adapter renders:
 *   The entry date , entry text
 *   A photo thumbnail  when user attach photo
 *  A delete button for removing the entry
 * It supports two callback functions:
 *   onDeleteClick(): called when the user requests to delete an entry
 *  onViewClick(): called when the user taps an entry to view its details
 */
class DiaryAdapter(
    private var entries: List<DiaryEntry>,
    private val onDeleteClick: (DiaryEntry) -> Unit,
    private val onViewClick: (DiaryEntry) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    /**
     * ViewHolder class representing a single item (card) on the list.
     * Holds references to UI components for fast RecylerView binding.
     */
    class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.itemDate)
        val text: TextView = itemView.findViewById(R.id.itemText)
        val photo: ImageView = itemView.findViewById(R.id.itemPhoto)
        val deleteButton: View = itemView.findViewById(R.id.deleteButton)
    }

    /**
     * Called when RecyclerView needs to create a new ViewHolder.
     * Inflates the custom layout used for individual diary entry cards.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }
    /**
     * Binds the data from a DiaryEntry object to the ViewHolder UI elements.
     * This includes: Date text ,Diary text, Showing or hiding the photo preview and Setting click/long-click listeners for view & delete actions
     */
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val entry = entries[position]

        holder.date.text = entry.date
        holder.text.text = entry.text

        // Show image if exists
        if (entry.photoUri != null) {
            holder.photo.visibility = View.VISIBLE
            holder.photo.setImageURI(Uri.parse(entry.photoUri))
        } else {
            holder.photo.visibility = View.GONE
        }

        // View entry
        holder.itemView.setOnClickListener {
            onViewClick(entry)
        }

        // DELETE BUTTON  ALWAYS reset visibility
        holder.deleteButton.visibility = View.VISIBLE
        holder.deleteButton.setOnClickListener {
            onDeleteClick(entry)
        }

        // Optional: long click also deletes
        holder.itemView.setOnLongClickListener {
            onDeleteClick(entry)
            true
        }
    }


    // List size
        override fun getItemCount(): Int = entries.size
  //  Updates the list of entries and refreshes the UI.
    //Called when data changes
        fun updateData(newEntries: List<DiaryEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}
