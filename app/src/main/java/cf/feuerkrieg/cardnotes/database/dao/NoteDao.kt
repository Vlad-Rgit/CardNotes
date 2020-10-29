package cf.feuerkrieg.cardnotes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cf.feuerkrieg.cardnotes.database.models.NoteDatabase
import cf.feuerkrieg.cardnotes.database.models.NoteWithGroupDatabase

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

    @Query("Delete from notes where folderId = :groupId")
    suspend fun deleteByGroupId(groupId: Int)

    @Delete
    suspend fun deleteAll(notes: Iterable<NoteDatabase>)

    @Query("Select * from notes where folderId = :folderId")
    suspend fun getByFolder(folderId: Int?): List<NoteDatabase>

    @Query("Select * from notes where folderId is NULL")
    suspend fun getWithoutFolder(): List<NoteDatabase>

    @Query("Select * from notes")
    fun getAll(): LiveData<List<NoteDatabase>>

    @Query("Select * from notes order by position")
    suspend fun getAllPositionSorted(): List<NoteDatabase>

    @Query(
        """
        Select * from notes
            Where instr(title, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0"""
    )
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
            Where folderId = :groupId and
                  (instr(title, :searchQuery) > 0 or
                    instr(value, :searchQuery) > 0)"""
    )
    suspend fun getAllBySearchQueryByGroup(searchQuery: String, groupId: Int)
        : List<NoteWithGroupDatabase>

    @Query("Select * from notes where noteId = :noteId")
    suspend fun getById(noteId: Int): NoteDatabase

    @Query("Select Count(noteId) from notes")
    suspend fun count(): Int
}