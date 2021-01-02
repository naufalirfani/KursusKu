package com.warnet.kursusku

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    val remaining: String = "",
    val video: String = ""
): Parcelable