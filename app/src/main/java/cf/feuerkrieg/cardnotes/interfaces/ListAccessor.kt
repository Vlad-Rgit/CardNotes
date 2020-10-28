package cf.feuerkrieg.cardnotes.interfaces

interface ListAccessor<in T> {

    fun add(item: T)
    fun remove(item: T)
    fun clear()
    fun addAll(collection: Collection<T>)
    fun contains(item: T): Boolean

    fun getSize(): Int

}