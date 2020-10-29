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

private const val CLASS_NAME_LABEL = "ClassNameLabel"

abstract class BaseViewHolder<T : BaseDomain>(
    view: View,
    protected val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(view),
    ItemTouchViewHolder {

    private var onDropListener:
            ((from: BaseDomain, to: BaseDomain) -> Unit)? = null

    var isHighlighted: Boolean = false
        private set

    protected val context = itemView.context

    protected val cardHost = view as MaterialCardView

    protected val primaryColorHighlight = ContextCompat.getColor(
        context,
        R.color.colorPrimary
    )
    protected var model: T? = null

    protected val highlightedStrokeWidth = context.resources
        .getDimension(R.dimen.card_border_width)

    abstract var isSelectionMode: Boolean

    abstract fun performBind(model: T, isSelectionMode: Boolean)

    protected fun highlight(color: Int) {
        buildStrokeColorAnimator(0, color).start()
    }

    init {

        /* cardHost.setOnLongClickListener {

             val shadow = View.DragShadowBuilder(it)

             if(Build.VERSION.SDK_INT >= 24) {
                 it.startDragAndDrop(
                     null,
                     shadow,
                     model,
                     0
                 )
             }
             else {
                 @Suppress("Deprecation")
                 it.startDrag(
                     null,
                     shadow,
                     model,
                     0
                 )
             }

             true
         }

         cardHost.setOnDragListener { v, event ->

             when(event.action) {
                 DragEvent.ACTION_DRAG_STARTED -> {

                     if(event.localState == model) {
                         //v.visibility = View.INVISIBLE
                         return@setOnDragListener false
                     }

                     if(event.localState is NoteDomain) {
                         return@setOnDragListener true
                     }

                     if(model is NoteDomain) {
                         return@setOnDragListener false
                     }

                     return@setOnDragListener true
                 }
                 DragEvent.ACTION_DRAG_ENTERED -> {
                     highlight()
                 }
                 DragEvent.ACTION_DRAG_EXITED -> {
                     stopHighlight()
                 }
                 DragEvent.ACTION_DRAG_ENDED -> {
                     stopHighlight()
                 }
                 DragEvent.ACTION_DROP -> {
                     onDropListener?.invoke(
                         event.localState as BaseDomain,
                         model!!
                     )
                 }
             }

             true
         }*/
    }

    open fun highlight() {
        if (!isHighlighted) {
            isHighlighted = true
            highlight(primaryColorHighlight)
        }
    }

    open fun detachObservers() {

    }

    fun stopHighlight() {
        if (isHighlighted) {
            isHighlighted = false
            buildStrokeColorAnimator(cardHost.strokeColor, 0)
                .start()
        }
    }

    fun setOnDropListener(listener: (from: BaseDomain, to: BaseDomain) -> Unit) {
        onDropListener = listener
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