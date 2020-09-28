package com.example.cardnotes.viewmodels

import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.launch

class NoteDetailViewModel(noteId: Int): ViewModel() {

    private val notesRepo = NotesRepo()

    val note = MutableLiveData<NoteDomain>()

    init {

        //if we create new note
        if(noteId == -1) {
            note.value = NoteDomain()
        }
        //If we edit existing note
        else {
            viewModelScope.launch {
                note.postValue(
                    notesRepo.getById(noteId))
            }
        }
    }


}