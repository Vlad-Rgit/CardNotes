package com.example.cardnotes.interfaces

import com.example.cardnotes.domain.NoteDomain

fun interface OnNoteClick {
    fun onNoteClick(noteDomain: NoteDomain)
}