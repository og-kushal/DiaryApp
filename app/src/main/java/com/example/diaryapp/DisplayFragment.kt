package com.example.diaryapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import android.widget.ImageView
import android.widget.TextView
import android.net.Uri

class DisplayFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiaryAdapter
    private lateinit var db: DiaryDatabase

    private lateinit var searchBox: EditText
    private lateinit var filterButton: Button
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_display, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = true

        // IDs
        searchBox = view.findViewById(R.id.searchBar)
        filterButton = view.findViewById(R.id.filterByDateButton)

        db = DiaryDatabase.getDatabase(requireContext())
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Load entries THEN attach listeners
        loadEntries {
            setupSearch()
            setupFilter()
        }

        return view
    }

    // ---------------------------
    // LOAD ALL ENTRIES
    // ---------------------------
    private fun loadEntries(onLoaded: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val entries = db.diaryDao().getAllEntries()

            CoroutineScope(Dispatchers.Main).launch {
                adapter = DiaryAdapter(
                    entries,
                    onDeleteClick = { entry -> deleteEntry(entry) },
                    onViewClick = { entry -> viewEntry(entry) }
                )

                recyclerView.adapter = adapter
                recyclerView.isNestedScrollingEnabled = true

                // ⭐ CRITICAL FIX: Allow RecyclerView to scroll inside ViewPager2
                recyclerView.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        recyclerView.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    false
                }

                onLoaded()
            }
        }
    }

    // ---------------------------
    // SEARCH
    // ---------------------------
    private fun setupSearch() {
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchEntries(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchEntries(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val results = db.diaryDao().searchEntries("%$query%")
            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(results)
            }
        }
    }

    // ---------------------------
    // FILTER BY DATE
    // ---------------------------
    private fun setupFilter() {
        filterButton.setOnClickListener {
            showFilterDatePicker()
        }
    }

    private fun showFilterDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                filterByDate("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year,
            month,
            day
        )

        dialog.show()
    }

    private fun filterByDate(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val results = db.diaryDao().filterByDate(date)
            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(results)

                if (results.isEmpty()) {
                    Toast.makeText(requireContext(), "No entries for $date", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ---------------------------
    // DELETE ENTRY
    // ---------------------------
    private fun deleteEntry(entry: DiaryEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            db.diaryDao().delete(entry)
            val updated = db.diaryDao().getAllEntries()

            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(updated)
            }
        }
    }

    // ---------------------------
    // VIEW ENTRY POPUP (WITH PHOTO)
    // ---------------------------
    private fun viewEntry(entry: DiaryEntry) {

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_view_entry, null)

        val imageView = dialogView.findViewById<ImageView>(R.id.viewEntryImage)
        val dateView = dialogView.findViewById<TextView>(R.id.viewEntryDate)
        val textView = dialogView.findViewById<TextView>(R.id.viewEntryText)

        dateView.text = "Date: ${entry.date}"
        textView.text = entry.text

        if (!entry.photoUri.isNullOrEmpty()) {
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(Uri.parse(entry.photoUri))
        } else {
            imageView.visibility = View.GONE
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Diary Entry")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .setNegativeButton("Edit") { _, _ ->
                editEntry(entry)
            }
            .create()
            .show()
    }

    // ---------------------------
    // EDIT ENTRY
    // ---------------------------
    private fun editEntry(entry: DiaryEntry) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_entry, null)

        val editText = dialogView.findViewById<EditText>(R.id.editEntryText)
        editText.setText(entry.text)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Entry")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    db.diaryDao().update(
                        DiaryEntry(
                            id = entry.id,
                            date = entry.date,
                            text = newText,
                            photoUri = entry.photoUri
                        )
                    )

                    val updatedList = db.diaryDao().getAllEntries()

                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.updateData(updatedList)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}
