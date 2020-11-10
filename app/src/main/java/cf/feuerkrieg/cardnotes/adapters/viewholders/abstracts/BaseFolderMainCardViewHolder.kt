package cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.domain.FolderDomain

abstract class BaseFolderMainCardViewHolder
    (view: View, lifecycleOwner: LifecycleOwner) :
    BaseMainCardViewHolder<FolderDomain>(view, lifecycleOwner) {


    private val tvFolderName = view.findViewById<TextView>(
        R.id.tvFolderName
    )

    private val tvModifiedAt = view.findViewById<TextView>(
        R.id.tvModifiedAt
    )

    private val tvNotesCount = view.findViewById<TextView>(
        R.id.tvCount
    )

    private val countObserver = Observer<Int> {
        tvNotesCount.text = it.toString()
    }


    override fun highlight() {
        model?.let {
            if (it.colorHex.isBlank()) {
                super.highlight()
            } else {
                val color = Color.parseColor(it.colorHex)
                highlight(color)
            }
        }
    }


    override fun performBind(model: FolderDomain, isSelectionMode: Boolean, isSelected: Boolean) {
        super.performBind(model, isSelectionMode, isSelected)
        tvNotesCount.text = model.notesCount.toString()
        tvFolderName.text = model.name
        tvModifiedAt.text = model.dateCreatedString
    }

}