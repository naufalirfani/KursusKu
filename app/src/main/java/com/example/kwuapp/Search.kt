package com.example.kwuapp

import android.annotation.SuppressLint

class Search {

    var listSearch: ArrayList<DataKursus> = ArrayList()
    @SuppressLint("DefaultLocale")
    fun searchJudul(judul: String, kursus: ArrayList<DataKursus>){
        val dicari = judul.toLowerCase()
        val pattern = dicari.toRegex()
        for (position in 0 until kursus.size){
            val list = kursus[position]
            val judulLower = list.nama.toLowerCase()
            if(pattern.containsMatchIn(judulLower)){
                listSearch.add(list)
            }
        }

    }
}