package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.animation.ValueAnimator
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import com.google.android.material.card.MaterialCardView

abstract class BaseViewHolder<T : BaseDomain>(view: View) : RecyclerView.ViewHolder(view),
    ItemTouchViewHolder {


    protected val lifecycleOwner = view.findViewTreeLifecycleOwner()!!
    protected val cardHost = view as MaterialCardView

    abstract fun performBind(model: T, isSelectionMode: Boolean)

    override fun downTouch() {

        val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                cardHost.cardElevation = animatedValue
            }
        }

        elevationAnimation.start()
    }

    override fun upTouch() {

        val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                cardHost.cardElevation = animatedValue
            }
        }

        elevationAnimation.reverse()
    }
}