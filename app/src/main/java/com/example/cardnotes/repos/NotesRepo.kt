package com.example.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.NoteDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepo {

    private val database = NotesDB.getInstance()

    val notes: LiveData<List<NoteDomain>>
        = Transformations.map(database.noteDao.getAll()) {
        it.map { it.asDomain() }
    }

    suspend fun getById(noteId: Int): NoteDomain {
        return withContext(Dispatchers.IO) {
            return@withContext database.noteDao
                .getById(noteId).asDomain()
        }
    }

}