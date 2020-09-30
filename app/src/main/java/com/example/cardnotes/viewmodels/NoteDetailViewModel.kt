package com.example.cardnotes.viewmodels

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteDetailViewModel(noteId: Int): ViewModel() {

    private val _isEdit: Boolean

    private val notesRepo = NotesRepo()

    /***
     * Note for editing
     */
    private val _note = MutableLiveData<NoteDomain>()

    val note: LiveData<NoteDomain>
        get() = _note


    /**
     * End edit event
     */
    private val _endEditEvent = MutableLiveData<Boolean>(false)

    val endEditEvent: LiveData<Boolean>
        get() = _endEditEvent

    init {

        //if we create new note
        if(noteId == -1) {
            _isEdit = false
            _note.value = NoteDomain()
        }
        //If we edit existing note
        else {
            _isEdit = true
            viewModelScope.launch {
                _note.postValue(
                    notesRepo.getById(noteId))
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


}