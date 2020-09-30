package com.example.cardnotes.domain


import androidx.lifecycle.MutableLiveData
import com.example.cardnotes.database.models.NoteDatabase
import com.example.cardnotes.interfaces.SortedItem
import java.lang.IllegalArgumentException
import java.sql.Timestamp

data class NoteDomain(
    var noteId: Int = 0,
    var groupId: Int? = null,
    var position: Int = noteId,
    var name:  MutableLiveData<String> = MutableLiveData(""),
    var value: MutableLiveData<String> = MutableLiveData(""),
    var groupName: String? = null,
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    val isSelectionEnabled: MutableLiveData<Boolean> = MutableLiveData(false),
    val isSelected: MutableLiveData<Boolean> = MutableLiveData(false)
): SortedItem<NoteDomain> {

    fun asDatabase(): NoteDatabase {
        return NoteDatabase(
            noteId = this.noteId,
            title = this.name.value!!,
            position = position,
            value = this.value.value!!,
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

    override fun equals(other: Any?): Boolean {

        val otherNote = other as? NoteDomain
            ?: throw IllegalArgumentException()

        return this.noteId == otherNote.noteId
    }

}