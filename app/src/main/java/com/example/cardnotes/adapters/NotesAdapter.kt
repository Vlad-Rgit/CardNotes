package com.example.cardnotes.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.adapters.viewholders.NotesViewHolder
import com.example.cardnotes.databinding.NoteItemBinding
import com.example.cardnotes.databinding.NoteListItemBinding
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.interfaces.OnNoteClick
import com.example.cardnotes.viewmodels.MainMenuViewModel
import java.lang.IllegalStateException
import java.util.*

class NotesAdapter(
    private val lifecycleOwner: LifecycleOwner,
    context: Context,
    resourceId: Int): RecyclerView.Adapter<NotesViewHolder>() {

    enum class LayoutType {
        List,
        Grid
    }

    var layoutType: LayoutType = LayoutType.Grid

    private var startEditCallback: Runnable? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var noteCheckedCallback
            : ((newQuantity: Int) -> Unit)? = null

    private val inflater = LayoutInflater.from(context)

    private val notes = mutableListOf<NoteDomain>()

    var isSelection: Boolean = false
        private set

    lateinit var selectedNotesAccessor
            : MainMenuViewModel.SelectedNotesAccessor

    var selectedNotesQuantity = 0
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {

        return when(layoutType) {
            LayoutType.Grid -> {
                GridViewHolder.from(parent, lifecycleOwner)
            }
            LayoutType.List -> {
                ListViewHolder.from(parent, lifecycleOwner)
            }
            else -> {
                throw IllegalStateException("Illegal layout type: $layoutType")
            }
        }
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.performBind(note)

        holder.setOnNoteClickCallback(object : OnNoteClick {
            override fun onNoteClick(noteDomain: NoteDomain) {
                if(isSelection) {
                    note.isSelected.value = !(note.isSelected.value ?: true)
                }
                else {
                    onNoteClickCallback?.invoke(note)
                }
            }
        })

        holder.setOnNoteLongClickCallback(object : OnNoteClick {
            override fun onNoteClick(noteDomain: NoteDomain) {
                startEdit()
            }
        })
    }

    override fun getItemCount(): Int {
        return notes.size
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
                notes.removeAt(index)
                Log.d("LiveDataNote",
                    "Removed observer from " + note.noteId + " id")
                note.isSelected.removeObservers(lifecycleOwner)
                notifyItemRemoved(index)
            }
        }

        for(note in list) {

            if(!notes.contains(note)) {
                notes.add(note)

                if(isSelection)
                    note.isSelectionEnabled.value = true

                Log.d("LiveDataNote",
                    "Added observer to " + note.noteId + " id")
                note.isSelected.observe(lifecycleOwner, IsNoteSelectedObserver(note))
                notifyItemInserted(notes.size)
            }
        }

        for(n in notes) {
            Log.d("DbNote", (n.groupId ?: -1).toString())
        }

        selectedNotesQuantity =
            selectedNotesAccessor.size()

        noteCheckedCallback?.invoke(selectedNotesQuantity)
    }



    private fun getSelectedNotesCount(): Int {
        return notes.filter {
            it.isSelected.value!!
        }.size
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
        callback: (note: NoteDomain) -> Unit) {
        onNoteClickCallback = callback
    }
    
    fun setIsSelectedForAllNotes(isSelected: Boolean) {
        for(note in notes) {
            note.isSelected.value = isSelected
        }
    }

    fun setNoteCheckedCallback(callback:
                                   (newQuantity: Int) -> Unit) {
        noteCheckedCallback = callback
    }

    /**
     * Start selection mode
     */
    fun startEdit() {

        if(!isSelection) {

            isSelection = true

            startEditCallback?.run()

            for (note in notes)
                note.isSelectionEnabled.value = true
        }
    }

    /**
     * End selection mode
     */
    fun disableSelection() {

        isSelection = false

        for(note in notes) {
            note.isSelectionEnabled.value = false
            note.isSelected.value = false
        }
    }


    class GridViewHolder
        private constructor(binding: NoteItemBinding, lifecycleOwner: LifecycleOwner)
        : NotesViewHolder(binding){

        companion object {

            fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): GridViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                val binding = NoteItemBinding.inflate(
                    inflater, parent, false)

                binding.lifecycleOwner = lifecycleOwner

                return GridViewHolder(binding, lifecycleOwner)
            }
        }
    }

    class ListViewHolder
        private constructor(binding: NoteListItemBinding, lifecycleOwner: LifecycleOwner)
        : NotesViewHolder(binding) {

        companion object {
            fun from(parent: ViewGroup,lifecycleOwner: LifecycleOwner): ListViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                val binding = NoteListItemBinding.inflate(
                    inflater, parent, false)

                binding.lifecycleOwner = lifecycleOwner

                return ListViewHolder(binding, lifecycleOwner)
            }
        }
    }

    inner class IsNoteSelectedObserver
        (private val note: NoteDomain):
        androidx.lifecycle.Observer<Boolean> {

        private var oldIsSelected = false

        override fun onChanged(isSelected: Boolean?) {

            if(isSelected != null &&
                oldIsSelected != isSelected) {

                oldIsSelected = isSelected

                if(isSelected) {
                    selectedNotesQuantity++
                    selectedNotesAccessor.add(note)
                }
                else {
                    selectedNotesQuantity--
                    selectedNotesAccessor.remove(note)
                }

                noteCheckedCallback?.invoke(selectedNotesQuantity)
            }
        }


    }

}
