package cf.feuerkrieg.cardnotes.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class RevealedConstraintLayout
    (context: Context, attributeSet: AttributeSet)
    : ConstraintLayout(context, attributeSet) {

    private lateinit var heightAnimator: ValueAnimator

    override fun setVisibility(visibility: Int) {

        super.setVisibility(visibility)

        if(!::heightAnimator.isInitialized) {
            heightAnimator = ValueAnimator.ofInt(0,
                measuredHeight)

            heightAnimator.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                val layoutParams = this.layoutParams
                layoutParams.height = animatedValue
                this.layoutParams = layoutParams
                postInvalidate()
            }
        }

        heightAnimator.cancel()

        if(visibility == View.VISIBLE)
            heightAnimator.start()
        else
            heightAnimator.reverse()
    }
}