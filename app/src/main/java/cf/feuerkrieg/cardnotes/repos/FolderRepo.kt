package cf.feuerkrieg.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cf.feuerkrieg.cardnotes.database.NotesDB
import cf.feuerkrieg.cardnotes.database.models.asDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepo {

    private val database = NotesDB.getInstance()

    val groups: LiveData<List<FolderDomain>> =
        Transformations.map(database.folderDao.getAllWithNotesCount()) {
            it.map { it.asDomain() }
        }


    suspend fun getById(groupId: Int): FolderDomain {
        return withContext(Dispatchers.IO) {
            database.folderDao.getById(groupId)
                .asDomain()
        }
    }

    suspend fun updateGroup(folder: FolderDomain) {
        withContext(Dispatchers.IO) {
            database.folderDao.update(folder.asDatabase())
        }
    }

    suspend fun updateAll(folders: Iterable<FolderDomain>) {
        withContext(Dispatchers.IO) {
            database.folderDao.updateAll(folders.map { it.asDatabase() })
        }
    }

    suspend fun removeAll(groups: Iterable<FolderDomain>) {
        withContext(Dispatchers.IO) {
            database.folderDao.deleteAll(groups.map { it.asDatabase() })
        }
    }

    suspend fun removeGroup(folder: FolderDomain) {
        withContext(Dispatchers.IO) {
            database.folderDao.delete(folder.asDatabase())
        }
    }

    suspend fun addGroup(folder: FolderDomain): Int {
        return withContext(Dispatchers.IO) {
            database.folderDao.insert(
                folder.asDatabase()
            )
                .toInt()
        }
    }

    suspend fun isExist(groupName: String): Boolean {
        return withContext(Dispatchers.IO) {
            database.folderDao.getByName(groupName)
                .isNotEmpty()
        }
    }

}