package com.example.cardnotes.database

import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cardnotes.NoteApp
import com.example.cardnotes.database.dao.GroupDao
import com.example.cardnotes.database.dao.NoteDao
import com.example.cardnotes.database.models.GroupDatabase
import com.example.cardnotes.database.models.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.acl.Group

@Database(
    entities =
    [NoteDatabase::class,
     GroupDatabase::class],
    version = 16,
    exportSchema = false)
abstract class NotesDB: RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val groupDao: GroupDao

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

                initGroups(this@Companion.getInstance().groupDao)
                initNotes(this@Companion.getInstance().noteDao)
                showNotes(this@Companion.getInstance().noteDao)
            }
        }

        private fun showNotes(noteDao: NoteDao) {
            CoroutineScope(Dispatchers.IO).launch {

            }
        }

        /**
         * Init table group with some data
         */
        private fun initGroups(groupDao: GroupDao) {
            CoroutineScope(Dispatchers.IO).launch {

                if(groupDao.count() == 0) {

                    val groups = listOf<GroupDatabase>(
                        GroupDatabase(
                            groupName = "Work"
                        ),
                        GroupDatabase(
                            groupName = "School"
                        ),
                        GroupDatabase(
                            groupName = "Family"
                        ),
                        GroupDatabase(
                            groupName = "Food"
                        )
                    )

                    groupDao.insertAll(groups)
                }
            }
        }

        /**
         * Init table notes with some data
         */

        private fun initNotes(noteDao: NoteDao) {

            CoroutineScope(Dispatchers.IO).launch {

                if (noteDao.count() == 0) {

                    val notes = listOf<NoteDatabase>(
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(title = "Son`s birthday", value = "Buy him a car"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),
                        NoteDatabase(
                            title = "Shopping for today",
                            value = "1. Chicken\n2. Tasty soap\n3. More chicken"),
                        NoteDatabase(title = "Day 1", value = "Start of day 1"),)

                    noteDao.insertAll(notes)
                }
            }

        }

    }

}