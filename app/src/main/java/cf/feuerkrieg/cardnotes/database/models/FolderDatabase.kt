package cf.feuerkrieg.cardnotes.database.models

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cf.feuerkrieg.cardnotes.domain.FolderDomain

@Entity(
    tableName = "folder",
    indices = [Index("folderName", unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = FolderDatabase::class,
            parentColumns = ["folderId"],
            childColumns = ["parentFolderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class FolderDatabase(
    @PrimaryKey(autoGenerate = true)
    var folderId: Int = 0,
    var parentFolderId: Int? = null,
    var folderName: String = "",
    var colorHex: String = "",
    var notesCount: Int = 0
) {

    fun asDomain(): FolderDomain {
        return FolderDomain(
            id = this.folderId,
            name = this.folderName,
            parentFolderId = this.parentFolderId,
            colorHex = this.colorHex,
            notesCount = this.notesCount
        )
    }
}

