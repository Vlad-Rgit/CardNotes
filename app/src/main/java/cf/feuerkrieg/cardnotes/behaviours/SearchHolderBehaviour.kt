package cf.feuerkrieg.cardnotes.behaviours

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import cf.feuerkrieg.cardnotes.R


class SearchHolderBehaviour
    (context: Context, attrsSet: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attrsSet) {

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private var maxHeight = -1
    private var offset = -1

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.topHost
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        return if (dependency.bottom != offset) {
            offset = dependency.bottom
            true
        } else {
            false
        }
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

    /* override fun onNestedScroll(
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
             child.layoutParams.height = maxHeight - dyConsumed
             child.requestLayout()
         }
         else if(dyUnconsumed < 0) {

         }
     }*/


}