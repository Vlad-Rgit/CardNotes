package cf.feuerkrieg.cardnotes.repos

import androidx.lifecycle.MutableLiveData
import cf.feuerkrieg.cardnotes.database.NotesDB
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepo {

    private val database = NotesDB.getInstance()

    val folders = MutableLiveData<MutableList<FolderDomain>>()

    suspend fun getById(groupId: Int): FolderDomain {
        return withContext(Dispatchers.IO) {
            database.folderDao.getById(groupId)
                .asDomain()
        }
    }

    suspend fun getWithoutParentFolder() {
        withContext(Dispatchers.IO) {
            folders.postValue(database.folderDao
                .getWithoutParentFolder()
                .map { it.asDomain() }
                .toMutableList()
            )
        }
    }

    suspend fun getByParentFolderId(folderId: Int) {
        withContext(Dispatchers.IO) {
            folders.postValue(database.folderDao
                .getByParentFolderId(folderId)
                .map { it.asDomain() }
                .toMutableList()
            )
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