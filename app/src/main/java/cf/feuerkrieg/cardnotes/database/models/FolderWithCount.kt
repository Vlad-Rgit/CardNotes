package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Embedded
import cf.feuerkrieg.cardnotes.domain.FolderDomain

data class FolderWithCount(
    @Embedded
    val folderDatabase: FolderDatabase,
    var notesCount: Int = -1
)

fun FolderWithCount.asDomain(): FolderDomain {
    val domain = folderDatabase.asDomain()
    domain.notesCount = this.notesCount
    return domain
}