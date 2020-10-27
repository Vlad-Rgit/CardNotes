package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.animation.ValueAnimator
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import com.google.android.material.card.MaterialCardView

abstract class BaseViewHolder<in T : BaseDomain>(
    view: View,
    protected val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(view),
    ItemTouchViewHolder {

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