package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Embedded
import androidx.room.Relation
import cf.feuerkrieg.cardnotes.domain.NoteDomain

data class NoteWithGroupDatabase(

    @Embedded
    var noteDatabase: NoteDatabase,

    @Relation(
        parentColumn = "folderId",
        entity = FolderDatabase::class,
        entityColumn = "folderId"
    )
    var folder: FolderDatabase
) {

    fun asDomain(): NoteDomain {
        val noteDomain = noteDatabase.asDomain()
        noteDomain.groupName = folder.folderName
        return noteDomain
    }
}