package cf.feuerkrieg.cardnotes.interfaces

interface SortedItem<T>: Comparable<T> {

    /**
     * If contents of the items are the same
     */
    fun areContentsTheSame(other: T): Boolean

    /**
     * If items are itself the same
     */
    fun areItemsTheSame(other: T): Boolean
}