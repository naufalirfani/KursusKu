package com.WarnetIT.kursusku

import android.os.Parcel
import android.os.Parcelable

data class DataKursus(
    var deskripsi: String = "",
    var dilihat: Long = 0,
    var gambar: String = "",
    var harga: String = "",
    val kategori: String = "",
    var nama: String = "",
    var pembuat: String = "",
    var pengguna: Long = 0,
    val rating: String = "",
    val remaining: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deskripsi)
        parcel.writeLong(dilihat)
        parcel.writeString(gambar)
        parcel.writeString(harga)
        parcel.writeString(kategori)
        parcel.writeString(nama)
        parcel.writeString(pembuat)
        parcel.writeLong(pengguna)
        parcel.writeString(rating)
        parcel.writeString(remaining)
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