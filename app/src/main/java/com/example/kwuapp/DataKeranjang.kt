package com.example.kwuapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataKeranjang(
    var namaKursus: String? = "",
    var jumlah: Long? = 0
): Parcelable