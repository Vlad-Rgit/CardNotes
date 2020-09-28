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
    private var endEditCallback: Runnable? = null

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

    fun replaceAll(list: List<NoteDomain>) {

        for (i in 0 until notes.size) {
            if(!list.contains(notes[i])) {
                notes.removeAt(i)
                notifyItemRemoved(i)
            }
        }

        for(i in 0 until list.size) {
            if(!notes.contains(list[i])) {
                notes.add(list[i])
                notifyItemInserted(i)
            }
        }

    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        Collections.swap(notes, fromIndex, toIndex)
        notifyItemMoved(fromIndex, toIndex)
    }

    fun setStartSelectionListener(callback: Runnable) {
        startEditCallback = callback
    }

    fun setEndSelectionListener(callback: Runnable) {
        endEditCallback = callback
    }

    fun startEdit() {

        if(!isSelection) {

            isSelection = true

            startEditCallback?.run()

            for (note in notes)
                note.isSelectionEnabled.value = true
        }
    }

    private fun disableSelection() {

        isSelection = false

        for(note in notes)
            note.isSelectionEnabled.value = false
    }

    fun acceptEdit() {
        disableSelection()

        val temp = notes.toList()

        for(note in temp) {
            if(note.isSelected.value == true) {
                val i = notes.indexOf(note)
                notes.removeAt(i)
                notifyItemRemoved(i  )
            }
        }
    }

    fun cancelEdit() {
        disableSelection()
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