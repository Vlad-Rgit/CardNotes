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

private const val CURRENT_GROUP_KEY = "CurrentGroupKey"


class MainMenuViewModel
    : ViewModel() {

    private val context = NoteApp.getAppInstance()
        .applicationContext

    private val notesRepo = NotesRepo()
    private val groupsRepo = FolderRepo()

    private val _selectedNotesAmount = MutableLiveData<Int>(0)

    val selectedItemsAccessor = SelectedItemsAccessor()

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
    val groups = groupsRepo.groups

    /**
     * Notes filtered with searchQuery
     */
    val notes = notesRepo.notes


    init {


        currentGroup.value = allGroup

        currentGroup.observeForever {
            refreshNotes()
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

                    groupsRepo.removeAll(folders)
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

                    groupsRepo.updateAll(folders)
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
            groupsRepo.updateGroup(group)
        }
    }


    suspend fun isGroupNameExists(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            groupsRepo.isExist(name)
        }
    }

    fun setCurrentGroup(groupId: Int) {

        if (groupId == -1) {
            currentGroup.value = allGroup
        } else {

            viewModelScope.launch {
                currentGroup.postValue(
                    groupsRepo.getById(groupId)
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
            groupsRepo.removeGroup(folder)
        }
    }

    fun removeCurrentGroup() {
        removeGroup(currentGroup.value!!)
    }

    suspend fun addGroupImpl(folder: FolderDomain): Int {
        return withContext(Dispatchers.IO) {
            val id = groupsRepo.addGroup(folder)
            id
        }
    }

    /**
     * Implementation of refreshNotes to use
     * within a coroutine
     */
    private suspend fun refreshNotesImpl() {
        withContext(Dispatchers.IO) {
            if (currentGroup.value!!.id == -1) {
                notesRepo.refreshItemsByQuery(searchQuery)
            } else {
                notesRepo.refreshItemsByQueryByGroup(
                    searchQuery, requireNotNull(currentGroup.value)
                )
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