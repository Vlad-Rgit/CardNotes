package com.example.cardnotes.domain


import java.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import com.example.cardnotes.NoteApp
import com.example.cardnotes.database.models.NoteDatabase
import com.example.cardnotes.interfaces.SortedItem
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.util.*

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

    val dateCreatedString: String
     get() {


        var dateComparator = DateTimeComparator.getDateOnlyInstance()
        val dateNow = Date()
         val locale = NoteApp.getLocale()

        if(dateComparator.compare(createdAt, dateNow) == 0) {

            var timeFormat: String

            if(NoteApp.is24hourFormat()) {
                timeFormat = "H:mm"
            }
            else {
                timeFormat = "h:mm a"
            }

            val formatter = SimpleDateFormat(timeFormat, locale)
            return formatter.format(createdAt)
        }

        dateComparator = DateTimeComparator.getInstance(DateTimeFieldType.year())

        if(dateComparator.compare(createdAt, dateNow) == 0) {
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
            title = this.name.value!!,
            position = position,
            value = this.value.value!!,
            createdAt = this.createdAt.time)
    }

    fun hasContent(): Boolean {
        return if(value.value == null)
            false
        else
            value.value!!.isNullOrBlank()
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