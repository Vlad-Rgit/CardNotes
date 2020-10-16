package com.example.cardnotes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.NoteApp
import com.example.cardnotes.R
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.interfaces.ListAccessor
import com.example.cardnotes.repos.GroupsRepo
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CURRENT_GROUP_KEY = "CurrentGroupKey"


class MainMenuViewModel
    : ViewModel() {

    private val context = NoteApp.getAppInstance()
        .applicationContext

    private val notesRepo = NotesRepo()
    private val groupsRepo = GroupsRepo()

    private val _selectedNotesAmount = MutableLiveData<Int>(0)

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

    private val allGroup = GroupDomain(
        groupId = -1,
        groupName = context.getString(R.string.all_folders))

    val currentGroup = MutableLiveData<GroupDomain>()

    private val _selectedNotes = mutableListOf<NoteDomain>()

    val selectedNotes: List<NoteDomain>
        get() = _selectedNotes

    /**
     * Notes filtered with searchQuery
     */
    val notes = notesRepo.notes

    /**
     * All available groups
     */
    val groups = groupsRepo.groups

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
            notesRepo.removeAll(_selectedNotes.toList())
            _selectedNotes.clear()
            refreshNotesImpl()
        }
    }


    /**
     * Move selected notes to group
     */
    fun moveSelectedNotes(group: GroupDomain) {
        viewModelScope.launch {
            _selectedNotes.forEach {
                it.groupId = group.groupId
            }
            notesRepo.updateNotes(_selectedNotes.toList())
            _selectedNotes.clear()
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

    fun addGroup(group: GroupDomain) {
        viewModelScope.launch {
            addGroupImpl(group)
        }
    }

    fun removeGroup(group: GroupDomain) {
        viewModelScope.launch {
            notesRepo.removeByGroupId(group.groupId)
            groupsRepo.removeGroup(group)
        }
    }

    fun removeCurrentGroup() {
        removeGroup(currentGroup.value!!)
    }

    suspend fun addGroupImpl(group: GroupDomain): Int {
        return withContext(Dispatchers.IO) {
            val id = groupsRepo.addGroup(group)
            id
        }
    }

    /**
     * Implementation of refreshNotes to use
     * within a coroutine
     */
    private suspend fun refreshNotesImpl() {
        withContext(Dispatchers.IO) {
            if(currentGroup.value!!.groupId == -1) {
                notesRepo.refreshItemsByQuery(searchQuery)
            }
            else {
                notesRepo.refreshItemsByQueryByGroup(
                    searchQuery, requireNotNull(currentGroup.value))
            }
        }
    }

    inner class SelectedNotesAccessor: ListAccessor<NoteDomain> {

        override fun add(item: NoteDomain) {
            _selectedNotes.add(item)
            _selectedNotesAmount.value = _selectedNotes.size
        }

        override fun remove(item: NoteDomain) {
            _selectedNotes.remove(item)
            _selectedNotesAmount.value = _selectedNotes.size
        }

        override fun clear() {
            _selectedNotes.clear()
            _selectedNotesAmount.value = _selectedNotes.size
        }

        override fun addAll(collection: Collection<NoteDomain>) {
            _selectedNotes.addAll(collection)
            _selectedNotesAmount.value = _selectedNotes.size
        }

        override fun contains(item: NoteDomain): Boolean {
            return _selectedNotes.contains(item)
        }

        override fun getSize(): Int {
            return _selectedNotes.size
        }
    }
}