package com.example.cardnotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cardnotes.database.dao.NoteDao
import com.example.cardnotes.database.models.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {

        private lateinit var INSTANCE: NoteDatabase

        fun getInstance(context: Context): NoteDatabase {

            var instance = INSTANCE

            if(!::INSTANCE.isInitialized) {
                instance = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "NotesDB")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
            }

            return instance
        }
    }

}