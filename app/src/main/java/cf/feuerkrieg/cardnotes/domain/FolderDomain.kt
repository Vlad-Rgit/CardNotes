package cf.feuerkrieg.cardnotes.domain

import android.os.Parcel
import android.os.Parcelable
import cf.feuerkrieg.cardnotes.database.models.FolderDatabase
import java.sql.Timestamp


const val DEFAULT_FOLDER_ID = -1

class FolderDomain(
    id: Int = 0,
    name: String = "",
    var parentFolderId: Int? = null,
    createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    var notesCount: Int = -1,
    var colorHex: String = ""
) : BaseDomain(id, name, createdAt, createdAt), Parcelable {

    val isDefaultFolder: Boolean
        get() = id == DEFAULT_FOLDER_ID

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    fun asDatabase(): FolderDatabase {
        return FolderDatabase(
            folderId = this.id,
            folderName = this.name,
            parentFolderId = this.parentFolderId
        )
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<FolderDomain> {

        fun createDefaultFolder() = FolderDomain(-1, "")

        override fun createFromParcel(parcel: Parcel): FolderDomain {
            return FolderDomain(parcel)
        }

        override fun newArray(size: Int): Array<FolderDomain?> {
            return arrayOfNulls(size)
        }
    }
}