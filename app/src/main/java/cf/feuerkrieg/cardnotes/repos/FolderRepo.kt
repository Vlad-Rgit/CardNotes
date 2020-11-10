package cf.feuerkrieg.cardnotes.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cf.feuerkrieg.cardnotes.database.NotesDB
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepo {

    private val database = NotesDB.getInstance()

    val filteredFolders = MutableLiveData<MutableList<FolderDomain>>()

    val foldersFlow = database.folderDao.getAllFlow()

    val allFolders: LiveData<List<FolderDomain>> =
        Transformations.map(database.folderDao.getAll()) {
            it.map { it.asDomain() }
        }

    suspend fun getById(groupId: Int): FolderDomain {
        return withContext(Dispatchers.IO) {
            database.folderDao.getById(groupId)
                .asDomain()
        }
    }



    suspend fun refreshWithoutParentFolder() {
        withContext(Dispatchers.IO) {
            filteredFolders.postValue(database.folderDao
                .getWithoutParentFolder()
                .map { it.asDomain() }
                .toMutableList()
            )
        }
    }


    suspend fun getByParentId(parentFolderId: Int): List<FolderDomain> {
        return database.folderDao
            .getByParentId(parentFolderId)
            .map {
                it.asDomain()
            }
    }


    suspend fun getWithoutParent(): List<FolderDomain> {
        return database.folderDao.getWithoutParentFolder()
            .map { it.asDomain() }
    }


    suspend fun refreshByParentFolderId(folderId: Int) {
        withContext(Dispatchers.IO) {
            filteredFolders.postValue(database.folderDao
                .getByParentFolderId(folderId)
                .map { it.asDomain() }
                .toMutableList()
            )
        }
    }

    suspend fun getByQuery(query: String): List<FolderDomain> {
        return withContext(Dispatchers.IO) {
            database.folderDao
                .getByQuery(query)
                .map { it.asDomain() }
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