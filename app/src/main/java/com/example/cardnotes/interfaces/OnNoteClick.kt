package com.example.cardnotes.interfaces

import com.example.cardnotes.domain.NoteDomain

interface OnNoteClick {
    fun onNoteClick(noteDomain: NoteDomain)
}