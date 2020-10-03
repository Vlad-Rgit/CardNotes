package com.example.cardnotes.database.dao

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cardnotes.database.models.NoteDatabase
import com.example.cardnotes.database.models.NoteWithGroupDatabase

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(noteDatabase: NoteDatabase)

    @Insert
    suspend fun insertAll(notes: Iterable<NoteDatabase>)

    @Update
    suspend fun update(noteDatabase: NoteDatabase)

    @Update
    suspend fun updateAll(notes: List<NoteDatabase>)

    @Delete
    suspend fun delete(noteDatabase: NoteDatabase)

    @Delete
    suspend fun deleteAll(notes: Iterable<NoteDatabase>)

    @Query("Select * from notes")
    fun getAll(): LiveData<List<NoteDatabase>>

    @Query("Select * from notes order by position")
    suspend fun getAllPositionSorted(): List<NoteDatabase>

    @Query(
        """
        Select * from notes
            Where instr(title, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0""")
    suspend fun getAllBySearchQuery(searchQuery: String)
            : List<NoteDatabase>

    @Transaction
    @Query(
        """
        Select * from notes
            Where instr(title, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0""")
    suspend fun getAllBySearchQueryWithGroup(searchQuery: String)
        : List<NoteWithGroupDatabase>

    @Transaction
    @Query(
        """
        Select * from notes
            Where groupId = :groupId and
                  (instr(title, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0)""")
    suspend fun getAllBySearchQueryByGroup(searchQuery: String, groupId: Int)
        : List<NoteWithGroupDatabase>

    @Query("Select * from notes where noteId = :noteId")
    suspend fun getById(noteId: Int): NoteDatabase

    @Query("Select Count(noteId) from notes")
    suspend fun count(): Int
}