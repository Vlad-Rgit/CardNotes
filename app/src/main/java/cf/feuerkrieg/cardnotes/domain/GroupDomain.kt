package cf.feuerkrieg.cardnotes.domain

import android.os.Parcel
import android.os.Parcelable
import cf.feuerkrieg.cardnotes.database.models.GroupDatabase

class GroupDomain(
    id: Int = 0,
    name: String = ""
) : BaseDomain(id, name), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    fun asDatabase(): GroupDatabase {
        return GroupDatabase(
            groupId = this.id,
            groupName = this.name
        )
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupDomain> {
        override fun createFromParcel(parcel: Parcel): GroupDomain {
            return GroupDomain(parcel)
        }

        override fun newArray(size: Int): Array<GroupDomain?> {
            return arrayOfNulls(size)
        }
    }
}