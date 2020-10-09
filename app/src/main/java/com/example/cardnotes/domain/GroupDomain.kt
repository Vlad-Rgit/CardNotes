package com.example.cardnotes.domain

import android.os.Parcel
import android.os.Parcelable
import com.example.cardnotes.database.models.GroupDatabase

data class GroupDomain(
    var groupId: Int = 0,
    var groupName: String = "")
    : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    fun asDatabase(): GroupDatabase {
        return GroupDatabase(
            groupId = this.groupId,
            groupName = this.groupName)
    }

    override fun toString(): String {
        return this.groupName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(groupId)
        parcel.writeString(groupName)
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