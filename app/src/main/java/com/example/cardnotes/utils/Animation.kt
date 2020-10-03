package com.example.cardnotes.utils

import android.animation.ObjectAnimator
import android.view.View
import java.time.Duration

fun animateRevealY(target: View, duration: Long) {

    val animator = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y,
        0f, target.translationY)

    animator.duration = duration

    animator.start()
}