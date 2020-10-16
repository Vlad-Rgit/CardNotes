package com.example.cardnotes.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cardnotes.repos.GroupsRepo

class GroupDialogViewModel: ViewModel() {

    private val groupsRepo = GroupsRepo()

    val groups = groupsRepo.groups

}