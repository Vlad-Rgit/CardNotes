package com.example.cardnotes.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.cardnotes.domain.NoteDomain
import java.sql.Timestamp

@Entity(tableName = "notes")
data class NoteDatabase(

    @PrimaryKey(autoGenerate = true)
    var noteId: Int = 0,

    @ForeignKey(
        entity = GroupDatabase::class,
        parentColumns = ["groupId"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)
    var groupId: Int? = null,
    var position: Int = noteId,
    var title: String,
    var value: String,
    val createdAt: Long = System.currentTimeMillis()) {

    fun asDomain(): NoteDomain {
        return NoteDomain(
            noteId = this.noteId,
            groupId = this.groupId,
            position = this.position,
            name = this.title,
            value = this.value,
            createdAt = Timestamp(createdAt))
    }
}