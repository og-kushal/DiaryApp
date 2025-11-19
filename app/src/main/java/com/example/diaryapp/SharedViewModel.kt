package com.example.diaryapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val selectedDate = MutableLiveData<String>()
    val diaryText = MutableLiveData<String>()
}
