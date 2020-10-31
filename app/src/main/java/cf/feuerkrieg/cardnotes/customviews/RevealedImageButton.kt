package cf.feuerkrieg.cardnotes.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton


class RevealedImageButton(
    context: Context,
    attrs: AttributeSet)
    : AppCompatImageButton(context, attrs) {

    var isRevealed: Boolean = false
        private set

    fun reveal() {
        if (!isRevealed) {
            isRevealed = true
            this.animate()
                .alpha(1f)
                .start()
        }
    }

    fun unreveal() {
        if (isRevealed) {
            isRevealed = false
            this.animate()
                .alpha(0f)
                .start()
        }
    }
}