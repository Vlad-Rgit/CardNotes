package com.example.cardnotes.database

import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cardnotes.NoteApp
import com.example.cardnotes.database.dao.NoteDao
import com.example.cardnotes.database.models.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(
    entities = [NoteDatabase::class],
    version = 14,
    exportSchema = false)
abstract class NotesDB: RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {

        /**
         * Instance of database
         */

        private lateinit var instance: NotesDB


        /**
         * Get instance of Room database
         */

        fun getInstance(): NotesDB {

            if(!::instance.isInitialized) {
                Log.d("RoomCallback", "Creating db")
                instance = Room.databaseBuilder(
                        NoteApp.getAppInstance(),
                        NotesDB::class.java,
                        "NotesDB")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance
        }


        /**
         * Do initializations at first database creation
         */

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                Log.d("RoomCallback", "Creating trigger")
                db.execSQL("""
                    Create trigger if not exists TR_notes_AfterInsert
                    after insert on notes 
                     begin
                       Update notes 
                        Set position = new.noteId
                        Where noteId = new.noteId;
                     end;
                """)

            }
        }

        /**
         * Init table notes with some data
         */

        private fun initNotes(noteDao: NoteDao) {

            CoroutineScope(Dispatchers.IO).launch {

                if (noteDao.count() == 0) {

                    val notes = listOf<NoteDatabase>(
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(name = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            name = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(name = "Day 1", value = "Start of day 1"),
                    )

                    noteDao.insertAll(notes)
                }
            }

        }

    }

}