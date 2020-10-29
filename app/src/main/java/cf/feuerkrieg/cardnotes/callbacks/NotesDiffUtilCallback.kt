package cf.feuerkrieg.cardnotes.callbacks

import androidx.recyclerview.widget.DiffUtil
import cf.feuerkrieg.cardnotes.domain.BaseDomain

class NotesDiffUtilCallback
    (
    private val oldList: List<BaseDomain>,
    private val newList: List<BaseDomain>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}