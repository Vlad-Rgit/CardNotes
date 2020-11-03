package cf.feuerkrieg.cardnotes.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cf.feuerkrieg.cardnotes.NoteApp
import cf.feuerkrieg.cardnotes.database.dao.FolderDao
import cf.feuerkrieg.cardnotes.database.dao.NoteDao
import cf.feuerkrieg.cardnotes.database.models.FolderDatabase
import cf.feuerkrieg.cardnotes.database.models.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities =
    [NoteDatabase::class,
        FolderDatabase::class],
    version = 23,
    exportSchema = false
)
abstract class NotesDB: RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val folderDao: FolderDao

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
            override fun onCreate(db: SupportSQLiteDatabase) {
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

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                initGroups(getInstance().folderDao)
                initNotes(getInstance().noteDao)
            }
        }

        /**
         * Init table group with some data
         */
        private fun initGroups(folderDao: FolderDao) {
            CoroutineScope(Dispatchers.IO).launch {

                if (folderDao.count() == 0) {

                    val groups = listOf<FolderDatabase>(
                        FolderDatabase(
                            folderName = "Work2"
                        ),
                        FolderDatabase(
                            folderName = "School2"
                        ),
                        FolderDatabase(
                            folderName = "Family2"
                        ),
                        FolderDatabase(
                            folderName = "Food2"
                        )
                    )

                    folderDao.insertAll(groups)
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