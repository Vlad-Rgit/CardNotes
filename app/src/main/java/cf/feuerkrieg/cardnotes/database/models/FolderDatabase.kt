package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cf.feuerkrieg.cardnotes.domain.FolderDomain

@Entity(
    tableName = "folder",
    indices = [Index("folderName", unique = true)]
)
data class FolderDatabase(
    @PrimaryKey(autoGenerate = true)
    var folderId: Int = 0,
    @ForeignKey(
        entity = FolderDatabase::class,
        parentColumns = ["folderId"],
        childColumns = ["parentFolderId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
    var parentFolderId: Int? = null,
    var folderName: String = "",
    var colorHex: String = ""
) {

    fun asDomain(): FolderDomain {
        return FolderDomain(
            id = this.folderId,
            name = this.folderName,
            parentFolderId = this.parentFolderId,
            colorHex = this.colorHex
        )
    }
}

