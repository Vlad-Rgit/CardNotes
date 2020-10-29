package cf.feuerkrieg.cardnotes.utils

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.adapters.NotesAdapter
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseFolderViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseNotesViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ItemTouchViewHolder

class ItemTouchHelperCallback: ItemTouchHelper.SimpleCallback(
    UP or DOWN or START or END, 0) {

    private var previousTarget: BaseViewHolder<*>? = null
    private var isOverFolder = false

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

       // Log.i("DropTarget", "OnMove target: ${target.javaClass.canonicalName}")

        val adapter = recyclerView.adapter as NotesAdapter
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        // adapter.moveItem(fromPosition, toPosition)

        if (target != previousTarget) {
            previousTarget?.stopHighlight()
            previousTarget = target as BaseViewHolder<*>
        }

        if (viewHolder is BaseNotesViewHolder) {
            previousTarget!!.highlight()
        }
        if (viewHolder is BaseFolderViewHolder &&
            target !is BaseNotesViewHolder
        ) {
            previousTarget!!.highlight()
        }

        return true
    }


    override fun chooseDropTarget(
        selected: RecyclerView.ViewHolder,
        dropTargets: MutableList<RecyclerView.ViewHolder>,
        curX: Int,
        curY: Int
    ): RecyclerView.ViewHolder {
        super.chooseDropTarget(selected, dropTargets, curX, curY)
        return dropTargets.first()
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            Log.i("DropTarget", "SelectedChanged")
            val notesViewHolder = viewHolder as? BaseViewHolder<*>
            notesViewHolder?.downTouch()
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

        super.clearView(recyclerView, viewHolder)
        val notesViewHolder = viewHolder as? ItemTouchViewHolder
        notesViewHolder?.upTouch()
        previousTarget?.stopHighlight()

        if (viewHolder is BaseFolderViewHolder) {
            viewHolder.stopHighlight()
        }
    }
}