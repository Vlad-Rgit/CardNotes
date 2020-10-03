package com.example.cardnotes.utils

import android.content.Context
import android.util.DisplayMetrics

fun getMaxHeight(context: Context): Int {
    val display = context.resources.displayMetrics
    return display.heightPixels
}