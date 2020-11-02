package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.interfaces.ListAccessor
import cf.feuerkrieg.cardnotes.repos.FolderRepo
import cf.feuerkrieg.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val CURRENT_GROUP_KEY = "CurrentGroupKey"

class MainMenuViewModel
    : ViewModel() {

    private val context = NoteApp.getAppInstance()
        .applicationContext

    private val foldersStack = Stack<FolderDomain>()

    private val notesRepo = NotesRepo()
    private val folderRepo = FolderRepo()

    private val _selectedNotesAmount = MutableLiveData<Int>(0)

    val selectedItemsAccessor = SelectedItemsAccessor()

    private var _allFolders: List<FolderDomain>? = null
    val allFolders = folderRepo.allFolders

    val selectedNotesAmount
        get() = _selectedNotesAmount

    /**
     * String by which filtering will be performed
     * Each time when the property is set,
     * the notes are updated
     */
    var searchQuery: String = ""
        set(value) {
            field = value
            refreshNotes()
        }

    private val allGroup = FolderDomain(
        id = -1,
        name = context.getString(R.string.all_folders)
    )

    val currentGroup = MutableLiveData<FolderDomain>()

    private val _selectedItems = mutableSetOf<BaseDomain>()

    val selectedItems: Set<BaseDomain>
        get() = _selectedItems

    /**
     * All available groups
     */
    val folders = folderRepo.filteredFolders

    /**
     * Notes filtered with searchQuery
     */
    val notes = notesRepo.notes


    init {


        currentGroup.value = allGroup

        currentGroup.observeForever {
            viewModelScope.launch {
                refreshFoldersImpl()
                refreshNotesImpl()
            }
        }

        allFolders.observeForever {
            _allFolders = it
        }
    }

    /**
     * Refresh notes and groups
     */
    fun refreshNotes() {
        viewModelScope.launch {
            refreshNotesImpl()
        }
    }

    fun setAllGroups() {
        currentGroup.value = allGroup
        refreshNotes()
    }


    /**
     * Remove selectedNotes notes and refresh
     */
    fun removeSelectedNotes() {
        viewModelScope.launch(Dispatchers.IO) {

            coroutineScope {

                launch {
                    val folders = _selectedItems
                        .filterIsInstance<FolderDomain>()

                    folderRepo.removeAll(folders)
                }

                launch {
                    val notes = _selectedItems
                        .filterIsInstance<NoteDomain>()

                    notesRepo.removeAll(notes)
                }
            }

            _selectedItems.clear()
            refreshNotesImpl()
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
                folders.value!!.remove(model)
            }
            is NoteDomain -> {
                removeNoteImpl(model)
                notes.value!!.remove(model)
            }
        }
    }

    suspend fun removeFolderImpl(folder: FolderDomain) {
        folderRepo.removeGroup(folder)
    }

    suspend fun removeNoteImpl(note: NoteDomain) {
        notesRepo.remove(note)
        refreshNotesImpl()
    }


    fun moveItems(movedItems: Collection<BaseDomain>, dest: FolderDomain) {
        viewModelScope.launch(Dispatchers.Main) {
            moveItemsImpl(movedItems, dest)
        }
    }

    suspend fun moveItemsImpl(movedItems: Collection<BaseDomain>, dest: FolderDomain) {

        for (item in movedItems) {
            when (item) {
                is FolderDomain -> moveFolderToFolderImpl(item, dest)
                is NoteDomain -> moveNoteToFolderImpl(item, dest)
            }
        }

        refreshFoldersImpl()
        refreshNotesImpl()
    }

    fun moveNoteToFolder(note: NoteDomain, folder: FolderDomain) {
        viewModelScope.launch {
            moveNoteToFolderImpl(note, folder)
            refreshNotesImpl()
        }
    }

    fun moveFolderToFolder(from: FolderDomain, to: FolderDomain) {
        viewModelScope.launch {
            moveFolderToFolderImpl(from, to)
            refreshFoldersImpl()
        }
    }

    suspend fun moveNoteToFolderImpl(note: NoteDomain, folder: FolderDomain) {

        if (note.groupId != null) {
            _allFolders?.let {

                val previousParent = it.first {
                    note.groupId == it.id
                }

                previousParent.notesCount.value = previousParent.notesCount.value!!.minus(1)
            }
        }

        if (folder.isDefaultFolder)
            note.groupId = null
        else {
            note.groupId = folder.id
            folder.notesCount.value = folder.notesCount.value!!.plus(1)
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

                previousParent.notesCount.value = previousParent.notesCount.value!!.minus(
                    folder.notesCount.value!!
                )
            }
        }

        if (dest.isDefaultFolder)
            folder.parentFolderId = null
        else {
            folder.parentFolderId = dest.id
            dest.notesCount.value = dest.notesCount.value!!.plus(
                folder.notesCount.value!!
            )
        }

        folderRepo.updateGroup(folder)
        folderRepo.updateGroup(dest)
    }


    /**
     * Move selected notes to group
     */
    fun moveSelectedNotes(folder: FolderDomain) {
        viewModelScope.launch {

            coroutineScope {

                launch {

                    val folders = _selectedItems
                        .filterIsInstance<FolderDomain>()

                    folders.forEach {
                        it.parentFolderId = folder.id
                    }

                    folderRepo.updateAll(folders)
                }

                launch {
                    val notes = _selectedItems
                        .filterIsInstance<NoteDomain>()

                    notes.forEach {
                        it.groupId = folder.id
                    }

                    notesRepo.updateNotes(notes)
                }
            }

            _selectedItems.clear()
            refreshNotesImpl()
        }
    }

    /**
     * Update note and refresh
     */
    fun updateNote(note: NoteDomain) {
        viewModelScope.launch {
            notesRepo.updateNote(note)
            refreshNotesImpl()
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
        foldersStack.push(folder)
        currentGroup.value = folder
    }

    fun goBackFolder() {
        //Pop current folder
        foldersStack.pop()
        if (foldersStack.empty()) {
            //If empty set all folders
            currentGroup.value = allGroup
        } else {
            //Set previous folder as current
            currentGroup.value = foldersStack.peek()
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
            notesRepo.removeByGroupId(folder.id)
            folderRepo.removeGroup(folder)
        }
    }

    fun removeCurrentGroup() {
        removeGroup(currentGroup.value!!)
    }

    suspend fun addGroupImpl(folder: FolderDomain): Int {
        return withContext(Dispatchers.IO) {
            val id = folderRepo.addGroup(folder)
            id
        }
    }

    /**
     * Implementation of refreshNotes to use
     * within a coroutine
     */
    private suspend fun refreshNotesImpl() {
        withContext(Dispatchers.IO) {
            if (currentGroup.value!!.isDefaultFolder) {
                notesRepo.refreshWithoutFolder()
            } else {
                notesRepo.refreshByFolderId(currentGroup.value!!.id)
            }
        }
    }

    private suspend fun refreshFoldersImpl() {
        viewModelScope.launch {
            if (currentGroup.value!!.isDefaultFolder) {
                folderRepo.refreshWithoutParentFolder()
            } else {
                folderRepo.refreshByParentFolderId(currentGroup.value!!.id)
            }
        }
    }

    inner class SelectedItemsAccessor : ListAccessor<BaseDomain> {

        override fun add(item: BaseDomain) {
            _selectedItems.add(item)
            _selectedNotesAmount.value = _selectedItems.size
        }

        override fun remove(item: BaseDomain) {
            _selectedItems.remove(item)
            _selectedNotesAmount.value = _selectedItems.size
        }

        override fun clear() {
            _selectedItems.clear()
            _selectedNotesAmount.value = _selectedItems.size
        }

        override fun addAll(collection: Collection<BaseDomain>) {
            _selectedItems.addAll(collection)
            _selectedNotesAmount.value = _selectedItems.size
        }

        override fun contains(item: BaseDomain): Boolean {
            return _selectedItems.contains(item)
        }

        override fun getSize(): Int {
            return _selectedItems.size
        }
    }
}