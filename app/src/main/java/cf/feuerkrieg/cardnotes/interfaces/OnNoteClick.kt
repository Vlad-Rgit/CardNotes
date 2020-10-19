package cf.feuerkrieg.cardnotes.interfaces

import cf.feuerkrieg.cardnotes.domain.NoteDomain

fun interface OnNoteClick {
    fun onNoteClick(noteDomain: NoteDomain)
}