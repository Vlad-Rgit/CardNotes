package cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts

import android.view.View
import androidx.annotation.CallSuper
import cf.feuerkrieg.cardnotes.domain.BaseDomain

abstract class BaseDomainViewHolder<T : BaseDomain>
    (view: View) : BaseCardViewHolder(view) {

    private var onModelClickCallback:
            ((model: BaseDomain, root: View) -> Unit)? = null

    private var onModelLongClickCallback:
            ((model: BaseDomain, root: View) -> Unit)? = null

    protected var model: T? = null

    @CallSuper
    open fun performBind(model: T) {
        this.model = model
    }

    @CallSuper
    override fun onCardClicked() {
        super.onCardClicked()
        model?.let {
            onModelClickCallback?.invoke(it, itemView)
        }
    }

    @CallSuper
    override fun onCardLongClicked() {
        super.onCardLongClicked()
        model?.let {
            onModelLongClickCallback?.invoke(it, itemView)
        }
    }

    fun setOnModelClickedCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onModelClickCallback = callback
    }

    fun setOnModelLongClickedCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onModelLongClickCallback = callback
    }

}