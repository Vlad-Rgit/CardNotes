package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.repos.FolderRepo
import cf.feuerkrieg.cardnotes.repos.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteDetailViewModel(noteId: Int, groupId: Int): ViewModel() {

    private val _isEdit: Boolean

    private val notesRepo = NotesRepo()
    private val groupRepo = FolderRepo()


    private lateinit var oldNote: NoteDomain

    /***
     * Note for editing
     */
    private val _note = MutableLiveData<NoteDomain>()

    val note: LiveData<NoteDomain>
        get() = _note


    val folderNote = MutableLiveData<FolderDomain>()

    val groups = groupRepo.folders

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
            oldNote = NoteDomain()

            //If new group is set
            if(groupId != -1) {
                viewModelScope.launch {
                    val group = groupRepo.getById(groupId)
                    folderNote.postValue(group)
                    note.value!!.groupId = groupId
                    oldNote.groupId = groupId
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

                oldNote = note.copy()

                _note.postValue(note)

                val groupId = note.groupId

                if (groupId == null) {
                    folderNote.postValue(null)
                } else {
                    folderNote.postValue(
                        groupRepo.getById(groupId)
                    )
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

    fun addGroup(folderDomain: FolderDomain) {
        viewModelScope.launch {

            note.value!!.groupId =
                groupRepo.addGroup(folderDomain)

            folderNote.postValue(folderDomain)
        }
    }

    fun setGroup(folderDomain: FolderDomain) {
        note.value!!.groupId = folderDomain.id
        folderNote.value = folderDomain
    }

    fun hasChanges(): Boolean {

        val newNote = note.value!!

        return newNote.groupId != oldNote.groupId ||
                newNote.name != oldNote.name ||
                newNote.value != oldNote.value
    }


}