package cf.feuerkrieg.cardnotes.database.models

import androidx.room.Embedded
import androidx.room.Relation
import cf.feuerkrieg.cardnotes.domain.NoteDomain

data class NoteWithGroupDatabase(

    @Embedded
    var noteDatabase: NoteDatabase,

    @Relation(
        parentColumn = "groupId",
        entity = GroupDatabase::class,
        entityColumn = "groupId")
    var group: GroupDatabase) {

    fun asDomain(): NoteDomain {
        val noteDomain = noteDatabase.asDomain()
        noteDomain.groupName = group.groupName
        return noteDomain
    }
}