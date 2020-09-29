package com.example.cardnotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.cardnotes.callbacks.SortedListCallback
import com.example.cardnotes.interfaces.SortedItem

open class RecyclerViewAdapter<T: SortedItem<T>,
        VH: RecyclerViewAdapter.ViewHolder>
    (clazz: Class<T>,
     private val viewHolderClazz: Class<VH>,
     private val context: Context,
     private val resourceId: Int)
    : RecyclerView.Adapter<VH>() {


    private val sortedList= SortedList<T>(clazz,
        SortedListCallback<T>(this))

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater, resourceId, parent, false)

        val cs = viewHolderClazz.getConstructor(ViewDataBinding::class.java)
        return cs.newInstance(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = sortedList[position]
        holder.performBind(item)
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }


    open class ViewHolder
        (val binding: ViewDataBinding)
        : RecyclerView.ViewHolder(binding.root) {

        open fun performBind(item: Any) {
            binding.setVariable(BR.model, item)
        }

    }

    fun edit(): Editor<T> {
        return Editor<T>(sortedList)
    }


    class Editor<T: SortedItem<T>>
        (private val sortedList: SortedList<T>) {

        private val actions = mutableListOf<() -> Unit>()

        fun replaceAll(collection: Collection<T>): Editor<T> {
            actions.add {
                sortedList.replaceAll(collection)
            }
            return this
        }

        fun moveItem(fromIndex: Int, toIndex: Int): Editor<T> {

            actions.add {
                val fromItem = sortedList[fromIndex]
                val toItem = sortedList[toIndex]
                sortedList.updateItemAt(fromIndex, toItem)
                sortedList.updateItemAt(toIndex, fromItem)
            }

            return this
        }

        /**
         * Commit all changed to list
         */
        fun commit() {

            sortedList.beginBatchedUpdates()

            for(action in actions) {
                action()
            }

            sortedList.endBatchedUpdates()

        }

    }

}