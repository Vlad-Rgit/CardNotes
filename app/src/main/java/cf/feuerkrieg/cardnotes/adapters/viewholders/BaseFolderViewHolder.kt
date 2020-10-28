package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.domain.FolderDomain

abstract class BaseFolderViewHolder
    (view: View, lifecycleOwner: LifecycleOwner) :
    BaseViewHolder<FolderDomain>(view, lifecycleOwner) {

    override var isSelectionMode: Boolean = false

    private lateinit var folder: FolderDomain

    private val tvFolderName = view.findViewById<TextView>(
        R.id.tvFolderName
    )

    private val tvModifiedAt = view.findViewById<TextView>(
        R.id.tvModifiedAt
    )

    private val tvNotesCount = view.findViewById<TextView>(
        R.id.tvCount
    )

    /* override fun highlight() {
         if(::folder.isInitialized) {
             if(folder.colorHex.isBlank()) {
                 super.highlight()
             }
             else {
                 val color = Color.parseColor(folder.colorHex)
                 highlight(color)
             }
         }
     }*/

    init {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performBind(model: FolderDomain, isSelectionMode: Boolean) {

        folder = model

        tvFolderName.text = model.name
        tvModifiedAt.text = model.dateCreatedString
        tvNotesCount.text = model.notesCount.toString()
    }

}