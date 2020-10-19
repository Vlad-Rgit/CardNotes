package cf.feuerkrieg.cardnotes.viewmodels

import androidx.lifecycle.ViewModel
import cf.feuerkrieg.cardnotes.repos.GroupsRepo

class GroupDialogViewModel: ViewModel() {

    private val groupsRepo = GroupsRepo()

    val groups = groupsRepo.groups

}