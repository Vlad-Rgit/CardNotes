package com.example.cardnotes.database.dao

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cardnotes.database.models.NoteDatabase

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(noteDatabase: NoteDatabase)

    @Insert
    suspend fun insertAll(notes: Iterable<NoteDatabase>)

    @Query("Select * from notes")
    fun getAll(): LiveData<List<NoteDatabase>>

    @Query("Select * from notes where noteId = :noteId")
    suspend fun getById(noteId: Int) : NoteDatabase

    @Query("Select Count(noteId) from notes")
    suspend fun count(): Int
}