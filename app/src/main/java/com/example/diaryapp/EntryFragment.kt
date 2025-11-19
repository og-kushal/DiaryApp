package com.example.diaryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
class EntryFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    private var selectedPhotoUri: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {

                // Persist permission so the photo can be loaded later
                val contentResolver = requireContext().contentResolver
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

                // Save the URI in your database
                selectedPhotoUri = uri.toString()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_entry, container, false)

        val selectedDateText = view.findViewById<TextView>(R.id.selectedDateText)
        val entryEditText = view.findViewById<EditText>(R.id.entryEditText)
        val clearButton = view.findViewById<Button>(R.id.clearButton)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val addPhotoButton = view.findViewById<Button>(R.id.addPhotoButton)

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            selectedDateText.text = "Date: $date"
        }

        clearButton.setOnClickListener {
            entryEditText.setText("")
            selectedPhotoUri = null
        }

        addPhotoButton.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }


        saveButton.setOnClickListener {
            val text = entryEditText.text.toString()
            val date = selectedDateText.text.toString()
                .removePrefix("Date: ").trim()

            if (text.isNotEmpty() && date.isNotEmpty() && date != "(not selected)") {

                val db = DiaryDatabase.getDatabase(requireContext())
                val dao = db.diaryDao()

                lifecycleScope.launch {
                    dao.insert(
                        DiaryEntry(
                            date = date,
                            text = text,
                            photoUri = selectedPhotoUri       // ⭐ photo saved!
                        )
                    )

                    entryEditText.setText("")
                    selectedPhotoUri = null
                }
            }
        }

        return view
    }
}
