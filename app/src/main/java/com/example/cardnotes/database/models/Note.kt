package com.example.cardnotes.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var value: String
)