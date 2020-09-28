package com.example.cardnotes.callbacks

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.cardnotes.interfaces.SortedItem

class SortedListCallback<T: SortedItem<T>>
    (private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>)
    : SortedList.Callback<T>() {

    override fun areItemsTheSame(item1: T, item2: T): Boolean {
        return item1.areItemsTheSame(item2)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun compare(o1: T, o2: T): Int {
        return o1.compareTo(o2)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}