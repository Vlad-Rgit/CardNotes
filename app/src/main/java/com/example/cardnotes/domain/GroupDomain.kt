package com.example.cardnotes.domain

import com.example.cardnotes.database.models.GroupDatabase

data class GroupDomain(
    var groupId: Int = 0,
    var groupName: String = "") {

    fun asDatabase(): GroupDatabase {
        return GroupDatabase(
            groupId = this.groupId,
            groupName = this.groupName)
    }
}