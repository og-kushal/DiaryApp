package com.example.diaryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
/**
* DateFragment
*
* This fragment represents the first screen of the Diary App.
* Its purpose is to allow the user to select a diary entry date
* using a DatePicker widget. Once selected, the date is stored
* inside a shared ViewModel so that it can be accessed by the
* next fragment (EntryFragment).
*
* The fragment also includes a "Next" button, which navigates
* the user to the second screen (EntryFragment) using ViewPager2.
*
* Key Responsibilities:
*  Display a DatePicker for selecting entry date
*  Store the selected date inside SharedViewModel
*   Navigate to the next fragment on button press
*/
class DateFragment : Fragment() {

    // Shared ViewModel for communication between fragments
    private lateinit var viewModel: SharedViewModel
    /**
     * Called when Android needs to create the fragment's UI.
     * Inflates the XML layout and sets up all UI interactions.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_date, container, false)

        // Shared ViewModel across fragments
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        val nextButton = view.findViewById<Button>(R.id.nextButton)

        // Set initial date in ViewModel
        val initialDay = datePicker.dayOfMonth
        val initialMonth = datePicker.month + 1
        val initialYear = datePicker.year
        viewModel.selectedDate.value = "$initialDay/$initialMonth/$initialYear"

        // Update date when user changes it
        datePicker.setOnDateChangedListener { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            viewModel.selectedDate.value = selectedDate
        }

        // Move to fragment 2 when clicking "Next"
        nextButton.setOnClickListener {
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            viewPager.currentItem = 1   // Go to Fragment 2
        }


        return view
    }
}
