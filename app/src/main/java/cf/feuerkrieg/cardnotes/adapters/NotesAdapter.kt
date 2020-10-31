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
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseFolderMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseNotesMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.callbacks.NotesDiffUtilCallback
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.ListAccessor
import java.util.*

const val VIEW_TYPE_NOTE = 7
const val VIEW_TYPE_FOLDER = 2

class NotesAdapter(
    private val selectedNotes: ListAccessor<BaseDomain>
) : RecyclerView.Adapter<BaseMainCardViewHolder<BaseDomain>>() {

    enum class LayoutType {
        List,
        Grid
    }

    lateinit var recyclerView: RecyclerView

    var layoutType: LayoutType = LayoutType.Grid

    var lifecycleOwner: LifecycleOwner? = null

    private val boundViewHolders = mutableSetOf<BaseMainCardViewHolder<BaseDomain>>()

    private var startEditCallback: Runnable? = null

    private var onDropListener:
            ((from: BaseDomain, to: BaseDomain) -> Unit)? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain, root: View) -> Unit)? = null

    private var onItemMoveCallback
            : ((model: BaseDomain, root: View) -> Unit)? = null

    private var onFolderClickCallback
            : ((folder: FolderDomain, root: View) -> Unit)? = null

    private var items = listOf<BaseDomain>()

    var isSelectionMode: Boolean = false
        private set


    fun setOnFolderClickCallback(callback: (folder: FolderDomain, root: View) -> Unit) {
        onFolderClickCallback = callback
    }


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
            is FolderDomain -> VIEW_TYPE_FOLDER
            is NoteDomain -> VIEW_TYPE_NOTE
            else -> throw IllegalArgumentException()
        }
    }


    @Suppress("Unchecked_Cast")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseMainCardViewHolder<BaseDomain> {

        Log.i("MemoryLeak", "Create View Holder!")


        val holder: BaseMainCardViewHolder<BaseDomain>

        if (viewType == VIEW_TYPE_NOTE) {
            holder = when (layoutType) {
                LayoutType.Grid -> {
                    NoteListMainCardViewHolder.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    NoteListMainCardViewHolder.from(parent, requireLifecycleOwner())
                }
            } as BaseMainCardViewHolder<BaseDomain>

        } else {
            holder = when (layoutType) {
                LayoutType.Grid -> {
                    FolderGridMainCardViewHolder.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    FolderListMainCardViewHolder.from(parent, requireLifecycleOwner())
                }
            } as BaseMainCardViewHolder<BaseDomain>
        }

        holder.setOnDropListener { from, to ->
            onDropListener?.invoke(from, to)
        }

        holder.setOnModelClickedCallback { model, root ->
            if (isSelectionMode) {
                model.isSelected.value = !(model.isSelected.value!!)
            } else {
                when (model) {
                    is NoteDomain -> onNoteClickCallback?.invoke(model, root)
                    is FolderDomain -> onFolderClickCallback?.invoke(model, root)
                }
            }
        }

        holder.setOnCardLongClickedCallback { root ->
            enableSelectionMode()
        }

        holder.setOnMoveCallback { model, root ->
            onItemMoveCallback?.invoke(model, root)
        }

        return holder
    }

    fun getFolders(): List<FolderDomain> = items.filterIsInstance<FolderDomain>()

    fun getNotes(): List<NoteDomain> = items.filterIsInstance<NoteDomain>()

    fun setFolders(newFolders: List<FolderDomain>) {
        val newItems = mutableListOf<BaseDomain>()
        newItems.addAll(newFolders)
        newItems.addAll(getNotes())
        setItems(newItems)
    }

    override fun onFailedToRecycleView(holderMain: BaseMainCardViewHolder<BaseDomain>): Boolean {
        return true
    }

    fun setOnItemMoveCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onItemMoveCallback = callback
    }

    fun setNotes(newNotes: List<NoteDomain>) {
        val newItems = mutableListOf<BaseDomain>()
        newItems.addAll(getFolders())
        newItems.addAll(newNotes)
        setItems(newItems)
    }

    private fun setItems(newItems: List<BaseDomain>) {
        val diffCallback = NotesDiffUtilCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }


    fun setOnDropListener(listener: (from: BaseDomain, to: BaseDomain) -> Unit) {
        onDropListener = listener
    }

    override fun onBindViewHolder(holderMain: BaseMainCardViewHolder<BaseDomain>, position: Int) {
        val item = items[position]
        holderMain.performBind(item, isSelectionMode)
        boundViewHolders.add(holderMain)
    }

    override fun onViewRecycled(holderMain: BaseMainCardViewHolder<BaseDomain>) {
        Log.i("MemoryLeak", boundViewHolders.size.toString())
        boundViewHolders.remove(holderMain)
        holderMain.detachObservers()
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
    fun enableSelectionMode() {

        if (!isSelectionMode) {

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
    fun endSelectionMode() {
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

    class NoteGridMainCardViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseNotesMainCardViewHolder(view, lifecycleOwner) {

        private lateinit var titleSeparator: View

        companion object : ViewHolderFactory<NoteGridMainCardViewHolder> {

            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): NoteGridMainCardViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                val holder = NoteGridMainCardViewHolder(
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

    class NoteListMainCardViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseNotesMainCardViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<NoteListMainCardViewHolder> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): NoteListMainCardViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return NoteListMainCardViewHolder(
                    inflater.inflate(
                        R.layout.note_list_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }
    }

    class FolderListMainCardViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseFolderMainCardViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<FolderListMainCardViewHolder> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): FolderListMainCardViewHolder {
                val inflater = LayoutInflater.from(parent.context)

                return FolderListMainCardViewHolder(
                    inflater.inflate(R.layout.folder_list_item, parent, false),
                    lifecycleOwner
                )
            }
        }
    }

    class FolderGridMainCardViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner) :
        BaseFolderMainCardViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<FolderGridMainCardViewHolder> {
            override fun from(
                parent: ViewGroup,
                lifecycleOwner: LifecycleOwner
            ): FolderGridMainCardViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return FolderGridMainCardViewHolder(
                    inflater.inflate(
                        R.layout.folder_grid_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }

    }


}