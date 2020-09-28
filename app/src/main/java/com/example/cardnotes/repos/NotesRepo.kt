package com.example.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.NoteDomain

class NotesRepo {

    private val database = NotesDB.getInstance()

    val notes: LiveData<List<NoteDomain>>
        = Transformations.map(database.noteDao.getAll()) {
        it.map { it.asDomain() }
    }

}