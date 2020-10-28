package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Embedded
import androidx.room.Relation


data class GroupWithNotesDatabase(

    @Embedded
    var folder: FolderDatabase,

    @Relation(
        parentColumn = "folderId",
        entity = NoteDatabase::class,
        entityColumn = "folderId"
    )
    var notes: List<NoteDatabase>
)