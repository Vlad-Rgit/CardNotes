package com.example.cardnotes.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cardnotes.domain.NoteDomain
import java.sql.Timestamp

@Entity(tableName = "notes")
data class NoteDatabase(
    @PrimaryKey(autoGenerate = true)
    var noteId: Int = 0,
    var name: String,
    var value: String,
    val createdAt: Long = System.currentTimeMillis()) {

    fun asDomain(): NoteDomain {
        return NoteDomain(
            noteId = this.noteId,
            name = this.name,
            value = this.value,
            createdAt = Timestamp(createdAt))
    }
}