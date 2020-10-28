package cf.feuerkrieg.cardnotes.adapters.viewholders

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ItemTouchViewHolder
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import com.google.android.material.card.MaterialCardView

abstract class BaseViewHolder<in T : BaseDomain>(
    view: View,
    protected val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(view),
    ItemTouchViewHolder {

    var isHighlighted: Boolean = false
        private set

    protected val context = itemView.context

    protected val cardHost = view as MaterialCardView
    protected val primaryColorHighlight = ContextCompat.getColor(
        context,
        R.color.colorPrimary
    )

    protected val highlightedStrokeWidth = context.resources
        .getDimension(R.dimen.card_border_width)

    abstract var isSelectionMode: Boolean

    abstract fun performBind(model: T, isSelectionMode: Boolean)

    protected fun highlight(color: Int) {
        buildStrokeColorAnimator(0, color).start()
    }

    open fun highlight() {
        if (!isHighlighted) {
            isHighlighted = true
            highlight(primaryColorHighlight)
        }
    }

    fun stopHighlight() {
        if (isHighlighted) {
            isHighlighted = false
            buildStrokeColorAnimator(cardHost.strokeColor, 0)
                .start()
        }
    }

    private fun buildStrokeColorAnimator(fromColor: Int, toColor: Int): ValueAnimator {
        return ValueAnimator.ofArgb(fromColor, toColor).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Int
                cardHost.strokeColor = animatedValue
                cardHost.invalidate()
            }

            doOnStart {
                if (fromColor == 0) {
                    cardHost.strokeWidth = highlightedStrokeWidth
                        .toInt()
                }
            }

            doOnEnd {
                if (toColor == 0) {
                    cardHost.strokeWidth = 0
                }
            }
        }
    }


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