package cf.feuerkrieg.cardnotes.utils

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts.BaseDomainViewHolder


class NotesItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as BaseDomainViewHolder<*>)
                .getItemDetails()
        }
        return null
    }
}