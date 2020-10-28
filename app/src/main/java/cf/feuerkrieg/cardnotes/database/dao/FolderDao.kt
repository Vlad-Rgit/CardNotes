package cf.feuerkrieg.cardnotes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cf.feuerkrieg.cardnotes.database.models.FolderDatabase
import cf.feuerkrieg.cardnotes.database.models.FolderWithCount
import cf.feuerkrieg.cardnotes.database.models.GroupWithNotesDatabase

@Dao
interface FolderDao {

    @Insert
    suspend fun insert(folder: FolderDatabase): Long

    @Insert
    suspend fun insertAll(groups: Iterable<FolderDatabase>)

    @Update
    suspend fun update(folder: FolderDatabase)

    @Update
    suspend fun updateAll(folders: Iterable<FolderDatabase>)

    @Delete
    suspend fun delete(folder: FolderDatabase)

    @Delete
    suspend fun deleteAll(groups: Iterable<FolderDatabase>)

    @Query("Select * from `folder` where folderId = :groupId")
    suspend fun getById(groupId: Int): FolderDatabase

    @Query("Select * from `folder`")
    fun getAll(): LiveData<List<FolderDatabase>>

    @Transaction
    @Query(
        """Select *,
            (Select Count(notes.noteId) from notes where notes.folderId = folder.folderId) as notesCount
            from folder"""
    )
    fun getAllWithNotesCount(): LiveData<List<FolderWithCount>>

    @Transaction
    @Query("Select * from `folder` where folderId = :groupId")
    suspend fun getGroupWithNotes(groupId: Int)
            : List<GroupWithNotesDatabase>


    @Query("Select Count(folderId) from `folder`")
    suspend fun count(): Int

    @Query("Select * from `folder` where folderName = :groupName")
    suspend fun getByName(groupName: String): List<FolderDatabase>
}