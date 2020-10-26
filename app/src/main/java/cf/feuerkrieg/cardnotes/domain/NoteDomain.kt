package cf.feuerkrieg.cardnotes.domain


import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.database.models.NoteDatabase
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class NoteDomain(
    id: Int = 0,
    var groupId: Int? = null,
    var position: Int = id,
    name: String = "",
    var value: String = "",
    var groupName: String? = null,
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis())
) : BaseDomain(id, name) {


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
             val formatter = SimpleDateFormat("d MMMM", locale)
             return formatter.format(createdAt)
         }

         val formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT, locale)
         return formatter.format(createdAt)
     }

    fun asDatabase(): NoteDatabase {
        return NoteDatabase(
            noteId = this.id,
            groupId = this.groupId,
            title = this.name,
            position = position,
            value = this.value,
            createdAt = this.createdAt.time
        )
    }

}