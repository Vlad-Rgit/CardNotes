package cf.feuerkrieg.cardnotes.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.abstracts.BaseAdapter
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseFolderMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseNotesMainCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.callbacks.NotesDiffUtilCallback
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.ListAccessor
import java.util.*


class NotesAdapter(
    private val selectedNotes: ListAccessor<BaseDomain>
) : BaseAdapter<BaseMainCardViewHolder<BaseDomain>>() {


    lateinit var recyclerView: RecyclerView

    var lifecycleOwner: LifecycleOwner? = null

    private val boundViewHolders = mutableSetOf<BaseMainCardViewHolder<BaseDomain>>()

    private var startEditCallback: Runnable? = null

    private var onDropListener:
            ((from: BaseDomain, to: BaseDomain) -> Unit)? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onItemMoveCallback
            : ((model: BaseDomain, root: View) -> Unit)? = null

    private var onItemDeleteCallback:
            ((model: BaseDomain, root: View) -> Unit)? = null

    private val itemsUpdateCallback = object : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
            for (i in position until position + count) {
                val item = items[i]
                item.isSelected.observe(requireLifecycleOwner()) { isSelected ->
                    if (isSelected && !selectedNotes.contains(item)) {
                        selectedNotes.add(item)
                    } else if (!isSelected) {
                        selectedNotes.remove(item)
                    }
                }
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
            for (i in position until position + count) {
                val item = oldItems!![i]
                item.isSelected.removeObservers(requireLifecycleOwner())
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }


    fun setOnItemDeleteCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onItemDeleteCallback = callback
    }


    private var oldItems: List<BaseDomain>? = null

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

        holder.setOnDeleteCallback { model, root ->
            onItemDeleteCallback?.invoke(model, root)
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

    fun setOnItemMoveCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onItemMoveCallback = callback
    }

    override fun setAllItems(newItems: List<BaseDomain>) {
        val diffCallback = NotesDiffUtilCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        oldItems = items
        items = newItems
        diffResult.dispatchUpdatesTo(itemsUpdateCallback)
        oldItems = null
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
        callback: (note: NoteDomain) -> Unit
    ) {
        noteUpdatedCallback = callback
    }


    fun setIsSelectedForAllItems(isSelected: Boolean) {
        for (item in items) {
            if (item.isSelected.value != isSelected) {
                item.isSelected.value = isSelected
            }
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