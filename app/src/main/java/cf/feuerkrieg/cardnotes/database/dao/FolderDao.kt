package cf.feuerkrieg.cardnotes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cf.feuerkrieg.cardnotes.database.models.FolderDatabase
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
    fun getAllWithNotesCount(): LiveData<List<FolderDatabase>>


    @Query("Select * from folder where parentFolderId = :folderId")
    suspend fun getChildren(folderId: Int): List<FolderDatabase>

    @Query("Select * from folder where instr(folderName, :query) > 0")
    suspend fun getByQuery(query: String): List<FolderDatabase>

    @Transaction
    @Query("Select * from `folder` where folderId = :groupId")
    suspend fun getGroupWithNotes(groupId: Int)
            : List<GroupWithNotesDatabase>

    @Query("Select Count(folderId) from `folder`")
    suspend fun count(): Int

    @Query("Select Count(noteId) from notes where folderId = :folderId")
    suspend fun notesCount(folderId: Int): Int

    @Query("""Select * from `folder` where folderName = :groupName""")
    suspend fun getByName(groupName: String): List<FolderDatabase>

    @Query("""Select * from `folder` where parentFolderId is NULL""")
    suspend fun getWithoutParentFolder(): List<FolderDatabase>

    @Query("""Select * from `folder` where parentFolderId = :parentFolderId""")
    suspend fun getByParentFolderId(parentFolderId: Int): List<FolderDatabase>
}