package cf.feuerkrieg.cardnotes.behaviours

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R


class RecyclerViewBehaviour : CoordinatorLayout.Behavior<RecyclerView> {

    private var searchHolderBottom = 0

    private var maxHeight: Int = -1

    constructor() : super()

    constructor(context: Context, attrsSet: AttributeSet)
            : super(context, attrsSet)


    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.txt_search_layout
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: RecyclerView,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        ViewCompat.offsetTopAndBottom(child, searchHolderBottom)
        return true
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {

        return if (searchHolderBottom != dependency.bottom) {
            searchHolderBottom = dependency.bottom
            true
        } else {
            false
        }
    }
}