package com.example.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepo {

    private val database = NotesDB.getInstance()

    private val _notes = MutableLiveData<List<NoteDomain>>()

    val notes: LiveData<List<NoteDomain>>
        get() = _notes

    suspend fun getById(noteId: Int): NoteDomain {
        return withContext(Dispatchers.IO) {
            return@withContext database.noteDao
                .getById(noteId).asDomain()
        }
    }

    suspend fun refreshItems() {
        withContext(Dispatchers.IO) {
            _notes.postValue(database.noteDao
                .getAllBySearchQuery("").map {
                    it.asDomain()
                })
        }
    }

    suspend fun refreshItemsByQuery(searchQuery: String) {
        withContext(Dispatchers.IO) {
            _notes.postValue(database.noteDao
                .getAllBySearchQuery(searchQuery)
                .map { it.asDomain() })
        }
    }

    suspend fun removeByGroupId(groupId: Int) {
        withContext(Dispatchers.IO) {
            database.noteDao.deleteByGroupId(groupId)
        }
    }

    suspend fun refreshItemsByQueryByGroup(searchQuery: String,
                                            groupDomain: GroupDomain) {
        withContext(Dispatchers.IO) {
            _notes.postValue(database.noteDao
                .getAllBySearchQueryByGroup(searchQuery, groupDomain.groupId)
                .map { it.asDomain() })
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

    suspend fun updateNotes(notes: Iterable<NoteDomain>) {
        withContext(Dispatchers.IO) {
            database.noteDao.updateAll(notes.map { it.asDatabase() })
        }
    }

}