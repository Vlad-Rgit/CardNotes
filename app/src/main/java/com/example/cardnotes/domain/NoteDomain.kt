package com.example.cardnotes.domain


import androidx.lifecycle.MutableLiveData
import com.example.cardnotes.database.models.NoteDatabase
import com.example.cardnotes.interfaces.SortedItem
import java.sql.Timestamp

data class NoteDomain(

    var noteId: Int = 0,
    var name: String = "Title",
    var value: String = "Write here the note",
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    val isSelectionEnabled: MutableLiveData<Boolean> = MutableLiveData(false),
    val isSelected: MutableLiveData<Boolean> = MutableLiveData(false)

): SortedItem<NoteDomain> {

    fun asDatabase(): NoteDatabase {
        return NoteDatabase(
            noteId = this.noteId,
            name = this.name,
            value = this.value,
            createdAt = this.createdAt.time)
    }

    override fun areContentsTheSame(other: NoteDomain): Boolean {
        return this == other
    }

    override fun areItemsTheSame(other: NoteDomain): Boolean {
        return this.noteId == other.noteId
    }

    override fun compareTo(other: NoteDomain): Int {
        return noteId.compareTo(other.noteId)
    }

}