package com.example.cardnotes.database.dao

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cardnotes.database.models.NoteDatabase

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(noteDatabase: NoteDatabase)

    @Insert
    suspend fun insertAll(notes: Iterable<NoteDatabase>)

    @Update
    suspend fun update(noteDatabase: NoteDatabase)

    @Delete
    suspend fun delete(noteDatabase: NoteDatabase)

    @Delete
    suspend fun deleteAll(notes: Iterable<NoteDatabase>)

    @Query("Select * from notes")
    fun getAll(): LiveData<List<NoteDatabase>>

    @Query("Select * from notes order by position")
    fun getAllPositionSorted(): LiveData<List<NoteDatabase>>

    @Query("""
        Select * from notes
            Where instr(name, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0
            Order by position
    """)
    fun getAllBySearchQueryPositionedSorted(searchQuery: String)
            : LiveData<List<NoteDatabase>>

    @Query("Select * from notes where noteId = :noteId")
    suspend fun getById(noteId: Int) : NoteDatabase

    @Query("Select Count(noteId) from notes")
    suspend fun count(): Int
}