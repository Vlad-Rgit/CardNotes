package com.example.cardnotes.callbacks

import android.animation.Animator

class AnimatorListener: Animator.AnimatorListener {

    private var visibilityOnEndCallback: Runnable? = null

    fun setVisibilityOnEndCallback(runnable: Runnable) {
        visibilityOnEndCallback = runnable
    }

    override fun onAnimationStart(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {
        visibilityOnEndCallback?.run()
    }

    override fun onAnimationCancel(animation: Animator?) {
        visibilityOnEndCallback?.run()
    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

}