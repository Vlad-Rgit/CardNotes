package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.customviews.RevealedCheckBox
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.OnNoteClick

abstract class BaseNotesViewHolder
    (
    view: View,
    lifecycleOwner: LifecycleOwner
) : BaseViewHolder<NoteDomain>(view, lifecycleOwner) {

    override var isSelectionMode = false
        set(value) {
            field = value
            if (value) {
                enableSelectionMode()
            } else {
                disableSelectionMode()
            }
        }

    protected var onNoteClick: OnNoteClick? = null
    protected var onLongNoteClick: OnNoteClick? = null

    protected val tvNoteName: TextView = itemView.findViewById(R.id.tvNoteName)
    protected val tvNoteValue: TextView = itemView.findViewById(R.id.tvNoteValue)
    protected val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    protected val cbIsSelected: RevealedCheckBox = itemView.findViewById(R.id.chIsSelected)


    init {


        cardHost.setOnClickListener {
            model?.let {
                onNoteClick?.onNoteClick(it)
            }
        }

        /* cardHost.setOnLongClickListener {
              model?.let {
                  onLongNoteClick?.onNoteClick(it)
              }
              true
          }
*/
        cbIsSelected.setOnCheckedChangeListener { _, isChecked ->
            model?.let {
                if (it.isSelected.value != isChecked) {
                    it.isSelected.value = isChecked
                }
            }
        }
    }

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
        cbIsSelected.isChecked = model.isSelected.value!!
        cardHost.transitionName = model.hashCode().toString()

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

        this.isSelectionMode = isSelectionMode
    }

    fun setOnNoteClickCallback(callback: OnNoteClick) {
        onNoteClick = callback
    }

    fun setOnNoteLongClickCallback(callback: OnNoteClick) {
        onLongNoteClick = callback
    }


    private fun enableSelectionMode() {
        if (cbIsSelected.visibility != View.VISIBLE) {
            cbIsSelected.reveal()
        }
    }

    private fun disableSelectionMode() {
        if (cbIsSelected.visibility != View.GONE) {
            cbIsSelected.isChecked = false
            cbIsSelected.unreveal()
        }
    }

    override fun detachObservers() {
        super.detachObservers()
        Log.i("Observers", "Detach")
    }

}