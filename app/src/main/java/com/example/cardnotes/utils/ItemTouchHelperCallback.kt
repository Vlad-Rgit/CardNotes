package com.example.cardnotes.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.adapters.NotesAdapter
import com.example.cardnotes.fragments.ItemTouchViewHolder

class ItemTouchHelperCallback: ItemTouchHelper.SimpleCallback(
    UP or DOWN or START or END, 0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder): Boolean {

        val adapter = recyclerView.adapter as NotesAdapter
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        adapter.moveItem(fromPosition, toPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        val notesViewHolder = viewHolder as? ItemTouchViewHolder
        notesViewHolder?.downTouch()
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        val notesViewHolder = viewHolder as? ItemTouchViewHolder
        notesViewHolder?.upTouch()
    }
}