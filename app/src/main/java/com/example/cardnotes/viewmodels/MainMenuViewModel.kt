package com.example.cardnotes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainMenuViewModel: ViewModel() {

    private val notesRepo = NotesRepo()

    /**
     * String by which filtering will be performed
     * Each time when the property is set,
     * the notes are updated
     */
    var searchQuery: String = ""
        set(value) {
            field = value
            refreshNotes()
        }

    /**
     * Notes filtered with searchQuery
     */
    val notes = notesRepo.notes

    init {
        refreshNotes()
    }

    /**
     * Refresh notes with searchQuery
     */
    fun refreshNotes() {
        viewModelScope.launch {
            refreshNotesImpl()
        }
    }


    /**
     * Remove notes and refresh
     */
    fun removeNotes(removed: List<NoteDomain>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepo.removeAll(removed)
            refreshNotesImpl()
        }
    }

    /**
     * Update note and refresh
     */
    fun updateNote(note: NoteDomain) {
        viewModelScope.launch {
            notesRepo.updateNote(note)
            refreshNotesImpl()
        }
    }

    /**
     * Implementation of refreshNotes to use
     * within a coroutine
     */
    private suspend fun refreshNotesImpl() {
        withContext(Dispatchers.IO) {
            notesRepo.refreshItemsByQuery(searchQuery)
        }
    }

}