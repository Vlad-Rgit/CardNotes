package cf.feuerkrieg.cardnotes.domain


import cf.feuerkrieg.cardnotes.database.models.NoteDatabase
import java.sql.Timestamp

class NoteDomain(
    id: Int = 0,
    var groupId: Int? = null,
    var position: Int = id,
    name: String = "",
    var value: String = "",
    var groupName: String? = null,
    createdAt: Timestamp = Timestamp(System.currentTimeMillis())
) : BaseDomain(id, name, createdAt, createdAt) {


    fun asDatabase(): NoteDatabase {
        return NoteDatabase(
            noteId = this.id,
            folderId = this.groupId,
            title = this.name,
            position = position,
            value = this.value,
            createdAt = this.createdAt.time
        )
    }


    fun copy(): NoteDomain {
        return NoteDomain(
            name = this.name,
            id = this.id,
            createdAt = this.createdAt,
            groupId = this.groupId,
            groupName = this.groupName
        )
    }

}