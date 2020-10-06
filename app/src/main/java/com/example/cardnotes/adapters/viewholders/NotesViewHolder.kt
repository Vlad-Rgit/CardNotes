package com.example.cardnotes.adapters.viewholders

import android.animation.ValueAnimator
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.BR
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.fragments.ItemTouchViewHolder
import com.example.cardnotes.interfaces.OnNoteClick
import com.google.android.material.card.MaterialCardView

abstract class NotesViewHolder
    (private val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root), ItemTouchViewHolder {

    protected val cardHost = itemView as MaterialCardView
    protected var onNoteClick: OnNoteClick? = null
    protected var onLongNoteClick: OnNoteClick? = null

    protected val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
        addUpdateListener {
            val animatedValue = animatedValue as Float
            cardHost.cardElevation = animatedValue
        }
    }

    fun performBind(model: NoteDomain) {

        binding.setVariable(BR.model, model)

        cardHost.setOnClickListener {
            onNoteClick?.onNoteClick(model)
        }

        cardHost.setOnLongClickListener {
            onLongNoteClick?.onNoteClick(model)
            false
        }
    }

    fun setOnNoteClickCallback(callback: OnNoteClick) {
        onNoteClick = callback
    }

    fun setOnNoteLongClickCallback(callback: OnNoteClick) {
        onLongNoteClick = callback
    }

    override fun downTouch() {
        elevationAnimation.cancel()
        elevationAnimation.start()
    }

    override fun upTouch() {
        elevationAnimation.cancel()
        elevationAnimation.reverse()
    }
}