package cf.feuerkrieg.cardnotes.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.NotesViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.GroupDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.ListAccessor
import java.util.*

class NotesAdapter(
    private val selectedNotes: ListAccessor<NoteDomain>
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

    val lifecycleOwner: LifecycleOwner? = null

    private val boundViewHolders = mutableListOf<BaseViewHolder<BaseDomain>>()

    private var startEditCallback: Runnable? = null

    private var noteUpdatedCallback
            : ((note: NoteDomain) -> Unit)? = null

    private var onNoteClickCallback
            : ((note: NoteDomain, root: View) -> Unit)? = null

    private val items = mutableListOf<Any>()

    var isSelectionMode: Boolean = false
        private set

    init {
        setHasStableIds(true)
    }

    private fun requireLifecycleOwner(): LifecycleOwner {
        return requireNotNull(
            lifecycleOwner
        ) {
            "Lifecycle owner of the notes adapter " +
                    "must be initialized!"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseDomain> {

        val holder: BaseViewHolder<BaseDomain>

        if (viewType == ViewType.Note.ordinal) {
            holder = when (layoutType) {
                LayoutType.Grid -> {
                    GridViewHolder.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    ListViewHolder.from(parent, requireLifecycleOwner())
                }
            } as BaseViewHolder<BaseDomain>

        } else {
            holder = when(layoutType) {
                LayoutType.Grid -> {
                    FolderViewHolder.from(parent, requireLifecycleOwner())
                }
                LayoutType.List -> {
                    FolderViewHolder.from(parent, requireLifecycleOwner())
                }
            } as BaseViewHolder<BaseDomain>
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseDomain>, position: Int) {

        val note = items[position]


        boundViewHolders.add(holder)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        boundViewHolders.remove(holder)
        //holder.detachListeners()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dispose()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }


    fun dispose() {

        for (holder in boundViewHolders)
            holder.detachListeners()

        boundViewHolders.clear()
    }

    /**
     * Update underlying notes list to
     * contain only the notes within the
     * list argument
     */
    fun replaceAll(list: List<NoteDomain>) {

        for (note in items.toList()) {
            if (!list.contains(note)) {
                val index = items.indexOf(note)
                // note.removeIsSelectedChangedListener(selectedChangedListener)
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }

        for(note in list) {

            if (!items.contains(note)) {
                items.add(note)
                note.addOnIsSelectedChangedListener(selectedChangedListener)
                notifyItemInserted(items.size)
            }
        }
    }



    private fun getSelectedNotesCount(): Int {
        return selectedNotes.getSize()
    }

    /**
     * Sort elements by their position
     */
    fun sortByPosition() {
        sort { note -> note.position  }
    }

    /**
     * Sort by creation date
     */
    fun sortByDate(isDescending: Boolean = false) {
        sort(isDescending) { note -> note.createdAt }
    }

    fun <E : Comparable<E>> sort(isDescending: Boolean = false, by: (note: NoteDomain) -> E) {

        /* for (i in 0 until items.size) {

             var min = by(items[i])
             var index = -1

             for(j in i until items.size) {
                 val v = by(items[j])
                 if(v < min && !isDescending || v > min && isDescending) {
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
         }*/
    }

    /**
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
                note.isSelected != isSelected
            )
                note.isSelected = isSelected
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

        for(holder in boundViewHolders) {
            holder.isSelectionMode = false
        }
    }


    class GridViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner)
        : NotesViewHolder(view, lifecycleOwner) {

        private lateinit var titleSeparator: View

        companion object : ViewHolderFactory<GridViewHolder>{

            override fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): GridViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                val holder = GridViewHolder(
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

    class ListViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner)
        : NotesViewHolder(view, lifecycleOwner) {

        companion object : ViewHolderFactory<ListViewHolder> {
            override fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): ListViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return ListViewHolder(
                    inflater.inflate(
                        R.layout.note_list_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }
    }

    class FolderViewHolder
    private constructor(view: View, lifecycleOwner: LifecycleOwner)
        : BaseViewHolder<GroupDomain>(view, lifecycleOwner) {

        private val tvFolderName = view.findViewById<TextView>(
            R.id.tvFolderName
        )

        companion object : ViewHolderFactory<FolderViewHolder>{
            override fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): FolderViewHolder {

                val inflater = LayoutInflater.from(parent.context)

                return FolderViewHolder(
                    inflater.inflate(
                        R.layout.folder_item, parent, false
                    ),
                    lifecycleOwner
                )
            }
        }

        override fun performBind(model: GroupDomain, isSelectionMode: Boolean) {
            tvFolderName.text = model.name
        }

    }


}