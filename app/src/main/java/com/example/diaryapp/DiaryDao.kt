package com.example.diaryapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
/**
 * Data Access Object for the diary entries.
 * Defines all database operations used in the app.
 */
@Dao
interface DiaryDao {
/**insert new diary entry*/
    @Insert
    suspend fun insert(entry: DiaryEntry)
    /** Update an existing diary entry */
    @Update
    suspend fun update(entry: DiaryEntry)
    /** Delete a diary entry */
    @Delete
    suspend fun delete(entry: DiaryEntry)

    /** Retrieve all entries, newest first */
    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    suspend fun getAllEntries(): List<DiaryEntry>
    /** Search entries by matching text content */
    @Query("SELECT * FROM diary_table WHERE text LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchEntries(query: String): List<DiaryEntry>
    /** Filter entries by exact date */
    @Query("SELECT * FROM diary_table WHERE date = :selectedDate ORDER BY id DESC")
    suspend fun filterByDate(selectedDate: String): List<DiaryEntry>
}
