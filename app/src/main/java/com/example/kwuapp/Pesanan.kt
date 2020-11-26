package com.example.kwuapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pesanan(
    var caraBayar: String? = "",
    var durasi: Long? = 0,
    var jumlah: Long? = 0,
    var status: String? = "",
    var waktu: Long? = 0
): Parcelable