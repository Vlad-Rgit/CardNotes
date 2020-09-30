package com.example.cardnotes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardnotes.repos.GroupsRepo
import kotlinx.coroutines.launch

class GroupDialogViewModel: ViewModel() {

    private val groupsRepo = GroupsRepo()

    val groups = groupsRepo.groups

    init {
        viewModelScope.launch {
            groupsRepo.refreshItems()
        }
    }

}