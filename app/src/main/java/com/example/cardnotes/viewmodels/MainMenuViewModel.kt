package com.example.cardnotes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainMenuViewModel: ViewModel() {

    private val notesRepo = NotesRepo()

    var searchQuery: String = ""

    val notes = notesRepo.notes

    fun filterNotes(searchQuery: String) {
        viewModelScope.launch {
            notesRepo.refreshItemsByQuery(searchQuery)
        }
    }

    fun removeNotes(removed: List<NoteDomain>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepo.removeAll(removed)
        }
    }

    fun updateNote(note: NoteDomain) {
        viewModelScope.launch {
            notesRepo.updateNote(note)
        }
    }

}