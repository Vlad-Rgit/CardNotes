package com.example.cardnotes.utils

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Button
import com.example.cardnotes.R


fun styleDialogButton(button: Button) {

    button.setTextColor(
        button.context.resources
            .getColor(R.color.colorPrimary))

    button.setBackgroundColor(
        button.context.resources
            .getColor(android.R.color.white))
}