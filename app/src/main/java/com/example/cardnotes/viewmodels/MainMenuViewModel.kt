package com.example.cardnotes.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cardnotes.repos.NotesRepo

class MainMenuViewModel: ViewModel() {

    private val notesRepo = NotesRepo()

    val notes = notesRepo.notes

}