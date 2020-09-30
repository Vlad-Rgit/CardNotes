package com.example.cardnotes.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.cardnotes.domain.GroupDomain

@Entity(tableName = "group",
        indices = [Index("groupName", unique = true)])
data class GroupDatabase (
    @PrimaryKey(autoGenerate = true)
    var groupId: Int = 0,
    var groupName: String = "") {

    fun asDomain(): GroupDomain {
        return GroupDomain(
            groupId = this.groupId,
            groupName = this.groupName)
    }
}

