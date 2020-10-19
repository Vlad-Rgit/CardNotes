package com.example.cardnotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.R
import com.example.cardnotes.adapters.viewholders.NotesViewHolder
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.interfaces.ListAccessor
import java.util.*

class NotesAdapter(
    private val selectedNotes: ListAccessor<NoteDomain>)
    : RecyclerView.Adapter<NotesViewHolder>(){

    enum class LayoutType {
        List,
        Grid
    }

    var layoutType: LayoutType = LayoutType.Grid

    private val boundViewHolders = mutableListOf<NotesViewHolder>()

    private val selectedChangedListener = NoteDomain.OnIsSelectedChangedListener {
        if(it.isSelected) {
            selectedNotes.add(it)
        }
        else {
            selectedNotes.remove(it)
        }
    }


    private var startEditCallback: Runnable? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain, root: View) -> Unit)? = null

    private val notes = mutableListOf<NoteDomain>()

    var isSelectionMode: Boolean = false
        private set

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {

        val holder = when (layoutType) {
            LayoutType.Grid -> {
                GridViewHolder.from(parent)
            }
            LayoutType.List -> {
                ListViewHolder.from(parent)
            }
        }


        return holder
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.performBind(note, isSelectionMode)

        holder.setOnNoteClickCallback {
            if (isSelectionMode) {
                it.isSelected = !it.isSelected
            } else {
                onNoteClickCallback?.invoke(it, holder.itemView)
            }
        }

        holder.setOnNoteLongClickCallback { startEdit() }

        boundViewHolders.add(holder)
    }

    override fun onViewRecycled(holder: NotesViewHolder) {
        super.onViewRecycled(holder)
        boundViewHolders.remove(holder)
        holder.detachListeners()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dispose()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemId(position: Int): Long {
        return notes[position].noteId.toLong()
    }


    fun dispose() {

        for (holder in boundViewHolders)
            holder.detachListeners()

        boundViewHolders.clear()
    }

    /**
     * Update underlying notes list to
     * contain only the notes within the
     * list argument
     */
    fun replaceAll(list: List<NoteDomain>) {

        for (note in notes.toList()) {
            if(!list.contains(note)) {
                val index = notes.indexOf(note)
                note.removeIsSelectedChangedListener(selectedChangedListener)
                notes.removeAt(index)
                notifyItemRemoved(index)
            }
        }

        for(note in list) {

            if(!notes.contains(note)) {
                notes.add(note)
                note.addOnIsSelectedChangedListener(selectedChangedListener)
                notifyItemInserted(notes.size)
            }
        }
    }



    private fun getSelectedNotesCount(): Int {
        return selectedNotes.getSize()
    }

    /**
     * Sort elements by their position
     */
    fun sortByPosition() {
        sort { note -> note.position  }
    }

    /**
     * Sort by creation date
     */
    fun sortByDate(isDescending: Boolean = false) {
        sort(isDescending) { note -> note.createdAt }
    }

    fun <E : Comparable<E>> sort(isDescending: Boolean = false, by: (note: NoteDomain) -> E) {

        for (i in 0 until notes.size) {

            var min = by(notes[i])
            var index = -1

            for(j in i until notes.size) {
                val v = by(notes[j])
                if(v < min && !isDescending || v > min && isDescending) {
                    min = v
                    index = j
                }
            }

            if(index != -1
                && index != i) {
                Collections.swap(notes, i, index)
                notifyItemMoved(i, index)
                notifyItemChanged(i)
            }
        }
    }

    /**
     * Move item in the recycler view and
     * update moved notes with new position
     */
    fun moveItem(fromIndex: Int, toIndex: Int) {

        Collections.swap(notes, fromIndex, toIndex)

        val tempPosition = notes[fromIndex].position
        notes[fromIndex].position = notes[toIndex].position
        notes[toIndex].position = tempPosition

        notifyItemMoved(fromIndex, toIndex)


        //Call this to update the information
        //about notes in the database
        noteUpdatedCallback?.invoke(notes[fromIndex])
        noteUpdatedCallback?.invoke(notes[toIndex])
    }

    //Setters for callbacks

    fun setStartSelectionListener(callback: Runnable) {
        startEditCallback = callback
    }

    fun setNoteUpdatedCallback(
        callback: (note: NoteDomain) -> Unit) {
        noteUpdatedCallback = callback
    }

    fun setOnNoteClickCallback(
        callback: (note: NoteDomain, root: View) -> Unit) {
        onNoteClickCallback = callback
    }

    fun setIsSelectedForAllNotes(isSelected: Boolean) {
        for(note in notes) {
            if(note.isSelected != isSelected)
                note.isSelected = isSelected
        }
    }


    /**
     * Start selection mode
     */
    fun startEdit() {

        if(!isSelectionMode) {

            isSelectionMode = true

            startEditCallback?.run()


            for (holder in boundViewHolders) {
                holder.isSelectionMode = true
            }
        }
    }

    /**
     * End selection mode
     */
    fun disableSelection() {
        isSelectionMode = false

        for(holder in boundViewHolders) {
            holder.isSelectionMode = false
        }
    }


    class GridViewHolder
    private constructor(view: View) : NotesViewHolder(view) {

        companion object {

            fun from(parent: ViewGroup): GridViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return GridViewHolder(
                    inflater.inflate(
                        R.layout.note_grid_item, parent, false
                    )
                )
            }
        }
    }

    class ListViewHolder
    private constructor(view: View)
        : NotesViewHolder(view) {

        companion object {
            fun from(parent: ViewGroup): ListViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return ListViewHolder(inflater.inflate(
                    R.layout.note_list_item, parent, false))
            }
        }
    }

}