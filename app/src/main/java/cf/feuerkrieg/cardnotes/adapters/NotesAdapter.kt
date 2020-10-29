package cf.feuerkrieg.cardnotes.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseFolderViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseNotesViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.callbacks.NotesDiffUtilCallback
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.ListAccessor
import java.util.*

class NotesAdapter(
    private val selectedNotes: ListAccessor<BaseDomain>
) : RecyclerView.Adapter<BaseViewHolder<BaseDomain>>() {

    enum class LayoutType {
        List,
        Grid
    }

    enum class ViewType {
        Note,
        Folder
    }

    var layoutType: LayoutType = LayoutType.Grid

    var lifecycleOwner: LifecycleOwner? = null

    private val boundViewHolders = mutableSetOf<BaseViewHolder<BaseDomain>>()

    private var startEditCallback: Runnable? = null

    private var onDropListener:
            ((from: BaseDomain, to: BaseDomain) -> Unit)? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain, root: View) -> Unit)? = null

    private val items = mutableListOf<BaseDomain>()

    var isSelectionMode: Boolean = false
        private set


    private fun requireLifecycleOwner(): LifecycleOwner {
        return requireNotNull(
            lifecycleOwner
        ) {
            "Lifecycle owner of the notes adapter " +
                    "must be initialized!"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FolderDomain -> ViewType.Folder.ordinal
            is NoteDomain -> ViewType.Note.ordinal
            else -> throw IllegalArgumentException()
        }
    }


    @Suppress("Unchecked_Cast")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseDomain> {

        Log.i("MemoryLeak", "Create View Holder!")

        val holder: BaseViewHolder<BaseDomain>

        if (viewType == ViewType.Note.ordinal) {
            holder = when (layoutType) {
                LayoutType.Grid -> {
                    NoteGridViewHolderBase.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    NoteListViewHolderBase.from(parent, requireLifecycleOwner())
                }
            } as BaseViewHolder<BaseDomain>

        } else {
            holder = when (layoutType) {
                LayoutType.Grid -> {
                    FolderGridViewHolder.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    FolderListViewHolder.from(parent, requireLifecycleOwner())
                }
            } as BaseViewHolder<BaseDomain>
        }


        holder.setOnDropListener { from, to ->
            onDropListener?.invoke(from, to)
        }


        return holder
    }

    fun getFolders(): List<FolderDomain> = items.filterIsInstance<FolderDomain>()

    fun getNotes(): List<NoteDomain> = items.filterIsInstance<NoteDomain>()

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun setFolders(newFolders: List<FolderDomain>) {
        val oldFolders = getFolders()
        val diffCallback = NotesDiffUtilCallback(oldFolders, newFolders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.removeAll(oldFolders)
        items.addAll(0, newFolders)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setNotes(newNotes: List<NoteDomain>) {
        val oldNotes = getNotes()
        val diffCallback = NotesDiffUtilCallback(oldNotes, newNotes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.removeAll(oldNotes)
        items.addAll(newNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnDropListener(listener: (from: BaseDomain, to: BaseDomain) -> Unit) {
        onDropListener = listener
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseDomain>, position: Int) {
        val item = items[position]
        holder.performBind(item, isSelectionMode)
        boundViewHolders.add(holder)
    }

    override fun onViewRecycled(holder: BaseViewHolder<BaseDomain>) {
        super.onViewRecycled(holder)
        holder.detachObservers()
        boundViewHolders.remove(holder)
        Log.i("MemoryLeak", "BoundViewHolders: ${boundViewHolders.size}")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dispose()
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun dispose() {
        boundViewHolders.clear()
    }

    fun addAll(collection: Collection<BaseDomain>) {
        val start = items.size
        val count = collection.size
        items.addAll(collection)
        notifyItemRangeInserted(start, count)
    }

    fun addFirst(collection: Collection<BaseDomain>) {
        val start = 0
        val count = collection.size
        items.addAll(start, collection)
        notifyItemRangeInserted(start, count)
    }

    fun sortFirstFolders() {
        for (i in items.indices) {
            val item = items[i]
            for (j in i + 1 until items.size) {
                val other = items[j]
                if (other is FolderDomain &&
                    item is NoteDomain
                ) {
                    Collections.swap(items, i, j)
                    notifyItemMoved(i, j)
                    notifyItemChanged(i)
                }
            }
        }
    }

    /**
     * Update underlying notes list to
     * contain only the notes within the
     * list argument
     */
    fun replaceAll(list: List<BaseDomain>) {

        for (item in items.toList()) {
            if (!list.contains(item)) {
                val index = items.indexOf(item)
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }

        for (item in list) {
            if (!items.contains(item)) {
                items.add(item)
                item.isSelected.observe(
                    requireLifecycleOwner(),
                    ItemIsSelectedObserver(item, selectedNotes)
                )
                notifyItemInserted(items.size - 1)
            }
        }
    }

    private fun getSelectedNotesCount(): Int {
        return selectedNotes.getSize()
    }

    /**
     * Sort by creation date
     */
    fun sortByDate(isDescending: Boolean = false) {
        sort(isDescending) { note -> note.createdAt }
    }

    fun <E : Comparable<E>> sort(isDescending: Boolean = false, by: (note: BaseDomain) -> E) {

        for (i in 0 until items.size) {

            var min = by(items[i])
            var index = -1

            for (j in i until items.size) {
                val v = by(items[j])
                if (v < min && !isDescending || v > min && isDescending) {
                    min = v
                    index = j
                }
             }

             if(index != -1
                 && index != i) {
                 Collections.swap(items, i, index)
                 notifyItemMoved(i, index)
                 notifyItemChanged(i)
             }
        }
    }

    /* /**
      * Move item in the recycler view and
      * update moved notes with new position
      */
     fun moveItem(fromIndex: Int, toIndex: Int) {

         Collections.swap(items, fromIndex, toIndex)

         val tempPosition = items[fromIndex].position
         // items[fromIndex].position = items[toIndex].position
         // items[toIndex].position = tempPosition

         notifyItemMoved(fromIndex, toIndex)


         //Call this to update the information
         //about notes in the database
         // noteUpdatedCallback?.invoke(items[fromIndex])
         // noteUpdatedCallback?.invoke(items[toIndex])
     }*/

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
        for (note in items) {
            if (note is NoteDomain &&
                note.isSelected.value != isSelected
            )
                note.isSelected.value = isSelected
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

        for (holder in boundViewHolders) {
            holder.isSelectionMode = false
        }
    }


    class ItemIsSelectedObserver(
        private val item: BaseDomain,
        private val selectedItems: ListAccessor<BaseDomain>
    ) : Observer<Boolean> {

        private var oldIsSelected = false

        override fun onChanged(t: Boolean?) {
            if (t != null && oldIsSelected != t) {
                oldIsSelected = t
                if (t)
                    selectedItems.add(item)
                else
                    selectedItems.remove(item)
            }
        }
    }

    class NoteGridViewHolderBase
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseNotesViewHolder(view, lifecycleOwner) {

        private lateinit var titleSeparator: View

        companion object : ViewHolderFactory<NoteGridViewHolderBase> {

            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): NoteGridViewHolderBase {

                val inflater = LayoutInflater.from(parent.context)

                val holder = NoteGridViewHolderBase(
                    inflater.inflate(
                        R.layout.note_grid_item, parent, false
                    ),
                    lifecycleOwner
                )

                holder.titleSeparator = holder.itemView.findViewById(
                    R.id.titleSeparator
                )

                return holder
            }
        }

        override fun performBind(model: NoteDomain, isSelectionMode: Boolean) {
            super.performBind(model, isSelectionMode)

            if (model.name.isBlank() || model.value.isBlank()) {
                titleSeparator.visibility = View.GONE
            } else {
                titleSeparator.visibility = View.VISIBLE
            }

            if (model.value.isBlank()) {
                tvNoteName.gravity = Gravity.START
            } else {
                tvNoteName.gravity = Gravity.CENTER
            }
        }
    }

    class NoteListViewHolderBase
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseNotesViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<NoteListViewHolderBase> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): NoteListViewHolderBase {

                val inflater = LayoutInflater.from(parent.context)

                return NoteListViewHolderBase(
                    inflater.inflate(
                        R.layout.note_list_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }
    }

    class FolderListViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseFolderViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<FolderListViewHolder> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): FolderListViewHolder {
                val inflater = LayoutInflater.from(parent.context)

                return FolderListViewHolder(
                    inflater.inflate(R.layout.folder_list_item, parent, false),
                    lifecycleOwner
                )
            }
        }
    }

    class FolderGridViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseFolderViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<FolderGridViewHolder> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): FolderGridViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return FolderGridViewHolder(
                    inflater.inflate(
                        R.layout.folder_grid_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }

    }


}