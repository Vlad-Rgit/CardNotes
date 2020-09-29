package com.example.cardnotes.customviews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.example.cardnotes.callbacks.AnimatorListener


class RevealedImageButton(
    context: Context,
    attrs: AttributeSet)
    : AppCompatImageButton(context, attrs) {


    private val animListener = AnimatorListener()

    private val alphaAnimatorReveal = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            this@RevealedImageButton.alpha = it.animatedValue as Float
        }
    }

    private val alphaAnimatorHide = ValueAnimator.ofFloat(1f, 0f).apply {
        addListener(animListener)
        addUpdateListener {
            this@RevealedImageButton.alpha = it.animatedValue as Float
        }
    }

    override fun setVisibility(visibility: Int) {

        if(visibility == View.VISIBLE &&
            this.visibility != View.VISIBLE) {
            alphaAnimatorReveal.cancel()
            super.setVisibility(visibility)
            alphaAnimatorReveal.start()
        }
        else if (visibility != View.VISIBLE) {

            alphaAnimatorHide.cancel()

            //Set visibility gone or invisible only
            //when the animation hide is complete
            animListener.setVisibilityOnEndCallback {
                super.setVisibility(visibility)
            }

            alphaAnimatorHide.start()
        }


    }
}