package com.example.cardnotes.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingDecorator
    (private val mLeft: Int,
     private val mTop: Int,
     private val mRight: Int,
     private val mBottom: Int)
    : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            left = mLeft
            top = mTop
            right = mRight
            bottom = mBottom
        }
    }
}