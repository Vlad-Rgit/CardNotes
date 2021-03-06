package cf.feuerkrieg.cardnotes.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cf.feuerkrieg.cardnotes.viewmodels.NoteDetailViewModel

class NoteDetailViewModelFactory(private val noteId: Int, private val groupId: Int)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(NoteDetailViewModel::class.java.isAssignableFrom(modelClass)) {
            return NoteDetailViewModel(noteId, groupId) as T
        }
        else {
            throw IllegalArgumentException("NoteDetailViewModel is" +
                    " not assignable from ${modelClass.name}")
        }
    }

}