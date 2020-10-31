package cf.feuerkrieg.cardnotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseCardViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.BaseDomainViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.domain.FolderDomain


class FolderPickerAdapter : RecyclerView.Adapter<BaseCardViewHolder>() {

    companion object {
        const val VIEW_TYPE_FOLDER = 1
        const val VIEW_TYPE_ADD_FOLDER = 2
    }

    private var items = listOf<FolderDomain>()

    fun setItems(items: List<FolderDomain>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCardViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD_FOLDER -> AddFolderCardViewHolder.from(parent).apply {
                setOnCardClickedCallback {

                }
            }
            VIEW_TYPE_FOLDER -> FolderCardViewHolder.from(parent).apply {
                setOnModelClickedCallback { model, root ->

                }
            }
            else -> throw IllegalArgumentException("There is no view type: $viewType")
        }
    }

    override fun onBindViewHolder(holderFolder: BaseCardViewHolder, position: Int) {
        if (holderFolder is FolderCardViewHolder) {
            holderFolder.performBind(items[position - 1])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_ADD_FOLDER
            else -> VIEW_TYPE_FOLDER
        }
    }

    override fun getItemCount(): Int {
        //First view holder will be view for add new folder
        //so we add 1 to the size
        return items.size + 1
    }


    class FolderCardViewHolder
    private constructor(view: View) : BaseDomainViewHolder<FolderDomain>(view) {

        private val tvFolderName = view.findViewById<TextView>(
            R.id.tv_folder_name
        )


        override fun performBind(model: FolderDomain) {
            tvFolderName.text = model.name
        }


        companion object : ViewHolderFactory<FolderCardViewHolder> {
            override fun from(
                parent: ViewGroup
            ): FolderCardViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return FolderCardViewHolder(
                    inflater.inflate(
                        R.layout.folder_picker_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

    class AddFolderCardViewHolder
    private constructor(view: View) : BaseCardViewHolder(view) {

        companion object : ViewHolderFactory<AddFolderCardViewHolder> {
            override fun from(
                parent: ViewGroup
            ): AddFolderCardViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return AddFolderCardViewHolder(
                    inflater.inflate(
                        R.layout.add_folder_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

}