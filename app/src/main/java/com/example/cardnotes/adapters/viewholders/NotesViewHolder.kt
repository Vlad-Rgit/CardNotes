package com.example.cardnotes.adapters.viewholders

import android.animation.ValueAnimator
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.R
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.interfaces.OnNoteClick
import com.google.android.material.card.MaterialCardView

abstract class NotesViewHolder
    (view: View)
    : RecyclerView.ViewHolder(view), ItemTouchViewHolder {


    var isSelectionMode = false
        set(value) {
            field = value
            if(value) {
                enableSelectionMode()
            }
            else {
                disableSelectionMode()
            }
        }

    protected val cardHost: MaterialCardView = itemView as MaterialCardView

    protected var onNoteClick: OnNoteClick? = null
    protected var onLongNoteClick: OnNoteClick? = null
    protected var onNoteSelectedChanged: OnNoteClick? = null

    protected val tvNoteName: TextView = itemView.findViewById(R.id.tvNoteName)
    protected val tvNoteValue: TextView = itemView.findViewById(R.id.tvNoteValue)
    protected val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    protected val cbIsSelected: CheckBox = itemView.findViewById(R.id.chIsSelected)

    protected var note: NoteDomain? = null

    protected val onNoteIsSelectedChangedCallback = NoteDomain.OnIsSelectedChangedListener {
        if (it.isSelected != cbIsSelected.isChecked) {
            cbIsSelected.isChecked = it.isSelected
        }
    }


    init {

        cardHost.setOnClickListener {
            note?.let {
                onNoteClick?.onNoteClick(it)
            }
        }

        cardHost.setOnLongClickListener {
            note?.let {
                onLongNoteClick?.onNoteClick(it)
            }
            true
        }

        cbIsSelected.setOnCheckedChangeListener { _, isChecked ->
            note?.let {
                if(it.isSelected != isChecked) {
                    it.isSelected = isChecked
                }
            }
        }
    }

    fun performBind(model: NoteDomain, isSelectionMode: Boolean) {

        note?.removeIsSelectedChangedListener(onNoteIsSelectedChangedCallback)

        note = model

        if(model.name.isBlank()) {
            tvNoteName.visibility = View.GONE
        }
        else {
            tvNoteName.visibility = View.VISIBLE
            tvNoteName.text = model.name
        }

        tvNoteValue.text = model.value
        tvCreatedAt.text = model.dateCreatedString
        cbIsSelected.isChecked = model.isSelected
        cardHost.transitionName = model.noteId.toString()

        model.addOnIsSelectedChangedListener(onNoteIsSelectedChangedCallback)

        this.isSelectionMode = isSelectionMode
    }

    fun setOnNoteClickCallback(callback: OnNoteClick) {
        onNoteClick = callback
    }

    fun setOnNoteLongClickCallback(callback: OnNoteClick) {
        onLongNoteClick = callback
    }

    fun setOnNoteSelectedChangedCallback(callback: OnNoteClick) {
        onNoteSelectedChanged = callback
    }

    private fun enableSelectionMode() {
        cbIsSelected.visibility = View.VISIBLE
    }

    private fun disableSelectionMode() {
        cbIsSelected.visibility = View.GONE
        cbIsSelected.isChecked = false
    }

    override fun downTouch() {

        val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                cardHost.cardElevation = animatedValue
            }
        }

        elevationAnimation.start()
    }

    override fun upTouch() {

        val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                cardHost.cardElevation = animatedValue
            }
        }

        elevationAnimation.reverse()
    }
}