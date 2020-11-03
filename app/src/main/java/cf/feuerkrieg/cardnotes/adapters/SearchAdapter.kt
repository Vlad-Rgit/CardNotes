package cf.feuerkrieg.cardnotes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import cf.feuerkrieg.cardnotes.BR
import cf.feuerkrieg.cardnotes.adapters.abstracts.BaseAdapter
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseDomainViewHolder
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ViewHolderFactory
import cf.feuerkrieg.cardnotes.databinding.SearchGridFolderItemBinding
import cf.feuerkrieg.cardnotes.databinding.SearchListFolderItemBinding
import cf.feuerkrieg.cardnotes.databinding.SearchNoteItemBinding
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain

class SearchAdapter : BaseAdapter<BaseDomainViewHolder<BaseDomain>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDomainViewHolder<BaseDomain> {

        val holder = if (viewType == VIEW_TYPE_FOLDER) {
            when (layoutType) {
                LayoutType.Grid -> {
                    FolderGridViewHolder.from(parent)
                }
                LayoutType.List -> {
                    FolderListViewHolder.from(parent)
                }
            }
        } else {
            NoteViewHolder.from(parent)
        }

        holder.setOnModelClickedCallback { model, root ->
            when (model) {
                is NoteDomain -> onNoteClickCallback?.invoke(model, root)
                is FolderDomain -> onFolderClickCallback?.invoke(model, root)
            }
        }

        return holder
    }

    abstract class BaseBindingViewHolder
        (private val binding: ViewDataBinding) : BaseDomainViewHolder<BaseDomain>(binding.root) {

        override fun performBind(model: BaseDomain) {
            super.performBind(model)
            binding.setVariable(BR.model, model)
        }
    }

    class FolderGridViewHolder
    private constructor(binding: ViewDataBinding) : BaseBindingViewHolder(binding) {

        companion object : ViewHolderFactory<FolderGridViewHolder> {

            override fun from(parent: ViewGroup): FolderGridViewHolder {
                return FolderGridViewHolder(
                    SearchGridFolderItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }


    class FolderListViewHolder
    private constructor(binding: ViewDataBinding) : BaseBindingViewHolder(binding) {

        companion object : ViewHolderFactory<FolderListViewHolder> {

            override fun from(parent: ViewGroup): FolderListViewHolder {
                return FolderListViewHolder(
                    SearchListFolderItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    class NoteViewHolder
    private constructor(binding: ViewDataBinding) : BaseBindingViewHolder(binding) {

        companion object : ViewHolderFactory<NoteViewHolder> {

            override fun from(parent: ViewGroup): NoteViewHolder {
                return NoteViewHolder(
                    SearchNoteItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

}