package cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.domain.NoteDomain

abstract class BaseNotesMainCardViewHolder
    (
    view: View,
    lifecycleOwner: LifecycleOwner
) : BaseMainCardViewHolder<NoteDomain>(view, lifecycleOwner) {

    protected val tvNoteName: TextView = itemView.findViewById(R.id.tvNoteName)
    protected val tvNoteValue: TextView = itemView.findViewById(R.id.tvNoteValue)
    protected val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)


    @CallSuper
    override fun performBind(model: NoteDomain, isSelectionMode: Boolean) {

        super.performBind(model, isSelectionMode)

        if (model.name.isBlank()) {
            tvNoteName.visibility = View.GONE
        } else {
            tvNoteName.visibility = View.VISIBLE
            tvNoteName.text = model.name
        }

        tvNoteValue.text = model.value
        tvCreatedAt.text = model.dateCreatedString

        if (model.name.isBlank()) {
            tvNoteName.visibility = View.GONE
        } else {
            tvNoteName.visibility = View.VISIBLE
        }

        if (model.value.isBlank()) {
            tvNoteValue.visibility = View.GONE
        } else {
            tvNoteValue.visibility = View.VISIBLE
        }
    }


    @CallSuper
    override fun detachObservers() {
        super.detachObservers()
        Log.i("Observers", "Detach")
    }

}