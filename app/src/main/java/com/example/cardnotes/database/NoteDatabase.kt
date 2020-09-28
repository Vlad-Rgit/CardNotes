package com.example.cardnotes.database

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
    version = 8,
    exportSchema = false)
abstract class NotesDB: RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {

        /**
         * Instance of database
         */
        @JvmStatic
        private lateinit var instance: NotesDB


        /**
         * Get instance of Room database
         */
        @JvmStatic
        fun getInstance(): NotesDB {

            if(!::instance.isInitialized) {
                instance = Room.databaseBuilder(
                        NoteApp.getAppInstance(),
                        NotesDB::class.java,
                        "NotesDB")
                    .fallbackToDestructiveMigration()
                    .build()

                initNotes(instance.noteDao)
            }

            return instance
        }


        /**
         * Do initializations at first database creation
         */
        @JvmStatic
        private val createCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Executors.newSingleThreadExecutor().execute {
                    initNotes(getInstance().noteDao)
                }
            }
        }

        /**
         * Init table notes with some data
         */
        @JvmStatic
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