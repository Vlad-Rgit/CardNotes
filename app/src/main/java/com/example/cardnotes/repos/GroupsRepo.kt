package com.example.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cardnotes.NoteApp
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.GroupDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupsRepo {

    private val database = NotesDB.getInstance()

    private val _groups = MutableLiveData<List<GroupDomain>>()

    val groups: LiveData<List<GroupDomain>>
        get() = _groups

    suspend fun refreshItems() {
        withContext(Dispatchers.IO) {
           _groups.postValue(
               database.groupDao.getAll()
                   .map {
                       it.asDomain()
                   })
        }
    }

    suspend fun addGroup(group: GroupDomain) {
        withContext(Dispatchers.IO) {
            database.groupDao.insert(
                group.asDatabase())
        }
    }

}