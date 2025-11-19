package com.example.diaryapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_table")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val text: String ,
    val photoUri: String? = null
)
