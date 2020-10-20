package cf.feuerkrieg.cardnotes.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import cf.feuerkrieg.cardnotes.R

class RevealedCheckBox
    constructor (
        context: Context,
        attrs: AttributeSet)
    : AppCompatCheckBox(context, attrs) {

    fun reveal() {
        animate()
            .alpha(1f)
            .setDuration(
                resources.getInteger(R.integer.transition_animation).toLong()
            )
            .withStartAction {
                visibility = View.VISIBLE
            }
            .start()
    }

    fun unreveal() {
        animate()
            .alpha(0f)
            .setDuration(
                resources.getInteger(R.integer.transition_animation).toLong()
            )
            .withEndAction {
                visibility = View.INVISIBLE
            }
            .start()
    }

}