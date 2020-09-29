package com.example.cardnotes.repos

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.NoteDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepo {

    private val database = NotesDB.getInstance()

    var notes: LiveData<List<NoteDomain>>
        = Transformations.map(database.noteDao.getAllPositionSorted()) {
        it.map { it.asDomain() }
    }

    suspend fun getById(noteId: Int): NoteDomain {
        return withContext(Dispatchers.IO) {
            return@withContext database.noteDao
                .getById(noteId).asDomain()
        }
    }

    suspend fun refreshItemsByQuery(searchQuery: String) {
        withContext(Dispatchers.IO) {
            notes = Transformations.map(database.noteDao
                .getAllBySearchQueryPositionedSorted(searchQuery)) {
                it.map { it.asDomain() }
            }
        }
    }

    suspend fun addNote(noteDomain: NoteDomain) {
        withContext(Dispatchers.IO) {
            database.noteDao.insert(noteDomain.asDatabase())
        }
    }

    suspend fun updateNote(noteDomain: NoteDomain) {
        withContext(Dispatchers.IO) {
            database.noteDao.update(noteDomain.asDatabase())
        }
    }

    suspend fun remove(noteDomain: NoteDomain) {
        withContext(Dispatchers.IO) {
            database.noteDao.delete(noteDomain.asDatabase())
        }
    }

    suspend fun removeAll(notes: List<NoteDomain>) {
        withContext(Dispatchers.IO) {
            val toRemove = notes.map { it.asDatabase() }
            database.noteDao.deleteAll(toRemove)
        }
    }

}