package cf.feuerkrieg.cardnotes.domain

import androidx.lifecycle.MutableLiveData
import cf.feuerkrieg.cardnotes.interfaces.SortedItem

abstract class BaseDomain(
    var id: Int = -1,
    var name: String = ""
) : SortedItem<BaseDomain> {

    val isSelected = MutableLiveData<Boolean>(false)

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