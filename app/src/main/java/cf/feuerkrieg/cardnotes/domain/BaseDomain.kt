package cf.feuerkrieg.cardnotes.domain

import androidx.lifecycle.MutableLiveData
import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.interfaces.SortedItem
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Base class for all Domain Models
 */
abstract class BaseDomain(
    var id: Int = -1,
    var name: String = "",
    val createdAt: Timestamp,
    var modifiedAt: Timestamp
) : SortedItem<BaseDomain> {

    open var isSelected = MutableLiveData(false)

    open val dateCreatedString: String
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

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is BaseDomain) return false

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return name
    }

    override fun areContentsTheSame(other: BaseDomain): Boolean {
        return this == other
    }

    override fun areItemsTheSame(other: BaseDomain): Boolean {
        return this.id == other.id
    }

    override fun compareTo(other: BaseDomain): Int {
        return this.name.compareTo(other.name)
    }
}