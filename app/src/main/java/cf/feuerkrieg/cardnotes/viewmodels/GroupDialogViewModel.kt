package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.ViewModel
import cf.feuerkrieg.cardnotes.repos.FolderRepo

class GroupDialogViewModel: ViewModel() {

    private val groupsRepo = FolderRepo()

    val groups = groupsRepo.filteredFolders

}