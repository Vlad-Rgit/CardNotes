package com.example.cardnotes.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import com.example.cardnotes.callbacks.AnimatorListener

class RevealedCheckBox
    constructor (
        context: Context,
        attrs: AttributeSet)
    : AppCompatCheckBox(context, attrs) {



    private val animatorListener = AnimatorListener()

    private val alphaAnimatorReveal = ValueAnimator.ofFloat(0f, 1f).apply {

        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            this@RevealedCheckBox.alpha = animatedValue
        }
    }

    private val alphaAnimatorHide = ValueAnimator.ofFloat(1f, 0f).apply {
        addListener(animatorListener)
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            this@RevealedCheckBox.alpha = animatedValue
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
            animatorListener.setVisibilityOnEndCallback {
                super.setVisibility(visibility)
            }

            alphaAnimatorHide.start()
        }
    }
}