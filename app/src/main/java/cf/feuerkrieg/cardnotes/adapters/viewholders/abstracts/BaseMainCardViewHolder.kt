package cf.feuerkrieg.cardnotes.adapters.viewholders.abstracts

import android.animation.ValueAnimator
import android.os.Build
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.CallSuper
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.viewholders.interfaces.ItemTouchViewHolder
import cf.feuerkrieg.cardnotes.customviews.RevealedCheckBox
import cf.feuerkrieg.cardnotes.customviews.RevealedImageButton
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import com.google.android.material.transition.MaterialFade

private const val CLASS_NAME_LABEL = "ClassNameLabel"

abstract class BaseMainCardViewHolder<T : BaseDomain>(
    view: View,
    protected val lifecycleOwner: LifecycleOwner,
) : BaseDomainViewHolder<T>(view),
    ItemTouchViewHolder {

    var isHighlighted: Boolean = false
        private set

    private var onDropListener:
            ((from: BaseDomain, to: BaseDomain) -> Unit)? = null

    private var onMoveCallback:
            ((model: BaseDomain, root: View) -> Unit)? = null

    private var onDeleteCallback:
            ((model: BaseDomain, root: View) -> Unit)? = null

    fun setOnDeleteCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onDeleteCallback = callback
    }

    protected val context = itemView.context

    protected val btnOptions =
        view.findViewById<RevealedImageButton>(R.id.btn_card_options)

    protected val chIsSelected =
        view.findViewById<RevealedCheckBox>(R.id.chIsSelected)

    protected open val popupMenu: PopupMenu by lazy {

        PopupMenu(
            context,
            btnOptions,
            Gravity.NO_GRAVITY,
            0,
            R.style.CardNotes_PopupMenu
        ).apply {
            inflate(R.menu.card_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_move -> onMoveCallback?.invoke(model!!, itemView)
                    R.id.menu_delete -> onDeleteCallback?.invoke(model!!, itemView)
                }
                true
            }
        }
    }

    protected val primaryColorHighlight = ContextCompat.getColor(
        context,
        R.color.colorPrimary
    )

    protected val highlightedStrokeWidth = context.resources
        .getDimension(R.dimen.card_border_width)

    open var isSelectionMode: Boolean = false
        set(value) {

            field = value

            if (value)
                enableSelectionMode()
            else
                disableSelectionMode()
        }

    open var isSelected: Boolean = false
        set(value) {
            field = value

            if(field != chIsSelected.isChecked) {
                chIsSelected.isChecked = field
            }
        }

    private val isSelectedObserver = Observer<Boolean> {
        if (chIsSelected.isChecked != it) {
            chIsSelected.isChecked = it
        }
    }

    init {

        cardHost.setOnDragListener { v, event ->

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {

                    if (event.localState == model) {
                        //v.visibility = View.INVISIBLE
                        return@setOnDragListener false
                    }

                    if (event.localState is NoteDomain) {
                        return@setOnDragListener true
                    }

                    if (model is NoteDomain) {
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
                DragEvent.ACTION_DRAG_STARTED -> {

                }
                DragEvent.ACTION_DROP -> {
                    onDropListener?.invoke(
                        event.localState as BaseDomain,
                        model!!
                    )
                }
            }

            true
        }


        btnOptions.setOnClickListener {
            popupMenu.show()
        }
    }

    fun setOnMoveCallback(callback: (model: BaseDomain, root: View) -> Unit) {
        onMoveCallback = callback
    }

    @CallSuper
    override fun onCardLongClicked() {

        val shadow = View.DragShadowBuilder(cardHost)

        if (Build.VERSION.SDK_INT >= 24) {
            cardHost.startDragAndDrop(
                null,
                shadow,
                model,
                0
            )
        } else {
            @Suppress("Deprecation")
            cardHost.startDrag(
                null,
                shadow,
                model,
                0
            )
        }


        super.onCardLongClicked()
    }

    open fun enableSelectionMode() {

        if (btnOptions.visibility != View.GONE &&
            chIsSelected.visibility != View.VISIBLE
        ) {

            TransitionManager.beginDelayedTransition(
                cardHost,
                buildSelectionModeTransitionSet()
            )

            btnOptions.visibility = View.GONE
            chIsSelected.visibility = View.VISIBLE

        }
    }


    open fun disableSelectionMode() {

        if (btnOptions.visibility != View.VISIBLE &&
            chIsSelected.visibility != View.GONE
        ) {

            TransitionManager.beginDelayedTransition(
                cardHost,
                buildSelectionModeTransitionSet()
            )

            btnOptions.visibility = View.VISIBLE
            chIsSelected.visibility = View.GONE
        }
    }

    fun setOnDropListener(listener: (from: BaseDomain, to: BaseDomain) -> Unit) {
        onDropListener = listener
    }


    protected open fun buildSelectionModeTransitionSet(): TransitionSet {
        return TransitionSet().apply {
            addTransition(MaterialFade().apply {
                addTarget(chIsSelected)
                addTarget(btnOptions)
            })
        }
    }

    @CallSuper
    open fun performBind(model: T, isSelectionMode: Boolean, isSelected: Boolean) {
        performBind(model)
        this.isSelectionMode = isSelectionMode
        model.isSelected.observe(lifecycleOwner, Observer {
            if(chIsSelected.isChecked != it) {
                chIsSelected.isChecked = it
            }
        })
    }

    protected fun highlight(color: Int) {
        buildStrokeColorAnimator(0, color).start()
    }

    open fun highlight() {
        if (!isHighlighted) {
            isHighlighted = true
            highlight(primaryColorHighlight)
        }
    }


    @CallSuper
    open fun detachObservers() {
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

                cardHost.setHasTransientState(false)
            }
        }
    }


    override fun downTouch() {

        val elevationAnimation = ValueAnimator.ofFloat(3f, 16f).apply {
            addUpdateListener {
                val animatedValue = animatedValue as Float
                cardHost.cardElevation = animatedValue
            }

            doOnEnd {
                cardHost.setHasTransientState(false)
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

            doOnEnd {
                cardHost.setHasTransientState(false)
            }
        }

        elevationAnimation.reverse()
    }
}