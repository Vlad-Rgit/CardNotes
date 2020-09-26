package com.example.cardnotes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cardnotes.database.models.Note

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note)

    @Query("Select * from Note")
    fun getAll(): LiveData<Note>

}