package com.example.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.cardnotes.database.NotesDB
import com.example.cardnotes.domain.GroupDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupsRepo {

    private val database = NotesDB.getInstance()

    val groups: LiveData<List<GroupDomain>> = 
        Transformations.map(database.groupDao.getAll()) {
        it.map { it.asDomain() }
    }


    suspend fun getById(groupId: Int): GroupDomain {
        return withContext(Dispatchers.IO) {
            database.groupDao.getById(groupId)
                .asDomain()
        }
    }


    suspend fun removeGroup(group: GroupDomain) {
        withContext(Dispatchers.IO) {
            database.groupDao.delete(group.asDatabase())
        }
    }

    suspend fun addGroup(group: GroupDomain): Int {
        return withContext(Dispatchers.IO) {
            database.groupDao.insert(
                group.asDatabase())
                .toInt()
        }
    }

    suspend fun isExist(groupName: String): Boolean {
        return withContext(Dispatchers.IO) {
            database.groupDao.getByName(groupName)
                .isNotEmpty()
        }
    }

}