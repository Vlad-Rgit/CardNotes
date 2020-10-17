package com.example.cardnotes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.repos.GroupsRepo
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteDetailViewModel(noteId: Int, groupId: Int): ViewModel() {

    private val _isEdit: Boolean

    private val notesRepo = NotesRepo()
    private val groupRepo = GroupsRepo()


    /***
     * Note for editing
     */
    private val _note = MutableLiveData<NoteDomain>()

    val note: LiveData<NoteDomain>
        get() = _note


    val folderNote = MutableLiveData<GroupDomain>()

    val groups = groupRepo.groups

    /**
     * End edit event
     */
    private val _endEditEvent = MutableLiveData(false)

    val endEditEvent: LiveData<Boolean>
        get() = _endEditEvent

    init {
        //if we create new note
        if(noteId == -1) {
            _isEdit = false
            _note.value = NoteDomain()

            //If new group is set
            if(groupId != -1) {
                viewModelScope.launch {
                    val group = groupRepo.getById(groupId)
                    folderNote.postValue(group)
                    note.value!!.groupId = groupId
                }
            }
            else {
                folderNote.value = null
            }

        }
        //If we edit existing note
        else {

            _isEdit = true

            viewModelScope.launch {

                val note = notesRepo.getById(noteId)

                _note.postValue(note)

                val groupId = note.groupId

                if(groupId == null) {
                    folderNote.postValue(null)
                }
                else {
                    folderNote.postValue(
                        groupRepo.getById(groupId))
                }
            }
        }

    }


    //Methods for endEditEvent
    fun onEndEditEvent() {
        _endEditEvent.postValue(true)
    }

    fun onEndEditEventComplete() {
        _endEditEvent.postValue(false)
    }

    /**
     * Save changes to database
     * and call endEditEvent
     */
    fun saveNote() {

        viewModelScope.launch(Dispatchers.IO) {

            if(_isEdit) {
                notesRepo.updateNote(note.value!!)
            }
            else {
                notesRepo.addNote(note.value!!)
            }

            onEndEditEvent()
        }
    }

    fun addGroup(groupDomain: GroupDomain) {
        viewModelScope.launch {

            note.value!!.groupId =
                groupRepo.addGroup(groupDomain)


            folderNote.postValue(groupDomain)
        }
    }

    fun setGroup(groupDomain: GroupDomain) {
        note.value!!.groupId = groupDomain.groupId
        folderNote.value = groupDomain
    }


}