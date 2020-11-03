package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import java.sql.Timestamp

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = FolderDatabase::class,
            parentColumns = ["folderId"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class NoteDatabase(

    @PrimaryKey(autoGenerate = true)
    var noteId: Int = 0,
    var folderId: Int? = null,
    var position: Int = noteId,
    var title: String,
    var value: String,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun asDomain(): NoteDomain {
        return NoteDomain(
            id = this.noteId,
            groupId = this.folderId,
            position = this.position,
            name = this.title,
            value = this.value,
            createdAt = Timestamp(createdAt)
        )
    }
}