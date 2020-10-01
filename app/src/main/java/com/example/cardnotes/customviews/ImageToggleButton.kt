package com.example.cardnotes.customviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton

class ImageToggleButton
    (context: Context, attributeSet: AttributeSet)
    : AppCompatImageButton(context, attributeSet) {


    var toggledColor = Color.BLACK
    var normalColor = Color.BLACK

    var isChecked: Boolean = false
        set(value) {

           field = value

            if(field)
                setColorFilter(toggledColor)
            else
                setColorFilter(normalColor)
        }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event?.action == MotionEvent.ACTION_UP) {
            isChecked = !isChecked
        }

        return super.onTouchEvent(event)
    }
}