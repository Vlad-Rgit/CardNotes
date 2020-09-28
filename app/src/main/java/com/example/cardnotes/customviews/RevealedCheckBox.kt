package com.example.cardnotes.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox

class RevealedCheckBox
    constructor (
        context: Context,
        attrs: AttributeSet)
    : AppCompatCheckBox(context, attrs) {


    private val alphaAnimator = ValueAnimator.ofFloat(0f, 1f).apply {

        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            this@RevealedCheckBox.alpha = animatedValue
        }
    }

    override fun setVisibility(visibility: Int) {

        super.setVisibility(visibility)

        if(visibility == View.VISIBLE) {
            alphaAnimator.start()
        }
        else {
            alphaAnimator.reverse()
        }
    }
}