package com.example.cardnotes.database.models

import androidx.room.Embedded
import androidx.room.Relation


data class GroupWithNotesDatabase(

    @Embedded
    var group: GroupDatabase,

    @Relation(
        parentColumn = "groupId",
        entity = NoteDatabase::class,
        entityColumn = "groupId")
    var notes: List<NoteDatabase>
)