package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.repos.FolderRepo
import cf.feuerkrieg.cardnotes.repos.NotesRepo
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val foldersRepo = FolderRepo()
    private val notesRepo = NotesRepo()

    private val _folders = MutableLiveData<List<FolderDomain>>()
    private val _notes = MutableLiveData<List<NoteDomain>>()

    val folders: LiveData<List<FolderDomain>>
        get() = _folders

    val notes: LiveData<List<NoteDomain>>
        get() = _notes

    val searchQuery = MutableLiveData<String>("")

    init {
        searchQuery.observeForever {
            refreshItems(it)
        }
    }


    private fun refreshItems(query: String) {

        viewModelScope.launch {
            _folders.postValue(
                foldersRepo.getByQuery(query)
            )
        }

        viewModelScope.launch {
            _notes.postValue(
                notesRepo.getByQuery(query)
            )
        }
    }

}