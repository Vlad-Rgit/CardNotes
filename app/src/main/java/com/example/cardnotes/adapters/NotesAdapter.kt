package com.example.cardnotes.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.R
import com.example.cardnotes.databinding.NoteItemBinding
import com.example.cardnotes.domain.NoteDomain
import com.google.android.material.card.MaterialCardView
import java.util.*

class NotesAdapter(
    private val lifecycleOwner: LifecycleOwner,
    context: Context,
    resourceId: Int): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private var startEditCallback: Runnable? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain) -> Unit)? = null

    private val inflater = LayoutInflater.from(context)

    private val notes = mutableListOf<NoteDomain>()

    var isSelection: Boolean = false
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(
            inflater, parent, false
        )

        binding.lifecycleOwner = lifecycleOwner

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.performBinding(note)
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
                notifyItemRemoved(index)
            }
        }

        for(note in list) {
            if(!notes.contains(note)) {
                notes.add(note)
                notifyItemInserted(notes.size)
            }
        }
    }

    /**
     * Sort elements by their position
     */
    fun sortByPosition() {

        for (i in 0 until notes.size) {

            var minPosition = Int.MAX_VALUE
            var index = -1

            for(j in i until notes.size) {
                if(notes[j].position < minPosition) {
                    minPosition = notes[j].position
                    index = j
                }
            }

            if(index != -1) {
                Collections.swap(notes, i, index)
                notifyItemMoved(i, index)
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

    /**
     * Get selected notes in the selection mode
     */
    fun getSelectedNotes(): List<NoteDomain> {
        return notes.filter {
            it.isSelected.value == true
        }
    }


    inner class ViewHolder(private val binding: NoteItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        private val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                binding.noteItemHost.cardElevation = animatedValue
            }
        }

        init {


            binding.noteItemHost.setOnLongClickListener {
                startEdit()
                return@setOnLongClickListener false
            }

            binding.noteItemHost.setOnClickListener {

                val model = binding.model

                if(model != null) {

                    if (!isSelection)
                        onNoteClickCallback?.invoke(model)
                }
            }
        }

        fun performBinding(model: NoteDomain) {
            binding.model = model
        }

        fun raiseCard() {
            elevationAnimation.cancel()
            elevationAnimation.start()
        }

        fun lowCard() {
            elevationAnimation.cancel()
            elevationAnimation.reverse()
        }

    }

}