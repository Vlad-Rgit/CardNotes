package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

abstract class BaseCardViewHolder
    (view: View) : RecyclerView.ViewHolder(view) {

    private var onCardClickCallback:
            ((root: View) -> Unit)? = null

    private var onCardLongClickCallback:
            ((root: View) -> Unit)? = null

    protected val cardHost = view as MaterialCardView

    init {
        cardHost.setOnClickListener {
            onCardClicked()
        }

        cardHost.setOnLongClickListener {
            onCardLongClicked()
            false
        }
    }

    protected open fun onCardClicked() {
        onCardClickCallback?.invoke(itemView)
    }

    protected open fun onCardLongClicked() {
        onCardLongClickCallback?.invoke(itemView)
    }

    fun setOnCardClickedCallback(callback: (root: View) -> Unit) {
        onCardClickCallback = callback
    }

    fun setOnCardLongClickedCallback(callback: (root: View) -> Unit) {
        onCardLongClickCallback = callback
    }

}