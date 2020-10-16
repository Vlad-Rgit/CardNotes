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

    init {

        cardHost.setOnClickListener {
            note?.let {
                cbIsSelected.isChecked = !it.isSelected
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
                if(it.isSelected != isChecked)
                    it.setIsSelectedAndNotify(isChecked, cbIsSelected)
            }
        }
    }

    fun performBind(model: NoteDomain, isSelectionMode: Boolean) {

        note?.removeSelectedChangedListener(cbIsSelected)

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

        model.addIfNotExistsSelectedChangedListener(cbIsSelected) {
            cbIsSelected.isChecked = it.isSelected
        }

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


    fun setIsSelected(isSelected: Boolean) {
        cbIsSelected.isChecked = isSelected
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