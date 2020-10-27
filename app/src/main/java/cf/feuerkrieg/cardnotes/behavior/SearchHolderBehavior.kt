package cf.feuerkrieg.cardnotes.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import cf.feuerkrieg.cardnotes.R


class SearchHolderBehavior: CoordinatorLayout.Behavior<View> {

    constructor(): super()

    constructor(context: Context, attrsSet: AttributeSet)
        : super(context, attrsSet)

    private var offset = 0
    private var maxHeight = -1

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.topHost
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {

        parent.onMeasureChild(
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed)

        if(maxHeight == -1)
            maxHeight = child.measuredHeight

        return true
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        ViewCompat.offsetTopAndBottom(child, offset)
        return true
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        return if(offset != dependency.bottom) {
            offset = dependency.bottom
            child.requestLayout()
            true
        }
        else {
            false
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        if(dyConsumed > 0) {
            Log.i("Scrolling", "dyConsumed: $dyConsumed")

            if(child.layoutParams.height < 0) {
                child.layoutParams.height = maxHeight - dyConsumed
            }
            else {
                child.layoutParams.height -= dyConsumed
            }

            if(child.layoutParams.height < 0)
                child.layoutParams.height = 0

            child.requestLayout()
        }
        else if(dyUnconsumed < 0) {
            Log.i("Scrolling", "dyUnconsumed: $dyUnconsumed")
            Log.i("Scrolling", "height: ${child.layoutParams.height}")
            Log.i("Scrolling", "maxHeight: ${maxHeight}")
            val previous = child.layoutParams.height
            child.layoutParams.height -= dyUnconsumed
            child.layoutParams.height = child.layoutParams.height.coerceIn(0, maxHeight)
            if(previous != child.layoutParams.height) {
                child.requestLayout()
            }
        }
    }

}