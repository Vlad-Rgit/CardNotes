package cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderFactory<T : RecyclerView.ViewHolder> {
    fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): T
}