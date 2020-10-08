package com.example.kwuapp

import android.os.Parcel
import android.os.Parcelable

data class DataKursus(
    var deskripsi: String = "",
    var dilihat: String = "",
    var gambar: String = "",
    var harga: String = "",
    val kategori: String = "",
    var nama: String = "",
    var pembuat: String = "",
    var pengguna: String = "",
    val rating: String = "",
    val remaining: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deskripsi)
        parcel.writeString(dilihat)
        parcel.writeString(gambar)
        parcel.writeString(harga)
        parcel.writeString(kategori)
        parcel.writeString(nama)
        parcel.writeString(pembuat)
        parcel.writeString(pengguna)
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