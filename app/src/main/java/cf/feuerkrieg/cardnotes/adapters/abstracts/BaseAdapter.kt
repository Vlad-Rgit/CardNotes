package cf.feuerkrieg.cardnotes.adapters.abstracts

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseDomainViewHolder
import cf.feuerkrieg.cardnotes.callbacks.NotesDiffUtilCallback
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain


abstract class BaseAdapter<VH : BaseDomainViewHolder<BaseDomain>>
    : RecyclerView.Adapter<VH>() {

    companion object {
        const val VIEW_TYPE_NOTE = 7
        const val VIEW_TYPE_FOLDER = 2
    }

    enum class LayoutType {
        List,
        Grid
    }

    var layoutType: LayoutType = LayoutType.Grid

    protected var onNoteClickCallback
            : ((note: NoteDomain, root: View) -> Unit)? = null

    protected var onFolderClickCallback
            : ((folder: FolderDomain, root: View) -> Unit)? = null

    protected var items = listOf<BaseDomain>()

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FolderDomain -> VIEW_TYPE_FOLDER
            is NoteDomain -> VIEW_TYPE_NOTE
            else -> throw IllegalArgumentException()
        }
    }

    fun setOnFolderClickListener(callback: (folder: FolderDomain, root: View) -> Unit) {
        onFolderClickCallback = callback
    }

    fun getFolders(): List<FolderDomain> = items.filterIsInstance<FolderDomain>()

    fun getNotes(): List<NoteDomain> = items.filterIsInstance<NoteDomain>()

    fun setFolders(newFolders: List<FolderDomain>) {
        val newItems = mutableListOf<BaseDomain>()
        newItems.addAll(newFolders)
        newItems.addAll(getNotes())
        setAllItems(newItems)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        return true
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setNotes(newNotes: List<NoteDomain>) {
        val newItems = mutableListOf<BaseDomain>()
        newItems.addAll(getFolders())
        newItems.addAll(newNotes)
        setAllItems(newItems)
    }

    protected open fun setAllItems(newItems: List<BaseDomain>) {
        val diffCallback = NotesDiffUtilCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnNoteClickListener(
        callback: (note: NoteDomain, root: View) -> Unit
    ) {
        onNoteClickCallback = callback
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.performBind(items[position])
    }
}