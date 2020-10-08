package com.example.kwuapp

import android.os.Parcel
import android.os.Parcelable

data class DataKursus(
    var username: String = "",
    var name: String = "",
    var location: String = "",
    var repository: String = "",
    var company: String = "",
    var followers: String = "",
    var following: String = "",
    val avatar: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(repository)
        parcel.writeString(company)
        parcel.writeString(followers)
        parcel.writeString(following)
        parcel.writeString(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataKursus> {
        override fun createFromParcel(parcel: Parcel): DataKursus {
            return DataKursus(parcel)
        }

        override fun newArray(size: Int): Array<DataKursus?> {
            return arrayOfNulls(size)
        }
    }
}