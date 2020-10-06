package com.example.cardnotes.database.dao

import androidx.room.*
import com.example.cardnotes.database.models.GroupDatabase
import com.example.cardnotes.database.models.GroupWithNotesDatabase

@Dao
interface GroupDao {

    @Insert
    suspend fun insert(group: GroupDatabase): Long

    @Insert
    suspend fun insertAll(groups: Iterable<GroupDatabase>)

    @Update
    suspend fun update(group: GroupDatabase)

    @Delete
    suspend fun delete(group: GroupDatabase)

    @Query("Select * from `group` where groupId = :groupId")
    suspend fun getById(groupId: Int): GroupDatabase

    @Query("Select * from `group`")
    suspend fun getAll(): List<GroupDatabase>

    @Transaction
    @Query("Select * from `group` where groupId = :groupId")
    suspend fun getGroupWithNotes(groupId: Int)
            : List<GroupWithNotesDatabase>


    @Query("Select Count(groupId) from `group`")
    suspend fun count(): Int

    @Query("Select * from `group` where groupName = :groupName")
    suspend fun getByName(groupName: String): List<GroupDatabase>
}