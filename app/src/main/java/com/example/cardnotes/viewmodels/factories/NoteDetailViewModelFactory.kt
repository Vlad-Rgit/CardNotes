package com.example.cardnotes.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cardnotes.viewmodels.NoteDetailViewModel
import java.lang.IllegalArgumentException

class NoteDetailViewModelFactory(private val noteId: Int)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(NoteDetailViewModel::class.java.isAssignableFrom(modelClass)) {
            return NoteDetailViewModel(noteId) as T
        }
        else {
            throw IllegalArgumentException("NoteDetailViewModel is" +
                    " not assignable from ${modelClass.name}")
        }
    }

}