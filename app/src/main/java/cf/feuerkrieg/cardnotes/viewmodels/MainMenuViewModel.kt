package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.*
import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.repos.FolderRepo
import cf.feuerkrieg.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CURRENT_GROUP_KEY = "CurrentGroupKey"

class MainMenuViewModel
    : ViewModel() {

    private val context = NoteApp.getAppInstance()
        .applicationContext

    private val currentFolderChannel = BroadcastChannel<FolderDomain>(Channel.CONFLATED)

    private val notesRepo = NotesRepo()
    private val folderRepo = FolderRepo()

    private var _allFolders: List<FolderDomain>? = null
    val allFolders = folderRepo.allFolders

    private val allGroup = FolderDomain(
        id = -1,
        name = context.getString(R.string.all_folders)
    )

    val currentGroup = MutableLiveData<FolderDomain>()

    private val _foldersFromCurrentFolderChannel = currentFolderChannel
        .asFlow()
        .mapLatest {
            if(it.isDefaultFolder)
                folderRepo.getWithoutParent()
            else
                folderRepo.getByParentId(it.id)
        }
        .asLiveData()

    val folders = MediatorLiveData<List<FolderDomain>>().apply {
        addSource(_foldersFromCurrentFolderChannel) {
            value = it
        }
        addSource(folderRepo.allFolders) {
            value = it.filter {
                currentGroup.value?.let {
                    current ->
                    return@filter (current.isDefaultFolder &&
                            it.parentFolderId == null) ||
                            it.parentFolderId == current.id
                }
                false
            }
        }
    }


    private val _notesFromCurrentFolderChannel = currentFolderChannel
        .asFlow()
        .mapLatest {
            if(it.isDefaultFolder)
                notesRepo.getWithoutFolder()
            else
                notesRepo.getByFolderId(it.id)
        }
        .asLiveData()


    /**
     * Notes filtered with searchQuery
     */
    val notes = MediatorLiveData<List<NoteDomain>>().apply {
        addSource(_notesFromCurrentFolderChannel) {
            value = it
        }
        addSource(notesRepo.allNotes) {
            value = it.filter {
                currentGroup.value?.let {
                    current ->
                    return@filter (current.isDefaultFolder &&
                            it.groupId == null) ||
                            current.id == it.groupId
                }
                false
            }
        }
    }


    init {

        currentGroup.value = allGroup

        currentGroup.observeForever {
            currentFolderChannel.offer(it)
        }

        allFolders.observeForever {
            _allFolders = it
        }
    }

    /**
     * Remove selectedNotes notes and refresh
     */
    fun removeItems(items: List<BaseDomain>) {

        viewModelScope.launch(Dispatchers.IO) {

            coroutineScope {

                val notes = mutableListOf<NoteDomain>()
                val folders = mutableListOf<FolderDomain>()

                for(item in items) {
                    when(item) {
                        is NoteDomain -> notes.add(item)
                        is FolderDomain -> folders.add(item)
                    }
                }

                launch {
                    folderRepo.removeAll(folders)
                }

                launch {
                    notesRepo.removeAll(notes)
                }
            }
        }
    }

    fun removeModel(model: BaseDomain, refreshLists: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            removeModelImpl(model)
            if (refreshLists) {
                folders.postValue(folders.value)
                notes.postValue(notes.value)
            }
        }
    }

    suspend fun removeModelImpl(model: BaseDomain) {
        when (model) {
            is FolderDomain -> {
                removeFolderImpl(model)
            }
            is NoteDomain -> {
                removeNoteImpl(model)
            }
        }
    }

    suspend fun removeFolderImpl(folder: FolderDomain) {
        folderRepo.removeGroup(folder)
    }

    suspend fun removeNoteImpl(note: NoteDomain) {
        notesRepo.remove(note)

    }


    fun moveItems(movedItems: Collection<BaseDomain>, dest: FolderDomain) {
        viewModelScope.launch(Dispatchers.Main) {
            moveItemsImpl(movedItems, dest)
        }
    }

    suspend fun moveItemsImpl(movedItems: Collection<BaseDomain>, dest: FolderDomain) {
        for (item in movedItems.toList()) {
            when (item) {
                is FolderDomain -> moveFolderToFolderImpl(item, dest)
                is NoteDomain -> moveNoteToFolderImpl(item, dest)
            }
        }
    }

    fun moveNoteToFolder(note: NoteDomain, folder: FolderDomain) {
        viewModelScope.launch {
            moveNoteToFolderImpl(note, folder)
        }
    }

    fun moveFolderToFolder(from: FolderDomain, to: FolderDomain) {
        viewModelScope.launch {
            moveFolderToFolderImpl(from, to)
        }
    }

    suspend fun moveNoteToFolderImpl(note: NoteDomain, folder: FolderDomain) {

        if (note.groupId != null) {

            _allFolders?.let {

                val previousParent = it.first {
                    note.groupId == it.id
                }

                previousParent.notesCount--
                folderRepo.updateGroup(previousParent)
            }
        }

        if (folder.isDefaultFolder)
            note.groupId = null
        else {
            note.groupId = folder.id
            folder.notesCount++
        }

        notesRepo.updateNote(note)
        folderRepo.updateGroup(folder)
    }

    suspend fun moveFolderToFolderImpl(folder: FolderDomain, dest: FolderDomain) {

        if (folder.parentFolderId != null) {

            _allFolders?.let {

                val previousParent = it.first {
                    folder.parentFolderId == it.id
                }

                previousParent.notesCount -= folder.notesCount

                folderRepo.updateGroup(previousParent)
            }

        }

        if (dest.isDefaultFolder)
            folder.parentFolderId = null
        else {
            folder.parentFolderId = dest.id
            dest.notesCount += folder.notesCount
        }

        folderRepo.updateGroup(folder)
        folderRepo.updateGroup(dest)
    }


    /**
     * Move selected notes to group
     */
    fun moveNotes(items: List<NoteDomain>, folder: FolderDomain) {
        viewModelScope.launch {
            moveNotesImpl(items, folder)
        }
    }

    suspend fun moveNotesImpl(items: List<NoteDomain>, folder: FolderDomain) {
        for(i in items) {
            moveNoteToFolderImpl(i, folder)
        }
    }

    /**
     * Update note and refresh
     */
    fun updateNote(note: NoteDomain) {
        viewModelScope.launch {
            notesRepo.updateNote(note)
        }
    }

    suspend fun updateCurrentGroup(name: String) {
        withContext(Dispatchers.IO) {
            val group = currentGroup.value!!
            group.name = name
            currentGroup.postValue(group)
            folderRepo.updateGroup(group)
        }
    }


    suspend fun isGroupNameExists(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            folderRepo.isExist(name)
        }
    }

    fun goToFolder(folder: FolderDomain) {
        currentGroup.value = folder
    }

    fun goBackFolder() {
        val parentFolderId = currentGroup.value!!.parentFolderId
        if (parentFolderId == null) {
            currentGroup.value = allGroup
        } else {
            val parentFolder = _allFolders!!.first {
                parentFolderId == it.id
            }
            currentGroup.value = parentFolder
        }
    }

    fun setCurrentGroup(groupId: Int) {

        if (groupId == -1) {
            currentGroup.value = allGroup
        } else {

            viewModelScope.launch {
                currentGroup.postValue(
                    folderRepo.getById(groupId)
                )
            }
        }
    }

    fun addGroup(folder: FolderDomain) {
        viewModelScope.launch {
            addGroupImpl(folder)
        }
    }

    fun removeGroup(folder: FolderDomain) {
        viewModelScope.launch {
            folderRepo.removeGroup(folder)
        }
    }

    fun removeCurrentGroup() {
        removeGroup(currentGroup.value!!)
        goBackFolder()
    }

    suspend fun addGroupImpl(folder: FolderDomain): Int {
        return withContext(Dispatchers.IO) {
            val id = folderRepo.addGroup(folder)
            id
        }
    }


}