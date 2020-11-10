package com.example.kwuapp

import android.os.Parcel
import android.os.Parcelable

data class UserDetail(
    var username: String = "",
    var email: String = "",
    var gambar: String = "",
    var saldo: String = "",
    var isiKeranjang: String = "",
    var jumlahKeranjang: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(gambar)
        parcel.writeString(saldo)
        parcel.writeString(isiKeranjang)
        parcel.writeString(jumlahKeranjang)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetail> {
        override fun createFromParcel(parcel: Parcel): UserDetail {
            return UserDetail(parcel)
        }

        override fun newArray(size: Int): Array<UserDetail?> {
            return arrayOfNulls(size)
        }
    }
}