package com.example.cardnotes.domain


import com.example.cardnotes.NoteApp
import com.example.cardnotes.database.models.NoteDatabase
import com.example.cardnotes.interfaces.SortedItem
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class NoteDomain(
    var noteId: Int = 0,
    var groupId: Int? = null,
    var position: Int = noteId,
    var name: String = "",
    var value: String = "",
    var groupName: String? = null,
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()))
    : SortedItem<NoteDomain> {


    fun interface OnIsSelectedChangedListener {
        fun onIsSelectedChanged(note: NoteDomain)
    }

    var isSelected = false
        set(value) {
            field = value
            for(listener in onIsSelectedChangedListeners)
                listener.onIsSelectedChanged(this)
        }

    private val onIsSelectedChangedListeners = mutableSetOf<OnIsSelectedChangedListener>()


    val dateCreatedString: String
     get() {
         var dateComparator = DateTimeComparator.getDateOnlyInstance()
         val dateNow = Date()
         val locale = NoteApp.getLocale()

         if (dateComparator.compare(createdAt, dateNow) == 0) {

             var timeFormat: String

             if (NoteApp.is24hourFormat()) {
                 timeFormat = "H:mm"
             } else {
                 timeFormat = "h:mm a"
             }

             val formatter = SimpleDateFormat(timeFormat, locale)
             return formatter.format(createdAt)
         }

         dateComparator = DateTimeComparator.getInstance(DateTimeFieldType.year())

         if (dateComparator.compare(createdAt, dateNow) == 0) {
             val formatter = SimpleDateFormat("MMMM d", locale)
             return formatter.format(createdAt)
         }

         val formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT, locale)
         return formatter.format(createdAt)
     }

    fun asDatabase(): NoteDatabase {
        return NoteDatabase(
            noteId = this.noteId,
            groupId = this.groupId,
            title = this.name,
            position = position,
            value = this.value,
            createdAt = this.createdAt.time)
    }

    fun addOnIsSelectedChangedListener(listener: OnIsSelectedChangedListener) {
        onIsSelectedChangedListeners.add(listener)
    }

    fun removeIsSelectedChangedListener(listener: OnIsSelectedChangedListener) {
        onIsSelectedChangedListeners.remove(listener)
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
                && this.name == otherNote.name
                && this.value == otherNote.value
    }


    override fun hashCode(): Int {
        return noteId.hashCode()
    }
}